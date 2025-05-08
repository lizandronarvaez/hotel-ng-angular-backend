pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'jdk-17.0.12'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test || echo "Algunos tests fallaron - revisar reportes"'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*', fingerprint: true
                }
            }
        }

        stage('Deploy') {
            when {
                branch 'main'
                expression { currentBuild.resultIsBetterOrEqualTo('UNSTABLE') }
            }
            steps {
                echo 'Desplegando aplicación...'
            }
        }
    }


}