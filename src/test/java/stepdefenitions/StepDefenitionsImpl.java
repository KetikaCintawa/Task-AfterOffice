package stepdefenitions;

import java.util.Map;

import org.testng.Assert;

import com.apiautomation.model.ResponseItem;
import com.apiautomation.model.request.RequestItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import resources.DataRequest;

public class StepDefenitionsImpl {
    ResponseItem responseItem;
    RequestItem requestItem;
    DataRequest dataRequest;
    String json;
    String idProduct;
    private static final String BASE_URI = "https://api.restful-api.dev";

    @Given("A list of objects are available")
    public void getAllObjects() {
        System.out.println("Getting all objects");
        RestAssured.baseURI = BASE_URI;
        
        try {
            RequestSpecification requestSpecification = RestAssured.given();
            Response response = requestSpecification
                .log()
                .all()
                .when()
                .get("/objects");

            Assert.assertEquals(response.getStatusCode(), 200, "Failed to get objects - Status code is not 200");
            Assert.assertNotNull(response.getBody(), "Response body is null");
            
            JsonPath jsonPath = response.jsonPath();
            Assert.assertNotNull(jsonPath.getList("$"), "No objects found in response");
            
            System.out.println("Response: " + response.asPrettyString());
        } catch (Exception e) {
            System.err.println("Error getting all objects: " + e.getMessage());
            throw e;
        }
    }

    @When("I add a new object to the etalase")
    public void addNewProduct() {
        System.out.println("Adding new object to etalase");
        try {
            String json = "{\n" +
                "    \"name\": \"Apple MacBook Pro 16\",\n" +
                "    \"data\": {\n" +
                "        \"year\": 2019,\n" +
                "        \"price\": 1849.99,\n" +
                "        \"CPU model\": \"Intel Core i9\",\n" +
                "        \"Hard disk size\": \"1 TB\"\n" +
                "    }\n" +
                "}";

            RestAssured.baseURI = BASE_URI;
            RequestSpecification requestSpecification = RestAssured.given();

            Response response = requestSpecification
                .log()
                .all()
                .body(json)
                .contentType("application/json")
                .when()
                .post("/objects");

            Assert.assertEquals(response.getStatusCode(), 200, "Failed to add object - Status code is not 200");
            
            JsonPath addJsonPath = response.jsonPath();
            responseItem = addJsonPath.getObject("", ResponseItem.class);

            // Validate response data
            Assert.assertNotNull(responseItem, "Response item is null");
            Assert.assertEquals(responseItem.name, "Apple MacBook Pro 16", "Product name mismatch");
            Assert.assertNotNull(responseItem.createdAt, "Created timestamp is null");
            Assert.assertNotNull(responseItem.id, "Product ID is null");
            Assert.assertEquals(responseItem.data.year, 2019, "Year mismatch");
            Assert.assertEquals(responseItem.data.price, 1849.99, "Price mismatch");
            Assert.assertEquals(responseItem.data.cpuModel, "Intel Core i9", "CPU model mismatch");
            Assert.assertEquals(responseItem.data.hardDiskSize, "1 TB", "Hard disk size mismatch");

            idProduct = responseItem.id;
            System.out.println("Created object ID: " + idProduct);
        } catch (Exception e) {
            System.err.println("Error adding new product: " + e.getMessage());
            throw e;
        }
    }


    @When("I add a new {string} to etalase")
    public void addNewProducts(String payload) throws JsonMappingException, JsonProcessingException {
        System.out.println("Adding new product with payload: " + payload);
        
        try {
            dataRequest = new DataRequest();
            RestAssured.baseURI = BASE_URI;
            
            Map<String, String> dataCollection = dataRequest.addObjectCollection();
            json = dataCollection.get(payload);
            
            if (json == null) {
                throw new RuntimeException("Payload '" + payload + "' not found in data collection");
            }

            RequestSpecification requestSpecification = RestAssured.given();
            Response response = requestSpecification
                .log()
                .all()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/objects");

            System.out.println("Response API: " + response.asPrettyString());

            Assert.assertEquals(response.getStatusCode(), 200, 
                "Failed to add object - Status code is not 200");

            ObjectMapper mapper = new ObjectMapper();
            requestItem = mapper.readValue(json, RequestItem.class);
            
            JsonPath addJsonPath = response.jsonPath();
            responseItem = addJsonPath.getObject("", ResponseItem.class);

            Assert.assertNotNull(responseItem, "Response item is null");
            Assert.assertNotNull(responseItem.id, "Created object ID is null");
            Assert.assertEquals(responseItem.name, requestItem.name, 
                "Created object name doesn't match request");
            Assert.assertNotNull(responseItem.createdAt, 
                "Created timestamp is null");

            // Fixed null comparison for primitive int
            if (responseItem.data != null && requestItem.data != null) {
                if (requestItem.data.year > 0) {  // Using > 0 instead of != null for int
                    Assert.assertEquals(responseItem.data.year, requestItem.data.year, 
                        "Year doesn't match request");
                }
                if (requestItem.data.cpuModel != null) {
                    Assert.assertEquals(responseItem.data.cpuModel, requestItem.data.cpuModel, 
                        "CPU model doesn't match request");
                }
                if (requestItem.data.hardDiskSize != null) {
                    Assert.assertEquals(responseItem.data.hardDiskSize, requestItem.data.hardDiskSize, 
                        "Hard disk size doesn't match request");
                }
            }

            idProduct = responseItem.id;
            System.out.println("Created object ID: " + idProduct);

        } catch (JsonMappingException e) {
            System.err.println("Error mapping JSON: " + e.getMessage());
            throw e;
        } catch (JsonProcessingException e) {
            System.err.println("Error processing JSON: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error adding new product: " + e.getMessage());
            throw e;
        }
    }

    @Then("I can update object {string}")
public void updateSingleProduct(String payload) throws JsonMappingException, JsonProcessingException {
    System.out.println("Updating object with ID: " + idProduct);
    
    try {
        // Validate product ID exists
        if (idProduct == null || idProduct.trim().isEmpty()) {
            throw new IllegalStateException("Product ID is null or empty. Make sure a product was created successfully before updating.");
        }

        // Initialize DataRequest and get update payload
        dataRequest = new DataRequest();
        Map<String, String> updateDataCollection = dataRequest.updateObjectCollection();
        json = updateDataCollection.get(payload);
        
        // Validate payload exists
        if (json == null) {
            throw new RuntimeException("Update payload '" + payload + "' not found in data collection");
        }

        // Parse the update payload
        ObjectMapper mapper = new ObjectMapper();
        RequestItem updateRequest = mapper.readValue(json, RequestItem.class);

        // Set up and execute the update request
        RestAssured.baseURI = BASE_URI;
        RequestSpecification requestSpecification = RestAssured.given();
        
        Response response = requestSpecification
            .log()
            .all()
            .pathParam("id", idProduct)
            .body(json)
            .contentType(ContentType.JSON)
            .when()
            .put("/objects/{id}");

        System.out.println("Update Response: " + response.asPrettyString());

        // Validate response status
        Assert.assertEquals(response.getStatusCode(), 200, 
            "Failed to update object - Status code is not 200");

        // Parse and validate response
        JsonPath jsonPath = response.jsonPath();
        ResponseItem updatedItem = jsonPath.getObject("", ResponseItem.class);

        // Basic validation
        Assert.assertNotNull(updatedItem, "Updated item response is null");
        Assert.assertEquals(updatedItem.id, idProduct, "Updated item ID doesn't match");
        Assert.assertNotNull(updatedItem.updatedAt, "Updated timestamp is null");

        // Validate updated fields match request
        Assert.assertEquals(updatedItem.name, updateRequest.name, 
            "Updated name doesn't match request");

        // Validate data fields if present
        if (updatedItem.data != null && updateRequest.data != null) {
            if (updateRequest.data.year > 0) {
                Assert.assertEquals(updatedItem.data.year, updateRequest.data.year, 
                    "Updated year doesn't match request");
            }
            
            if (updateRequest.data.price > 0) {
                Assert.assertEquals(updatedItem.data.price, updateRequest.data.price, 
                    "Updated price doesn't match request");
            }
            
            if (updateRequest.data.cpuModel != null) {
                Assert.assertEquals(updatedItem.data.cpuModel, updateRequest.data.cpuModel, 
                    "Updated CPU model doesn't match request");
            }
            
            if (updateRequest.data.hardDiskSize != null) {
                Assert.assertEquals(updatedItem.data.hardDiskSize, updateRequest.data.hardDiskSize, 
                    "Updated hard disk size doesn't match request");
            }
        }

        System.out.println("Successfully updated object with ID: " + idProduct);

    } catch (JsonMappingException e) {
        System.err.println("Error mapping JSON for update: " + e.getMessage());
        throw e;
    } catch (JsonProcessingException e) {
        System.err.println("Error processing JSON for update: " + e.getMessage());
        throw e;
    } catch (Exception e) {
        System.err.println("Error updating product: " + e.getMessage());
        throw e;
    }
}

    @Then("The object is available")
    public void getSingleObject() {
        System.out.println("Getting single object");
        
        if (idProduct == null || idProduct.trim().isEmpty()) {
            throw new IllegalStateException("Product ID is null or empty. Make sure a product was created successfully before getting it.");
        }

        try {
            RestAssured.baseURI = BASE_URI;
            RequestSpecification requestSpecification = RestAssured.given();
            
            Response response = requestSpecification
                .log()
                .all()
                .pathParam("id", idProduct)
                .when()
                .get("/objects/{id}");

            Assert.assertEquals(response.getStatusCode(), 200, "Failed to get object - Status code is not 200");
            
            JsonPath jsonPath = response.jsonPath();
            ResponseItem retrievedItem = jsonPath.getObject("", ResponseItem.class);
            
            Assert.assertNotNull(retrievedItem, "Retrieved item is null");
            Assert.assertEquals(retrievedItem.id, idProduct, "Retrieved item ID doesn't match");
            Assert.assertNotNull(retrievedItem.name, "Retrieved item name is null");
            Assert.assertNotNull(retrievedItem.data, "Retrieved item data is null");
            
            System.out.println("Response: " + response.asPrettyString());
        } catch (Exception e) {
            System.err.println("Error getting single object: " + e.getMessage());
            throw e;
        }
    }
}