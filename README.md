# 🚀 Threadly
**Threadly는 Jira의 칸반보드 기능을 기반으로 하면서, 실시간 협업을 강화하기 위해 채팅 기능을 추가한 프로젝트 관리 도구입니다.
이슈 관리, 프로젝트 진행 상황 추적과 더불어 WebSocket 기반의 실시간 채팅을 지원하여 팀원 간 원활한 커뮤니케이션을 돕습니다.**

---

## 📌 기술 스택

### 🔹 백엔드
| 기술                                                                                                | 버전 |
|---------------------------------------------------------------------------------------------------|---|
| ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)|OpenJDK 17|
| ![SpringBoot](https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) | 3.3.1|
| ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white) |8.3.0|
| ![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white) |3.3.1|
| ![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white) |8.0.4|
| ![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black) |9.1.1|

![WebSocket](https://img.shields.io/badge/WebSocket-000000?style=for-the-badge&logo=websocket&logoColor=white) 
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white) 

### 🔹 프론트엔드
| 기술 | 버전 |
|---|---|
|![React](https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black)  |18|
|![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white)  |1.18.0|
|![Axios](https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=axios&logoColor=white)|Latest|

### 🔹 배포
![GitLab Runner](https://img.shields.io/badge/GitLab%20Runner-FC6D26?style=for-the-badge&logo=gitlab&logoColor=white)

---

## 🏗️ 시스템 아키텍처
![architecture.png](img%2Farchitecture.png)

---

## 🗂 ERD
![ERD](img/ERD.png)

---

## API 문서

👉 [Swagger 링크](http://34.22.102.28:8080/swagger-ui/index.html#/)


---

## 🎨 와이어프레임
👉 [Figma 링크](https://www.figma.com/design/jPUtIC4aj6eWIFThgUOOIN/%EC%97%98%EB%A6%AC%EC%8A%A4-%ED%8F%AC%ED%8A%B8%ED%8F%B4%EB%A6%AC%EC%98%A4%ED%8A%B8%EB%9E%99-1%EC%B0%A8-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8?node-id=0-1&p=f&t=KZs31uNgFLwv4KjM-0)

---

## 📅 일정 관리
![Kanban](img/kanban.png)

---

## 📜 코드 컨벤션

### 🔹 백엔드
![BackEnd Convention](img/backEndConvention.png)

### 🔹 프론트엔드
![FrontEnd Convention](img/frontEndConvention.png)

---

## 🚀 기능

### ✅ 메인 기능
- **프로젝트 관리**: 프로젝트 생성, 수정, 삭제 및 권한 관리
- **이슈 관리**: 이슈 생성, 배정, 상태 변경 및 삭제
- **실시간 채팅**: **WebSocket**를 활용한 프로젝트 내 팀원 간 실시간 채팅
- **사용자 관리**: 회원가입, 로그인, 권한 설정 및 초대

### 📌 추가 기능
- **이메일 초대 시스템**
- **프로젝트 검색 및 필터링**
- **이슈 상태 트래킹**
- **이슈 검색 및 필터링**
- **캐싱 및 성능 최적화 (Redis 활용)**
- **보안 강화 (JWT 기반 인증 및 인가, 리프레시 토큰 지원)**
- **GitLab Runner를 활용한 CI/CD 자동 배포**

---

## 🌍 환경 변수 설정 (application.yaml)

```bash
server:
  port: 8080

spring:
  cache:
    type: redis
  data:
    mongodb:
      uri: mongodb://localhost:27017/threadly
      database: threadly
      auto-index-creation: true
    redis:
      host: localhost
      port: 6379

  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB
      max-request-size: 10MB
  application:
    name: team04-backend
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
    defer-datasource-initialization: true

  datasource:
    username: "USER_USERNAME"
    password: "USER_PASSWORD"
    url: jdbc:mysql://localhost:49153/threadly?serverTimezone=UTC&characterEncoding=UTF-8

  mail:
    host: "USER_HOST"
    port: "USER_PORT"
    username: "USER_USERNAME"
    password: "USER_PASSWORD"
    properties:
      smtp:
        auth: true
        timeout: 5000
        starttls:
          enable: true


springdoc:
  api-docs:
    path: /api-docs  # API 문서 기본 경로
  swagger-ui:
    path: /swagger-ui.html  # Swagger UI 경로

token:
  secret: "USER_SECRET"
  access-token-expiration: 600000  # 10분 (밀리초)
  refresh-token-expiration: 1  # 1시간 (시간)

firebase:
  storage:
    bucket-name: "USER_BUCKET_NAME"
    json-path: "USER_JSON_PATH"


oauth2:
  google:
    oauth-uri: "oauth-uri"
    client-id: "client-id"
    client-secret: "client-secret"
    access-scope: "access-scope"
    redirect-uri: "redirect-uri"
    grant-type: "grant-type"
  kakao:
    oauth-uri: "oauth-uri"
    client-id: "client-id"
    redirect-uri: "redirect-uri"
    grant-type: "grant-type"
  naver:
    oauth-uri: "oauth-uri"
    client-id: "client-id"
    client-secret: "client-secret"
    redirect-uri: "redirect-uri"
    grant-type: "grant-type"

app:
  page-size: 10

```


