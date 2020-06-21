package com.slackbot.slackbot.Query.MockQuery;

// https://github.com/skyscreamer/JSONassert
// STRICT MODE which matches all fields order of arrays and no additional fields allowed, and
// ONLY_MATCHING_FIELDS/LENIENT which only matches fields provided in the request matcher

public class MockRequest {

    private String teamKey;                 // Team Secret API Key
    private String method;                  // POST PUT DEL GET HEAD TRACE OPTIONS
    private String path;                    // some/simple/or/[a-zA-Z0-9]+/path/with/regex
    private String requestBody;             // The PayloadResponse with the request, will be for if GET HEAD TRACE OPTIONS
    private String queryParameters;         // ?simple=query&parameter=(s)
    private String queryParametersRegex;    // ?complex=[a-zA-Z]+&query=parameters[0-9]+
    private boolean checkMode = false;      // PayLoad matching to be STRICT(true) or LENIENT(false)

    public MockRequest() {

    }

    public String getTeamKey() {
        return teamKey;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getQueryParameters() {
        return queryParameters;
    }

    public String getQueryParametersRegex() {
        return queryParametersRegex;
    }

    public String getPath() {
        return path;
    }

    public boolean getCheckMode() {
        return checkMode;
    }

    public MockRequest fromTeam(String teamKey) {
        this.teamKey = teamKey;
        return this;
    }

    public MockRequest inCheckMode(boolean checkMode) {
        this.checkMode = checkMode;
        return this;
    }

    public MockRequest hasMethod(String method) {
        this.method = method;
        return this;
    }

    public MockRequest hasPath(String path) {
        this.path = path;
        return this;
    }

    public MockRequest hasQueryParameters(String queryParameters) {
        this.queryParameters = queryParameters;
        return this;
    }

    public MockRequest hasQueryParametersRegex(String queryParametersRegex) {
        this.queryParametersRegex = queryParametersRegex;
        return this;
    }

    public MockRequest hasRequestBody(String jsonBody) {
        this.requestBody = jsonBody;
        return this;
    }

}


