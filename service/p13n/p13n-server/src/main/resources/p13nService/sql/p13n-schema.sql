--
-- Tables
--

-- Favorites Support

CREATE TABLE favorites (
  user_id           INTEGER NOT NULL,
  ext_id            INTEGER NOT NULL,
  ext_type_id       INTEGER NOT NULL,
  CONSTRAINT pk_favorites PRIMARY KEY (user_id, ext_id, ext_type_id)
);

--
-- Indexes
--
