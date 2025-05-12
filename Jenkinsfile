pipeline {
    agent any

    environment {
        // Configuración Docker
        DOCKER_IMAGE = 'hotel-ng-backend'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        APP_PORT = 3000
    }

    stages {
        stage('Clonar el repositorio') {
            steps {
                git url: 'https://github.com/lizandronarvaez/hotel-ng-angular-backend.git', branch: 'main'
            }
        }


        stage('Dockerizar la imagen') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                }
            }
        }

        stage('Ejecutar la imagen') {
            steps {
                script {
                    sh "docker stop ${DOCKER_IMAGE} || true"
                    sh "docker rm ${DOCKER_IMAGE} || true"

                    // Ejecutar el contenedor
                    sh """
                        docker run -d \
                        --name ${DOCKER_IMAGE} \
                        -p ${APP_PORT}:${APP_PORT} \
                        ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline completado - Limpieza de recursos'
            sh "docker system prune -f"
        }
        success {
            echo "✅ Aplicación desplegada en http://localhost:${APP_PORT}"
        }
        failure {
            echo '❌ Error en el pipeline. Consulta los logs.'
        }
    }
}
