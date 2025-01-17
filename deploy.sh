#!/bin/bash

# 서버에서 Git 레포지토리로 이동 (적절한 경로로 수정)
cd /home/gitlab-runner/builds/mhH7LCxy/0/pttrack/class_01/web_project_i/team04/team04-backend || exit

# Gradle 빌드 실행 (최신 소스 코드로 빌드)
./gradlew clean build || exit

# 기존 빌드 파일 삭제
rm -f /home/gitlab-runner/app/demo.jar

# 새로 빌드한 JAR 파일을 복사
cp /home/gitlab-runner/app/build/libs/team04-backend-0.0.1-SNAPSHOT.jar /home/gitlab-runner/app/demo.jar

# 기존에 실행 중인 프로세스가 있다면 종료
pkill -f 'java -jar'

# 새로 빌드한 jar 파일로 애플리케이션 실행
nohup java -jar /home/gitlab-runner/app/demo.jar &

echo "Deployment completed successfully!"
