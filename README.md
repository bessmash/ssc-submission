## Technical Assessment Submission  

### Overview  
This repository contains my technical responses and code implementations for the SS&C QA Assessment. Questions included API testing, Selenium automation (Java & JS), Gherkin scenarios, WinAppDriver, and a GitHub Actions pipeline integrated with Serenity for reporting.
The assessment was completed as a mix of written explanations, code projects, and supporting screenshots. Each question has its own section in the PDF submission.
---

### Repository Structure  
```bash
.
├── AssessmentProject/         # Maven project using Java, RestAssured, Serenity
│   └── src/
│       └── test/
│           ├── java/com/ssc/Assessment/tests/
│           ├── java/com/ssc/Assessment/steps/
│           └── resources/features/
├── Question6JS/               # Node.js project with JavaScript Selenium test
├── Question8Project/          # Windows-only project with WinAppDriver test
├── Postman/                   # Postman Collection for API testing
├── Screenshots/              # Screenshots referenced in the PDF
└── .github/workflows/        # GitHub Actions YAML file
```
---

### CI/CD with GitHub Actions  
All Serenity-compatible Java tests in `AssessmentProject/` are executed automatically via GitHub Actions. The pipeline runs on every push to the `main` branch. If tests fail, the Serenity report is still generated and uploaded as an artifact.

**One of the Successful Runs (with full test and report generation):**  
[Download Serenity Report (available for 90 days)](https://github.com/bessmash/ssc-submission/actions/runs/15404070496/artifacts/3245344394)

---

### Notes  
- All code was tested and working at the time of submission.
- Serenity tests expect ParaBank to be available online.
- One test is expected to fail intentionally (sessionless access – security check, Question 3) 
- Serenity reports are generated regardless of test failures using continue-on-error: true 
- WinAppDriver test (Question 8) runs only on Windows 10+ with the correct driver setup. 

---

### How to Run the Projects  

#### Java + Serenity (AssessmentProject)  
Run with default values:  
```bash
cd AssessmentProject  
mvn clean verify
mvn serenity:aggregate
```

Run with custom parameters:  
```bash
mvn clean verify -Dtest.api.url=https://jsonplaceholder.typicode.com/posts/2 -Dtest.username=john -Dtest.password=demo -Dtest.headless=true
```

---

#### JavaScript Selenium (Question6JS)  
Install dependencies:  
```bash
cd Question6JS  
npm install
```

Run with default values:  
```bash
node question6test.js
```

Run with custom parameters:  
```bash
TEST_USERNAME=john TEST_PASSWORD=demo TEST_HEADLESS=false node question6test.js
```

---

#### WinAppDriver Test (Windows Only, Question 8)  
To run the calculator test:  
Start WinAppDriver on your Windows 10+ machine.  
```bash
cd Question8Project  
mvn clean test
```
