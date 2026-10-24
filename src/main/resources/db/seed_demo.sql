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
-- UPLOAD (8)  ids 1..8
-- ============================================================
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, nr_confianca_duplicidade, id_usuario, id_missao) VALUES ('APROVADO', DATE '2026-08-05', DATE '2026-08-05', 'upload_001.mp4', 'Vídeo demonstra a organização e entrega dos materiais.', 3.5, 1, 25);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, nr_confianca_duplicidade, id_usuario, id_missao) VALUES ('APROVADO', DATE '2026-08-06', DATE '2026-08-06', 'upload_002.mp4', 'Vídeo mostra a bicicleta e o percurso de 5 km.', 2.8, 1, 11);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, nr_confianca_duplicidade, id_usuario, id_missao) VALUES ('APROVADO', DATE '2026-08-10', DATE '2026-08-10', 'upload_003.mp4', 'Vídeo demonstra a origem, o armazenamento e a reutilização da água.', 4.1, 2, 17);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, nr_confianca_duplicidade, id_usuario, id_missao) VALUES ('APROVADO', DATE '2026-08-14', DATE '2026-08-14', 'upload_004.mp4', 'Vídeo demonstra a doação dos alimentos.', 1.9, 8, 19);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, nr_confianca_duplicidade, id_usuario, id_missao) VALUES ('APROVADO', DATE '2026-09-06', DATE '2026-09-06', 'upload_005.mp4', 'Vídeo mostra a bicicleta e o percurso de 5 km.', 3.0, 1, 11);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, nr_confianca_duplicidade, id_usuario, id_missao) VALUES ('APROVADO', DATE '2026-09-09', DATE '2026-09-09', 'upload_006.mp4', 'Vídeo mostra a mudança e os dados de redução de 2 kWh.', 2.4, 3, 18);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, nr_confianca_duplicidade, id_usuario, id_missao) VALUES ('REPROVADO', DATE '2026-09-12', DATE '2026-09-12', 'upload_007.mp4', 'Vídeo identificado como duplicado.', 99.2, 2, 17);
INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, nr_confianca_duplicidade, id_usuario, id_missao) VALUES ('EM_ANALISE', DATE '2026-09-15', NULL, 'upload_008.mp4', NULL, NULL, 4, 10);


-- ============================================================
-- FINGERPRINT (8) e FINGERPRINT_FRAME (16)
-- O upload 7 (reprovado) repete o fingerprint do upload 3 (duplicado)
-- ============================================================
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('1a2b3c4d5e6f70811a2b3c4d5e6f70811a2b3c4d5e6f70811a2b3c4d5e6f7081', DATE '2026-08-05', 1);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('2b3c4d5e6f7081922b3c4d5e6f7081922b3c4d5e6f7081922b3c4d5e6f708192', DATE '2026-08-06', 2);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('3c4d5e6f708192a33c4d5e6f708192a33c4d5e6f708192a33c4d5e6f708192a3', DATE '2026-08-10', 3);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('4d5e6f708192a3b44d5e6f708192a3b44d5e6f708192a3b44d5e6f708192a3b4', DATE '2026-08-14', 4);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('5e6f708192a3b4c55e6f708192a3b4c55e6f708192a3b4c55e6f708192a3b4c5', DATE '2026-09-06', 5);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('6f708192a3b4c5d66f708192a3b4c5d66f708192a3b4c5d66f708192a3b4c5d6', DATE '2026-09-09', 6);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('3c4d5e6f708192a33c4d5e6f708192a33c4d5e6f708192a33c4d5e6f708192a3', DATE '2026-09-12', 7);
INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES ('708192a3b4c5d6e7708192a3b4c5d6e7708192a3b4c5d6e7708192a3b4c5d6e7', DATE '2026-09-15', 8);

INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, '9f3a1c5e7b2d4a60', NULL, 1);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, 'a04b2d6f8c3e5b71', NULL, 1);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, 'b15c3e709d4f6c82', NULL, 2);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, 'c26d4f81ae507d93', NULL, 2);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, 'd37e5092bf618ea4', NULL, 3);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, 'e48f61a3c0729fb5', NULL, 3);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, 'f59072b4d183a0c6', NULL, 4);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, '06a183c5e294b1d7', NULL, 4);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, '17b294d6f3a5c2e8', NULL, 5);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, '28c3a5e704b6d3f9', NULL, 5);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, '39d4b6f815c7e40a', NULL, 6);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, '4ae5c70926d8f51b', NULL, 6);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, 'd37e5092bf618ea4', NULL, 7);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, 'e48f61a3c0729fb5', NULL, 7);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (1, 1, '5bf6d81a37e9062c', NULL, 8);
INSERT INTO Fingerprint_Frame (nr_frame, nr_tempo_segundo, ds_phash, ds_embedding, id_fingerprint) VALUES (2, 2, '6c07e92b48fa173d', NULL, 8);


-- ============================================================
-- USUARIO_MISSAO
-- nr_tentativas = uploads enviados (continuas) / uploads que contribuiram (mensais)
-- nr_progresso_atual = soma da metrica dos uploads aprovados (continuas) / progresso da meta (mensais)
-- ============================================================
-- Missoes continuas
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 100, 1, 25);  -- Camila: 100 itens
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (2, 10, 1, 11);   -- Camila: 2 x 5 km
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (2, 50, 2, 17);   -- Enzo: 1 aprovado (50 L) + 1 reprovado
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 5, 8, 19);    -- Gabriel: 5 kg
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 2, 3, 18);    -- Lucas: 2 kWh
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 0, 4, 10);    -- Sophia: em analise (sem progresso)
-- Missoes mensais (setembro)
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 5, 1, 29);    -- Camila: 5 km de 50
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 1, 1, 30);    -- Camila: 1 de 5 missoes de impacto medio
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 1, 1, 34);    -- Camila: 1 de 10 missoes faceis
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 1, 3, 30);    -- Lucas: 1 de 5 missoes de impacto medio
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 1, 3, 36);    -- Lucas: 1 de 5 missoes dificeis
INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) VALUES (1, 2, 3, 37);    -- Lucas: 2 kWh de 10


-- ============================================================
-- USUARIO_SELO (cada selo desconta os pontos necessarios do saldo)
-- ============================================================
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-08', 1, 1);  -- Camila (140 pts ate aqui) -55
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-12', 2, 1);  -- Enzo   (80 pts)           -55
INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (DATE '2026-08-16', 8, 1);  -- Gabriel (60 pts)          -55


-- ============================================================
-- RANKING (ids: 1 Jul indiv, 2 Jul grupal, 3 Ago indiv, 4 Ago grupal, 5 Set indiv, 6 Set grupal)
-- Julho sem posicoes: nao ha uploads aprovados em julho.
-- Grupal = soma dos pontos dos membros. Participam so quem pontuou.
-- ============================================================
-- Agosto individual (fechado)
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (1, 140, 3, 1, NULL);  -- Camila: 100 + 40
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (2, 80, 3, 2, NULL);   -- Enzo: 80
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (3, 60, 3, 8, NULL);   -- Gabriel: 60
-- Agosto grupal (fechado)
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (1, 220, 4, NULL, 1);  -- Eco Exploradores: 140 + 80
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (2, 60, 4, NULL, 4);   -- Impacto Sustentavel: 60
-- Setembro individual (aberto)
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (1, 80, 5, 3, NULL);   -- Lucas: 80
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (2, 40, 5, 1, NULL);   -- Camila: 40
-- Setembro grupal (aberto)
INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) VALUES (1, 120, 6, NULL, 1);  -- Eco Exploradores: 80 + 40


-- ============================================================
-- RESGATE (premios do ranking individual de agosto) e PAGAMENTO_CONTA
-- Uma unica conta paga por usuario
-- ============================================================
INSERT INTO Resgate (dt_concessao, id_usuario, id_recompensa) VALUES (DATE '2026-09-01', 1, 5);  -- 1o: pagamento integral
INSERT INTO Resgate (dt_concessao, id_usuario, id_recompensa) VALUES (DATE '2026-09-01', 2, 4);  -- 2o: 20%
INSERT INTO Resgate (dt_concessao, id_usuario, id_recompensa) VALUES (DATE '2026-09-01', 8, 3);  -- 3o: 15%

INSERT INTO Pagamento_Conta (dt_pagamento, vl_pago, id_usuario, id_conta_luz) VALUES (DATE '2026-09-02', 0.00, 1, 1);     -- 151.23 - 100%
INSERT INTO Pagamento_Conta (dt_pagamento, vl_pago, id_usuario, id_conta_luz) VALUES (DATE '2026-09-03', 207.90, 2, 2);   -- 259.87 - 20%
INSERT INTO Pagamento_Conta (dt_pagamento, vl_pago, id_usuario, id_conta_luz) VALUES (DATE '2026-09-04', 120.79, 8, 3);   -- 142.10 - 15%


-- ============================================================
-- SEQUENCIA (maior sequencia = dias consecutivos com missao aprovada)
-- ============================================================
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (1, 2, 2, DATE '2026-08-05', DATE '2026-09-06', 1);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (2, 1, 1, DATE '2026-08-10', DATE '2026-08-10', 2);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (3, 1, 1, DATE '2026-09-09', DATE '2026-09-09', 3);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (4, 0, 0, NULL, NULL, 4);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (5, 0, 0, NULL, NULL, 5);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (6, 0, 0, NULL, NULL, 6);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (7, 0, 0, NULL, NULL, 7);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (8, 1, 1, DATE '2026-08-14', DATE '2026-08-14', 8);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (9, 0, 0, NULL, NULL, 9);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (10, 0, 0, NULL, NULL, 10);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (11, 0, 0, NULL, NULL, 11);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (12, 0, 0, NULL, NULL, 12);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (13, 0, 0, NULL, NULL, 13);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (14, 0, 0, NULL, NULL, 14);
INSERT INTO Sequencia (id_sequencia, nr_maior_sequencia, nr_meses_ativos, dt_primeira_atividade, dt_ultima_atividade, id_usuario) VALUES (15, 0, 0, NULL, NULL, 15);


-- ============================================================
-- PERFIL_SUSTENTAVEL
-- xp e pontos_sustentaveis = total de pontos ganhos (nao diminui com gastos)
-- missoes_concluidas = uploads aprovados
-- agua = litros (missao 17) | energia = kWh (missao 18)
-- carbono = pontos ganhos x 0,0142 (razao do selo Elite: 157 / 11038)
-- ============================================================
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (1, 180, 3, 180, 0, 0, 2.56, 2, 1);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (2, 80, 1, 80, 50, 0, 1.14, 1, 2);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (3, 80, 1, 80, 0, 2, 1.14, 1, 3);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (4, 0, 0, 0, 0, 0, 0, 1, 4);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (5, 0, 0, 0, 0, 0, 0, 1, 5);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (6, 0, 0, 0, 0, 0, 0, 1, 6);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (7, 0, 0, 0, 0, 0, 0, 1, 7);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (8, 60, 1, 60, 0, 0, 0.85, 1, 8);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (9, 0, 0, 0, 0, 0, 0, 1, 9);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (10, 0, 0, 0, 0, 0, 0, 1, 10);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (11, 0, 0, 0, 0, 0, 0, 1, 11);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (12, 0, 0, 0, 0, 0, 0, 1, 12);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (13, 0, 0, 0, 0, 0, 0, 1, 13);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (14, 0, 0, 0, 0, 0, 0, 1, 14);
INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, nr_missoes_concluidas, nr_pontos_sustentaveis, nr_agua_economizada, nr_energia_economizada, nr_carbono_economizada, id_nivel, id_usuario) VALUES (15, 0, 0, 0, 0, 0, 0, 1, 15);


-- ============================================================
-- CARTEIRA (UPDATE) = pontos ganhos - pontos gastos em selos
-- Convertido em R$ pela razao dos selos: 110,38 pontos = R$ 1,00
--   Camila:   180 - 55 = 125
--   Enzo:      80 - 55 = 25
--   Lucas:     80 - 0  = 80
--   Gabriel:   60 - 55 = 5
--   Demais:    0
-- ============================================================
UPDATE Carteira SET nr_saldo_pontos = 125, vl_pontos_convertidos = ROUND(125 / 110.38, 2) WHERE id_usuario = 1;
UPDATE Carteira SET nr_saldo_pontos = 25,  vl_pontos_convertidos = ROUND(25 / 110.38, 2)  WHERE id_usuario = 2;
UPDATE Carteira SET nr_saldo_pontos = 80,  vl_pontos_convertidos = ROUND(80 / 110.38, 2)  WHERE id_usuario = 3;
UPDATE Carteira SET nr_saldo_pontos = 5,   vl_pontos_convertidos = ROUND(5 / 110.38, 2)   WHERE id_usuario = 8;
UPDATE Carteira SET nr_saldo_pontos = 0,   vl_pontos_convertidos = 0                      WHERE id_usuario NOT IN (1, 2, 3, 8);

COMMIT;