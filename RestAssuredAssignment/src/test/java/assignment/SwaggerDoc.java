package assignment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class SwaggerDoc {
	public static String baseUrl = "http://rest-api.upskills.in";
	public String accessToken, username, password;

	@Test
	public void testCase1() {
		RestAssured.baseURI = baseUrl;

		// given is to provide all the preconditions and hence the parameters
		Response response = given().header("Authorization",
				"Basic dXBza2lsbHNfcmVzdF9hZG1pbl9vYXV0aF9jbGllbnQ6dXBza2lsbHNfcmVzdF9hZG1pbl9vYXV0aF9zZWNyZXQ=").when()
				.post("/api/rest_admin/oauth2/token/client_credentials").then().assertThat().statusCode(200).and()
				.extract().response();
		String jsonresp = response.asString();
		JsonPath responsebody = new JsonPath(jsonresp);
		accessToken = "Bearer " + responsebody.get("data.access_token");
		System.out.println("The accessToken is " + accessToken);
	}

	@Test
	public void testCase2() throws IOException {
		getTestData();
		login();
		getAdminUser();
		logOut();
	}

	public void getTestData() throws IOException {
		File testDataFile = new File(
				"C:\\Users\\PadmajaNayak\\eclipse-workspace\\RestAssuredAssignment\\src\\test\\resources\\testData\\RestAssignment.xls");
		FileInputStream fis = new FileInputStream(testDataFile);
		HSSFWorkbook wb = new HSSFWorkbook(fis);
		HSSFSheet sheet = wb.getSheetAt(0);
		int rows = sheet.getLastRowNum();
		for(int i=1;i<=rows;i++) {
			username=sheet.getRow(i).getCell(0).toString();
			password=sheet.getRow(i).getCell(1).toString();
		}
		System.out.println("The username is " + username);
		System.out.println("The password is " + password);
		wb.close();
	}

	public void login() {
		RestAssured.baseURI = baseUrl;

		String requestBody = "{\r\n" + "  \"username\": \""+username+"\",\r\n" + "  \"password\": \""+password+"\"\r\n"
				+ "}";

		Response response = given().header("Authorization", accessToken).body(requestBody)
				.header("content-Type", "application/json").when().post("/api/rest_admin/login").then().assertThat()
				.statusCode(200).body("data.username", equalTo("upskills_admin")).and().extract().response();
		String jsonresp = response.asString();
		JsonPath responsebody = new JsonPath(jsonresp);
		String uname = responsebody.get("data.username");
		System.out.println("The username in login is " + uname);
	}

	public void getAdminUser() {
		RestAssured.baseURI = baseUrl;

		Response response = given().header("Authorization", accessToken).when().get("/api/rest_admin/user").then()
				.assertThat().statusCode(200).body("data.username", equalTo("upskills_admin")).and().extract()
				.response();
		String jsonresp = response.asString();
		JsonPath responsebody = new JsonPath(jsonresp);
		String uname = responsebody.get("data.username");
		System.out.println("The username in getAdminUser is " + uname);
	}

	public void logOut() {
		RestAssured.baseURI = baseUrl;

		given().header("Authorization", accessToken).when().post("/api/rest_admin/logout").then().assertThat()
				.statusCode(200);
		System.out.println("The user has successfully logged out");
	}

}
