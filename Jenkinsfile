pipeline {
    agent any

    stages {
        stage('编译打包') {
            steps {
                // 关键：进入 backend 目录执行 Maven 命令
                dir('ai-code-mother') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('展示结果') {
            steps {
                // 列出打好的 JAR 包，方便在日志里看到路径
                sh 'ls -la ai-code-mother/target/*.jar'
            }
        }
    }

    post {
        success {
            echo '🎉 恭喜！Pipeline 构建成功了！'
        }
        failure {
            echo '❌ 构建失败了，快去看日志！'
        }
    }
}