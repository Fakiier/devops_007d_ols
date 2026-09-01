CREATE TABLE productos (
    id            BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100)  NOT NULL UNIQUE,
    precio        DECIMAL(10,2) NOT NULL,
    categoria     VARCHAR(30)   NOT NULL,
    descripcion   TEXT,
    stock         INT DEFAULT 0,

    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                  ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_categoria (categoria),
    INDEX idx_nombre (nombre)
);