INSERT INTO study.`member`
(email, created_at, updated_at, id, name, password, role)
VALUES
('email1@gmail.com', '20240117122007395', '20240112222007395', 'wakeupUser1', 'wakeupUser1', '$2ahjFyW8werwefffgdX49fdgdfvxp45645667g', 'USER'),
('email2@gmail.com', '20240117122007395', '20240112222007395', 'wakeupUser2', 'wakeupUser2', 'ewere34m*$erwerOdyVODxN/lOMp;[sdsdcdcsd', 'USER'),
('email3@gmail.com', '20240117122007395', '20240112222007395', 'wakeupUser3', 'wakeupUser3', '34sd@#sdfnfvaQWR34ert234^&^%#$BGHDFWxdv', 'USER');

INSERT INTO study.`wakeup`
(member_id, wakeup_time)
VALUES
((SELECT member_id FROM study.`member` WHERE id = 'wakeupUser1'),'0730'),
((SELECT member_id FROM study.`member` WHERE id = 'wakeupUser2'),'0830');
