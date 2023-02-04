package Trello;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.Configurations;

import static io.restassured.RestAssured.given;

public class TrelloTests {
    RequestSpecification request;
    Response response;
    String organizationID;
    String memberID;
    String boardID;
    String listID;

    @BeforeClass
    public void requestSpec() {

        Configurations config = new Configurations();

        request = RestAssured.given();
        request.baseUri(config.getBaseURL());
        request.basePath("/organizations");
        request.queryParam("key", config.getApiKey());
        request.queryParam("token", config.getToken());
        request.header("Content-Type", "application/json");
        //request.log().all();
    }

    @Test(priority = 1)
    public void testCreatingNewOrganization() {
        response =
                given()
                        .spec(request)
                        .queryParam("displayName", "FWD_Trello")
                        .when()
                        .post();

        response.prettyPrint();

        response.then().statusCode(200);
        response.then().statusLine("HTTP/1.1 200 OK");

        JsonPath path = response.jsonPath();

        organizationID = path.getString("id");
        System.out.println(organizationID);
    }

    @Test(enabled = true, priority = 2)
    public void getMemberID() {
        response = given()
                .spec(request)
                .basePath("/members/me")
                .when()
                .get();
        //response.prettyPrint();

        JsonPath path = response.jsonPath();

        memberID = path.getString("id");
        System.out.println(memberID);
    }

    @Test(priority = 3)
    public void testGetOrganizationsForAMember() {
        given()
                .spec(request).basePath("/members/" + memberID)
                .when().get()
                .then().assertThat()
                .statusCode(200);
    }

    @Test(priority = 4)
    public void testCreatingNewBoard() {
        response = given()
                .spec(request)
                .basePath("/boards/")
                .queryParam("name", "B1")
                .queryParam("idOrganization", organizationID)
                .when()
                .post();

        response.prettyPrint();
        response.then().statusCode(200);

        JsonPath path = response.jsonPath();
        boardID = path.getString("id");
        System.out.println(boardID);
    }

    @Test(priority = 5)
    public void getBoardsInOrganization() {
        response = given()
                .spec(request)
                .basePath("/organizations/" + organizationID)
                .when()
                .get();

        response.prettyPrint();
        response.then().statusCode(200);
    }

    @Test(priority = 6)
    public void createNewList() {
        response = given()
                .spec(request)
                .basePath("/lists")
                .queryParam("name", "List No.1")
                .queryParam("idBoard", boardID)
                .when()
                .post();

        response.prettyPrint();
        response.then().statusCode(200);

        JsonPath path = response.jsonPath();
        listID = path.getString("id");
        System.out.println(listID);
    }

    @Test(priority = 7)
    public void getAllListsOnBoard() {
        response = given()
                .spec(request)
                .basePath("/boards/" + boardID)
                .when()
                .get();
        response.prettyPrint();
        response.then().statusCode(200);
    }

    @Test(priority = 8)
    public void archiveList() {
        response = given()
                .spec(request)
                .basePath("/lists/" + listID)
                .when()
                .put();
        response.prettyPrint();
        response.then().statusCode(200);
    }

    @Test(priority = 9)
    public void deleteBoard() {
        response = given()
                .spec(request)
                .basePath("/boards/" + boardID)
                .when()
                .delete();
        response.prettyPrint();
        response.then().statusCode(200);
    }

    @Test(priority = 10)
    public void deleteOrganization() {
        response = given()
                .spec(request)
                .basePath("/organizations/"+organizationID)
                .when()
                .delete();
        response.prettyPrint();
        response.then().statusCode(200);
    }

}
