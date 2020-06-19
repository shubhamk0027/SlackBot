package com.slackbot.slackbot.Query;

public class DeleteMockRequest {
    private String teamKey;
    private String method;
    private String path;
    private String queryParameters;
    private String queryParametersRegex;


    public void setQueryParameters(String queryParameters) {
        this.queryParameters=queryParameters;
    }

    public void setQueryParametersRegex(String queryParametersRegex){
        this.queryParametersRegex=queryParametersRegex;
    }

    public String getQueryParameters() {
        return queryParameters;
    }
    public String getQueryParametersRegex() {
        return queryParametersRegex;
    }

    public String getTeamKey() { return teamKey; }
    public void setTeamKey(String teamKey) { this.teamKey = teamKey;}

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

}
