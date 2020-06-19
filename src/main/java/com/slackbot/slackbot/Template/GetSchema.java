package com.slackbot.slackbot.Template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.bolt.handler.builtin.BlockActionHandler;
import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewState;
import com.slackbot.slackbot.MessagePoster;
import com.slackbot.slackbot.Query.GetSchemaQuery;
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
import static com.slack.api.model.block.element.BlockElements.*;
import static com.slack.api.model.view.Views.*;
import static com.slack.api.model.view.Views.viewClose;

public class GetSchema {

    private static final Logger logger = LoggerFactory.getLogger(GetSchema.class);
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();


    // build view
    public static View buildView() {
        return view(view -> view
                .callbackId("get-schema")
                .type("modal")
                .notifyOnClose(true)
                .title(viewTitle(title -> title.type("plain_text").text("Enter Following Details").emoji(true)))
                .submit(viewSubmit(submit -> submit.type("plain_text").text("Submit").emoji(true)))
                .close(viewClose(close -> close.type("plain_text").text("Cancel").emoji(true)))
                .blocks(asBlocks(
                        input(input -> input
                                .blockId("teamKey-block")
                                .element(plainTextInput(pti -> pti.actionId("get-schema")))
                                .label(plainText(pt -> pt.text("Enter team API Key").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("method-block")
                                .element(plainTextInput(pti -> pti.actionId("get-schema")))
                                .label(plainText(pt -> pt.text("Enter Method Type(PUT/POST/DEL)").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("path-block")
                                .element(plainTextInput(pti -> pti.actionId("get-schema")))
                                .label(plainText(pt -> pt.text("Enter relative path with query parameters to find the schema there").emoji(true)))
                        )
                ))
        );
    }

    // input validation--->
    public static final ViewSubmissionHandler submissionHandler = (req, ctx) -> {
        logger.info("Verifying get schema details");
        Map<String, Map <String, ViewState.Value>> stateValues = req.getPayload().getView().getState().getValues();
        String teamKey = stateValues.get("teamKey-block").get("get-schema").getValue();
        String path = stateValues.get("path-block").get("get-schema").getValue();
        String method = stateValues.get("method-block").get("get-schema").getValue();
        logger.info("Received Schema Req "+teamKey+" for "+path+"  and "+method);

        Map <String, String> errors = new HashMap <>();

        if (!method.equals("POST") && !method.equals("PUT") && !method.equals("DEL")) {
            errors.put("method-block", "This method type is invalid");
            return ctx.ack(r -> r.responseAction("errors").errors(errors));
        }
        try {
            GetSchemaQuery getSchemaQuery= new GetSchemaQuery();
            getSchemaQuery.setMethod(method);
            getSchemaQuery.setTeamKey(teamKey);
            getSchemaQuery.setPath(path);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/_admin/_get/_schema"))
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(getSchemaQuery)))
                    .build();
            HttpResponse <String> resp = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            logger.info(resp.toString());
            if(resp.statusCode() != 200) throw new InterruptedException(resp.body());
            MessagePoster.send(resp.body(),req.getPayload().getUser().getId());
        } catch(Exception e) {
            e.printStackTrace();
            errors.put("teamKey-block", e.getMessage());
            return ctx.ack(r -> r.responseAction("errors").errors(errors));
        }
        return ctx.ack();
    };

    public static final BlockActionHandler blockActionHandler = ((req, ctx) -> ctx.ack());

}
