package restassured;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import restassured.models.ResponseObject;

import java.util.List;

public class POJOValidationTest {

    private static final String BASE_URL = "https://api.restful-api.dev";
    private static final String OBJECTS_ENDPOINT = "/objects";
    private String createdObjectId; // Untuk menyimpan ID objek yang dibuat

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test(priority = 1)
    public void getAllObjects() {
        RequestSpecification requestSpecification = RestAssured
                                                    .given();

        Response response = requestSpecification
                .log()
                .all()
                .pathParam("path", "objects")
                .when()
                .get("{path}");

        System.out.println("Response API: " + response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 200, "Status code is not 200");

        List<ResponseObject> responseObjects = response.jsonPath().getList("", ResponseObject.class);

        Assert.assertFalse(responseObjects.isEmpty(), "List of objects is empty");

        ResponseObject firstObject = responseObjects.get(0);
        Assert.assertNotNull(firstObject.id, "ID of the first object is null");
        Assert.assertNotNull(firstObject.name, "Name of the first object is null");
    }

    @Test(priority = 2)
    public void createObject() {
        String json = "{\r\n" +
                "    \"name\": \"Apple MacBook Pro 16\",\r\n" +
                "    \"data\": {\r\n" +
                "        \"year\": 2019,\r\n" +
                "        \"price\": 1849.99,\r\n" +
                "        \"CPU model\": \"Intel Core i9\",\r\n" +
                "        \"Hard disk size\": \"1 TB\"\r\n" +
                "    }\r\n" +
                "}";

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

        Assert.assertEquals(response.getStatusCode(), 200, "Failed to create object");

        ResponseObject responseObject = response.as(ResponseObject.class);

        createdObjectId = responseObject.id;

        Assert.assertEquals(responseObject.name, "Apple MacBook Pro 16");
        Assert.assertNotNull(responseObject.createdAt);
        Assert.assertNotNull(responseObject.id);
        Assert.assertEquals(responseObject.data.year, 2019);
        Assert.assertEquals(responseObject.data.price, 1849.99);
        Assert.assertEquals(responseObject.data.cpuModel, "Intel Core i9");
        Assert.assertEquals(responseObject.data.hardDiskSize, "1 TB");
    }

    @Test(priority = 3)
    public void getSingleObjects() {
        String objectId = createdObjectId; 

        RequestSpecification requestSpecification = RestAssured
                .given();

        Response response = requestSpecification
                .log()
                .all()
                .pathParam("path", "objects")
                .pathParam("id", objectId) 
                .when()
                .get("{path}/{id}"); 

        System.out.println("Response API: " + response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 200, "Failed to fetch object by ID");

        ResponseObject responseObject = response.as(ResponseObject.class);

        Assert.assertNotNull(responseObject.id, "Object ID is null");
        Assert.assertNotNull(responseObject.name, "Object name is null");
        Assert.assertNotNull(responseObject.data, "Object data is null");

        Assert.assertEquals(responseObject.id, objectId, "Object ID does not match");
        Assert.assertEquals(responseObject.name, "Apple MacBook Pro 16", "Object name does not match");
        Assert.assertEquals(responseObject.data.year, 2019, "Year is incorrect");
        Assert.assertEquals(responseObject.data.price, 1849.99, "Price is incorrect");
        Assert.assertEquals(responseObject.data.cpuModel, "Intel Core i9", "CPU model is incorrect");
        Assert.assertEquals(responseObject.data.hardDiskSize, "1 TB", "Hard disk size is incorrect");
}

    @Test(priority = 4)
    public void updateObject() {
        String objectId = createdObjectId;

        String json = "{\r\n" +
        "    \"name\": \"Apple MacBook Pro 16 (Update Object)\",\r\n" +
        "    \"data\": {\r\n" +
        "        \"year\": 2020,\r\n" +
        "        \"price\": 1999,\r\n" +
        "        \"CPU model\": \"Intel Core i9\",\r\n" +
        "        \"Hard disk size\": \"1 TB\"\r\n" +
        "    }\r\n" +
        "}";

        RequestSpecification requestSpecification = RestAssured
                                            .given();

        Response response = requestSpecification
                .log()
                .all()
                .pathParam("path", "objects")
                .pathParam("id", objectId) 
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                   .put("{path}/{id}");

        System.out.println("Response API: " + response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 200, "Failed to update object");

        ResponseObject responseObject = response.as(ResponseObject.class);

        Assert.assertEquals(responseObject.name, "Apple MacBook Pro 16 (Update Object)", "Object name is incorrect");
        Assert.assertNotNull(responseObject.updatedAt);
        Assert.assertNotNull(responseObject.id, "Object ID should not be null");
        Assert.assertEquals(responseObject.data.year, 2020, "Year is incorrect");
        Assert.assertEquals(responseObject.data.price, 1999.0, "Price is incorrect"); 
        Assert.assertEquals(responseObject.data.cpuModel, "Intel Core i9", "CPU model is incorrect");
        Assert.assertEquals(responseObject.data.hardDiskSize, "1 TB", "Hard disk size is incorrect");
}

    @Test(priority = 5)
    public void partiallyUpdateObject() {
        String objectId = createdObjectId;

        String json = "{\n" +
                "   \"name\": \"Apple MacBook Pro 16  New Release\"\n" +
                "}";

        RequestSpecification requestSpecification = RestAssured
                                                    .given();
                        
        Response response = requestSpecification
                            .log()
                            .all()
                            .pathParam("id", objectId) 
                            .pathParam("path", "objects")
                            .body(json)
                            .contentType("application/json")
                            .when()
                                .patch("/{path}/{id}");
        
        System.out.println("Patch Object Response:" + response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 200, "Failed to partially update object");

        ResponseObject responseObject = response.as(ResponseObject.class);

        Assert.assertEquals(responseObject.name, "Apple MacBook Pro 16  New Release", "Object name is incorrect");

}               

     @Test(priority = 6)
     public void deleteObject(){
        String objectId = createdObjectId;
   
        RequestSpecification requestSpecification = RestAssured
            .given();

        Response response = requestSpecification
            .log()
            .all()
            .pathParam("path", "objects")
            .pathParam("id", objectId) 
            .when()
            .delete("{path}/{id}");

        System.out.println("Response API: " + response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 200, "Failed to delete object");

        Response getResponse = requestSpecification
            .log()
            .all()
            .pathParam("path", "objects")
            .pathParam("id", objectId)
            .when()
            .get("{path}/{id}");

        Assert.assertEquals(getResponse.getStatusCode(), 404, "Object was not deleted");


        }


    }
    
