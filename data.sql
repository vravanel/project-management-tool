CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username varchar(150) NOT NULL,
    email varchar(150) UNIQUE NOT NULL,
    password varchar(255) NOT NULL
);

CREATE TABLE project (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    creator_id BIGINT NOT NULL,
    start_date date NOT NULL,
    CONSTRAINT fk_project_users FOREIGN KEY (creator_id) REFERENCES users(id)
);

CREATE TABLE project_member (
   id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    role VARCHAR(100) NOT NULL CHECK (role IN ('ADMINISTRATEUR', 'MEMBRE', 'OBSERVATEUR')),
    CONSTRAINT fk_project_member_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_project_member_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    CONSTRAINT uq_project_member_user_project UNIQUE (user_id, project_id)
);

CREATE TABLE task (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(100) NOT NULL CHECK (priority in ('BASSE', 'MOYENNE', 'HAUTE')),
    status VARCHAR(100) NOT NULL CHECK (status in ('A FAIRE', 'EN COURS', 'FAIT')),
    due_date DATE NOT NULL,
    end_date DATE,
    user_id BIGINT,
    project_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_task_users FOREIGN KEY (user_id) REFERENCES users(id)  ON DELETE SET NULL,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

CREATE TABLE task_history (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    task_id BIGINT NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    field_value_old TEXT,
    field_value_new TEXT,
    modified_at TIMESTAMP NOT NULL,
    user_id BIGINT,
    CONSTRAINT fk_task_history_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_task_history_task FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE
);

CREATE TABLE notification (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    type VARCHAR(150) NOT NULL,
    sent_at TIMESTAMP NOT NULL,
    is_read BOOLEAN NOT NULL,
    CONSTRAINT fk_notification_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notification_task FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE 
);

INSERT INTO users (username, email, password) VALUES
('alice', 'alice@example.com', '$2y$10$hashedpassword1'),
('bob', 'bob@example.com', '$2y$10$hashedpassword2'),
('charlie', 'charlie@example.com', '$2y$10$hashedpassword3');

INSERT INTO project (title, description, creator_id, start_date) VALUES
('Refonte site web', 'Migration du site vitrine vers le nouveau design', 1, '2026-01-15'),
('App mobile interne', 'Développement de l''application de gestion des congés', 2, '2026-02-01');

INSERT INTO project_member (user_id, project_id, role) VALUES
(1, 1, 'ADMINISTRATEUR'),
(2, 1, 'MEMBRE'),
(3, 1, 'OBSERVATEUR'),
(2, 2, 'ADMINISTRATEUR'),
(1, 2, 'MEMBRE');

-- TASKS
INSERT INTO task (name, description, priority, status, due_date, end_date, user_id, project_id, created_at, updated_at) VALUES
('Maquettes UI', 'Créer les maquettes des pages principales', 'HAUTE', 'EN COURS', '2026-03-01', NULL, 2, 1, '2026-01-16 09:00:00', '2026-01-20 14:30:00'),
('Mise en place CI/CD', 'Configurer le pipeline de déploiement', 'MOYENNE', 'A FAIRE', '2026-03-15', NULL, 1, 1, '2026-01-17 10:00:00', NULL),
('Spécifications fonctionnelles', 'Rédiger le cahier des charges de l''app', 'HAUTE', 'FAIT', '2026-02-10', '2026-02-09', 1, 2, '2026-02-02 08:30:00', '2026-02-09 17:00:00');