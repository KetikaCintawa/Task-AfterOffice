package com.apiautomation.constants;

public class Constants {
    /*
     * 169.10.11.23
     * URL
     * 
     * mvn -test production
     */

    public static final String BASE_URL = "https://api.restful-api.dev";
    private String env;
    private String BASE_URL1;

    public Constants(String env) {
        this.env = env;
        setEnv();
    }

    private void setEnv() {
        if ("Staging".equals(env)) { 
            BASE_URL1 = "https://api.restful-api.dev-staging.com";
        } else {
            BASE_URL1 = "https://api.restful-api.dev.com";
        }
    }

    public String getBaseUrl() {
        return BASE_URL1;
    }
}
