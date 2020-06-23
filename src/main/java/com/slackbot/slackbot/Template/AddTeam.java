package com.slackbot.slackbot.Template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.bolt.handler.builtin.BlockActionHandler;
import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewState;
import com.slackbot.slackbot.Config;
import com.slackbot.slackbot.MessagePoster;
import com.slackbot.slackbot.Query.TeamQuery;
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
                        ),
                        input(input -> input
                                .blockId("password-block")
                                .element(plainTextInput(pti -> pti.actionId("add-team")))
                                .label(plainText(pt -> pt.text("Enter Password").emoji(true)))
                        )
                ))
        );
    }

    // input validation--->
    public static final ViewSubmissionHandler submissionHandler = (req, ctx) -> {
        logger.info("Verifying Create Team");
        Map<String, Map <String, ViewState.Value>> stateValues = req.getPayload().getView().getState().getValues();
        String name = stateValues.get("name-block").get("add-team").getValue();
        String password = stateValues.get("password-block").get("add-team").getValue();

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

        // no restriction on password

        if (!errors.isEmpty()) {
            return ctx.ack(r -> r.responseAction("errors").errors(errors));
        } else {
            logger.info("Full req->"+req.getPayload().getView().toString());
            logger.info("Finally submitted!");
            logger.info(req.getHeaders().toString());

            TeamQuery teamQuery = new TeamQuery();
            teamQuery.setTeamName(name);
            teamQuery.setPassword(password);

            try {
                HttpRequest httpRequest =  HttpRequest.newBuilder()
                        .uri(new URI("http://"+ Config.getBaseConfig()+"/_admin/_add/_team"))
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(teamQuery)))
                        .build();
                HttpResponse<String> response = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
                logger.info(response.toString());
                if(response.statusCode()!=200) throw new InterruptedException(response.body());
                MessagePoster.send(response.body(),req.getPayload().getUser().getId());
                return ctx.ack();
            } catch(URISyntaxException | InterruptedException | IOException e) {
                e.printStackTrace();
                errors.put("name-block", e.getMessage());
                return ctx.ack(r -> r.responseAction("errors").errors(errors));
            }
        }
    };


    // will be req in case of wrong submission!
    public static final BlockActionHandler blockActionHandler = ((req, ctx) -> ctx.ack());

}

