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

public class DeleteTeam {

    private static final Logger logger = LoggerFactory.getLogger(DeleteTeam.class);
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();


    // build view
    public static View buildView() {
        return view(view -> view
                .callbackId("del-team")
                .type("modal")
                .notifyOnClose(true)
                .title(viewTitle(title -> title.type("plain_text").text("Delete Team").emoji(true)))
                .submit(viewSubmit(submit -> submit.type("plain_text").text("Submit").emoji(true)))
                .close(viewClose(close -> close.type("plain_text").text("Cancel").emoji(true)))
                .blocks(asBlocks(
                        input(input -> input
                                .blockId("teamName-block")
                                .element(plainTextInput(pti -> pti.actionId("del-team")))
                                .label(plainText(pt -> pt.text("Enter Team Name").emoji(true)))
                        ),
                        input(input -> input
                                .blockId("password-block")
                                .element(plainTextInput(pti -> pti.actionId("del-team")))
                                .label(plainText(pt -> pt.text("Enter Password").emoji(true)))
                        )

                ))
        );
    }

    // input validation--->
    public static final ViewSubmissionHandler submissionHandler = (req, ctx) -> {
        logger.info("Verifying Delete Team");

        Map<String, Map <String, ViewState.Value>> stateValues = req.getPayload().getView().getState().getValues();
        String teamName = stateValues.get("teamName-block").get("del-team").getValue();
        String password = stateValues.get("password-block").get("del-team").getValue();

        Map <String, String> errors = new HashMap <>();
        try {
            TeamQuery  teamQuery = new TeamQuery();
            teamQuery.setPassword(password);
            teamQuery.setTeamName(teamName);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://"+ Config.getBaseConfig()+"/_admin/_del/_team"))
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(teamQuery)))
                    .build();
            HttpResponse <String> resp = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            logger.info(resp.toString());
            if(resp.statusCode() != 200) throw new InterruptedException(resp.body());
            MessagePoster.send(resp.body(),req.getPayload().getUser().getId());
        } catch(Exception e) {
            e.printStackTrace();
            errors.put("teamName-block", e.getMessage());
            return ctx.ack(r -> r.responseAction("errors").errors(errors));
        }
        return ctx.ack();
    };

    public static final BlockActionHandler blockActionHandler = ((req, ctx) -> ctx.ack());

}
