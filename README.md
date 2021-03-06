# Cypress + Jenkins

## Summary
This pipeline demonstrates a way of using a declarative Jenkins pipeline to both start a bare-bones npm-based application, and then run [Cypress](https://github.com/cypress-io/cypress) tests against the application. It assumes that you have the required dependencies wrapped up in containers -- I recommend starting with one of the [Cypress docker images](https://github.com/cypress-io/cypress-docker-images) to do so. 

If your application is running elsewhere, then you may skip that section of the pipeline and simply start the tests.

This isn't intended to be a fully-runnable application, but it should provide useful insight as to how to get your own configurations up and running.

NOTE: The `xunit` command in Jenkins does not absorb the `xunit` reporter provided by Cypress properly, for some reason. This is a known issue. You may get around it by using the following reporter configuration in cypress.json, and then using the stylesheet provided in this repository in your pipeline:
```
 "reporter": "junit",
 "reporterOptions": {
   "mochaFile": "results/cypress-report-[hash].xml",
   "toConsole": true
 }
```
