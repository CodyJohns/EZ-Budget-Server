pipeline {
    agent any
    //Pipeline for EZ-Budget Server

    environment {
        dockerhub=credentials('dockerhub')
        BUILD_NAME = 'ezbudget_server'
    }

    stages {
        stage('Build') {
            steps {
                sh 'JAVA_HOME=/usr/lib/jvm/java-17-openjdk-arm64 ./mvnw package'
            }
        }
        stage('Docker Build') {
            steps {
                sh 'docker build -t docker.western-solutions.dev/${BUILD_NAME} .'
            }
        }
        stage('Deploy') {
            steps {
                sh 'echo $dockerhub_PSW | docker login https://docker.western-solutions.dev -u $dockerhub_USR --password-stdin'
                sh 'docker push docker.western-solutions.dev/${BUILD_NAME}:latest'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}