CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(100) NOT NULL,
    first_name VARCHAR(50),
    last_name  VARCHAR(50),

    login      VARCHAR(20) UNIQUE,
    role       VARCHAR(50)  NOT NULL,
    password   VARCHAR(250) NOT NULL
);

CREATE TABLE events
(
    id                BIGSERIAL PRIMARY KEY,
    title             VARCHAR(150) NOT NULL,
    description       VARCHAR(150) NOT NULL,
    date_time         TIMESTAMPTZ    NOT NULL,
    total_tickets     BIGINT       NOT NULL,
    available_tickets BIGINT       NOT NULL,
    cover_url         VARCHAR(100)


);

CREATE TABLE bookings
(
    id             BIGSERIAL PRIMARY KEY,
    customer_email VARCHAR(100) NOT NULL,
    ticket_count   BIGINT       NOT NULL,
    confirmed      BOOLEAN      NOT NULL,
    created_at     TIMESTAMPTZ    NOT NULL,
    expiry_time    TIMESTAMPTZ,
    event_id       BIGINT         NOT NULL,

    CONSTRAINT fk_bookings_event FOREIGN KEY (event_id) REFERENCES events (id)

);


