-- ============================================================
-- LIMPEZA (dos filhos para os pais, para nao violar chaves estrangeiras)
-- ============================================================
DELETE FROM Fingerprint_Frame;
DELETE FROM Fingerprint;
DELETE FROM Upload;
DELETE FROM Usuario_Missao;
DELETE FROM Pagamento_Conta;
DELETE FROM Resgate;
DELETE FROM Usuario_Selo;
DELETE FROM Posicao_Ranking;
DELETE FROM Perfil_Sustentavel;
DELETE FROM Sequencia;
DELETE FROM Carteira;

COMMIT;


-- ============================================================
-- REINICIA OS IDs IDENTITY EM 1
-- (DELETE nao reinicia a sequence; necessario Oracle 18c ou superior)
-- ============================================================
ALTER TABLE Fingerprint_Frame MODIFY (id_fingerprint_frame GENERATED ALWAYS AS IDENTITY (RESTART START WITH 1));
ALTER TABLE Fingerprint       MODIFY (id_fingerprint       GENERATED ALWAYS AS IDENTITY (RESTART START WITH 1));
ALTER TABLE Upload            MODIFY (id_upload            GENERATED ALWAYS AS IDENTITY (RESTART START WITH 1));
ALTER TABLE Usuario_Missao    MODIFY (id_usuario_missao    GENERATED ALWAYS AS IDENTITY (RESTART START WITH 1));
ALTER TABLE Pagamento_Conta   MODIFY (id_pagamento         GENERATED ALWAYS AS IDENTITY (RESTART START WITH 1));
ALTER TABLE Resgate           MODIFY (id_resgate           GENERATED ALWAYS AS IDENTITY (RESTART START WITH 1));
ALTER TABLE Usuario_Selo      MODIFY (id_usuario_selo      GENERATED ALWAYS AS IDENTITY (RESTART START WITH 1));
ALTER TABLE Posicao_Ranking   MODIFY (id_posicao           GENERATED ALWAYS AS IDENTITY (RESTART START WITH 1));


-- ============================================================
-- CARGA (dos pais para os filhos)
-- ============================================================

-- ------------------------------------------------------------
-- UPLOAD (10)  ids 1..10
-- ------------------------------------------------------------
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, id_usuario, id_missao) VALUES ('APROVADO', DATE '2026-09-05', DATE '2026-09-05', 'upload_001.mp4', 'Vídeo compatível com a missão.', 1, 1);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, id_usuario, id_missao) VALUES ('APROVADO', DATE '2026-09-07', DATE '2026-09-07', 'upload_002.mp4', 'Vídeo compatível com a missão.', 1, 1);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, id_usuario, id_missao) VALUES ('APROVADO', DATE '2026-09-08', DATE '2026-09-08', 'upload_003.mp4', 'Vídeo demonstra a entrega das pilhas.', 2, 2);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, id_usuario, id_missao) VALUES ('PARCIALMENTE_APROVADO', DATE '2026-09-10', DATE '2026-09-10', 'upload_004.mp4', 'Parte dos requisitos foi identificada no vídeo.', 3, 4);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, id_usuario, id_missao) VALUES ('REPROVADO', DATE '2026-09-12', DATE '2026-09-12', 'upload_005.mp4', 'Não foi possível confirmar a realização da missão.', 4, 4);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, id_usuario, id_missao) VALUES ('EM_ANALISE', DATE '2026-09-15', NULL, 'upload_006.mp4', NULL, 5, 10);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, id_usuario, id_missao) VALUES ('APROVADO', DATE '2026-09-17', DATE '2026-09-17', 'upload_007.mp4', 'Vídeo demonstra a destinação dos resíduos para compostagem.', 6, 13);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, id_usuario, id_missao) VALUES ('APROVADO', DATE '2026-09-18', DATE '2026-09-18', 'upload_008.mp4', 'Vídeo demonstra a doação dos alimentos.', 8, 19);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, id_usuario, id_missao) VALUES ('APROVADO', DATE '2026-09-19', DATE '2026-09-19', 'upload_009.mp4', 'Vídeo demonstra a organização e entrega dos materiais.', 10, 25);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, id_usuario, id_missao) VALUES ('REPROVADO', DATE '2026-09-20', DATE '2026-09-20', 'upload_010.mp4', 'Vídeo identificado como duplicado.', 10, 25);


-- ------------------------------------------------------------
-- FINGERPRINT (10), um por upload (ids 1..10)
-- O upload 10 (reprovado) repete o fingerprint do upload 9 (duplicado)
-- ------------------------------------------------------------
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff', DATE '2026-09-05', 1);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00', DATE '2026-09-07', 2);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('2233445566778899aabbccddeeff00112233445566778899aabbccddeeff0011', DATE '2026-09-08', 3);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('33445566778899aabbccddeeff00112233445566778899aabbccddeeff001122', DATE '2026-09-10', 4);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233', DATE '2026-09-12', 5);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('5566778899aabbccddeeff00112233445566778899aabbccddeeff0011223344', DATE '2026-09-15', 6);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('66778899aabbccddeeff00112233445566778899aabbccddeeff001122334455', DATE '2026-09-17', 7);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566', DATE '2026-09-18', 8);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('8899aabbccddeeff00112233445566778899aabbccddeeff0011223344556677', DATE '2026-09-19', 9);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('8899aabbccddeeff00112233445566778899aabbccddeeff0011223344556677', DATE '2026-09-20', 10);


-- ------------------------------------------------------------
-- FINGERPRINT_FRAME (30), 3 frames por fingerprint
-- ------------------------------------------------------------
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, 'a1b2c3d4e5f60718', NULL, 1);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, 'b2c3d4e5f6071829', NULL, 1);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (3, 3, 'c3d4e5f60718293a', NULL, 1);

INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, 'd4e5f60718293a4b', NULL, 2);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, 'e5f60718293a4b5c', NULL, 2);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (3, 3, 'f60718293a4b5c6d', NULL, 2);

INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, '0718293a4b5c6d7e', NULL, 3);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, '18293a4b5c6d7e8f', NULL, 3);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (3, 3, '293a4b5c6d7e8f90', NULL, 3);

INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, '3a4b5c6d7e8f9012', NULL, 4);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, '4b5c6d7e8f901223', NULL, 4);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (3, 3, '5c6d7e8f90122334', NULL, 4);

INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, '6d7e8f9012233445', NULL, 5);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, '7e8f901223344556', NULL, 5);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (3, 3, '8f90122334455667', NULL, 5);

INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, '9012233445566778', NULL, 6);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, '1223344556677889', NULL, 6);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (3, 3, '233445566778899a', NULL, 6);

INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, '3445566778899aab', NULL, 7);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, '45566778899aabbc', NULL, 7);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (3, 3, '566778899aabbccd', NULL, 7);

INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, '6778899aabbccdde', NULL, 8);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, '78899aabbccddeef', NULL, 8);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (3, 3, '899aabbccddeeff0', NULL, 8);

INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, '9aabbccddeeff001', NULL, 9);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, 'abbccddeeff00112', NULL, 9);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (3, 3, 'bccddeeff0011223', NULL, 9);

INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, '9aabbccddeeff001', NULL, 10);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, 'abbccddeeff00112', NULL, 10);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (3, 3, 'bccddeeff0011223', NULL, 10);


-- ------------------------------------------------------------
-- USUARIO_MISSAO (10)
-- ------------------------------------------------------------
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (2, 10, 1, 1);
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 5, 2, 2);
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 0, 3, 4);
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 0, 4, 4);
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 0, 5, 10);
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 2, 6, 13);
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 5, 8, 19);
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (2, 100, 10, 25);
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 3, 4, 28);
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 2, 8, 29);


-- ------------------------------------------------------------
-- USUARIO_SELO (15)
-- ------------------------------------------------------------
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-03', 1, 1);
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-20', 1, 2);
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-05', 2, 1);
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-08', 3, 1);
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-10', 4, 2);
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-12', 5, 3);
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-14', 6, 2);
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-16', 7, 1);
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-18', 8, 3);
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-20', 9, 1);
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-22', 10, 4);
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-23', 11, 1);
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-24', 12, 2);
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-25', 13, 3);
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-26', 13, 1);


-- ------------------------------------------------------------
-- POSICAO_RANKING: ranking 3 (individual, 13 posicoes) e ranking 4 (grupal, 6 posicoes)
-- ------------------------------------------------------------
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (1, 100, 3, 1, NULL);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (2, 95, 3, 2, NULL);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (3, 90, 3, 8, NULL);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (4, 85, 3, 3, NULL);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (5, 80, 3, 10, NULL);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (6, 75, 3, 4, NULL);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (7, 70, 3, 5, NULL);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (8, 65, 3, 6, NULL);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (9, 60, 3, 11, NULL);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (10, 55, 3, 7, NULL);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (11, 50, 3, 13, NULL);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (12, 45, 3, 9, NULL);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (13, 40, 3, 12, NULL);

INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (1, 28, 4, NULL, 5);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (2, 24, 4, NULL, 6);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (3, 21, 4, NULL, 2);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (4, 19, 4, NULL, 4);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (5, 13, 4, NULL, 1);
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (6, 10, 4, NULL, 3);


-- ------------------------------------------------------------
-- RESGATE (5)
-- ------------------------------------------------------------
INSERT INTO Resgate (dt_concessao, id_usuario, id_recompensa) VALUES (DATE '2026-08-25', 1, 5);
INSERT INTO Resgate (dt_concessao, id_usuario, id_recompensa) VALUES (DATE '2026-08-24', 2, 4);
INSERT INTO Resgate (dt_concessao, id_usuario, id_recompensa) VALUES (DATE '2026-08-23', 3, 3);
INSERT INTO Resgate (dt_concessao, id_usuario, id_recompensa) VALUES (DATE '2026-08-22', 4, 2);
INSERT INTO Resgate (dt_concessao, id_usuario, id_recompensa) VALUES (DATE '2026-08-21', 5, 1);


-- ------------------------------------------------------------
-- SEQUENCIA (15)
-- ------------------------------------------------------------
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (1, 8, 5, DATE '2026-05-03', DATE '2026-09-25', 1);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (2, 6, 4, DATE '2026-06-07', DATE '2026-09-23', 2);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (3, 7, 5, DATE '2026-05-12', DATE '2026-09-20', 3);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (4, 5, 4, DATE '2026-06-15', DATE '2026-09-18', 4);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (5, 6, 4, DATE '2026-06-20', DATE '2026-09-21', 5);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (6, 4, 3, DATE '2026-07-10', DATE '2026-09-17', 6);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (7, 5, 3, DATE '2026-07-05', DATE '2026-09-16', 7);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (8, 7, 5, DATE '2026-05-08', DATE '2026-09-26', 8);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (9, 4, 3, DATE '2026-07-18', DATE '2026-09-14', 9);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (10, 6, 4, DATE '2026-06-11', DATE '2026-09-24', 10);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (11, 5, 4, DATE '2026-06-28', DATE '2026-09-19', 11);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (12, 3, 2, DATE '2026-08-03', DATE '2026-09-12', 12);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (13, 4, 3, DATE '2026-07-22', DATE '2026-09-15', 13);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (14, 0, 0, NULL, NULL, 14);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (15, 0, 0, NULL, NULL, 15);


-- ------------------------------------------------------------
-- PERFIL_SUSTENTAVEL (15)
-- ------------------------------------------------------------
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (1, 920, 20, 1500, 18, 92, 45, 5, 1);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (2, 760, 16, 1200, 12, 75, 38, 4, 2);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (3, 540, 13, 850, 10, 61, 29, 4, 3);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (4, 430, 11, 700, 9, 52, 24, 3, 4);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (5, 390, 10, 620, 8, 48, 21, 3, 5);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (6, 320, 8, 510, 7, 41, 18, 3, 6);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (7, 280, 7, 450, 6, 38, 16, 3, 7);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (8, 610, 14, 980, 13, 72, 36, 4, 8);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (9, 220, 6, 360, 5, 29, 12, 2, 9);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (10, 510, 12, 840, 11, 63, 31, 4, 10);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (11, 350, 9, 560, 8, 45, 19, 3, 11);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (12, 180, 4, 300, 4, 22, 9, 2, 12);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (13, 260, 6, 420, 6, 34, 14, 3, 13);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (14, 0, 0, 0, 0, 0, 0, 1, 14);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (15, 0, 0, 0, 0, 0, 0, 1, 15);


-- ------------------------------------------------------------
-- CARTEIRA (15)
-- ------------------------------------------------------------
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (1, 80, 0.73, 1);
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (2, 65, 0.60, 2);
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (3, 50, 0.46, 3);
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (4, 40, 0.37, 4);
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (5, 35, 0.32, 5);
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (6, 60, 0.55, 6);
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (7, 30, 0.28, 7);
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (8, 90, 0.83, 8);
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (9, 45, 0.41, 9);
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (10, 120, 1.10, 10);
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (11, 70, 0.64, 11);
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (12, 55, 0.51, 12);
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (13, 100, 0.92, 13);
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (14, 0, 0.00, 14);
INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) VALUES (15, 0, 0.00, 15);

COMMIT;