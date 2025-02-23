package stepdefenitions;

import java.util.Map;

import org.testng.Assert;

import com.apiautomation.model.ResponseItem;
import com.apiautomation.model.ResponseObject;
import com.apiautomation.model.request.RequestItem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import resources.DataRequest;

public class StepDefenitionsImpl {
    /*
     *  Given A list of objects are available
        When I add new objects to etalase
        Then The objects is available
     */
    ResponseItem responseItem;
    RequestItem requestItem;
    DataRequest dataRequest;
    String json;
    int idProduct;


    @Given("A list of objects are available")
        public void getAllObjects(){
            //Implementation
            System.out.println("getAllObjects");
            RestAssured.baseURI = "https://api.restful-api.dev";
            RequestSpecification requestSpecification = RestAssured
                                                        .given();

            Response response2 = requestSpecification
                                    .log()
                                    .all()
                                .when()
                                    .get("objects");
            System.out.println("reponse" + response2.asPrettyString());
        }

    @When("I add a new object to the etalase")
        public void addNewProduct() {
            System.out.println("Add new objects to etalase");
            String json = "{\n" +
                "    \"name\": \"Apple MacBook Pro 16\",\n" +
                "    \"data\": {\n" +
                "        \"year\": 2019,\n" +
                "        \"price\": 1849.99,\n" +
                "        \"CPU model\": \"Intel Core i9\",\n" +
                "        \"Hard disk size\": \"1 TB\"\n" +
                "    }\n" +
                "}";

            RestAssured.baseURI = "https://api.restful-api.dev";
            RequestSpecification requestSpecification = RestAssured.given();

            Response response = requestSpecification
                .log()
                .all()
                .body(json)
                .contentType("application/json")
                .when()
                .post("/objects");

            Assert.assertEquals(response.getStatusCode(), 200);
            System.out.println("add product response: " + response.asPrettyString());

            JsonPath addJsonPath = response.jsonPath();
            responseItem = addJsonPath.getObject("", ResponseItem.class);

            Assert.assertEquals(responseItem.name, "Apple MacBook Pro 16");
            Assert.assertNotNull(responseItem.createdAt);
            Assert.assertNotNull(responseItem.id);
            Assert.assertEquals(responseItem.data.year, 2019);
            Assert.assertEquals(responseItem.data.price, 1849.99);
            Assert.assertEquals(responseItem.data.cpuModel, "Intel Core i9");
            Assert.assertEquals(responseItem.data.hardDiskSize, "1 TB");

            String idProduct = responseItem.id;
            System.out.println("Created object ID: " + idProduct);
        }

    @When("I add a new {string} to etalase")
        public void addNewProducts(String payload) throws JsonMappingException, JsonProcessingException {
        // Implementation
        dataRequest = new DataRequest();
        
        RestAssured.baseURI = "https://api.restful-api.dev";
        RequestSpecification requestSpecification = RestAssured.given();

        Map<String, String> dataCollection = dataRequest.addObjectCollection();
        json = dataCollection.get(payload);
        
        if (json == null) {
            throw new RuntimeException("Payload " + payload + " not found in data collection");
        }

        Response response = requestSpecification
            .log()
            .all()
            .body(json)
            .contentType("application/json")
            .when()
            .post("/objects");

        // Validation
        Assert.assertEquals(response.getStatusCode(), 200);

        ObjectMapper mapper = new ObjectMapper();
        requestItem = mapper.readValue(json, RequestItem.class);
        
        JsonPath addJsonPath = response.jsonPath();
        responseItem = addJsonPath.getObject("", ResponseItem.class);

        Assert.assertEquals(responseItem.name, requestItem.name);
        Assert.assertNotNull(responseItem.createdAt);
        Assert.assertEquals(responseItem.data.year, requestItem.data.year);
        Assert.assertEquals(responseItem.data.cpuModel, requestItem.data.cpuModel);
        Assert.assertEquals(responseItem.data.hardDiskSize, requestItem.data.hardDiskSize);
    }

    @Then("The object is available")
public void getSingleObject() {
    // Implementation
    System.out.println("Get single object");

    RestAssured.baseURI = "https://api.restful-api.dev";
    RequestSpecification requestSpecification = RestAssured.given();

    // Debugging: Print the ID being used
    System.out.println("Retrieving object with ID: " + idProduct);

    // Send GET request to retrieve the object by ID
    Response response = requestSpecification
        .log()
        .all()
        .pathParam("id", idProduct) // Use the stored idProduct
        .when()
        .get("/objects/{id}");

    // Print the response for debugging
    System.out.println("Response: " + response.asPrettyString());

    // Validate the status code
    Assert.assertEquals(response.getStatusCode(), 200, "Status code is not 200");
}
    }




