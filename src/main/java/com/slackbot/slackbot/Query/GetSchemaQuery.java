package com.slackbot.slackbot.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetSchemaQuery {
    private static final Logger logger = LoggerFactory.getLogger(GetSchemaQuery.class);
    private String method;
    private String teamKey;
    private String path;

    public String getTeamKey() {
        return teamKey;
    }
    public String getPath() {
        return path;
    }
    public String getMethod() { return method; }

    public void setTeamKey(String teamKey){
        this.teamKey=teamKey;
    }

    public void setPath(String path){
        this.path=path;
    }
    public void setMethod(String method) { this.method= method;}

    public void log(){
        logger.info("Schema Query......");
        logger.info("Method: "+method);
        logger.info("teamKey: "+teamKey);
        logger.info("path: "+path);
    }
}

