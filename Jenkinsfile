pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                bat 'echo Hello World'
                bat '''
                    echo Multiline shell steps works too
                    dir
                '''
            }
        }
        stage('Test') {
                    steps {
                        bat 'mvn test'
    }
}
}
}