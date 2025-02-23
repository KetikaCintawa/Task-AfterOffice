package resources;

import java.util.HashMap;
import java.util.Map;

public class DataRequest {
    public Map<String, String> addObjectCollection() {
        Map<String, String> dataCollection = new HashMap<>();

        dataCollection.put("addObject", "{\n" +
                "  \"name\": \"Apple MacBook Pro 16\",\n" +
                "  \"data\": {\n" +
                "    \"year\": 2019,\n" +
                "    \"price\": 1849.99,\n" +
                "    \"CPU model\": \"Intel Core i9\",\n" +
                "    \"Hard disk size\": \"1 TB\"\n" +
                "  }\n" +
                "}");

        dataCollection.put("addObject2", "{\n" +
                "  \"name\": \"Dell XPS 13\",\n" +
                "  \"data\": {\n" +
                "    \"year\": 2021,\n" +
                "    \"price\": 1499.99,\n" +
                "    \"CPU model\": \"Intel Core i7\",\n" +
                "    \"Hard disk size\": \"512 GB\"\n" +
                "  }\n" +
                "}");

        return dataCollection;
    }

    public Map<String, String> updateObjectCollection() {
        Map<String, String> dataCollection = new HashMap<>();

        dataCollection.put("updateObject", "{\n" +
                "  \"name\": \"Apple MacBook Pro 16\",\n" +
                "  \"data\": {\n" +
                "    \"year\": 2020,\n" +
                "    \"price\": 1999.99,\n" +
                "    \"CPU model\": \"Intel Core i9\",\n" +
                "    \"Hard disk size\": \"1 TB\"\n" +
                "  }\n" +
                "}");

        dataCollection.put("updateObject2", "{\n" +
                "  \"name\": \"Dell XPS 13\",\n" +
                "  \"data\": {\n" +
                "    \"year\": 2022,\n" +
                "    \"price\": 1599.99,\n" +
                "    \"CPU model\": \"Intel Core i7\",\n" +
                "    \"Hard disk size\": \"1 TB\"\n" +
                "  }\n" +
                "}");

        return dataCollection;
    }
}