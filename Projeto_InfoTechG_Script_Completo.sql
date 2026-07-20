/* ==============================================================
   PROJETO INTEGRADOR: SISTEMA INFOTECHG - SCRIPT FINAL
   Responsável: Jonas Henrique
   Etapa 4: inclui tabela Usuarios (login) e as colunas de
   responsável (vendedor/técnico) em Vendas e Ordens_Servico.
   ============================================================== */

/* 1. LIMPEZA INICIAL (O "NUKE")
   Garante que o banco seja recriado do zero a cada execução.
*/
DROP SCHEMA IF EXISTS `mydb`;
SET SQL_SAFE_UPDATES = 0;


SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

/* 3. CRIAÇÃO DO BANCO E TABELAS (DDL) */

-- Criando o Schema
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- Tabela 0: Usuarios (login do sistema - RF001)
CREATE TABLE IF NOT EXISTS `mydb`.`Usuarios` (
  `idUsuario` INT NOT NULL AUTO_INCREMENT,
  `login` VARCHAR(50) NOT NULL,
  `senha_hash` VARCHAR(255) NOT NULL,
  `nome_completo` VARCHAR(100) NULL,
  `tipo_perfil` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`idUsuario`),
  UNIQUE INDEX `uq_Usuarios_login` (`login` ASC) VISIBLE)
ENGINE = InnoDB;

-- Tabela 1: Clientes
CREATE TABLE IF NOT EXISTS `mydb`.`Clientes` (
  `idCliente` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(100) NULL,
  `telefone` VARCHAR(20) NULL,
  `email` VARCHAR(100) NULL,
  PRIMARY KEY (`idCliente`))
ENGINE = InnoDB;

-- Tabela 2: Fornecedores
CREATE TABLE IF NOT EXISTS `mydb`.`Fornecedores` (
  `idFornecedor` INT NOT NULL AUTO_INCREMENT,
  `nome_empresa` VARCHAR(100) NULL,
  `telefone_contato` VARCHAR(20) NULL,
  `email_contato` VARCHAR(100) NULL,
  PRIMARY KEY (`idFornecedor`))
ENGINE = InnoDB;

-- Tabela 3: Produtos (Ligada a Fornecedores)
CREATE TABLE IF NOT EXISTS `mydb`.`Produtos` (
  `idProduto` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(100) NULL,
  `preco` DECIMAL(10,2) NULL,
  `quantidade_estoque` INT NULL,
  `Fornecedores_idFornecedor` INT NOT NULL,
  PRIMARY KEY (`idProduto`),
  INDEX `fk_Produtos_Fornecedores1_idx` (`Fornecedores_idFornecedor` ASC) VISIBLE,
  CONSTRAINT `fk_Produtos_Fornecedores1`
    FOREIGN KEY (`Fornecedores_idFornecedor`)
    REFERENCES `mydb`.`Fornecedores` (`idFornecedor`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- Tabela 4: Vendas (Ligada a Clientes, Produtos e ao Usuario vendedor responsavel)
CREATE TABLE IF NOT EXISTS `mydb`.`Vendas` (
  `idVenda` INT NOT NULL AUTO_INCREMENT,
  `data_venda` DATETIME NULL,
  `quantidade_vendida` INT NULL,
  `valor_total_venda` DECIMAL(10,2) NULL,
  `Clientes_idCliente` INT NOT NULL,
  `Produtos_idProduto` INT NOT NULL,
  `Usuarios_idUsuario` INT NOT NULL,
  PRIMARY KEY (`idVenda`),
  INDEX `fk_Vendas_Clientes_idx` (`Clientes_idCliente` ASC) VISIBLE,
  INDEX `fk_Vendas_Produtos1_idx` (`Produtos_idProduto` ASC) VISIBLE,
  INDEX `fk_Vendas_Usuarios1_idx` (`Usuarios_idUsuario` ASC) VISIBLE,
  CONSTRAINT `fk_Vendas_Clientes`
    FOREIGN KEY (`Clientes_idCliente`)
    REFERENCES `mydb`.`Clientes` (`idCliente`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Vendas_Produtos1`
    FOREIGN KEY (`Produtos_idProduto`)
    REFERENCES `mydb`.`Produtos` (`idProduto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Vendas_Usuarios1`
    FOREIGN KEY (`Usuarios_idUsuario`)
    REFERENCES `mydb`.`Usuarios` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- Tabela 5: Ordens_Servico (Ligada a Clientes e ao Usuario tecnico responsavel)
CREATE TABLE IF NOT EXISTS `mydb`.`Ordens_Servico` (
  `idOS` INT NOT NULL AUTO_INCREMENT,
  `data_abertura` DATETIME NULL,
  `defeito_relatado` VARCHAR(255) NULL,
  `status_servico` VARCHAR(45) NULL,
  `Clientes_idCliente` INT NOT NULL,
  `Usuarios_idUsuario` INT NOT NULL,
  PRIMARY KEY (`idOS`),
  INDEX `fk_Ordens_Servico_Clientes1_idx` (`Clientes_idCliente` ASC) VISIBLE,
  INDEX `fk_Ordens_Servico_Usuarios1_idx` (`Usuarios_idUsuario` ASC) VISIBLE,
  CONSTRAINT `fk_Ordens_Servico_Clientes1`
    FOREIGN KEY (`Clientes_idCliente`)
    REFERENCES `mydb`.`Clientes` (`idCliente`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Ordens_Servico_Usuarios1`
    FOREIGN KEY (`Usuarios_idUsuario`)
    REFERENCES `mydb`.`Usuarios` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

/* Restaurando configurações de segurança */
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;



-- Inserindo os 3 Usuarios de teste (login/senha usados nas Etapas 1-3).
-- senha_hash = SHA-256(salt + senha), formato "saltHex:hashHex" (ver infotechg.util.SenhaUtil).
-- Senhas de teste: gerente123 / vendedor123 / tecnico123.
INSERT INTO `mydb`.`Usuarios` (login, senha_hash, nome_completo, tipo_perfil) VALUES
('gerente', '3f8186638d07a1ac902b237630f4c43d:d6de57d58e0c31caab400ad4a5c9e7feda35c08ee2712279073bd5bdd40b54d4', 'Jonas', 'Gerente'),
('vendedor', 'eab6b619b0efe89f75ea8b3dce9c4a3a:4614c754c0c0edd71164a6f683bff4cea495553cf53e42fb2f992806ece8ede6', 'Maria', 'Vendedor'),
('tecnico', 'ec7e2af543f80cf3f5d822400ab13cd3:ecca2042064099a7cd7116cebf79133ff4a6e42620d3344dfa7bb918a6f70ace', 'Carlos', 'Tecnico');

-- Inserindo 5 Clientes
INSERT INTO `mydb`.`Clientes` (nome, telefone, email) VALUES
('Jorge Santos', '(21) 99999-0001', 'jorge.s@email.com'),
('Maria Silva', '(11) 98888-0002', 'maria.silva@email.com'),
('Carlos Pereira', '(31) 97777-0003', 'carlos.p@email.com'),
('Ana Beatriz', '(41) 96666-0004', 'ana.beatriz@email.com'),
('Ricardo Almeida', '(51) 95555-0005', 'ricardo.a@email.com');

-- Inserindo 5 Fornecedores
INSERT INTO `mydb`.`Fornecedores` (nome_empresa, telefone_contato, email_contato) VALUES
('TechDistro Brasil', '(11) 5555-1010', 'vendas@techdistro.com'),
('PC Peças Atacado', '(41) 4444-2020', 'contato@pcpecas.com'),
('ImportaInfo', '(21) 3333-3030', 'comercial@importainfo.com'),
('Mega Hardware SA', '(11) 2222-4040', 'sac@megahardware.com'),
('Suprimentos TI Ltda', '(51) 1111-5050', 'pedidos@suprimentos.com');

-- Inserindo 5 Produtos
INSERT INTO `mydb`.`Produtos` (nome, preco, quantidade_estoque, Fornecedores_idFornecedor) VALUES
('Placa de Vídeo RTX 4060', 2500.00, 10, 1),
('Mouse Gamer XYZ', 150.00, 50, 2),
('Teclado Mecânico Red', 350.00, 30, 1),
('Monitor 24" 144Hz', 1200.00, 15, 3),
('SSD 1TB NVMe', 450.00, 0, 4);

-- Inserindo 5 Vendas (Usuarios_idUsuario = 1 -> gerente, responsavel pelas vendas de exemplo)
INSERT INTO `mydb`.`Vendas` (data_venda, quantidade_vendida, valor_total_venda, Clientes_idCliente, Produtos_idProduto, Usuarios_idUsuario) VALUES
('2025-11-07 10:30:00', 1, 2500.00, 1, 1, 1),
('2025-11-07 11:15:00', 2, 300.00, 2, 2, 2),
('2025-11-06 14:00:00', 1, 1200.00, 1, 4, 2),
('2025-11-06 16:45:00', 1, 450.00, 3, 5, 1),
('2025-11-05 09:12:00', 1, 350.00, 4, 3, 2);

-- Inserindo 5 Ordens de Serviço (Usuarios_idUsuario = 3 -> tecnico responsavel)
-- status_servico grava o nome do enum StatusOS (EM_ANALISE / AGUARDANDO_PECA / CONCLUIDO / ENTREGUE)
INSERT INTO `mydb`.`Ordens_Servico` (data_abertura, defeito_relatado, status_servico, Clientes_idCliente, Usuarios_idUsuario) VALUES
('2025-11-07 09:00:00', 'Computador não liga', 'AGUARDANDO_PECA', 2, 3),
('2025-11-07 12:15:00', 'Limpeza geral', 'EM_ANALISE', 5, 3),
('2025-11-06 15:00:00', 'Notebook tela quebrada', 'CONCLUIDO', 1, 3),
('2025-11-05 10:00:00', 'Formatação', 'ENTREGUE', 3, 3),
('2025-11-05 11:30:00', 'Mouse com defeito', 'EM_ANALISE', 2, 3);


/* 5. OPERAÇÕES FINAIS (SELECT, UPDATE, DELETE) - validação manual do script */

-- 5.1 CONSULTAS (SELECT)
SELECT * FROM `mydb`.`Usuarios`;
SELECT * FROM `mydb`.`Clientes`;
SELECT * FROM `mydb`.`Vendas`;

-- Consultas com Filtro (WHERE)
SELECT * FROM `mydb`.`Produtos` WHERE idProduto = 4;
SELECT * FROM `mydb`.`Ordens_Servico` WHERE status_servico = 'EM_ANALISE';

-- 5.2 ATUALIZAÇÃO (UPDATE)
UPDATE `mydb`.`Ordens_Servico` SET status_servico = 'CONCLUIDO' WHERE idOS = 2;
UPDATE `mydb`.`Produtos` SET preco = 175.00 WHERE idProduto = 2;

-- 5.3 EXCLUSÃO (DELETE)
/* Truque: Criar um cliente temporário só para apagar, evitando erro 1451 de FK */
INSERT INTO `mydb`.`Clientes` (nome, telefone, email) VALUES ('Cliente Para Deletar', '000', 'del@test.com');
DELETE FROM `mydb`.`Clientes` WHERE nome = 'Cliente Para Deletar';

/* Prova Final: Listar clientes (O 'Cliente Para Deletar' não deve aparecer) */
SELECT * FROM `mydb`.`Clientes`;
