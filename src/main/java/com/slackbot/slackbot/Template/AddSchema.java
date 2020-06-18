package com.slackbot.slackbot.Template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.bolt.handler.builtin.BlockActionHandler;
import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewState;
import com.slackbot.slackbot.MessagePoster;
import com.slackbot.slackbot.Query.MockSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.plainTextInput;
import static com.slack.api.model.view.Views.*;
import static com.slack.api.model.view.Views.viewClose;

public class AddSchema {

    private static final Logger logger = LoggerFactory.getLogger(AddSchema.class);
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    // build view
    public static View buildView() {
        return view(view -> view
                .callbackId("add-schema")
                .type("modal")
                .notifyOnClose(true)
                .title(viewTitle(title -> title.type("plain_text").text("Add/Update Schema").emoji(true)))
                .submit(viewSubmit(submit -> submit.type("plain_text").text("Submit").emoji(true)))
                .close(viewClose(close -> close.type("plain_text").text("Cancel").emoji(true)))
                .privateMetadata("{\"response_url\":\"https://hooks.slack.com/actions/T1ABCD2E12/330361579271/0dAEyLY19ofpLwxqozy3firz\"}")
                .blocks(asBlocks(
                        input(input -> input
                                .blockId("teamKey-block")
                                .element(plainTextInput(pti -> pti.actionId("add-schema")))
                                .label(plainText(pt -> pt.text("Your team key here").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("method-block")
                                .element(plainTextInput(pti -> pti.actionId("add-schema")))
                                .label(plainText(pt -> pt.text("Enter Method Type(PUT/POST/DEL)").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("schema-block")
                                .element(plainTextInput(pti -> pti.actionId("add-schema").multiline(true)))
                                .label(plainText(pt -> pt.text("Enter JSON schema here").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("path-block")
                                .element(plainTextInput(pti -> pti.actionId("add-schema")))
                                .label(plainText(pt -> pt.text("Enter relative path. Dir names can be a regex.").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("query-block")
                                .optional(true)
                                .element(plainTextInput(pti -> pti.actionId("add-schema")))
                                .label(plainText(pt -> pt.text("Enter query parameters here(with ?)").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("queryRegex-block")
                                .optional(true)
                                .element(plainTextInput(pti -> pti.actionId("add-schema")))
                                .label(plainText(pt -> pt.text("Enter query parameters here with regex (with ?)").emoji(true)))
                        )
                ))
        );
    }

    // input validation--->
    public static final ViewSubmissionHandler submissionHandler = (req, ctx) -> {
        logger.info("Verifier running on add schema");

        Map<String, String> errors = new HashMap <>();
        Map<String, Map <String, ViewState.Value>> stateValues = req.getPayload().getView().getState().getValues();
        String method = stateValues.get("method-block").get("add-schema").getValue() ;
        String query = stateValues.get("query-block").get("add-schema").getValue();
        String queryRegex = stateValues.get("queryRegex-block").get("add-schema").getValue();
        method=method.toUpperCase();

        if (!method.equals("POST") && !method.equals("PUT") && !method.equals("DEL")) {
            errors.put("method-block", "This method type is invalid");
        }else if(query!=null && queryRegex!=null) {
                errors.put("queryRegex-block","You cant have both type of query at same time!");
        }

        if (!errors.isEmpty()) {
            return ctx.ack(r -> r.responseAction("errors").errors(errors));
        } else {
            try {
                MockSchema mockSchema = new MockSchema()
                    .setSchema(stateValues.get("schema-block").get("add-schema").getValue())
                    .setPath(stateValues.get("path-block").get("add-schema").getValue())
                    .setTeamKey(stateValues.get("teamKey-block").get("add-schema").getValue())
                    .setMethod(method)
                    .setQueryParameters(query)
                    .setQueryParameters(queryRegex);
                mockSchema.log();
                HttpRequest httpRequest =  HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/_admin/_add/_schema"))
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(mockSchema)))
                        .build();
                HttpResponse <String> response = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
                logger.info(response.toString());
                if(response.statusCode()!=200) throw new InterruptedException(response.body());
                MessagePoster.send(response.body(),req.getPayload().getUser().getId());
            } catch(Exception e) {
                e.printStackTrace();
                errors.put("teamKey-block", e.getMessage());
                return ctx.ack(r -> r.responseAction("errors").errors(errors));
            }
            return ctx.ack();
        }
    };

    public static final BlockActionHandler blockActionHandler = ((req, ctx) -> {
        logger.info("Action handler");
        logger.info(req.toString());
        return ctx.ack();
    });

}
//    SectionBlock(type=section, text=MarkdownTextObject(type=mrkdwn, text=Select method type, verbatim=false), blockId=method-block, fields=null, accessory=StaticSelectElement(type=static_select, placeholder=PlainTextObject(type=plain_text, text=Select a method, emoji=true), actionId=add-schema, options=[OptionObject(text=PlainTextObject(type=plain_text, text=POST, emoji=true), value=POST, description=null, url=null), OptionObject(text=PlainTextObject(type=plain_text, text=PUT, emoji=true), value=PUT, description=null, url=null), OptionObject(text=PlainTextObject(type=plain_text, text=DEL, emoji=true), value=DEL, description=null, url=null)], optionGroups=null, initialOption=null, confirm=null))
//    SectionBlock(type=section, text=MarkdownTextObject(type=mrkdwn, text=Select method type, verbatim=false), blockId=method-block, fields=null, accessory=StaticSelectElement(type=static_select, placeholder=PlainTextObject(type=plain_text, text=Select a method, emoji=true), actionId=add-schema, options=[OptionObject(text=PlainTextObject(type=plain_text, text=POST, emoji=true), value=POST, description=null, url=null), OptionObject(text=PlainTextObject(type=plain_text, text=PUT, emoji=true), value=PUT, description=null, url=null), OptionObject(text=PlainTextObject(type=plain_text, text=DEL, emoji=true), value=DEL, description=null, url=null)], optionGroups=null, initialOption=null, confirm=null))
