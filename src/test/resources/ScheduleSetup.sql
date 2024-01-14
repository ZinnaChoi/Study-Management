INSERT INTO schedule (event_name, start_time, end_time)
SELECT 'AM1', '202301111710', '202301111710'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM schedule WHERE event_name = 'AM1'
);
