
--
-- Accounts Data
--

-- User Profile
-- password 'test': $2a$10$W5YdtLrCN.3dH8hilF2queEvfJedIhzSEzszgcjJ8e/NrWBCURIUW
CREATE TABLE user_profile (
  id            INTEGER PRIMARY KEY,
  nickname      CHAR(32) NOT NULL,
  email         CHAR(64) NOT NULL,
  password_hash CHAR(128) NOT NULL,
  created       TIMESTAMP NOT NULL,
  CONSTRAINT uq_user_profile_nickname UNIQUE (nickname),
  CONSTRAINT uq_user_profile_email UNIQUE (email)
);

CREATE SEQUENCE seq_user_profile      START WITH 1000;

-- Roles
CREATE TABLE role (
  id            INTEGER PRIMARY KEY,
  role_name     CHAR(32) NOT NULL,
  CONSTRAINT uq_role_name UNIQUE (role_name)
);

INSERT INTO role (id, role_name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO role (id, role_name) VALUES (20, 'ROLE_USER');

-- User Roles
CREATE TABLE user_role (
  user_id       INTEGER NOT NULL,
  role_id       INTEGER NOT NULL,
  CONSTRAINT pk_user_role PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES user_profile(id),
  CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES role(id)
);

--
-- Invitation Tokens
--

CREATE TABLE invitation_token (
  id            INTEGER PRIMARY KEY,
  code          CHAR(64) NOT NULL,
  note          VARCHAR(256) NOT NULL,
  CONSTRAINT uq_invitation_token_code UNIQUE (code)
);

--
-- Personalization Stuff
--

CREATE CONSTANT c_entity_kind_author            VALUE 200;
CREATE CONSTANT c_entity_kind_book              VALUE 201;

CREATE TABLE favorite (
  id            INTEGER PRIMARY KEY,
  user_id       INTEGER NOT NULL,
  entity_id     INTEGER NOT NULL, -- denormalized value - refers to either author(id) or book_meta(id)
  entity_kind   INTEGER NOT NULL,
  CONSTRAINT fk_favorite_user_id FOREIGN KEY (user_id) REFERENCES user_profile(id)
);
