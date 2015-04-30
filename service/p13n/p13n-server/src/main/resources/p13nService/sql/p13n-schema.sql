--
-- Tables
--

-- Favorites Support

CREATE TABLE favorites (
  ext_id            INTEGER NOT NULL,
  ext_type_id       INTEGER NOT NULL,
  CONSTRAINT pk_favorites PRIMARY KEY (ext_id, ext_type_id)
);

--
-- Indexes
--
