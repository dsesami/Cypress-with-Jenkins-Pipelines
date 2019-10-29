pipeline {
    options {
        timeout(time: 1, unit: 'HOURS')
        timestamps()
        ansiColor('xterm')
    }
    stages {
        stage ('Clone repo') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[ name: "master" ]],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [[
                        $class: 'RelativeTargetDirectory', 
                        relativeTargetDir: 'some-application'
                    ]], 
                ])
            }
        }
        stage('Start application and run tests') {
            steps {
                sh '''#!/bin/bash -xe
                # Build application container setup, and start it
                # 'npm start' isn't very friendly in Jenkins jobs (it takes control and doesn't progress), so this is often easier
                docker build -t app .
                # Hook the ports up on your application so that cypress can speak to it
                docker run --name 'some-application' --network='default' -p '3000:3000' -d -v $(pwd)/some-application:/some-application app
                
                # If you don't have a container, try this instead:
                # setsid npm start >/dev/null 2>&1 < /dev/null &
                # from https://stackoverflow.com/questions/41594233/how-to-run-npm-start-via-jenkins-to-start-application?rq=1
              
                # Move into your test repo and start your run
                cd tests
                npx cypress run
                '''
            }
          
        }  
    }
    post {
        always {
            xunit thresholds: [failed(failureThreshold: '2', unstableThreshold: '1')], 
                  tools: [
                      Custom(customXSL: 'tests/cypress-report-stylesheet.xsl', 
                      deleteOutputFiles: true, 
                      failIfNotNew: true, 
                      pattern: 'tests/results/*.xml', 
                      skipNoTestFiles: false, stopProcessingIfError: true)
                  ]
        }
        success {
            slackSend(
                teamDomain: "my-team",
                channel: "testbot",
                message: "UI pipeline is <${env.RUN_DISPLAY_URL}|passing>"
            )
        }
        unstable {
            slackSend(
                teamDomain: "my-team",
                channel: "testbot",
                message: "UI pipeline is <${env.RUN_DISPLAY_URL}|unstable>"
            )
        }
        failure {
            slackSend(
                teamDomain: "my-team",
                channel: "testbot",
                message: "UI pipeline is <${env.RUN_DISPLAY_URL}|failing>"
            )
        }
    }
}
