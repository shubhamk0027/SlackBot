package com.slackbot.slackbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.bolt.handler.builtin.BlockActionHandler;
import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
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

public class AddMockQuery {

    private static final Logger logger = LoggerFactory.getLogger(AddMockQuery.class);
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();


    public static View buildView() {
        return view(view -> view
                .callbackId("add-mock")
                .type("modal")
                .notifyOnClose(true)
                .title(viewTitle(title -> title.type("plain_text").text("Add new MockQuery").emoji(true)))
                .submit(viewSubmit(submit -> submit.type("plain_text").text("Submit").emoji(true)))
                .close(viewClose(close -> close.type("plain_text").text("Cancel").emoji(true)))
                .privateMetadata("{\"response_url\":\"https://hooks.slack.com/actions/T1ABCD2E12/330361579271/0dAEyLY19ofpLwxqozy3firz\"}")
                .blocks(asBlocks(
                        input(input -> input
                                .blockId("teamKey-block")
                                .element(plainTextInput(pti -> pti.actionId("add-mock")))
                                .label(plainText(pt -> pt.text("Your team key here").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("method-block")
                                .element(plainTextInput(pti -> pti.actionId("add-mock")))
                                .label(plainText(pt -> pt.text("Enter Method Type(PUT/POST/DEL/GET/OPTIONS/TRACE/HEAD)").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("request-block")
                                .optional(true)
                                .element(plainTextInput(pti -> pti.actionId("add-mock").multiline(true)))
                                .label(plainText(pt -> pt.text("Enter JSON request here").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("path-block")
                                .element(plainTextInput(pti -> pti.actionId("add-mock")))
                                .label(plainText(pt -> pt.text("Enter relative path. Dir names can be a regex.").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("query-block")
                                .optional(true)
                                .element(plainTextInput(pti -> pti.actionId("add-mock")))
                                .label(plainText(pt -> pt.text("Enter query parameters here(with ?)").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("queryRegex-block")
                                .optional(true)
                                .element(plainTextInput(pti -> pti.actionId("add-mock")))
                                .label(plainText(pt -> pt.text("Enter query parameters here with regex (with ?)").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("check-block")
                                .optional(true)
                                .element(plainTextInput(pti -> pti.actionId("add-mock")))
                                .label(plainText(pt -> pt.text("Enter check mode (true for strict false for non strict matching").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("status-block")
                                .element(plainTextInput(pti -> pti.actionId("add-mock")))
                                .label(plainText(pt -> pt.text("Enter Status Code").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("response-block")
                                .optional(true)
                                .element(plainTextInput(pti -> pti.actionId("add-mock").multiline(true)))
                                .label(plainText(pt -> pt.text("Enter Response body JSON").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("headers-block")
                                .optional(true)
                                .element(plainTextInput(pti -> pti.actionId("add-mock").multiline(true)))
                                .label(plainText(pt -> pt.text("Enter headers as: key1:val1, key2:val2 (without quotes)").emoji(true)))
                        )
                ))
        );
    }

    // input validation--->
    public static final ViewSubmissionHandler submissionHandler = (req, ctx) -> {
        logger.info("Verifier running on add schema");
        Map <String, String> errors = new HashMap <>();
        Map<String, Map <String, ViewState.Value>> stateValues = req.getPayload().getView().getState().getValues();

        String teamKey = stateValues.get("teamKey-block").get("add-mock").getValue();
        String method = stateValues.get("method-block").get("add-mock").getValue() ;
        String request = stateValues.get("request-block").get("add-mock").getValue();
        String query = stateValues.get("query-block").get("add-mock").getValue();
        String queryRegex = stateValues.get("queryRegex-block").get("add-mock").getValue();
        String path = stateValues.get("path-block").get("add-mock").getValue();
        String checkMode = stateValues.get("check-block").get("add-mock").getValue();
        String status = stateValues.get("status-block").get("add-mock").getValue();
        String response = stateValues.get("response-block").get("add-mock").getValue();

        String temp = stateValues.get("headers-block").get("add-mock").getValue();
        String[] res= temp.split(",");
        Map<String,String> mp = new HashMap <>();
        for(String pair:res){
            int i=0;
            for(;i<pair.length();i++){
                if(pair.charAt(i)==':') break;
            }
            if(i==pair.length()) {
                errors.put("headers-block","Invalid header format!");
                break;
            }
            mp.put(pair.substring(0,i),pair.substring(i+1));
        }

        if(checkMode==null) checkMode="false";

        method=method.toUpperCase();
        if (!method.equals("POST") && !method.equals("PUT") && !method.equals("DEL") && !method.equals("GET") && !method.equals("TRACE") && !method.equals("HEAD") && !method.equals("OPTIONS")) {
            errors.put("method-block", "This method type is invalid");
        }else if(query!=null && queryRegex!=null) {
            errors.put("queryRegex-block","You cant have both type of query at same time!");
        }else if(!checkMode.equals("false") && !checkMode.equals("true")){
            errors.put("check-block","Check mode must be boolean!");
        }

        if (!errors.isEmpty()) {
            return ctx.ack(r -> r.responseAction("errors").errors(errors));
        } else {
            try {
                MockQuery mockQuery = new MockQuery().inCase( new MockRequest()
                                .fromTeam(teamKey)
                                .hasBody(request)
                                .hasMethod(method)
                                .hasPath(path)
                                .hasQueryParameters(query)
                                .hasQueryParametersRegex(queryRegex)
                                .inCheckMode(checkMode.equals("true")))
                        .respondWith( new MockResponse()
                                .withBody(response)
                                .withHeader(mp)
                                .withStatus(Integer.parseInt(status)));
                    HttpRequest httpRequest =  HttpRequest.newBuilder()
                            .uri(new URI("http://localhost:8080/_admin/_add/_mock"))
                            .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(mockQuery)))
                            .build();
                    HttpResponse <String> resp= httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
                    logger.info(resp.toString());
                    if(resp.statusCode()!=200) throw new InterruptedException(resp.body());
                    MessagePoster.send(resp.body());
            } catch(Exception  e) {
                e.printStackTrace();
                errors.put("teamKey-block",e.getMessage());
                return ctx.ack(r -> r.responseAction("errors").errors(errors));
            }
            return ctx.ack();
        }
    };

    public static final BlockActionHandler blockActionHandler = ((req, ctx) -> {
        String categoryId = req.getPayload().getActions().get(0).getSelectedOption().getValue();
        logger.info("Action handler");
        return ctx.ack();
    });


}
