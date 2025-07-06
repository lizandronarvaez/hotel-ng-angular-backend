pipeline {
    agent any

    environment {
        APP_PORT = '3000'
    }

    stages {
        stage('Clonar repositorio') {
            steps {
                git url: 'https://github.com/lizandronarvaez/hotel-ng-angular-backend.git',
                    branch: "${params.BRANCH_DEVELOP}"
            }
        }

        stage('Construir imagen con docker') {
            steps {
                sh 'docker build -t hotel-ng-backend .'
            }
        }

         stage('Desplegar contenedor con Docker') {
                    steps {
                        sh '''
                            echo "Eliminando contenedor anterior..."
                            docker rm -f hotel-ng-backend || true

                            echo "Desplegando nuevo contenedor"
                            docker run -d --name hotel-ng-backend -p ${APP_PORT}:8081 --env-file ./variables.env hotel-ng-backend
                        '''
                    }
                }
            }

    post {
        success {
            echo "La aplicación estará disponible enseguida, el contenedor está levantandose..."
            sleep time: 30, unit: 'SECONDS'
            echo "✅ Se desplegó la aplicación en http://localhost:${APP_PORT}/api/v1/swagger-ui/index.html#/"
        }
        failure {
            echo '❌ Hubo un error en el pipeline. Consulta los logs.'
        }
    }

}
