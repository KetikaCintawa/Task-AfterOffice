package scenario;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import restassured.models.ResponseItem;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import restassured.models.ResponseObject;

public class RestE2ETest {

    ResponseItem responseItem;

    private static final String BASE_URL = "https://api.restful-api.dev";
    private static final String OBJECTS_ENDPOINT = "/objects";
    private String idObject; 

    
    /*
     * Scenario Add Object
     * Create new object (hit API add_object)
     * Verify new object is added (hit API single_object)
     * Delete product (hit API delete_object)
     * Verify new object is deleted (hit API single_object)
     */ 

     
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    public void scenarioE2ETest(){
        String json = "{\r\n" +
                "    \"name\": \"Apple MacBook Pro 16\",\r\n" +
                "    \"data\": {\r\n" +
                "        \"year\": 2019,\r\n" +
                "        \"price\": 1849.99,\r\n" +
                "        \"CPU model\": \"Intel Core i9\",\r\n" +
                "        \"Hard disk size\": \"1 TB\"\r\n" +
                "    }\r\n" +
                "}";

        // 1. Create new object
        RequestSpecification requestSpecification = RestAssured
                                                    .given();
        Response response = requestSpecification
                .log()
                .all()
                .pathParam("path", "objects")
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                    .post("{path}");

        System.out.println("Response API: " + response.asPrettyString());

        JsonPath addJsonPath = response.jsonPath();
        responseItem = addJsonPath.getObject("", ResponseItem.class);

        Assert.assertEquals(response.getStatusCode(), 200, "Failed to create object");
        Assert.assertEquals(responseItem.name, "Apple MacBook Pro 16");
        Assert.assertNotNull(responseItem.createdAt);
        Assert.assertNotNull(responseItem.id);
        Assert.assertEquals(responseItem.data.year, 2019);
        Assert.assertEquals(responseItem.data.price, 1849.99);
        Assert.assertEquals(responseItem.data.cpuModel, "Intel Core i9");
        Assert.assertEquals(responseItem.data.hardDiskSize, "1 TB");

        idObject = responseItem.id;

        // 2. Verify new object is added 
        Response getResponse = requestSpecification
                .log()
                .all()
                .pathParam("path", "objects")
                .pathParam("id", idObject)
                .when()
                    .get("{path}/{id}");

        System.out.println("Get Response API: " + getResponse.asPrettyString());

        Assert.assertEquals(getResponse.getStatusCode(), 200, "Failed to fetch object by ID");

        ResponseObject fetchedObject = getResponse.as(ResponseObject.class);

        Assert.assertEquals(fetchedObject.id, idObject, "Object ID does not match");
        Assert.assertEquals(fetchedObject.name, "Apple MacBook Pro 16", "Object name does not match");

        // 3. Delete product
        Response deleteResponse = requestSpecification
            .log()
            .all()
            .pathParam("path", "objects")
            .pathParam("id", idObject) 
            .when()
                .delete("{path}/{id}");

        System.out.println("Response API: " + deleteResponse.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 200, "Failed to delete object");

        // 4. Verify new object is deleted
        Response verifyDeleteResponse = requestSpecification
            .log()
            .all()
            .pathParam("path","objects")
            .pathParam("id", idObject)
            .when()
                .get("{path}/{id}");

        System.out.println("Verify Delete Response API:" + verifyDeleteResponse.asPrettyString());

        Assert.assertEquals(verifyDeleteResponse.getStatusCode(), 404, "Object was not deleted");
    }

    
    }



    /*
     * Gherkin
     * 1. Feature
     * - Given, Then, When, And,But
     * 
     * 
     * - Checkout barang
     * Given : 
     * - user login to apps
     * 
     * When :
     * - action -> user checkout item
     * 
     * Then : 
     * - result/expectation scenario
     * - user successfully checkout
     * 
     * And : 
     * simply prefix di step
     * 
     * 2. Stepdefenition
     * 3. Runner
     */

     

