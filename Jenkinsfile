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
        
        stage('Build') {

            steps {
                echo 'Realizando build de la aplicación...'
            }
        }
        
        stage('Test') {

            steps {
                echo 'Realizando test de la aplicación...'
            }
        } 
        
        stage('Deploy') {

            steps {
                echo 'Desplegando aplicación...'
            }
        }
    }


}
