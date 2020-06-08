pipeline {
    agent any
    stages {
        stage('build') {
            steps {
                configFileProvider([configFile(fileId: 'dev-azure-com-kth-integration-integration', targetLocation: '.m2/settings.xml')]) {
                    jdk = tool name: 'jdk11'
                    env.JAVA_HOME = "${jdk}"
                    echo "jdk installation path is: ${jdk}"
                    sh './mvnw -Duser.home=. --no-transfer-progress clean deploy'
                }
            }
        }
    }
}
