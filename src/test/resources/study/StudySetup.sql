INSERT INTO study.`member`
(contact, created_at, updated_at, id, name, password, `role`)
VALUES
('010-1111-1111', DATE_FORMAT(NOW(6), '%Y%m%d%H%i%s%f'), DATE_FORMAT(NOW(6), '%Y%m%d%H%i%s%f'), 'user1', '사용자1', '$2a$10$khWE6krzNMGVRgpisGJmrOADrpklNtVEyGDVLJsHMb2UjyNNj7a0O', 'USER');

insert into study_info(db_password, db_url, db_user, study_name, study_logo) values ('password', '10.10.110.110', 'admin', 'NotExistStudyName', null);

insert into schedule(end_time, schedule_name, start_time) values('13:00','NotExistScheduleName','14:00');


