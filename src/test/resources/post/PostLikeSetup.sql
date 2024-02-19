INSERT INTO study.`member`
(email, created_at, updated_at, id, name, password, `role`)
VALUES
('email1@gmail.com', '20240112222007395', '20240112222007395', 'PostUser', 'PostUser', '$2a$10$LFyW8UyygbwOdyVODxN/lOMVo.Euubxgx9F7c7tX49bqHOgOXE/Z6', 'USER'),
('email1@gmail.com', '20240112222007395', '20240112222007395', 'PostUser2', 'PostUser2', '$2a$10$LFyW8UyygbwOdyVODxN/lOMVo.Euubxgx9F7c7tX49bqHOgOXE/Z6', 'USER');

INSERT INTO study.post (member_id, title, content, view_cnt, created_at, updated_at)
SELECT
    (SELECT member_id FROM member WHERE id = 'PostUser'),
    title,
    content,
    0,
    DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f'),
    DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f')
FROM (
    SELECT 'post1' AS title, 'content1' AS content
    UNION ALL
    SELECT 'post2' AS title, 'content2' AS content
    UNION ALL
    SELECT 'post3' AS title, 'content3' AS content
    UNION ALL
    SELECT 'post4' AS title, 'content4' AS content
) AS data;


INSERT INTO study.post_like
(member_id, post_id)
VALUES
(
    (SELECT member_id FROM member WHERE id = 'PostUser2'),
    (SELECT post_id FROM post WHERE title = 'post1')

);