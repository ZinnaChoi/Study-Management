INSERT INTO study.`member`
(contact, created_at, updated_at, id, name, password, role)
VALUES
('010-1111-2222', '20240117122007395', '20240112222007395', 'wakeupUser1', 'wakeupUser1', '$2ahjFyW8werwefffgdX49fdgdfvxp45645667g', 'USER'),
('010-1111-3333', '20240117122007395', '20240112222007395', 'wakeupUser2', 'wakeupUser2', 'ewere34m*$erwerOdyVODxN/lOMp;[sdsdcdcsd', 'USER'),
('010-1111-4444', '20240117122007395', '20240112222007395', 'wakeupUser3', 'wakeupUser3', '34sd@#sdfnfvaQWR34ert234^&^%#$BGHDFWxdv', 'USER');

INSERT INTO study.`wakeup`
(member_id, wakeup_time)
VALUES
((SELECT member_id FROM study.`member` WHERE id = 'wakeupUser1'),'0730'),
((SELECT member_id FROM study.`member` WHERE id = 'wakeupUser2'),'0830');
