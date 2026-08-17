pipeline {
    agent any

    stages {

        stage('Initialize') {
            steps {
                checkout scm

                echo "Branch        : ${params.Branch}"
                echo "Users         : ${params.users}"
                echo "Duration      : ${params.duration}"
                echo "Property File : ${params.propertyFile}"
            }
        }

        stage('Execution') {
            steps {
                script {
                    env.RESULT_FILE = "Result/DummyAPI_Build_${env.BUILD_NUMBER}.jtl"
                    env.REPORT_DIR = "Result/html_${env.BUILD_NUMBER}"
                }

                bat """
                    if not exist "%WORKSPACE%\\Result" mkdir "%WORKSPACE%\\Result"

                    set "JAVA_HOME=C:\\Program Files\\Java\\jdk-21"
                    set "PATH=%%JAVA_HOME%%\\bin;%%PATH%%"

                    "C:\\apache-jmeter-5.6.3\\apache-jmeter-5.6.3\\bin\\jmeter.bat" ^
                    -n ^
                    -q "%WORKSPACE%\\properties\\%propertyFile%" ^
                    -Jusers=%users% ^
                    -Jduration=%duration% ^
                    -t "%WORKSPACE%\\scripts\\DummyAPI.jmx" ^
                    -l "%WORKSPACE%\\${env.RESULT_FILE}" ^
                    -e ^
                    -o "%WORKSPACE%\\${env.REPORT_DIR}"
                """
            }
        }

        stage('Results') {
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
            archiveArtifacts(
                artifacts: 'Result/**/*',
                allowEmptyArchive: true
            )
        }
    }
}