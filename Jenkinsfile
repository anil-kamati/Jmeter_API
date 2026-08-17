pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify JMeter') {
            steps {
                bat '"C:\\apache-jmeter-5.6.3\\apache-jmeter-5.6.3\\bin\\jmeter.bat" -v'
            }
        }

        stage('Run JMeter') {
            steps {
                bat '''
                    if not exist results mkdir results

                    "C:\\apache-jmeter-5.6.3\\apache-jmeter-5.6.3\\bin\\jmeter.bat" ^
                    -n ^
                    -t DummyAPI.jmx ^
                    -l results\\results.jtl
                '''
            }
        }
    }
}