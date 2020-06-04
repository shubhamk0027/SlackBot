package com.slackbot.slackbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.bolt.handler.builtin.BlockActionHandler;
import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

public class AddTeam {

    private static final Logger logger = LoggerFactory.getLogger(AddTeam.class);
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    // build view
    public static View buildView() {
        return view(view -> view
                .callbackId("add-team")
                .type("modal")
                .notifyOnClose(true)
                .title(viewTitle(title -> title.type("plain_text").text("Create New Team").emoji(true)))
                .submit(viewSubmit(submit -> submit.type("plain_text").text("Submit").emoji(true)))
                .close(viewClose(close -> close.type("plain_text").text("Cancel").emoji(true)))
                .blocks(asBlocks(
                        input(input -> input
                                .blockId("name-block")
                                .element(plainTextInput(pti -> pti.actionId("add-team")))
                                .label(plainText(pt -> pt.text("Enter New Team Name").emoji(true)))
                        )
                ))
        );
    }

    // input validation--->
    public static final ViewSubmissionHandler submissionHandler = (req, ctx) -> {
        logger.info("Verifying team name");
        Map<String, Map <String, ViewState.Value>> stateValues = req.getPayload().getView().getState().getValues();
        String name = stateValues.get("name-block").get("add-team").getValue();
        Map<String, String> errors = new HashMap <>();
        for(int i=0;i<name.length();i++) {
            if(i==0 && !Character.isAlphabetic(name.charAt(i))){
                errors.put("name-block","Team name should start with an alphabet!");
                break;
            }else if(!Character.isLetterOrDigit(name.charAt(i))) {
                errors.put("name-block", "Team Name can not contain special characters!");
                break;
            }
        }

        if (!errors.isEmpty()) {
            return ctx.ack(r -> r.responseAction("errors").errors(errors));
        } else {
            logger.info("Full req->"+req.getPayload().getView().toString());
            logger.info("Finally submitted!");
            logger.info(req.getHeaders().toString());

            CreateTeamQuery createTeamQuery = new CreateTeamQuery();
            createTeamQuery.teamName=name;
            createTeamQuery.adminId=req.getPayload().getUser().getUsername();

            try {
                HttpRequest httpRequest =  HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/_admin/_add/_team"))
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(createTeamQuery)))
                        .build();
                HttpResponse<String> response = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
                logger.info(response.toString());
                if(response.statusCode()!=200) throw new InterruptedException(response.body());
                MessagePoster.send(response.body());
                return ctx.ack();
            } catch(URISyntaxException | InterruptedException | IOException e) {
                e.printStackTrace();
                errors.put("name-block", e.getMessage());
                return ctx.ack(r -> r.responseAction("errors").errors(errors));
            }
        }
    };


    // will be req in case of wrong submission!
    public static final BlockActionHandler blockActionHandler = ((req, ctx) -> {
        logger.info("Values recieved: "+req.toString());
        logger.info(req.getHeaders().toString());
        logger.info(req.getPayload().getActions().get(0).getValue());
        return ctx.ack();
    });

}

