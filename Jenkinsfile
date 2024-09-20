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
                echo "Checked out branch: ${GIT_BRANCH} from repo: ${GIT_REPO_URL}"
            }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn clean compile'
                    } else {
                        bat 'mvn clean compile'
                    }
                }
                echo 'Maven build completed'
            }
        }

        stage('Test') {
            steps {
                script {
                    def hasTests = isUnix() ? sh(script: 'find src/test/java -name "*.java" | wc -l', returnStdout: true).trim() : bat(script: 'dir /s /b src\\test\\java\\*.java | find /c ":"', returnStdout: true).trim()

                    if (hasTests != "0") {
                        echo "Running tests..."
                        if (isUnix()) {
                            sh 'mvn test'
                        } else {
                            bat 'mvn test'
                        }
                    } else {
                        echo "No tests found, skipping test stage"
                    }
                }
            }
        }

        stage('Package') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn package -DskipTests'
                    } else {
                        bat 'mvn package -DskipTests'
                    }
                }
                echo 'Maven package completed'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    try {
                        // Build the Docker image
                        if (isUnix()) {
                            sh "docker build -t ${DOCKER_IMAGE} ."
                        } else {
                            bat "docker build -t ${DOCKER_IMAGE} ."
                        }
                        echo "Docker image built: ${DOCKER_IMAGE}"
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
                            if (isUnix()) {
                                sh """
                                docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}
                                docker tag ${DOCKER_IMAGE} ${DOCKERHUB_USERNAME}/${DOCKER_IMAGE}
                                docker push ${DOCKERHUB_USERNAME}/${DOCKER_IMAGE}
                                """
                            } else {
                                bat """
                                docker login -u %DOCKERHUB_USERNAME% -p %DOCKERHUB_PASSWORD%
                                docker tag ${DOCKER_IMAGE} %DOCKERHUB_USERNAME%/${DOCKER_IMAGE}
                                docker push %DOCKERHUB_USERNAME%/${DOCKER_IMAGE}
                                """
                            }
                            echo "Docker image pushed to Docker Hub"
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
                        if (isUnix()) {
                            sh """
                            docker stop \$(docker ps -q --filter ancestor=${DOCKER_IMAGE}) || true
                            docker run -d ${DOCKER_IMAGE}
                            """
                        } else {
                            bat """
                            docker stop $(docker ps -q --filter ancestor=${DOCKER_IMAGE}) || true
                            docker run -d ${DOCKER_IMAGE}
                            """
                        }
                        echo "Application deployed using Docker image: ${DOCKER_IMAGE}"
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
