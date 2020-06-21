package com.slackbot.slackbot.Query;

public class DeleteMockRequest {

    private String teamKey;
    private String method;
    private String path;
    private String queryParameters;
    private String queryParametersRegex;
    private String requestBody;

    public DeleteMockRequest() {

    }

    public String getQueryParameters() {
        return queryParameters;
    }

    public String getQueryParametersRegex() {
        return queryParametersRegex;
    }

    public String getTeamKey() {
        return teamKey;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setQueryParameters(String queryParameters) {
        this.queryParameters = queryParameters;
    }

    public void setQueryParametersRegex(String queryParametersRegex) {
        this.queryParametersRegex = queryParametersRegex;
    }

    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

}
