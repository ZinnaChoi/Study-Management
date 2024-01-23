-- member table
INSERT INTO study.`member`
(member_id, contact, created_at, updated_at, id, name, password, `role`)
VALUES
(999, '010-1111-1111', '20240112222007395', '20240112222007395', 'user1', '사용자1', '$2a$10$khWE6krzNMGVRgpisGJmrOADrpklNtVEyGDVLJsHMb2UjyNNj7a0O', 'USER');

INSERT INTO study.`member`
(member_id, contact, created_at, updated_at, id, name, password, `role`)
VALUES
(998, '010-1111-1111', '20240112222007395', '20240112222007395', 'user2', '사용자2', '$2a$10$khWE6krzNMGVRgpisGJmrOADrpklNtVEyGDVLJsHMb2UjyNNj7a0O', 'USER');


-- schedule table
INSERT IGNORE INTO schedule (schedule_id, schedule_name, start_time, end_time)
VALUES (999, 'AM1', '1500', '1600');

INSERT IGNORE INTO schedule (schedule_id, schedule_name, start_time, end_time)
VALUES (998, 'AM2', '1600', '1700');

INSERT IGNORE INTO schedule (schedule_id, schedule_name, start_time, end_time)
VALUES (997, 'AM3', '1700', '1800');

INSERT IGNORE INTO schedule (schedule_id, schedule_name, start_time, end_time)
VALUES (996, 'AM4', '1900', '2000');

-- member_schedule table
INSERT INTO study.member_schedule
(member_id, created_at, schedule_id, updated_at)
VALUES(999, '111111111111111', 999, '111111111111111');

INSERT INTO study.member_schedule
(member_id, created_at, schedule_id, updated_at)
VALUES(999, '111111111111111', 998, '111111111111111');

INSERT INTO study.member_schedule
(member_id, created_at, schedule_id, updated_at)
VALUES(999, '111111111111111', 997, '111111111111111');

INSERT INTO study.member_schedule
(member_id, created_at, schedule_id, updated_at)
VALUES(999, '111111111111111', 996, '111111111111111');



-- wakeup table
INSERT INTO study.wakeup
(member_id, wakeup_time)
VALUES(999, '1530');

INSERT INTO study.wakeup
(member_id, wakeup_time)
VALUES(998, '1800');