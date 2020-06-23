package com.slackbot.slackbot;

public class Config {

    private static String baseConfig = "localhost:8080";

    public static void setBaseConfig(String address){
        baseConfig=address;
    }

    public static String getBaseConfig(){
        return baseConfig;
    }

}
