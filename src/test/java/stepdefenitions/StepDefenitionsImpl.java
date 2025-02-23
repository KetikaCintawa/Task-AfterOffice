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
import restassured.models.ResponseObject;

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

    @When("I add new objects to etalase")
    public void addNewProduct(){
         //Implementation
        System.out.println("Add new objects to etalase");
        String json = "{\r\n" +
                "    \"name\": \"Apple MacBook Pro 16\",\r\n" +
                "    \"data\": {\r\n" +
                "        \"year\": 2019,\r\n" +
                "        \"price\": 1849.99,\r\n" +
                "        \"CPU model\": \"Intel Core i9\",\r\n" +
                "        \"Hard disk size\": \"1 TB\"\r\n" +
                "    }\r\n" +
                "}";

        RestAssured.baseURI = "https://api.restful-api.dev";
        RequestSpecification requestSpecification = RestAssured
                                                    .given();

        Response response = requestSpecification
                            .log()
                            .all()
                            .pathParam("path", "objects")
                            .body(json)
                            .contentType("application/json")
                            .when()
                                .post("/{path}");
        System.out.println("add product" + response.asPrettyString());

        //Validation

        JsonPath addJsonPath = response.jsonPath();
        responseItem = addJsonPath.getObject("", ResponseItem.class);

        Assert.assertEquals(responseItem.name, "Apple MacBook Pro 16");
        Assert.assertNotNull(responseItem.createdAt);
        Assert.assertNotNull(responseItem.id);
        Assert.assertEquals(responseItem.data.year, 2019);
        Assert.assertEquals(responseItem.data.price, 1849.99);
        Assert.assertEquals(responseItem.data.cpuModel, "Intel Core i9");
        Assert.assertEquals(responseItem.data.hardDiskSize, "1 TB");
    
    }

    @When("I add new {string} to etalase")
    public void addNewProductWithPayload(String payload) throws JsonMappingException, JsonProcessingException {
        dataRequest = new DataRequest();
        
        RestAssured.baseURI = "https://api.restful-api.dev";
        RequestSpecification requestSpecification = RestAssured.given();

        Map<String, String> dataCollection = dataRequest.addItemCollection();
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

        Assert.assertEquals(response.getStatusCode(), 201);

        ObjectMapper mapper = new ObjectMapper();
        requestItem = mapper.readValue(json, RequestItem.class);
        
        JsonPath addJsonPath = response.jsonPath();
        responseItem = addJsonPath.getObject("", ResponseItem.class);

        Assert.assertEquals(responseItem.name, requestItem.name);
        Assert.assertNotNull(responseItem.createdAt);
        Assert.assertEquals(responseItem.data.year, requestItem.data.year);
        Assert.assertEquals(responseItem.data.price, requestItem.data.price);
        Assert.assertEquals(responseItem.data.cpuModel, requestItem.data.cpuModel);
        Assert.assertEquals(responseItem.data.hardDiskSize, requestItem.data.hardDiskSize);
    }

    @Then("The objects is available")
    public void getSingleObject(){
                //Implementation
    System.out.println("get single object");

    RestAssured.baseURI = "https://api.restful-api.dev";
        RequestSpecification requestSpecification = RestAssured.given();
    
        Response response = requestSpecification
                                .log()
                                .all()
                                .pathParam("idProduct", 7)
                                .pathParam("path", "objects")
                                .when()
                                    .get("{path}/{idProduct}");
    
        System.out.println("Ini adalah response" + response.asPrettyString());
    }


}

