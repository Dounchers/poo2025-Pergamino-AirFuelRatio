CREATE TABLE usuario (
  id SERIAL PRIMARY KEY,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  user_type VARCHAR(31) NOT NULL
);

CREATE TABLE torneo (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  date_start DATE NOT NULL,
  date_end DATE NOT NULL,
  publish BOOLEAN NOT NULL DEFAULT FALSE,
  administrador_id INTEGER NOT NULL,
  FOREIGN KEY (administrador_id) REFERENCES usuario(id)
);

CREATE TABLE competencia (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  base_price DECIMAL(10,2) NOT NULL,
  capacity INTEGER NOT NULL,
  torneo_id INTEGER NOT NULL,
  FOREIGN KEY (torneo_id) REFERENCES torneo(id)
);

CREATE TABLE inscripcion (
  id SERIAL PRIMARY KEY,
  participante_id INTEGER NOT NULL,
  competencia_id INTEGER NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  date_enrollment DATE NOT NULL,
  FOREIGN KEY (participante_id) REFERENCES usuario(id),
  FOREIGN KEY (competencia_id) REFERENCES competencia(id),
  UNIQUE (participante_id, competencia_id)
)