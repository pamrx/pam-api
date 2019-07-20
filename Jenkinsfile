pipeline {
  agent any

  stages {
    stage('Build') {
      steps {
        echo 'Building app image'
        sh 'docker build -t $DOCKER_IMAGE:latest .'
      }
    }
    stage('Tag') {
      when { branch 'master' }
      steps {
        echo 'Tagging and Pushing Latest Image'
        script {
          docker.withRegistry('', 'dockerhub-credentials') {
            sh "docker push $DOCKER_IMAGE:latest"
          }
        }
      }
    }
    stage('Deploy') {
      when { branch 'master' }
      steps {
        echo 'Deploying the latest code...'
        build job: 'pam-deploy/master', wait: false
      }
    }
  }
  post {
    failure {
      slackSend color: "danger", message: "ಠ_ಠ ${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - ${currentBuild.currentResult}: \n Check console output at ${env.BUILD_URL} to view the results. @here"
    }

    success {
      slackSend color: "good", message: "ʘ‿ʘ ${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - ${currentBuild.currentResult}: \n Check console output at ${env.BUILD_URL} to view the results."
    }
  }
  environment {
    DOCKER_IMAGE = "pamrx/pam-api"
  }
}
