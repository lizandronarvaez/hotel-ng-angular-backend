pipeline {
    agent any

  environment {
        APP_PORT = 3000
    }

    stages {
        stage('Clonar repositorio') {
            steps {
                git url: 'https://github.com/lizandronarvaez/hotel-ng-angular-backend.git',
                     branch: 'main'
            }
        }

        stage('Construir imagen con docker') {
            steps {
                agent { docker 'amazoncorretto:21-alpine-jdk'}

            }
        }

        stage('Desplegar contenedor') {
            steps {
                echo 'Desplegando contenedor'
            }
        }
    }

    post {
        success {
            echo "✅ Aplicación desplegada en http://localhost:${APP_PORT}"
        }
        failure {
            echo '❌ Error en el pipeline. Consulta los logs.'
        }
    }
}