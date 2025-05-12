pipeline {

    agent any

    //Declarar herramientas que utilizaremos
    tools {
        maven 'Maven'
        jdk 'jdk-17.0.12'
    }

    //
    stages {
//         paso
        stage('Tareas de limpieza') {
            steps {
//                 Clonar el repositorio
                git 'https://github.com/lizandronarvaez/hotel-ng-angular-backend.git'
//                 ejecutar la tarea de limpieza
                sh 'mvn clean'
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
