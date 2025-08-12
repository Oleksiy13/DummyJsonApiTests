pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                cmd 'echo "Hello World"'
                cmd '''
                    echo "Multiline shell steps works too"
                    ls -lah
                '''
            }
        }
    }
}