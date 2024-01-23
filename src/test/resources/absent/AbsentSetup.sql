INSERT INTO study.`member`
(contact, created_at, updated_at, id, name, password, `role`)
VALUES
('010-1111-2222', '20240112222007395', '20240112222007395', 'AbsentUser', 'AbsentUser', '$2a$10$LFyW8UyygbwOdyVODxN/lOMVo.Euubxgx9F7c7tX49bqHOgOXE/Z6', 'USER'),
('010-1111-2222', '20240112222007395', '20240112222007395', 'AbsentUser2', 'AbsentUser2', '$2a$10$LFyW8UyygbwOdyVODxN/lOMVo.Euubxgx9F7c7tX49bqHOgOXE/Z6', 'USER');


INSERT INTO study.schedule
(end_time, schedule_name, start_time)
VALUES
('1500', 'TESTPM1', '1300'),
('1700', 'TESTPM3', '1500'),
('2100', 'TESTPM7', '1900'),
('2300', 'TESTPM9', '2100');



INSERT INTO study.member_schedule
(member_id, created_at, schedule_id, updated_at)
VALUES
((SELECT member_id FROM member WHERE id = 'AbsentUser'), DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f'), (SELECT schedule_id FROM schedule WHERE schedule_name ='TESTPM1'), DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f')),
((SELECT member_id FROM member WHERE id = 'AbsentUser'), DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f'),(SELECT schedule_id FROM schedule WHERE schedule_name ='TESTPM3') , DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f')),
((SELECT member_id FROM member WHERE id = 'AbsentUser'), DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f'), (SELECT schedule_id FROM schedule WHERE schedule_name ='TESTPM7'), DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f')),
((SELECT member_id FROM member WHERE id = 'AbsentUser2'), DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f'), (SELECT schedule_id FROM schedule WHERE schedule_name ='TESTPM1'), DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f')),
((SELECT member_id FROM member WHERE id = 'AbsentUser2'), DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f'), (SELECT schedule_id FROM schedule WHERE schedule_name ='TESTPM3'), DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f'));


INSERT INTO study.absent_schedule
(member_id, absent_date, created_at, description, schedule_id, updated_at)
VALUES
((SELECT member_id FROM member WHERE id = 'AbsentUser'), '20240116', DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f'), '가족여행', (SELECT schedule_id FROM schedule WHERE schedule_name ='TESTPM1'), DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f')),
((SELECT member_id FROM member WHERE id = 'AbsentUser2'), '20240116', DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f'), '가족여행',(SELECT schedule_id FROM schedule WHERE schedule_name ='TESTPM1'), DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f')),
((SELECT member_id FROM member WHERE id = 'AbsentUser2'), '20240116', DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f'), '가족여행', (SELECT schedule_id FROM schedule WHERE schedule_name ='TESTPM3'), DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f'));