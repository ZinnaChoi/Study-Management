-- member table
INSERT INTO study.`member`
(email, created_at, updated_at, id, name, password, `role`)
VALUES
('email1@gmail.com', DATE_FORMAT(NOW(6), '%Y%m%d%H%i%s%f'), DATE_FORMAT(NOW(6), '%Y%m%d%H%i%s%f'), 'user1', '사용자1', '$2a$10$khWE6krzNMGVRgpisGJmrOADrpklNtVEyGDVLJsHMb2UjyNNj7a0O', 'USER');

INSERT INTO study.`member`
(email, created_at, updated_at, id, name, password, `role`)
VALUES
('email1@gmail.com', '20240112222007395', '20240112222007395', 'user2', '사용자2', '$2a$10$khWE6krzNMGVRgpisGJmrOADrpklNtVEyGDVLJsHMb2UjyNNj7a0O', 'USER');


-- schedule table
INSERT IGNORE INTO schedule (schedule_name, start_time, end_time)
VALUES ('AM1', '1500', '1600');

INSERT IGNORE INTO schedule (schedule_name, start_time, end_time)
VALUES ('AM2', '1600', '1700');

INSERT IGNORE INTO schedule (schedule_name, start_time, end_time)
VALUES ('AM3', '1700', '1800');

INSERT IGNORE INTO schedule (schedule_name, start_time, end_time)
VALUES ('AM4', '1900', '2000');

-- member_schedule table
INSERT INTO study.member_schedule
(member_id, created_at, schedule_id, updated_at)
VALUES
(
    (SELECT member_id FROM member WHERE id = 'user1'), 
    DATE_FORMAT(NOW(6), '%Y%m%d%H%i%s%f'), 
    (SELECT schedule_id FROM schedule WHERE schedule_name = 'AM1'), 
    DATE_FORMAT(NOW(6), '%Y%m%d%H%i%s%f')
);

INSERT INTO study.member_schedule
(member_id, created_at, schedule_id, updated_at)
VALUES
(
    (SELECT member_id FROM member WHERE id = 'user1'), 
    DATE_FORMAT(NOW(6), '%Y%m%d%H%i%s%f'), 
    (SELECT schedule_id FROM schedule WHERE schedule_name = 'AM2'), 
    DATE_FORMAT(NOW(6), '%Y%m%d%H%i%s%f')
);

INSERT INTO study.member_schedule
(member_id, created_at, schedule_id, updated_at)
VALUES
(
    (SELECT member_id FROM member WHERE id = 'user1'), 
    DATE_FORMAT(NOW(6), '%Y%m%d%H%i%s%f'), 
    (SELECT schedule_id FROM schedule WHERE schedule_name = 'AM3'), 
    DATE_FORMAT(NOW(6), '%Y%m%d%H%i%s%f')
);

INSERT INTO study.member_schedule
(member_id, created_at, schedule_id, updated_at)
VALUES
(
    (SELECT member_id FROM member WHERE id = 'user1'), 
    DATE_FORMAT(NOW(6), '%Y%m%d%H%i%s%f'), 
    (SELECT schedule_id FROM schedule WHERE schedule_name = 'AM4'), 
    DATE_FORMAT(NOW(6), '%Y%m%d%H%i%s%f')
);


-- wakeup table
INSERT INTO study.wakeup
(member_id, wakeup_time)
VALUES
(
    (SELECT member_id FROM member WHERE id = 'user1'), 
    '1530'
);

INSERT INTO study.wakeup
(member_id, wakeup_time)
VALUES
(
    (SELECT member_id FROM member WHERE id = 'user2'), 
    '1800'
);