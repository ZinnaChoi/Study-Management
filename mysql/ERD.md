# Table

## study_info

> 스터디 메타 정보 저장 테이블

| column name | key         | type   | description        |
| ----------- | ----------- | ------ | ------------------ |
| study_id    | Primary Key | Long   |                    |
| study_name  |             | String | 스터디 이름        |
| study_logo  |             | byte[] | 스터디 로고 이미지 |
| db_url      |             | String | jdbc_url           |
| db_user     |             | String |                    |
| db_password |             | String |                    |

## member

> 스터디원 정보 테이블

| Column Name | Key         | Type   | Description        |
| ----------- | ----------- | ------ | ------------------ |
| member_id   | Primary Key | Long   |                    |
| id          | Unique      | String | 사용자 식별자      |
| password    |             | String | 비밀번호           |
| name        |             | String | 사용자 이름        |
| contact     |             | String | 연락처             |
| role        |             | String | 사용자 역할        |
| created_at  |             | String | 생성 날짜          |
| updated_at  |             | String | 정보 업데이트 날짜 |
| expired_at  |             | String | 계정 만료 날짜     |

## wakeup

> 목표 기상 시간 테이블

| Column Name | Key         | Type   | Description |
| ----------- | ----------- | ------ | ----------- |
| wakeup_id   | Primary Key | Long   |             |
| member_id   | Foreign Key | Long   | 사용자 ID   |
| wakeup_time |             | String | 기상 시간   |

## post

> 게시글 테이블

| Column Name | Key         | Type          | Description   |
| ----------- | ----------- | ------------- | ------------- |
| post_id     | Primary Key | Long          |               |
| member_id   | Foreign Key | Long          | 게시자 ID     |
| title       |             | String(60)    | 게시글 제목   |
| content     |             | String(20000) | 게시글 내용   |
| view_cnt    |             | Integer       | 조회 수       |
| created_at  |             | String        | 생성 날짜     |
| updated_at  |             | String        | 업데이트 날짜 |

## post_like

> 게시글 좋아요 테이블

| Column Name | Key         | Type | Description        |
| ----------- | ----------- | ---- | ------------------ |
| likes_id    | Primary Key | Long |                    |
| post_id     | Foreign Key | Long | 게시글 ID          |
| member_id   | Foreign Key | Long | 좋아요한 사용자 ID |

## post_comment

> 게시글 댓글 테이블

| Column Name       | Key         | Type        | Description    |
| ----------------- | ----------- | ----------- | -------------- |
| comment_id        | Primary Key | Long        |                |
| parent_comment_id |             | Long        | 상위 댓글 ID   |
| post_id           | Foreign Key | Long        | 게시글 ID      |
| member_id         | Foreign Key | Long        | 댓글 작성자 ID |
| content           |             | String(500) | 댓글 내용      |
| created_at        |             | String      | 생성 날짜      |
| updated_at        |             | String      | 업데이트 날짜  |

## schedule

> 스터디 스케줄 관리 테이블

| Column Name   | Key         | Type   | Description   |
| ------------- | ----------- | ------ | ------------- |
| schedule_id   | Primary Key | Long   | 스케줄 아이디 |
| schedule_name | Primary Key | String | 스케줄 이름   |
| start_time    |             | String | 시작 시간     |
| end_time      |             | String | 종료 시간     |

## member_schedule

> 스터디원 별 스터디 참여시간 테이블

| Column Name        | Key         | Type   | Description   |
| ------------------ | ----------- | ------ | ------------- |
| member_schedule_id | Primary Key | Long   |               |
| member_id          | Foreign Key | Long   | 사용자 ID     |
| event_name         | Foreign Key | String | 스케줄 이름   |
| created_at         |             | String | 생성 날짜     |
| updated_at         |             | String | 업데이트 날짜 |

## absent_schedule

> 스터디원 별 부재일정 테이블

| Column Name | Key         | Type   | Description   |
| ----------- | ----------- | ------ | ------------- |
| absent_id   | Primary Key | Long   |               |
| member_id   | Foreign Key | Long   | 사용자 ID     |
| event_name  | Foreign Key | String | 스케줄 이름   |
| description |             | String | 부재 사유     |
| absent_date |             | String | 부재 날짜     |
| created_at  |             | String | 생성 날짜     |
| updated_at  |             | String | 업데이트 날짜 |

## daily_log

> 스터디원 하루 단위 기상, 출석 기록

| Column Name | Key         | Type    | Description            |
| ----------- | ----------- | ------- | ---------------------- |
| stat_id     | Primary Key | Long    |                        |
| member_id   | Foreign Key | Long    | 사용자 ID              |
| date        |             | String  | 날짜                   |
| type        |             | String  | 로그 유형 (기상, 출석) |
| score       |             | Integer | 점수                   |
| created_at  |             | String  | 생성 날짜              |

## notice

> 알림 테이블

| Column Name     | Key         | Type    | Description         |
| --------------- | ----------- | ------- | ------------------- |
| notice_id       | Primary Key | Long    |                     |
| member_id       | Foreign Key | Long    | 사용자 ID           |
| wakeup          |             | Boolean | 기상 알림 여부      |
| absent          |             | Boolean | 부재 알림 여부      |
| new_post        |             | Boolean | 새 게시글 알림 여부 |
| link_share      |             | Boolean | 링크 공유 알림 여부 |
| last_share_date |             | String  | 마지막 공유 날짜    |
