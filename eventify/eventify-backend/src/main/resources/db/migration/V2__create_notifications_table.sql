CREATE TABLE notifications
(
    id                  BIGSERIAL PRIMARY KEY,
    notify_new_events   BOOLEAN,
    notify_upcoming     BOOLEAN,
    notify_before_hours BIGINT,
    user_id             UUID NOT NULL
)
