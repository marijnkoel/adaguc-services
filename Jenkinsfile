pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            args '-v /root/.m2:/root/.m2'
        }
    }
    options {
        skipStagesAfterUnstable()
    }
    stages {

        stage("build & SonarQube analysis") {
            agent any
            steps {
              withSonarQubeEnv('sonarQube') {
                sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.0.1398:sonar'
              }
            }
          }
    }
}
