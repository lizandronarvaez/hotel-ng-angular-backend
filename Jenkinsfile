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
