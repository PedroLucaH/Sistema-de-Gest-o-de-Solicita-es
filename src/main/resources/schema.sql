DROP TABLE IF EXISTS solicitacao;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS solicitante;

CREATE TABLE solicitante (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             nome VARCHAR(255) NOT NULL,
                             cpf_cnpj VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE categoria (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           nome VARCHAR(100) NOT NULL
);

CREATE TABLE solicitacao (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             solicitante_id BIGINT NOT NULL,
                             categoria_id BIGINT NOT NULL,
                             descricao TEXT NOT NULL,
                             valor DECIMAL(10,2) NOT NULL,
                             data_solicitacao TIMESTAMP NOT NULL,
                             status VARCHAR(20) NOT NULL,
                             CONSTRAINT fk_solicitante FOREIGN KEY (solicitante_id) REFERENCES solicitante(id),
                             CONSTRAINT fk_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);