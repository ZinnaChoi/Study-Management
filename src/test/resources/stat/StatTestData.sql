INSERT INTO study.`member`
(email, created_at, updated_at, id, name, password, role)
VALUES
('wowdayeon@gmail.com', '20240117122007395', '20240112222007395', '다연', '다연', '$2ahjFyW8werwefffgdX49fdgdfvxp45645667g', 'USER'),
('dayeondayeon@naver.com', '20240117122007395', '20240112222007395', '예희', '예희', 'ewere34m*$erwerOdyVODxN/lOMp;[sdsdcdcsd', 'USER'),
('wowdayeon@daum.net', '20240117122007395', '20240112222007395', '찬혁', '찬혁', '34sd@#sdfnfvaQWR34ert234^&^%#$BGHDFWxdv', 'USER');


INSERT INTO study.`schedule`
(start_time,end_time, schedule_name)
VALUES
('13:00','14:00','test_PM1'),
('14:00','15:00','test_PM2'),
('18:13','19:00','test_PM3'),
('16:00','17:00','test_PM4'),
('17:00','18:00','test_PM5'),
('18:00','19:00','test_PM6');


INSERT INTO study.`member_schedule`
( member_id,schedule_id, created_at,updated_at)
VALUES
( (SELECT member_id FROM study.`member` WHERE id = '다연'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM1')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '다연'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM2')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '다연'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM3')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '다연'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM4')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '다연'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM5')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '다연'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM6')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '예희'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM1')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '예희'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM2')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '예희'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM3')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '찬혁'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM1')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '찬혁'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM2')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '찬혁'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM3')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '찬혁'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM4')),'20240125122007395', '20240125222007395');



INSERT INTO study.`absent_schedule`
( member_id, schedule_id, absent_date, created_at, description, updated_at)
VALUES
( (SELECT member_id FROM study.`member` WHERE id = '다연'), ((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM6')), DATE_FORMAT(CURDATE(), '%Y%m%d'), '20240125222007395', '면접', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '예희'), ((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM2')), DATE_FORMAT(CURDATE(), '%Y%m%d'), '20240125222007395', '개인 사유', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '예희'), ((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM3')), DATE_FORMAT(CURDATE(), '%Y%m%d'), '20240125222007395', '가족 행사 참여', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = '찬혁'), ((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM4')), DATE_FORMAT(CURDATE(), '%Y%m%d'), '20240125222007395', '가족 행사 참여', '20240125222007395');







INSERT INTO study.`notice` (absent, last_share_date, link_share, member_id, new_post, wakeup)
VALUES 
( 1, '20231116',1, (SELECT member_id FROM study.`member` WHERE id = '다연'), 1, 1),
( 1, '20240126',1, (SELECT member_id FROM study.`member` WHERE id = '예희'), 1, 1),
( 1, '20240101',1, (SELECT member_id FROM study.`member` WHERE id = '찬혁'), 0, 0);


	INSERT INTO daily_log (score,member_id,created_at,`date`,`type`) VALUES
	 (3,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-15, '%Y%m%d'),'ABSENT'),
	 (0,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-14, '%Y%m%d'),'ABSENT'),
	 (3,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-13, '%Y%m%d'),'ABSENT'),
	 (0,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-12, '%Y%m%d'),'ABSENT'),
	 (3,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-5, '%Y%m%d'),'ABSENT'),
	 (0,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-4, '%Y%m%d'),'ABSENT'),
	 (3,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-1, '%Y%m%d'),'ABSENT'),
	 (0,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE(), '%Y%m%d'),'ABSENT'),
	 (2,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240208122008395',DATE_FORMAT(CURDATE()-15, '%Y%m%d'),'ABSENT'),
	 (3,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240208122008395',DATE_FORMAT(CURDATE()-14, '%Y%m%d'),'ABSENT'),
	 (2,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240208122008395',DATE_FORMAT(CURDATE()-13, '%Y%m%d'),'ABSENT'),
	 (3,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240208122008395',DATE_FORMAT(CURDATE()-12, '%Y%m%d'),'ABSENT'),
	 (2,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240208122008395',DATE_FORMAT(CURDATE()-5, '%Y%m%d'),'ABSENT'),
	 (3,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240208122008395',DATE_FORMAT(CURDATE()-4, '%Y%m%d'),'ABSENT'),
	 (2,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240208122008395',DATE_FORMAT(CURDATE()-1, '%Y%m%d'),'ABSENT'),
	 (3,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240208122008395',DATE_FORMAT(CURDATE(), '%Y%m%d'),'ABSENT'),
	 (4,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240209122006395',DATE_FORMAT(CURDATE()-15, '%Y%m%d'),'ABSENT'),
	 (4,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240209122006395',DATE_FORMAT(CURDATE()-14, '%Y%m%d'),'ABSENT'),
	 (4,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240209122006395',DATE_FORMAT(CURDATE()-13, '%Y%m%d'),'ABSENT'),
	 (4,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240209122006395',DATE_FORMAT(CURDATE()-12, '%Y%m%d'),'ABSENT'),
	 (4,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240209122006395',DATE_FORMAT(CURDATE()-5, '%Y%m%d'),'ABSENT'),
	 (4,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240209122006395',DATE_FORMAT(CURDATE()-4, '%Y%m%d'),'ABSENT'),
	 (4,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240209122006395',DATE_FORMAT(CURDATE()-1, '%Y%m%d'),'ABSENT'),
	 (4,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240209122006395',DATE_FORMAT(CURDATE(), '%Y%m%d'),'ABSENT');
	 
	

INSERT INTO daily_log (score,member_id,created_at,`date`,`type`) VALUES
	 (0,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-10, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-9, '%Y%m%d'),'WAKEUP'),
	 (0,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-8, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-7, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-6, '%Y%m%d'),'WAKEUP'),
	 (0,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-5, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-4, '%Y%m%d'),'WAKEUP'),
	 (0,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-3, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-2, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE()-1, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '다연'),'20240207122007395',DATE_FORMAT(CURDATE(), '%Y%m%d'),'WAKEUP'),
	 (0,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240207122007395',DATE_FORMAT(CURDATE()-10, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240207122007395',DATE_FORMAT(CURDATE()-9, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240207122007395',DATE_FORMAT(CURDATE()-8, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240207122007395',DATE_FORMAT(CURDATE()-7, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240207122007395',DATE_FORMAT(CURDATE()-6, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240207122007395',DATE_FORMAT(CURDATE()-5, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240207122007395',DATE_FORMAT(CURDATE()-4, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240207122007395',DATE_FORMAT(CURDATE()-3, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240207122007395',DATE_FORMAT(CURDATE()-2, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240207122007395',DATE_FORMAT(CURDATE()-1, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '예희'),'20240207122007395',DATE_FORMAT(CURDATE(), '%Y%m%d'),'WAKEUP'),
	 (0,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240207122007395',DATE_FORMAT(CURDATE()-10, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240207122007395',DATE_FORMAT(CURDATE()-9, '%Y%m%d'),'WAKEUP'),
	 (0,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240207122007395',DATE_FORMAT(CURDATE()-8, '%Y%m%d'),'WAKEUP'),
	 (0,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240207122007395',DATE_FORMAT(CURDATE()-7, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240207122007395',DATE_FORMAT(CURDATE()-6, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240207122007395',DATE_FORMAT(CURDATE()-5, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240207122007395',DATE_FORMAT(CURDATE()-4, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240207122007395',DATE_FORMAT(CURDATE()-3, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240207122007395',DATE_FORMAT(CURDATE()-2, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240207122007395',DATE_FORMAT(CURDATE()-1, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = '찬혁'),'20240207122007395',DATE_FORMAT(CURDATE(), '%Y%m%d'),'WAKEUP');