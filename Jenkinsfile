pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'mkevin01/employee-image:latest'
        GIT_REPO_URL = 'https://github.com/kevin-mboni/employee-management.git'
        GIT_BRANCH = 'main'
        DOCKERHUB_CREDENTIALS_ID = '1'
    }

    stages {
        stage('Checkout') {
            steps {
                // Clone the repository
                git branch: "${GIT_BRANCH}", url: "${GIT_REPO_URL}"
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean compile'
                echo 'Maven build completed'
            }
        }

        stage('Test') {
            steps {
                script {
                    def hasTests = sh(script: 'find src/test/java -name "*.java" | wc -l', returnStdout: true).trim()
                    if (hasTests != "0") {
                        echo "Running tests..."
                        bat 'mvn test'
                    } else {
                        echo "No tests found, skipping test stage"
                    }
                }
            }
        }

        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
                echo 'Maven package completed'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    try {
                        // Build the Docker image
                        bat "docker build -t ${DOCKER_IMAGE} ."
                    } catch (Exception e) {
                        error "Docker image build failed: ${e.message}"
                    }
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    try {
                        // Login to Docker Hub and push the image
                        withCredentials([usernamePassword(credentialsId: "${DOCKERHUB_CREDENTIALS_ID}", usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                            bat """
                            docker login -u %DOCKERHUB_USERNAME% -p %DOCKERHUB_PASSWORD%
                            docker tag ${DOCKER_IMAGE} %DOCKERHUB_USERNAME%/${DOCKER_IMAGE}
                            docker push %DOCKERHUB_USERNAME%/${DOCKER_IMAGE}
                            """
                        }
                    } catch (Exception e) {
                        error "Docker push failed: ${e.message}"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    try {
                        // Stop and redeploy Docker container
                        bat """
                        docker stop \$(docker ps -q --filter ancestor=${DOCKER_IMAGE}) || true
                        docker run -d ${DOCKER_IMAGE}
                        """
                    } catch (Exception e) {
                        error "Deployment failed: ${e.message}"
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs() // Clean the workspace after the job
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}