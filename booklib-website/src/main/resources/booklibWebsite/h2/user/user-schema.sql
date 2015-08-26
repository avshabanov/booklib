
--
-- Personalization Stuff
--

CREATE TABLE favorite (
  user_id       INTEGER NOT NULL,
  entity_id     INTEGER NOT NULL, -- denormalized value - refers to either author(id) or book_meta(id)
  entity_kind   INTEGER NOT NULL,
  CONSTRAINT pk_favorite PRIMARY KEY (user_id, entity_id, entity_kind)
);
