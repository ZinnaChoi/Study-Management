INSERT INTO study.`member`
(contact, created_at, updated_at, id, name, password, role)
VALUES
('010-1111-2222', '20240117122007395', '20240112222007395', 'User1', 'User1', '$2ahjFyW8werwefffgdX49fdgdfvxp45645667g', 'USER'),
('010-1111-3333', '20240117122007395', '20240112222007395', 'User2', 'User2', 'ewere34m*$erwerOdyVODxN/lOMp;[sdsdcdcsd', 'USER'),
('010-1111-4444', '20240117122007395', '20240112222007395', 'User3', 'User3', '34sd@#sdfnfvaQWR34ert234^&^%#$BGHDFWxdv', 'USER');


INSERT INTO study.`schedule`
(start_time,end_time, schedule_name)
VALUES
('13:00','14:00','test_PM1'),
('14:00','15:00','test_PM2'),
('15:00','16:00','test_PM3'),
('16:00','17:00','test_PM4'),
('17:00','18:00','test_PM5'),
('18:00','19:00','test_PM6');


INSERT INTO study.`member_schedule`
( member_id,schedule_id, created_at,updated_at)
VALUES
( (SELECT member_id FROM study.`member` WHERE id = 'User1'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM1')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User1'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM2')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User1'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM3')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User1'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM4')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User1'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM5')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User1'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM6')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User2'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM1')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User2'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM2')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User2'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM3')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User3'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM1')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User3'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM2')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User3'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM3')),'20240125122007395', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User3'),((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM4')),'20240125122007395', '20240125222007395');



INSERT INTO study.`absent_schedule`
( member_id, schedule_id, absent_date, created_at, description, updated_at)
VALUES
( (SELECT member_id FROM study.`member` WHERE id = 'User1'), ((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM1')), DATE_FORMAT(CURDATE(), '%Y%m%d'), '20240125222007395', '면접', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User2'), ((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM2')), DATE_FORMAT(CURDATE(), '%Y%m%d'), '20240125222007395', '개인 사유', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User3'), ((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM1')), DATE_FORMAT(CURDATE(), '%Y%m%d'), '20240125222007395', '가족 행사 참여', '20240125222007395'),
( (SELECT member_id FROM study.`member` WHERE id = 'User3'), ((SELECT schedule_id FROM study.`schedule` WHERE schedule_name = 'test_PM3')), DATE_FORMAT(CURDATE(), '%Y%m%d'), '20240125222007395', '가족 행사 참여', '20240125222007395');
