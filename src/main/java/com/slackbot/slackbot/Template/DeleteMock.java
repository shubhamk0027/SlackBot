package com.slackbot.slackbot.Template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.bolt.handler.builtin.BlockActionHandler;
import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewState;
import com.slackbot.slackbot.MessagePoster;
import com.slackbot.slackbot.Query.DeleteMockRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.input;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.plainTextInput;
import static com.slack.api.model.view.Views.*;
import static com.slack.api.model.view.Views.viewClose;


public class DeleteMock {

    private static final Logger logger = LoggerFactory.getLogger(DeleteMock.class);
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();


    public static View buildView() {
        return view(view -> view
                .callbackId("del-mock")
                .type("modal")
                .notifyOnClose(true)
                .title(viewTitle(title -> title.type("plain_text").text("Delete a MockQuery").emoji(true)))
                .submit(viewSubmit(submit -> submit.type("plain_text").text("Submit").emoji(true)))
                .close(viewClose(close -> close.type("plain_text").text("Cancel").emoji(true)))
                .privateMetadata("{\"response_url\":\"https://hooks.slack.com/actions/T1ABCD2E12/330361579271/0dAEyLY19ofpLwxqozy3firz\"}")
                .blocks(asBlocks(
                        input(input -> input
                                .blockId("teamKey-block")
                                .element(plainTextInput(pti -> pti.actionId("del-mock")))
                                .label(plainText(pt -> pt.text("Your team key here").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("method-block")
                                .element(plainTextInput(pti -> pti.actionId("del-mock")))
                                .label(plainText(pt -> pt.text("Enter Method Type(PUT/POST/DEL/GET/OPTIONS/TRACE/HEAD)").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("path-block")
                                .element(plainTextInput(pti -> pti.actionId("del-mock")))
                                .label(plainText(pt -> pt.text("Enter relative path. Dir names can be a regex").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("query-block")
                                .optional(true)
                                .element(plainTextInput(pti -> pti.actionId("del-mock")))
                                .label(plainText(pt -> pt.text("Enter query parameters here(with ?)").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("queryRegex-block")
                                .optional(true)
                                .element(plainTextInput(pti -> pti.actionId("del-mock")))
                                .label(plainText(pt -> pt.text("Enter query parameters here with regex (with ?)").emoji(true)))
                        )))
        );
    }

    // input validation--->
    public static final ViewSubmissionHandler submissionHandler = (req, ctx) -> {
        logger.info("Verifier running on del mock");
        Map <String, String> errors = new HashMap <>();
        Map<String, Map <String, ViewState.Value>> stateValues = req.getPayload().getView().getState().getValues();

        String teamKey = stateValues.get("teamKey-block").get("del-mock").getValue();
        String method = stateValues.get("method-block").get("del-mock").getValue() ;
        String path = stateValues.get("path-block").get("del-mock").getValue();
        String query = stateValues.get("query-block").get("del-mock").getValue();
        String queryRegex = stateValues.get("queryRegex-block").get("del-mock").getValue();

        method=method.toUpperCase();
        if (!method.equals("POST") && !method.equals("PUT") && !method.equals("DEL") && !method.equals("GET") && !method.equals("TRACE") && !method.equals("HEAD") && !method.equals("OPTIONS")) {
            errors.put("method-block", "This method type is invalid");
        }else if(query!=null && queryRegex!=null) {
            errors.put("queryRegex-block","You cant have both type of query at same time!");
        }

        if (!errors.isEmpty()) {
            return ctx.ack(r -> r.responseAction("errors").errors(errors));
        } else {
            try {
                DeleteMockRequest deleteMockRequest = new DeleteMockRequest();
                deleteMockRequest.setMethod(method);
                deleteMockRequest.setPath(path);
                deleteMockRequest.setTeamKey(teamKey);
                deleteMockRequest.setQueryParameters(query);
                deleteMockRequest.setQueryParameters(queryRegex);
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/_admin/_del/_mock"))
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(deleteMockRequest)))
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
        }
    };

    public static final BlockActionHandler blockActionHandler = ((req, ctx) -> ctx.ack());

}
