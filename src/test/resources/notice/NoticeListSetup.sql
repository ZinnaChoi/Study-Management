INSERT INTO study.`member`
(contact, created_at, updated_at, id, name, password, `role`)
VALUES
('010-1111-2222', '20240117122007395', '20240112222007395', 'noticeUser1', 'noticeUser1', '$2ahjFyW8werwefffgdX49fdgdfvxxcxh087845645667g', 'USER'),
('010-1111-3333', '20240117122007316', '20240112222007495', 'noticeUser2', 'noticeUser2', '$2ahjFwerwerOdyVODxN/lOMp;[sdsdcdcsdfsewere34m*$', 'USER');


INSERT INTO study.`notice` (notice_id, absent, last_share_date, link_share, member_id, new_post, wakeup)
VALUES (
    1,
    1,
    '20240116',
    1,
    (SELECT member_id FROM study.`member` WHERE id = 'noticeUser1'),
    1,
    1
);


INSERT INTO study.`notice` (notice_id, absent, last_share_date, link_share, member_id, new_post, wakeup)
VALUES (
    2,
    1,
    '20240117',
    1,
    (SELECT member_id FROM study.`member` WHERE id = 'noticeUser2'),
    1,
    1
);

