INSERT INTO study.`member`
(contact, created_at, updated_at, id, name, password, role)
VALUES
('010-1111-2222', '20240117122007395', '20240112222007395', 'User1', 'User1', '$2ahjFyW8werwefffgdX49fdgdfvxp45645667g', 'USER'),
('010-1111-3333', '20240117122007395', '20240112222007395', 'User2', 'User2', 'ewere34m*$erwerOdyVODxN/lOMp;[sdsdcdcsd', 'USER'),
('010-1111-4444', '20240117122007395', '20240112222007395', 'User3', 'User3', '34sd@#sdfnfvaQWR34ert234^&^%#$BGHDFWxdv', 'USER');


INSERT INTO study.`schedule`
(schedule_id, start_time,end_time, schedule_name)
VALUES
('1','13:00','14:00','PM1'),
('2','14:00','15:00','PM2'),
('3','15:15','16:15','PM315'),
('4','13:15','17:15','PM415'),
('5','15:00','22:00','PM9'),
('6','22:00','23:00','PM10');


INSERT INTO study.`member_schedule`
(member_schedule_id, member_id,schedule_id, created_at,updated_at)
VALUES
('1', (SELECT member_id FROM study.`member` WHERE id = 'User1'),'1','20240125122007395', '20240125222007395'),
('2', (SELECT member_id FROM study.`member` WHERE id = 'User1'),'2','20240125122007395', '20240125222007395'),
('3', (SELECT member_id FROM study.`member` WHERE id = 'User1'),'3','20240125122007395', '20240125222007395'),
('4', (SELECT member_id FROM study.`member` WHERE id = 'User1'),'4','20240125122007395', '20240125222007395'),
('5', (SELECT member_id FROM study.`member` WHERE id = 'User1'),'5','20240125122007395', '20240125222007395'),
('6', (SELECT member_id FROM study.`member` WHERE id = 'User1'),'6','20240125122007395', '20240125222007395'),
('7', (SELECT member_id FROM study.`member` WHERE id = 'User2'),'1','20240125122007395', '20240125222007395'),
('8', (SELECT member_id FROM study.`member` WHERE id = 'User2'),'2','20240125122007395', '20240125222007395'),
('9', (SELECT member_id FROM study.`member` WHERE id = 'User2'),'3','20240125122007395', '20240125222007395'),
('10', (SELECT member_id FROM study.`member` WHERE id = 'User3'),'1','20240125122007395', '20240125222007395'),
('11', (SELECT member_id FROM study.`member` WHERE id = 'User3'),'2','20240125122007395', '20240125222007395'),
('12', (SELECT member_id FROM study.`member` WHERE id = 'User3'),'3','20240125122007395', '20240125222007395'),
('13', (SELECT member_id FROM study.`member` WHERE id = 'User3'),'4','20240125122007395', '20240125222007395');



INSERT INTO absent_schedule
(absent_id, member_id, schedule_id, absent_date, created_at, description, updated_at)
VALUES
('1', (SELECT member_id FROM study.`member` WHERE id = 'User1'), '1', DATE_FORMAT(CURDATE(), '%Y%m%d'), '20240125222007395', '면접', '20240125222007395'),
('2', (SELECT member_id FROM study.`member` WHERE id = 'User2'), '2', DATE_FORMAT(CURDATE(), '%Y%m%d'), '20240125222007395', '개인 사유', '20240125222007395'),
('3', (SELECT member_id FROM study.`member` WHERE id = 'User3'), '1', DATE_FORMAT(CURDATE(), '%Y%m%d'), '20240125222007395', '가족 행사 참여', '20240125222007395'),
('4', (SELECT member_id FROM study.`member` WHERE id = 'User3'), '2', DATE_FORMAT(CURDATE(), '%Y%m%d'), '20240125222007395', '가족 행사 참여', '20240125222007395');
