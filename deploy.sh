#!/bin/bash

# 서버로 최신 빌드 파일 복사 (예: /home/gitlab-runner/app 디렉토리로)
cp build/libs/team04-backend-0.0.1-SNAPSHOT.jar /home/gitlab-runner/app/demo.jar

# 서버에서 Spring Boot 애플리케이션 실행
# 기존에 실행 중인 프로세스가 있다면 종료
pkill -f 'java -jar'

# 새로 빌드한 jar 파일로 애플리케이션 실행
nohup java -jar /home/gitlab-runner/app/demo.jar &

echo "Deployment completed successfully!"