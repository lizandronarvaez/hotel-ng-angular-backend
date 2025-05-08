pipeline {
    agent any

    tools {
        // Asegúrate que estos nombres coincidan con tus herramientas configuradas en Jenkins
        jdk 'jdk17'  // Nombre de tu JDK configurado en Jenkins
        maven 'maven-3.8.6'  // Nombre de tu Maven configurado en Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm  // Esto clona el repositorio automáticamente
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'  // Reportes de pruebas
                }
            }
        }

        stage('Deploy') {
            when {
                branch 'main'  // Solo se ejecuta en la rama main
            }
            steps {
                // Aquí irían tus pasos de despliegue
                echo 'Desplegando aplicación...'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            cleanWs()  // Limpiar workspace
        }
    }
}