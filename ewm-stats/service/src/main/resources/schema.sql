CREATE TABLE IF NOT EXISTS apps
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY,
    app VARCHAR(255) NOT NULL,
    CONSTRAINT pk_apps PRIMARY KEY (id),
    CONSTRAINT uq_app UNIQUE (app)
);
CREATE TABLE IF NOT EXISTS stats
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app_id    BIGINT,
    uri       VARCHAR(255) NOT NULL,
    ip        VARCHAR(50)  NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_stats PRIMARY KEY (id),
    CONSTRAINT fk_apps FOREIGN KEY (app_id) REFERENCES apps (id)
);