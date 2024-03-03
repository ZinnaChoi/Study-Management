# Study-Management

스터디 관리 시스템 ✏️

## 01. 프로젝트 소개

### (1) 목적

- Google Meet를 활용한 스터디에서 그룹 멤버들의 효율성과 참여율을 향상시키는 것을 목표로 합니다.
- 분산되고 수동적인 방법을 사용하여 스터디 활동을 관리하는 한게를 극복하는 것이 목표로, 부재 일정 캘린더, Google Meet 생성 알림, 전용 토론 게시판, 출석/기상 통계 등의 솔루션을 제공합니다.

### (2) ERD

![image](https://github.com/ZinnaChoi/Study-Management/assets/73517372/dc938b1b-1a33-4658-be7e-57a08abfd4ad)

### (3) 화면 정의서

![image](https://github.com/ZinnaChoi/Study-Management/assets/73517372/7b3d01d8-f9ae-46cd-979b-3f0a3ccf0ae9)
![image](https://github.com/ZinnaChoi/Study-Management/assets/73517372/72e99447-b4ec-485c-82c3-6c15586a2228)

참조 : https://docs.google.com/presentation/d/1fEcGCQcjfnf8uTkW8xaAKWsY-t_qPGdPkzJy83yHVJA/edit

### (4) 아키텍처

```

```

### (5) 배포 URL

```
개발 버전 :
릴리즈 버전 :
```

### (6) 팀원 및 역할

| 작업자 | 담당 기능                 | 담당 역할     | 추가 역할                                                                                                                               | git 주소                          |
| ------ | ------------------------- | ------------- | --------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------- |
| 최예희 | 부재일정 관리, 게시판     | 설계, API, UI | 프로젝트 기획, Docker 환경 구성, JPA 엔티티 설계 및 구현, 로깅 시스템 개발                                                              | https://github.com/ZinnaChoi      |
| 이다연 | 통계, 알림                | 설계, API, UI | Readme 작성, 프로젝트 init, 배포                                                                                                        | https://github.com/dayeon-dayeon  |
| 이찬혁 | 계정 및 권한, 스터디 관리 | 설계, API, UI | [프로젝트 일정 관리](https://docs.google.com/spreadsheets/d/1VXsirwc0Vpu-1I1Nq3qSeFSSbDZcV-1SRO0ZoidfiSI/edit#gid=148490993), UI Layout | https://github.com/MeMyself-And-I |

## 02. 기술 스택

### Environment

<img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white"> <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white"> <img src="https://img.shields.io/badge/Visual Studio Code-007ACC?style=for-the-badge&logo=Visual Studio Code&logoColor=white">

### Development

<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"> <img src="https://img.shields.io/badge/Java-437291?style=for-the-badge&logo=OpenJDK&logoColor=white"> <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=React&logoColor=white"> <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white"> <img src="https://img.shields.io/badge/MySQL-39477F?style=for-the-badge&logo=mysql&logoColor=white">

### Communication

<img src="https://img.shields.io/badge/Google Drive-4285F4?style=for-the-badge&logo=Google Drive&logoColor=white"> <img src="https://img.shields.io/badge/Google Meet-00897B?style=for-the-badge&logo=Google Meet&logoColor=white">

## 03. 주요 기능

### 1. 부재 일정 캘린더

- AS-IS : 개별적인 카카오톡 메시지를 통해 참석 또는 부재 여부를 공유하여, 특정 날짜에 참여하는 스터디원을 파악하기 어렵습니다.
- TO-BE : 부재 일정을 캘린더에 등록함으로써 참여하는 멤버를 시각적으로 쉽게 식별할 수 있으며, 개인별 알림 설정을 통해 스터디원들이 서로의 상태에 대해 알림을 받을 수 있습니다.

### 2. Google Meet 개설 알림

- AS-IS : 먼저 접속하는 스터디원이 Google Meet를 개설하며, 대부분 특정 스터디원이 미팅을 준비합니다.
- TO-BE : 해당 날짜에 참여할 스터디원들이 돌아가면서 Google Meet 세션을 생성할 책임을 지도록 이메일 알림 시스템을 구현하여, 모든 멤버가 이 업무에 동등하게 참여할 수 있도록 유도합니다.

### 3. 게시판

- AS-IS : 스터디 관련 정보를 카카오톡 채팅방에 공유하여 필요할 때 정보를 찾기 어렵습니다.
- TO-BE : 정보 공유를 위한 전용 토론 게시판을 개설하여, 게시글과 댓글을 통해 정보 및 의견을 공유할 수 있도록 하여, 스터디 관련 내용에 대한 접근성과 관리를 용이하게 합니다.

### 4. 통계

- AS-IS : 멤버들은 기상 확인을 위한 카카오톡 메시지를 보내고 부재 시 사전에 공유합니다.
- TO-BE : 부재 일정을 통한 `참여왕`, 개인별 목표 기상시간 설정 및 달성 일 측정으로 `기상왕`을 선정하여 스터디원의 참여율을 높이고 동기를 부여합니다

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

```
./gradlew bootRun
```

#### Frontend

```
cd src/main/frontend
npm install
npm start
```

#### Docker Setup

```
cd docker
docker-compose up -d
```

##### JDBC URL

```
jdbc:mysql://localhost:3307/study
```
