package com.slackbot.slackbot;

import com.slack.api.bolt.response.Response;
import com.slack.api.methods.response.views.ViewsOpenResponse;
import com.slack.api.bolt.App;
import com.slack.api.bolt.jetty.SlackAppServer;
import com.slackbot.slackbot.Template.AddMockQuery;
import com.slackbot.slackbot.Template.DeleteMock;
import com.slackbot.slackbot.Template.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * the Query sub package contains all the same Classes, except that Method is of type string instead of Enum
 * Note use https connection
 * Change url at both interactive and slash command site
 */

@SpringBootApplication
public class SlackbotApplication {

    private static final Logger logger = LoggerFactory.getLogger(SlackbotApplication.class);
    private final String bearerToken;

    SlackbotApplication(@Value("${SLACK_BOT_TOKEN}") String bearerToken){
        this.bearerToken=bearerToken;
    }

    public static void main(String[] args)  {
        SpringApplication.run(SlackbotApplication.class, args);
        logger.info("Spring Application Ready!");
    }

    @Bean
    public CommandLineRunner commandLineRunner(){
        return args -> {
            MessagePoster.setBearerToken(bearerToken);
            App app = new App();

            // Add a team
            app.command("/addteam", (req, ctx) -> {
                ViewsOpenResponse viewsOpenRes = ctx.client().viewsOpen(r -> r
                        .triggerId(ctx.getTriggerId())
                        .view(AddTeam.buildView()));
                if (viewsOpenRes.isOk()) return ctx.ack();
                else return Response.builder().statusCode(500).body(viewsOpenRes.getError()).build();
            });

            app.blockAction("add-team", AddTeam.blockActionHandler);
            app.viewSubmission("add-team", AddTeam.submissionHandler);
            app.viewClosed("add-team", (req, ctx) -> {
                logger.info("App closed!");
                logger.info(req.toString());
                logger.info(req.getHeaders().toString());
                return ctx.ack();
            });


            // delete a team
            app.command("/delteam", (req, ctx) -> {
                ViewsOpenResponse viewsOpenRes = ctx.client().viewsOpen(r -> r
                        .triggerId(ctx.getTriggerId())
                        .view(DeleteTeam.buildView()));
                if (viewsOpenRes.isOk()) return ctx.ack();
                else return Response.builder().statusCode(500).body(viewsOpenRes.getError()).build();
            });

            app.blockAction("del-team", DeleteTeam.blockActionHandler);
            app.viewSubmission("del-team", DeleteTeam.submissionHandler);
            app.viewClosed("del-team", (req, ctx) -> ctx.ack());


            // get Schema on this path
            app.command("/getschema", (req, ctx) -> {
                ViewsOpenResponse viewsOpenRes = ctx.client().viewsOpen(r -> r
                        .triggerId(ctx.getTriggerId())
                        .view(GetSchema.buildView()));
                if (viewsOpenRes.isOk()) return ctx.ack();
                else return Response.builder().statusCode(500).body(viewsOpenRes.getError()).build();
            });

            app.blockAction("get-schema", GetSchema.blockActionHandler);
            app.viewSubmission("get-schema", GetSchema.submissionHandler);
            app.viewClosed("get-schema", (req, ctx) -> ctx.ack());



            // add schema on this path
            app.command("/addschema", (req, ctx) -> {
                ViewsOpenResponse viewsOpenRes = ctx.client().viewsOpen(r -> r
                        .triggerId(ctx.getTriggerId())
                        .view(AddSchema.buildView()));
                if (viewsOpenRes.isOk()) return ctx.ack();
                else return Response.builder().statusCode(500).body(viewsOpenRes.getError()).build();
            });

            app.blockAction("add-schema", AddSchema.blockActionHandler);
            app.viewSubmission("add-schema", AddSchema.submissionHandler);
            app.viewClosed("add-schema", (req, ctx) -> ctx.ack());



            // Add a new Mock Query
            app.command("/addmock", (req, ctx) -> {
                ViewsOpenResponse viewsOpenRes = ctx.client().viewsOpen(r -> r
                        .triggerId(ctx.getTriggerId())
                        .view(AddMockQuery.buildView()));
                if (viewsOpenRes.isOk()) return ctx.ack();
                else return Response.builder().statusCode(500).body(viewsOpenRes.getError()).build();
            });

            app.blockAction("add-mock", AddMockQuery.blockActionHandler);
            app.viewSubmission("add-mock", AddMockQuery.submissionHandler);
            app.viewClosed("add-mock", (req, ctx) -> ctx.ack());



            // delete a MockQuery
            app.command("/delmock", (req, ctx) -> {
                ViewsOpenResponse viewsOpenRes = ctx.client().viewsOpen(r -> r
                        .triggerId(ctx.getTriggerId())
                        .view(DeleteMock.buildView()));
                if (viewsOpenRes.isOk()) return ctx.ack();
                else return Response.builder().statusCode(500).body(viewsOpenRes.getError()).build();
            });

            app.blockAction("del-mock", DeleteMock.blockActionHandler);
            app.viewSubmission("del-mock", DeleteMock.submissionHandler);
            app.viewClosed("del-mock", (req, ctx) -> ctx.ack());



            // Get a lost Key
            app.command("/getkey", (req, ctx) -> {
                ViewsOpenResponse viewsOpenRes = ctx.client().viewsOpen(r -> r
                        .triggerId(ctx.getTriggerId())
                        .view(GetKey.buildView()));
                if (viewsOpenRes.isOk()) return ctx.ack();
                else return Response.builder().statusCode(500).body(viewsOpenRes.getError()).build();
            });

            app.blockAction("get-key", GetKey.blockActionHandler);
            app.viewSubmission("get-key", GetKey.submissionHandler);
            app.viewClosed("get-key", (req, ctx) -> ctx.ack());


            SlackAppServer server = new SlackAppServer(app);
            server.start(); // http://localhost:3000/slack/event
        };
    }

}

// https://7a3e3e11cf7a.ngrok.io/slack/events