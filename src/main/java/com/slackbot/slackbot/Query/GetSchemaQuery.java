package com.slackbot.slackbot.Query;

public class GetSchemaQuery {

    private String method;
    private String teamKey;
    private String path;

    public GetSchemaQuery() {

    }

    public String getTeamKey() {
        return teamKey;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}

