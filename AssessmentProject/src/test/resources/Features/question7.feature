@question7
Feature: Money Transfer
  As a registered user of the Online Banking Portal  
  I want to be able to transfer money to another account  
  So that I can complete my personal transactions successfully

  Scenario: Successful money transfer to another account
    Given the user is logged into the Online Banking Portal
    When they navigate to the "Money Transfer" page
    And they enter "Account Number" and "Transfer Amount"
    And they submit the transfer request
    Then a confirmation message should be displayed
