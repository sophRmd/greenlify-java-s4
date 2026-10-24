-- ============================================================
-- LIMPEZA (dos filhos para os pais, para nao violar chaves estrangeiras)
-- ============================================================
DELETE FROM Fingerprint_Frame;
DELETE FROM Fingerprint;

COMMIT;


-- ============================================================
-- REINICIA OS IDs IDENTITY EM 1
-- (DELETE nao reinicia a sequence; necessario Oracle 18c ou superior)
-- ============================================================
ALTER TABLE Fingerprint_Frame MODIFY (id_fingerprint_frame GENERATED ALWAYS AS IDENTITY (RESTART START WITH 1));
ALTER TABLE Fingerprint       MODIFY (id_fingerprint       GENERATED ALWAYS AS IDENTITY (RESTART START WITH 1));

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

COMMIT;