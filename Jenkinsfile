pipeline {
    agent any

    environment {
        APP_VERSION = sh(script: 'date +%Y%m%d%H%M%S', returnStdout: true).trim()
        NEXUS_URL = 'http://192.168.150.100:8081'
    }


    stages {
        stage('拉取代码') {
            steps {
                echo '代码已由 SCM 自动拉取'
            }
        }
        stage('查看目录结构') {
    		steps {
        		sh '''
            		echo "当前工作目录："
           		 	pwd
            		echo "目录内容："
            		ls -la
            		echo "检查 ai-code-mother 目录是否存在："
            		ls -la ai-code-mother/ || echo "ai-code-mother 目录不存在！"
        			'''
    		}
		}



        stage('编译打包') {
            steps {
                // ✅ 进入正确的后端目录：ai-code-mother
                dir('ai-code-mother') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('上传 Nexus') {
            steps {
                dir('ai-code-mother') {
                    sh 'mvn deploy -DskipTests'
                }
            }
        }

        stage('部署到服务器') {
            steps {
                sh '''
                    # ✅ JAR 包在 ai-code-mother/target/ 下
                    JAR=$(ls ai-code-mother/target/*.jar | grep -v original | head -1)
                    echo "即将部署的 JAR：$JAR"
                    bash /opt/ai-code-mother/deploy.sh "$JAR" "${APP_VERSION}"
                '''
            }
        }

        stage('验证') {
            steps {
                sh '''
                    sleep 5
                    curl -sf http://localhost:8080/health/test
                    echo ""
                    echo "部署完成，健康检查通过！"
                '''
            }
        }
    }

    post {
        success {
            // ✅ 归档路径也要改
            archiveArtifacts artifacts: 'ai-code-mother/target/*.jar', fingerprint: true
            echo '🎉 构建成功！JAR 包已归档。'
        }
        failure {
            echo '❌ 构建失败！请检查控制台输出。'
        }
    }
}