package com.apiautomation.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestItem {

    @JsonProperty("name")
    public String name;

    @JsonProperty("data")
    public DataItem data; 

    @JsonProperty("createdAt")
    public String createdAt;

    @JsonProperty("updatedAt")
    public String updatedAt;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataItem {
        @JsonProperty("year")
        public int year;

        @JsonProperty("price")
        public double price;

        @JsonProperty("CPU model")
        public String cpuModel;

        @JsonProperty("Hard disk size")
        public String hardDiskSize;
    }
         
     }


