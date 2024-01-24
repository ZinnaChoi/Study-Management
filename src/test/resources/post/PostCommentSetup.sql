INSERT INTO study.`member`
(contact, created_at, updated_at, id, name, password, `role`)
VALUES
('010-1111-1111', '20240112222007395', '20240112222007395', 'PostUser', 'PostUser', '$2a$10$LFyW8UyygbwOdyVODxN/lOMVo.Euubxgx9F7c7tX49bqHOgOXE/Z6', 'USER'),
('010-1111-1111', '20240112222007395', '20240112222007395', 'PostUser2', 'PostUser2', '$2a$10$LFyW8UyygbwOdyVODxN/lOMVo.Euubxgx9F7c7tX49bqHOgOXE/Z6', 'USER');


INSERT INTO study.post (member_id, title, content, created_at, updated_at)
SELECT
    (SELECT member_id FROM member WHERE id = 'PostUser'),
    title,
    content,
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




INSERT INTO study.post_comment
( member_id, parent_comment_id, post_id, content, created_at, updated_at)
VALUES 
(   
    (SELECT member_id FROM member WHERE id = 'PostUser'),
    null,
    (select  post_id from post
    where title  = 'post1'),
    'comment1',
    DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f'),
    DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f')
);

INSERT INTO study.post_comment
(member_id, parent_comment_id, post_id, content, created_at, updated_at)
VALUES 
(
    (SELECT member_id FROM member WHERE id = 'PostUser'),
    (SELECT comment_id FROM (SELECT comment_id FROM post_comment WHERE content = 'comment1') AS tmp),
    ( SELECT post_id FROM post WHERE title = 'post1'),
    'reply1',
    DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f'),
    DATE_FORMAT(NOW(6), '%Y%m%d24%H%i%s%f')
);
