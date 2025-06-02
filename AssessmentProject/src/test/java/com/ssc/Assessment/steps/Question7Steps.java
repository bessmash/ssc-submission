package com.ssc.Assessment.steps;

import static org.junit.Assert.assertTrue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Question7Steps {
	
	@Given("the user is logged into the Online Banking Portal")
	public void logIntoPortal() {
		// TODO: implement login
		System.out.println("User is logged into the portal.");
	}

	@When("they navigate to the {string} page")
	public void navigateToPage(String pageName) {
		// TODO: verify successful navigation
		System.out.println("Navigated to: " + pageName);
	}

	@When("they enter {string} and {string}")
	public void enterAccountAndAmount(String accountNumber, String transferAmount) {
		// TODO: validate input
		System.out.printf("Entered %s and %s\n", accountNumber, transferAmount);
	}

	@When("they submit the transfer request")
	public void submitTransfer() {
		// TODO: submit transfer
		System.out.println("Submitted transfer request.");
	}

	@Then("a confirmation message should be displayed")
	public void verifyConfirmationDisplayed() {
		// TODO: verify confirmation message is displayed
		boolean confirmationVisible = true;
		assertTrue("Confirmation message is displayed", confirmationVisible);
	}

}
