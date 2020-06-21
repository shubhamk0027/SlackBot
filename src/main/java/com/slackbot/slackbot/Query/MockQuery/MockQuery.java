package com.slackbot.slackbot.Query.MockQuery;

import com.slackbot.slackbot.Query.MockResponse;

// A mock query is a pair of request and the response to be returned on that request

public class MockQuery {

    private MockRequest mockRequest;
    private MockResponse mockResponse;

    public MockQuery inCase(MockRequest mockRequest) {
        this.mockRequest = mockRequest;
        return this;
    }

    public MockQuery respondWith(MockResponse mockResponse) {
        this.mockResponse = mockResponse;
        return this;
    }

    public MockRequest getMockRequest() {
        return mockRequest;
    }

    public MockResponse getMockResponse() {
        return mockResponse;
    }
}
