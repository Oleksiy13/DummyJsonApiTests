pipeline {
    agent { label 'aws-agent' } // Указываем конкретный агент
    stages {
        stage('Prepare') {
            steps {
                sh 'mvn clean install' // Установка зависимостей
            }
        }
        stage('Build') {
            steps {
                sh 'echo Hello World'
                sh '''
                    echo Multiline shell steps works too
                    ls -la
                '''
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test' // Запуск тестов
            }
        }
    }
    post {
        always {
            junit 'target/surefire-reports/*.xml' // Сбор результатов тестов
        }
    }
}