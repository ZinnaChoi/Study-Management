INSERT INTO study.`member`
(email, created_at, updated_at, id, name, password, `role`)
VALUES
('email1@gmail.com', '20240117122007395', '20240112222007395', 'statUser1', 'statUser1', '$2ahjFyW8werwefffgdX49fdgdfvxxcxh087845645667g', 'USER'),
('email2@gmail.com', '20240117122007395', '20240112222007395', 'statUser2', 'statUser2', '$2ahjFwerwerOdyVODxN/lOMp;[sdsdcdcsdfsewere34m*$', 'USER'),
('email3@gmail.com', '20240117122007395', '20240112222007395', 'statUser3', 'statUser3', '$2ahjFyW8234sd@#sdfnfvaQWR34ert234^&^%#$BGHDFWxdv', 'USER');


INSERT INTO study.`daily_log` (score, member_id, created_at, date, type)
SELECT
    1,
    m.member_id,
    '20240116152007395',
    '20240116',
    'WAKEUP'
FROM study.`member` m
WHERE m.id = 'statUser1';

INSERT INTO study.`daily_log` (score, member_id, created_at, date, type)
SELECT
    1,
    m.member_id,
    '20240116152007395',
    '20240116',
    'WAKEUP'
FROM study.`member` m
WHERE m.id = 'statUser2';

INSERT INTO study.`daily_log` (score, member_id, created_at, date, type)
SELECT
    1,
    m.member_id,
    '20240116152007395',
    '20240116',
    'WAKEUP'
FROM study.`member` m
WHERE m.id = 'statUser3';