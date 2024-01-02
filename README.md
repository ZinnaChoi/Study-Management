# Study-Management

스터디 관리 시스템 ✏️


## 01. 프로젝트 소개
### (1) 목적

Google Meet를 활용한 개인 공부 스터디에서 참여율을 높이고 체계적인 관리를 목적으로하는 프로젝트.
백엔드 개발자 4명이 각자 경험해보지 않은 기술 스택을 도입해, 기능별로 역할을 나누어 설계부터 화면 구성 및 백엔드 기능을 구현.

### (2) 팀원 및 역할

 |이다연|이찬혁|정주환|최예희|
|------|---|---|---|
|통계, 알림, 프로젝트 init |회원가입, 화면 전체 틀, 프로젝트 일정 관리|부재 일정, 배포|게시판, DB entity 생성|
|https://github.com/dayeon-dayeon|https://github.com/MeMyself-And-I|https://github.com/jeong-joohwan|https://github.com/ZinnaChoi|

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
<img src="https://img.shields.io/badge/Visual Studio Code-007ACC?style=for-the-badge&logo=Visual Studio Code&logoColor=white">  <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white"> <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white">

### Config
<img src="https://img.shields.io/badge/npm-CB3837?style=for-the-badge&logo=npm&logoColor=white"> 


### Development
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white">  <img src="https://img.shields.io/badge/Java-437291?style=for-the-badge&logo=OpenJDK&logoColor=white"> <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=React&logoColor=white"> 



### Communication
<img src="https://img.shields.io/badge/Google Drive-4285F4?style=for-the-badge&logo=Google Drive&logoColor=white"> <img src="https://img.shields.io/badge/Google Meet-00897B?style=for-the-badge&logo=Google Meet&logoColor=white"> 


## 04. 주요 기능

1) 부재 일정 캘린더
카카오톡 메세지로 참/불참 여부를 알려 특정 일자의 참여 멤버를 파악하기 어려움.
=> 캘린더에 부재 일정을 등록함으로써 참여 멤버를 한 눈에 파악할 수 있음.


2) Google Meet 개설 알림
먼저 접속하는 스터디원이 Google Meet를 개설하여 대부분 특정 스터디원이 Google Meet을 개설.
=> 해당 일자에 참여할 멤버에게 돌아가면서 Google Meet 개설 알림을 전송. 이를 통해 여러 멤버가 균등하게 Google Meet를 생성함.

3) 게시판 
정보 공유 등의 목적으로 게시판을 신설.

4) 출석왕
참여율을 높이고 스터디원들의 의욕을 고취하기 위해 부재 일정 통계를 활용하여 기간별로 '출석왕'을 선정함.

5) 기상 성공율 
카카오톡 메세지로 단순히 '기상했습니다'만 전송하는 것으로는 생활 패턴이나 시간 관리 측면에서 유용하지 않음 . 
=> 각 스터디원은 개인 목표 기상시간을 설정하고, 특정 기간 동안 이를 얼마나 성공적으로 달성했는지를 %로 표시하여 공유함.



## 05. 아키 텍쳐
## 06. 기타 추가 사항

