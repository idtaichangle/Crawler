CREATE TABLE alive_proxy
(
id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
proxy VARCHAR(32) NOT NULL,
CONSTRAINT primary_key PRIMARY KEY (id)
)