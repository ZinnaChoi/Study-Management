INSERT INTO study.`member`
(email, created_at, updated_at, id, name, password, `role`)
VALUES
('email1@gmail.com', '20240117122007395', '20240112222007395', 'statUser1', 'statUser1', '$2ahjFyW8werwefffgdX49fdgdfvxxcxh087845645667g', 'USER'),
('email2@gmail.com', '20240117122007395', '20240112222007395', 'statUser2', 'statUser2', '$2ahjFwerwerOdyVODxN/lOMp;[sdsdcdcsdfsewere34m*$', 'USER'),
('email3@gmail.com', '20240117122007395', '20240112222007395', 'statUser3', 'statUser3', '$2ahjFyW8234sd@#sdfnfvaQWR34ert234^&^%#$BGHDFWxdv', 'USER');

INSERT INTO daily_log (score,member_id,created_at,`date`,`type`) VALUES
	 (0,(SELECT member_id FROM study.`member` WHERE id = 'statUser1'),'20240207122007395',DATE_FORMAT(CURDATE()-2, '%Y%m%d'),'WAKEUP'),
	 (0,(SELECT member_id FROM study.`member` WHERE id = 'statUser1'),'20240207122007395',DATE_FORMAT(CURDATE()-1, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = 'statUser2'),'20240208122008395',DATE_FORMAT(CURDATE(), '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = 'statUser2'),'20240208122008395',DATE_FORMAT(CURDATE()-2, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = 'statUser3'),'20240209122006395',DATE_FORMAT(CURDATE()-1, '%Y%m%d'),'WAKEUP'),
	 (1,(SELECT member_id FROM study.`member` WHERE id = 'statUser3'),'20240209122006395',DATE_FORMAT(CURDATE(), '%Y%m%d'),'WAKEUP');
