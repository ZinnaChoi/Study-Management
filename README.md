# Study-Management

스터디 관리 시스템 ✏️

## 01. 프로젝트 소개

### (1) 목적

Google Meet를 활용한 개인 공부 스터디에서 참여율을 높이고 체계적인 관리를 목적으로하는 프로젝트.
백엔드 개발자 4명이 각자 경험해보지 않은 기술 스택을 도입해, 기능별로 역할을 나누어 설계부터 화면 구성 및 백엔드 기능을 구현.

### (2) 팀원 및 역할

| 작업자 | 담당 기능              | 추가 역할                                        | git 주소                          |
| ------ | ---------------------- | ------------------------------------------------ | --------------------------------- |
| 이다연 | 통계, 알림             | Readme 작성, 프로젝트 init                       | https://github.com/dayeon-dayeon  |
| 이찬혁 | 회원가입, 화면 전체 틀 | 프로젝트 일정 관리                               | https://github.com/MeMyself-And-I |
| 정주환 | 부재 일정              | 배포                                             | https://github.com/jeong-joohwan  |
| 최예희 | 게시판                 | 프로젝트 기획, Docker 환경 설정, JPA entity 생성 | https://github.com/ZinnaChoi      |

### (3) 배포 URL

```
개발 버전 :
릴리즈 버전 :
```

## 02. 시작 가이드

### (1) 요구사항

```
java 17
react
mysql
```

### (2) 설치 및 실행

#### Installation

```
git clone https://github.com/ZinnaChoi/Study-Management
```

#### Backend

```

```

#### Frontend

```
cd src/main/frontend
npm install
npm start
```

#### DataBase Setup

```
cd mysql
docker-compose up -d
```

#### JDBC URL

```
jdbc:mysql://localhost:3307/study
```

## 03. 기술 스택

### Environment

<img src="https://img.shields.io/badge/Visual Studio Code-007ACC?style=for-the-badge&logo=Visual Studio Code&logoColor=white"> <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white"> <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white">

### Config

<img src="https://img.shields.io/badge/npm-CB3837?style=for-the-badge&logo=npm&logoColor=white">

### Development

<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"> <img src="https://img.shields.io/badge/Java-437291?style=for-the-badge&logo=OpenJDK&logoColor=white"> <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=React&logoColor=white">

### Communication

<img src="https://img.shields.io/badge/Google Drive-4285F4?style=for-the-badge&logo=Google Drive&logoColor=white"> <img src="https://img.shields.io/badge/Google Meet-00897B?style=for-the-badge&logo=Google Meet&logoColor=white">

## 04. 주요 기능

### 1. 부재 일정 캘린더

- AS-IS: 개별 카카오톡 메세지를 통해 스터디 참/불참 여부를 공유, 특정 일자의 참여 멤버 파악이 어려움.
- TO-BE: 캘린더에 부재 일정을 등록함으로써 참여 멤버를 한 눈에 파악할 수 있음.

### 2. Google Meet 개설 알림

- AS-IS: 먼저 접속하는 스터디원이 Google Meet를 개설하여 대부분 특정 스터디원이 Google Meet을 개설.
- TO-BE: 해당 일자에 참여할 멤버에게 돌아가면서 Google Meet 개설 알림을 전송. 이를 통해 멤버 모두가 골고루 미팅을 개설할 수 있도록 유도.

### 3. 게시판

- AS-IS: 스터디 관련 정보를 카카오톡 채팅방에 공유하여, 필요할 때 정보 찾기 어려움.
- TO-BE: 정보 공유를 위한 전용 게시판 신설.

### 4. 통계

- AS-IS: 기상 시 카카오톡 메시지에 기상 확인 카톡 전송, 부재 시 사전 공유.
- TO-BE: 부재일정과 기상시간을 기준으로 통계 산출 및 기상왕, 통계왕을 선정하여 스터디원의 참여율 증진 및 의욕 증진, 개인별 목표 기상시간 설정

## 05. 아키 텍쳐

## 06. 기타 추가 사항
