package com.ssc.Assessment.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import net.serenitybdd.junit.runners.SerenityRunner;

//import static io.restassured.RestAssured.*;
import static net.serenitybdd.rest.SerenityRest.*;
import static org.hamcrest.Matchers.*;



@RunWith(SerenityRunner.class)
public class Question9Test {

	@Test
	public void getRequestToAPublicAPI() {
        String url = System.getProperty("test.api.url", 
        		"https://jsonplaceholder.typicode.com/posts/1");

            given()
            .when()
                .get(url)
            .then()
                .statusCode(200)
                .body("title", not(emptyOrNullString()));
    }
}
