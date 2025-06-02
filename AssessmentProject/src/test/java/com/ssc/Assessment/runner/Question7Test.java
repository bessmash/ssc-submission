package com.ssc.Assessment.runner;
import org.junit.runner.RunWith;
import net.serenitybdd.cucumber.CucumberWithSerenity;
//import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.ssc.Assessment.steps",
    tags = "@question7",
    plugin = {
        "pretty",
        "html:target/assessment-report.html",
        "json:target/assessment-report.json"
    },
    monochrome = true
)
public class Question7Test {

}
