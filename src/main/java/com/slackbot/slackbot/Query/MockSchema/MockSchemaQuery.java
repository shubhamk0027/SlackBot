package com.slackbot.slackbot.Query.MockSchema;

import com.slackbot.slackbot.Query.MockResponse;
import org.springframework.stereotype.Service;

@Service
public class MockSchemaQuery {

    private MockSchema mockSchema;
    private MockResponse mockResponse;

    public MockSchemaQuery inCase(MockSchema mockSchema){
        this.mockSchema=mockSchema;
        return this;
    }

    public MockSchemaQuery respondWith(MockResponse mockResponse){
        this.mockResponse=mockResponse;
        return this;
    }

    public MockSchema getMockSchema() {
        return mockSchema;
    }

    public MockResponse getMockResponse() {
        return mockResponse;
    }
}
