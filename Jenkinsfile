pipeline {
    agent any

    environment {
        // Configuración Docker
        DOCKER_IMAGE = 'hotel-ng-backend'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        APP_PORT = 3000
        DOCKER_SOCKET = '/home/narvaez/Escritorio/docker-socket/docker.sock'  // ¡Nueva variable!
    }

    stages {
        stage('Clonar repositorio') {
            steps {
                git url: 'https://github.com/lizandronarvaez/hotel-ng-angular-backend.git',
                     branch: 'main'
            }
        }

        stage('Construir imagen') {
            steps {
                script {
                    // Usa el socket personalizado
                    sh "DOCKER_HOST=unix://${DOCKER_SOCKET} docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                }
            }
        }

        stage('Desplegar contenedor') {
            steps {
                script {
                    // Detener y eliminar contenedor previo (si existe)
                    sh """
                        DOCKER_HOST=unix://${DOCKER_SOCKET} docker stop ${DOCKER_IMAGE} || true
                        DOCKER_HOST=unix://${DOCKER_SOCKET} docker rm ${DOCKER_IMAGE} || true
                    """

                    // Ejecutar nuevo contenedor
                    sh """
                        DOCKER_HOST=unix://${DOCKER_SOCKET} docker run -d \
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
            echo 'Limpieza de recursos'
            sh "DOCKER_HOST=unix://${DOCKER_SOCKET} docker system prune -f"
        }
        success {
            echo "✅ Aplicación desplegada en http://localhost:${APP_PORT}"
        }
        failure {
            echo '❌ Error en el pipeline. Consulta los logs.'
        }
    }
}