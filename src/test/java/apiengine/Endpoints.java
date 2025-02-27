package apiengine;

import com.apiautomation.constants.Constants;

import io.cucumber.java.an.E;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Endpoints {
    RequestSpecification requestSpecification;

    public Endpoints(){
        RestAssured.baseURI = Constants.BASE_URL;
        requestSpecification = RestAssured
                                .given();
    }


    public Response getAllObjects(String path){
        Response response = requestSpecification
                            .when()
                                .get(path);
        return response;
    }

    public Response addObjectData(String path, String json) {
        Response response = requestSpecification
                .log()
                .all()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                    .post(path); 
        return response;
    }

    public Response getObjectById(String path, String idProduct){
        Response response = requestSpecification
                               .pathParam("idProduct", idProduct)
                               .pathParam("path", path)
                           .when()
                               .get("{path}/{idProduct}");
        return response;
    }


    public Response updateObjectById(String path, String idProduct, String json){
        Response response = requestSpecification
                            .pathParam("path", path)
                            .pathParam("idProduct", idProduct)
                            .body(json)
                            .contentType("application/json")
                            .when()
                                .put("{path}/{idProduct}");
        return response;
    }

    public Response deleteProductById(String path, String idProduct){
        Response response = requestSpecification
                            .pathParam("path", path)
                            .pathParam("idProduct", idProduct)
                            .contentType("application/json")
                            .when()
                                .delete("{path}/{idProduct}");
        return response;
    }

    public Response getObjectByIds(String path, String idProducts){
         Response response = requestSpecification
                               .pathParam("path", path)
                               .queryParam("id", idProducts)
                           .when()
                               .get("{path}/{id}");
        return response;
    }
}


