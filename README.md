# Study-Management

스터디 관리 시스템 ✏️

## 01. 프로젝트 소개

### (1) 목적

> Google Meet를 활용한 스터디에서 그룹 멤버들의 효율성과 참여율을 향상시키는 것을 목표로 합니다.

> 분산되고 수동적인 방법을 사용하여 스터디 활동을 관리하는 한계를 극복하는 것이 목표로, 부재 일정 캘린더, Google Meet 생성 알림, 전용 토론 게시판, 출석/기상 통계 등의 솔루션을 제공합니다.

### (2) 팀원 및 역할

| 작업자 | 담당 기능                 | 담당 역할     | 추가 역할                                                                                                                                                     | git 주소                          |
| ------ | ------------------------- | ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------- |
| 최예희 | 부재일정 관리, 게시판     | 설계, API, UI | 프로젝트 기획, Docker 환경 구성, JPA 엔티티 설계 및 구현, 로깅 시스템 개발                                                                                    | https://github.com/ZinnaChoi      |
| 이다연 | 통계, 알림                | 설계, API, UI | Readme 작성, 프로젝트 init, 배포                                                                                                                              | https://github.com/dayeon-dayeon  |
| 이찬혁 | 계정 및 권한, 스터디 관리 | 설계, API, UI | [프로젝트 일정 관리](https://docs.google.com/spreadsheets/d/1VXsirwc0Vpu-1I1Nq3qSeFSSbDZcV-1SRO0ZoidfiSI/edit#gid=148490993), UI Layout, Spring Security 설정 | https://github.com/MeMyself-And-I |

### (3) ERD

![image](https://github.com/ZinnaChoi/Study-Management/assets/73517372/dc938b1b-1a33-4658-be7e-57a08abfd4ad)

### (4) 화면 정의서

![image](https://github.com/ZinnaChoi/Study-Management/assets/73517372/7b3d01d8-f9ae-46cd-979b-3f0a3ccf0ae9)
![image](https://github.com/ZinnaChoi/Study-Management/assets/73517372/72e99447-b4ec-485c-82c3-6c15586a2228)

참조 : https://docs.google.com/presentation/d/1fEcGCQcjfnf8uTkW8xaAKWsY-t_qPGdPkzJy83yHVJA/edit

### (5) 배포

#### 서버 정보

```
AWS EC2 인스턴스
- 유형 : t2.micro
- 메모리 : 30G
- 네트워크 성능 : 낮음에서 중간
- 운영체제 : Amazon Linux
```

#### 아키텍쳐

<div align="center">

![image](https://github.com/ZinnaChoi/Study-Management/assets/73517372/91ed8074-57de-455d-95bc-a277a1027536)

</div>

#### URL

```
http://54.180.198.112:8090
```

#### 테스트 계정

```
ID : viewer
PASSWORD : password123!
```

---

## 02. 주요 기능

### 1. 부재 일정 캘린더

- AS-IS : 개별 카카오톡 메세지를 통해 스터디 참/불참 여부를 공유, 특정 일자의 참여 멤버 파악이 어려움.
- TO-BE : 캘린더에 부재 일정을 등록함으로써 참여 멤버를 한 눈에 파악할 수 있으며, 개인별 알림 설정을 통해 멤버들의 상태에 대해 알림을 받을 수 있음.

### 2. Google Meet 개설 알림

- AS-IS : 먼저 접속하는 스터디원이 Google Meet를 개설하여 대부분 특정 스터디원이 Google Meet을 개설함.
- TO-BE : 해당 일자에 참여할 멤버에게 돌아가면서 Google Meet 개설 알림을 이메일로 전송. 이를 통해 멤버 모두가 골고루 Google Meet 를 개설할 수 있도록 유도함.

### 3. 게시판

- AS-IS : 스터디 관련 정보를 카카오톡 채팅방에 공유하여, 필요할 때 정보 찾기 어려움.
- TO-BE : 정보 공유를 위한 전용 게시판 개설해 게시글과 댓글을 통해 정보 및 의견 공유가 가능함.

### 4. 통계

- AS-IS : 기상 시 카카오톡 메시지에 기상 확인 카톡 전송, 부재 시 사전 공유함.
- TO-BE : 부재 일정을 통한 참여왕, 개인별 목표 기상시간 설정 및 달성일 측정으로 기상왕을 선정하여 스터디원의 참여율 증진 및 의욕을 고취함.

---

## 03. 기술 스택

### Environment

<img src="https://img.shields.io/badge/Visual Studio Code-007ACC?style=for-the-badge&logo=Visual Studio Code&logoColor=white"> <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white"> <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white">

### Development

<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"> <img src="https://img.shields.io/badge/Java-437291?style=for-the-badge&logo=OpenJDK&logoColor=white"> <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=React&logoColor=white"> <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white"> <img src="https://img.shields.io/badge/MySQL-39477F?style=for-the-badge&logo=mysql&logoColor=white"> <img alt="Redis" src ="https://img.shields.io/badge/Redis-DC382D.svg?&style=for-the-badge&logo=Redis&logoColor=white"/>

### Communication

<img src="https://img.shields.io/badge/Google Drive-4285F4?style=for-the-badge&logo=Google Drive&logoColor=white"> <img src="https://img.shields.io/badge/Google Meet-00897B?style=for-the-badge&logo=Google Meet&logoColor=white">

---

## 04. 시작 가이드

### (1) 요구사항

```
java 17
react
mysql
npm 8.19.2
```

### (2) 설치 및 실행

#### Installation

```
git clone https://github.com/ZinnaChoi/Study-Management
```

#### Backend

```bash
./gradlew bootRun
```

#### Frontend

```cmd
cd src/main/frontend
npm install
npm start
```

#### Docker Setup

```cmd
cd docker
docker-compose up -d
```

##### Junit Test

- `build/reports/tests/test/index.html` 에서 세부 정보 확인

```bash
./gradlew test --tests "mogakco.StudyManagement.StudyManagementApplicationTests"
```

##### Swagger 접속 정보

```
http://localhost:8090/swagger-ui/index.html
```
