pipeline {
    agent any

    environment {
        APP_PORT = '3000'
    }

    stages {
        stage('Clonar repositorio') {
            steps {
                git url: 'https://github.com/lizandronarvaez/hotel-ng-angular-backend.git', branch: 'main'
            }
        }

        stage('Construir imagen con Docker') {
            steps {
                script {
                    echo "🛠️ Construyendo imagen Docker"

                    sh 'docker build -t hotel-ng-backend .'
                }
            }
        }

        stage('Desplegar contenedor') {
            steps {
                script {
                    echo "🚀 Desplegando contenedor Docker"

                    sh 'docker rm -f hotel-ng-backend || true'
                    sh "docker run -d --name hotel-ng-backend -p ${APP_PORT}:${APP_PORT} hotel-ng-backend"
                }
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
