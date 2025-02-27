package stepdefenitions;

import java.util.Map;

import org.testng.Assert;

import com.apiautomation.model.ResponseItem;
import com.apiautomation.model.request.RequestItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import apiengine.Endpoints;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import resources.DataRequest;
import apiengine.Assertion;

public class StepDefenitionsImpl {
    ResponseItem responseItem;
    RequestItem requestItem;
    DataRequest dataRequest;
    String json;
    String idProduct;
    private static final String BASE_URI = "https://api.restful-api.dev";
    Endpoints endpoints;
    Response response;
    Assertion assertion;

    @BeforeStep
    public void setUp(){
        endpoints = new Endpoints();
        assertion = new Assertion();
    }

    @Given("A list of objects are available")
    public void getAllObjects() {
        System.out.println("Getting all objects");
            endpoints = new Endpoints();
            response = endpoints.getAllObjects("objects");
            System.out.println("Response migration: " + response.asPrettyString());
    }

    @When("I add a new object to the etalase")
    public void addNewObject() {
        System.out.println("Adding new object to etalase");
            String json = "{\n" +
                "    \"name\": \"Apple MacBook Pro 16\",\n" +
                "    \"data\": {\n" +
                "        \"year\": 2019,\n" +
                "        \"price\": 1849.99,\n" +
                "        \"CPU model\": \"Intel Core i9\",\n" +
                "        \"Hard disk size\": \"1 TB\"\n" +
                "    }\n" +
                "}";

                response = endpoints.addObjectData("objects", json);
                System.out.println("add object response: " + response.asPrettyString());
        
                //Validation
                JsonPath addJsonPath = response.jsonPath();
                responseItem = addJsonPath.getObject("", ResponseItem.class);
        
                Assert.assertEquals(response.statusCode(), 200);
                Assert.assertEquals(responseItem.name,"Apple MacBook Pro 16");
                Assert.assertEquals(responseItem.data.year,2019);
                Assert.assertEquals(responseItem.data.price, 1849.99);
                Assert.assertEquals(responseItem.data.cpuModel, "Intel Core i9");
                Assert.assertEquals(responseItem.data.hardDiskSize, "1 TB");
        
                /*
                 * Simulate kalau idproduct nya kita dapat dari responseItem.id,
                 * Tapi karena id nya akan selalu sama bakanya kita modify manual
                 *  idProduct = responseItem.id;
                 */
                idProduct = responseItem.id;
        }

        
        @When("I add a new {string} to etalase")
        public void addNewProducts(String payload) throws JsonMappingException, JsonProcessingException {
            dataRequest = new DataRequest();
            Map<String, String> dataCollection = dataRequest.addObjectCollection();
            json = dataCollection.get(payload);
    
            if (json == null) {
                throw new RuntimeException("Payload '" + payload + "' not found in data collection");
            }
    
            response = endpoints.addObjectData("objects", json);
            System.out.println("Response API: " + response.asPrettyString());
    
            Assert.assertEquals(response.getStatusCode(), 200, "Failed to add object - Status code is not 200");
    
            ObjectMapper mapper = new ObjectMapper();
            requestItem = mapper.readValue(json, RequestItem.class);
    
            JsonPath jsonPath = response.jsonPath();
            responseItem = jsonPath.getObject("", ResponseItem.class);
    
            assertion.assertAddObject(responseItem, requestItem);
            idProduct = responseItem.id;
        }
    
    @Then("The object is available")
    public void getSingleObject() {
        System.out.println("Getting single object");
        endpoints = new Endpoints();
        response = endpoints.getObjectById("objects", idProduct);

        System.out.println("ini adalah response" + response.asPrettyString());

        }
    
    
    @Then("I can update object {string}")
    public void updateSingleObject(String payload) throws JsonMappingException, JsonProcessingException {
    System.out.println("Updating single object");

    Map<String, String> dataCollection = dataRequest.updateObjectCollection();
    json = dataCollection.get(payload);
    
    if (json == null) {    
        throw new RuntimeException("Payload '" + payload + "' not found in data collection");
    }
    
    response = endpoints.updateObjectById("objects", idProduct, json);
        System.out.println("Response API: " + response.asPrettyString());
    
        Assert.assertEquals(response.getStatusCode(), 200, "Failed to add object - Status code is not 200");
    
        ObjectMapper mapper = new ObjectMapper();
        requestItem = mapper.readValue(json, RequestItem.class);

        JsonPath jsonPath = response.jsonPath();
        responseItem = jsonPath.getObject("", ResponseItem.class);
    
        assertion.assertUpdateObject(responseItem, requestItem);
        idProduct = responseItem.id;

    
    }
    
}