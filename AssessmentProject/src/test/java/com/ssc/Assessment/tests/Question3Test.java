package com.ssc.Assessment.tests;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

//import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;


@RunWith(SerenityRunner.class)
public class Question3Test {
	
	// URL of the bank application under the test
	private static final String BASE_URL = "https://parabank.parasoft.com/parabank";
	
	// variables for user credentials
	private static String username;
	private static String password;
	
	// variable for session Id
	private static String sessionId;

	@BeforeClass
	public static void setup() {
		
		// reading user name and password from the command line
		username = System.getProperty("test.username", "john");
		password = System.getProperty("test.password", "demo");
	    
		if (username == null || password == null)
			throw new IllegalArgumentException("Username and password must be provided "
					+ "as system properties.");
		}

	
	// login and extract the session Id and redirectUrl
	private static void loginAndExtractSessionId() {
		
		// if user is not logged in yet
		if (sessionId == null) {
	    	
			// login
			Response response = SerenityRest
					.given()
						.baseUri(BASE_URL)
						.contentType("application/x-www-form-urlencoded")
						.formParam("username", username)
						.formParam("password", password)
					.post("/login.htm");

			// check redirect status code
			response.then().statusCode(302);
			
			// extract sessionId from the cookie
	        sessionId = response.getCookie("JSESSIONID");
	        assertNotNull(sessionId, "Session ID should not be null or empty");
			
		}
				
	}
	
	// get userId
	private int getUserId() {
			    
		// sending get request
		Response response = SerenityRest
				.given()
					.baseUri(BASE_URL)
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
					.cookie("JSESSIONID", sessionId)
				.when()
					.get("/services/bank/login/{username}/{password}", username, password)
				.then()
					.statusCode(200)
					.contentType(ContentType.JSON)
					.body("id", notNullValue())
					.body("firstName", 
							equalTo(username.substring(0, 1).toUpperCase()+username.substring(1)))
					.extract()
					.response();

		// extracting userId
		return response.jsonPath().getInt("id");
	}
	
	@Test
	public void loginFunctionalTest() {
	    
		// this method will trigger login, verify redirect, and store the sessionId
		loginAndExtractSessionId();
		
		// follow-up response to check that user gets inside their account
		Response followUp = SerenityRest
				.given()
					.baseUri(BASE_URL)
					.cookie("JSESSIONID", sessionId)
				.get("/overview.htm");

		// verifications
		followUp.then()
				.statusCode(200)
				.body(containsString("Accounts Overview"));

	}
	
	@Test
    public void getUserDataFunctionalTest() {
		
		// login to the application
		loginAndExtractSessionId();
		
		// this method will get user info and verify status code 200, and user name and id in the response
		int userId = getUserId();
		assertTrue("User ID should be a positive number", userId > 0);
    }
	
	@Test
	public void testGetUserAccountsDataFunctionalTest() {
	    
		// login to the application
		loginAndExtractSessionId();

		// get user id
		int userId = getUserId();

		// send get request and verify status code and visibility of checking account
		SerenityRest.given()
					.baseUri(BASE_URL)
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
					.cookie("JSESSIONID", sessionId)
				.when()
					.get("/services/bank/customers/{userId}/accounts", userId)
				.then()
					.statusCode(200)
					.body(containsString("CHECKING"));
	}
	
	@Test
	public void attemptToLoginOverHttpSecurityTest() {
		
		// getting application http-based url
		String httpUrl = BASE_URL.replaceFirst("https://", "http://")+"/login.htm";
	    
		// attempt to login
		Response response = SerenityRest
				.given()
					.baseUri(BASE_URL)
					.contentType("application/x-www-form-urlencoded")
					.formParam("username", username)
					.formParam("password", password)
				.post(httpUrl);

	    // verifying that statusCode is not 200 and is not 302(redirect)
		int statusCode = response.getStatusCode();
		assertTrue(statusCode>=300);
		assertNotEquals(302, statusCode);

	}
	
	@Test
	public void sqlInjectionAttemptSecurityTest() {

		// login to the application
		loginAndExtractSessionId();

		// get user id
		int userId = getUserId();
		
	    // constructing maliciousId
		String maliciousId = userId + "' OR '1'='1";

		// sending get request and verifying status code 403
		SerenityRest.given()
				.baseUri(BASE_URL)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.cookie("JSESSIONID", sessionId)
			.when()
				.get("/services/bank/customers/{maliciousId}/accounts", maliciousId)
			.then()
				.statusCode(403);
	}
	
	
	// this test will fail as there is a real security issue
	@Test
	public void AttemptToAccessUserAccountsWithoutSessionId_ExpectedToFail() {
	    
		// userId for testing
		int userId = 12212;

		SerenityRest
	        .given()
				.baseUri(BASE_URL)
	        	.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.when()
				.get("/services/bank/customers/{userId}/accounts", userId)
			.then()
				.statusCode(403); // expected 403: Forbidden 
	}

}
