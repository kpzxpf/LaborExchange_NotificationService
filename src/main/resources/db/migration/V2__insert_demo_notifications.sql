-- Stored notifications for demo inboxes and unread counters.

INSERT INTO notifications (id, user_id, type, message, is_read, created_at)
VALUES
    (100, 121, 'NEW_APPLICATION', 'Новый отклик: Александр Петров откликнулся на Senior Java/Kafka Backend Developer.', FALSE, NOW() - INTERVAL '9 days'),
    (101, 102, 'ACCEPTED_APPLICATION', 'Ваш отклик на Middle React/Next.js Frontend Engineer принят. Работодатель готов обсудить оффер.', FALSE, NOW() - INTERVAL '2 days'),
    (102, 105, 'NEW_APPLICATION', 'Ваш отклик на DevOps/SRE Engineer отправлен работодателю.', TRUE, NOW() - INTERVAL '7 days'),
    (103, 103, 'ACCEPTED_APPLICATION', 'FinPulse принял ваш отклик на Data Engineer ClickHouse/Airflow.', FALSE, NOW() - INTERVAL '1 day'),
    (104, 106, 'REJECTED_APPLICATION', 'MedCloud отклонил отклик на Python ML Engineer. Попробуйте обновить резюме и откликнуться позже.', FALSE, NOW() - INTERVAL '3 days'),
    (105, 101, 'WITHDRAWN_APPLICATION', 'Отклик на Kotlin Backend Intern был отозван.', TRUE, NOW() - INTERVAL '2 days'),
    (106, 125, 'NEW_APPLICATION', 'Новый отклик: Мария Соколова откликнулась на HR Tech Project Manager.', FALSE, NOW() - INTERVAL '2 days'),
    (107, 102, 'ACCEPTED_APPLICATION', 'RetailCore принял ваш отклик на HR Tech Project Manager.', FALSE, NOW() - INTERVAL '12 hours')
ON CONFLICT (id) DO UPDATE SET
    user_id = EXCLUDED.user_id,
    type = EXCLUDED.type,
    message = EXCLUDED.message,
    is_read = EXCLUDED.is_read,
    created_at = EXCLUDED.created_at;

SELECT setval(pg_get_serial_sequence('notifications', 'id'), COALESCE((SELECT MAX(id) FROM notifications), 1), TRUE);
