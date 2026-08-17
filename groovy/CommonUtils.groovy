pipeline {
    agent any

    parameters {
        string(name: 'USERS', defaultValue: '10', description: 'Number of JMeter users')
        string(name: 'RAMPUP', defaultValue: '10', description: 'Ramp-up time in seconds')
        string(name: 'DURATION', defaultValue: '60', description: 'Test duration in seconds')
        string(name: 'SCRIPT', defaultValue: 'DummyAPI.jmx', description: 'JMeter script')
    }

    stages {

        stage('Initialize') {
            steps {
                checkout scm
            }
        }

        stage('Execution') {
            steps {

                script {
                    env.RESULT_FILE = "Result/${params.SCRIPT.replace('.jmx', '')}_Build_${env.BUILD_NUMBER}.jtl"
                    env.REPORT_DIR = "Result/html_${env.BUILD_NUMBER}"
                }

                echo "Result file : ${env.RESULT_FILE}"
                echo "Report dir  : ${env.REPORT_DIR}"

                bat '''
                    if not exist Result mkdir Result

                    "C:\\apache-jmeter-5.6.3\\apache-jmeter-5.6.3\\bin\\jmeter.bat" ^
                    -n ^
                    -t "%WORKSPACE%\\%SCRIPT%" ^
                    -Jusers=%USERS% ^
                    -Jrampup=%RAMPUP% ^
                    -Jduration=%DURATION% ^
                    -l "%WORKSPACE%\\%RESULT_FILE%" ^
                    -e ^
                    -o "%WORKSPACE%\\%REPORT_DIR%"
                '''
            }
        }

        stage('Report') {
            steps {
                script {
                    perfReport(
                        sourceDataFiles: env.RESULT_FILE,
                        errorFailedThreshold: 1,
                        errorUnstableThreshold: 1,
                        errorUnstableResponseTimeThreshold: '1000',
                        modePerformancePerTestCase: true,
                        modeThroughput: true,
                        showTrendGraphs: true
                    )

                    publishHTML([
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: env.REPORT_DIR,
                        reportFiles: 'index.html',
                        reportName: 'JMeter HTML Report'
                    ])
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'Result/**/*', allowEmptyArchive: true
        }
    }
}