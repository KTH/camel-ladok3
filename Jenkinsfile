pipeline {
    agent any
    tools {
        jdk 'jdk8'
    }
    stages {
        stage('build') {
            steps {
                configFileProvider([configFile(fileId: 'dev-azure-com-kth-integration-integration', targetLocation: '.m2/settings.xml')]) {
                    sh './mvnw -Duser.home=. --no-transfer-progress clean deploy'
                }
            }
        }
    }
}
