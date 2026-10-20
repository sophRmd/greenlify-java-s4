package br.com.fiap.greenlify.dao;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Sorted Set {@value #CHAVE} do ranking all time: membro = id do usuário,
 * score = missões concluídas. Usa REDIS_HOST/REDIS_PORT (mesmas do
 * application.properties). O Oracle continua sendo a fonte da verdade: se o
 * Redis estiver fora do ar, os métodos não lançam exceção ({@code listar}
 * devolve {@code null}) e o chamador usa o Oracle.
 */
public class RankingRedisDAO {

    public static final String CHAVE = "ranking:all-time";

    private static volatile StringRedisTemplate template;

    /** ZINCRBY: soma 1 missão concluída ao usuário. */
    public void incrementar(int idUsuario) {
        try {
            obterTemplate().opsForZSet().incrementScore(CHAVE, String.valueOf(idUsuario), 1);
        } catch (RuntimeException e) {
            System.err.println("Redis indisponível (incrementar): " + e.getMessage());
        }
    }

    /** ZREVRANGE 0 -1 WITHSCORES: idUsuario -> missões concluídas, ou {@code null} se o Redis falhar. */
    public Map<Integer, Integer> listar() {
        try {
            Set<ZSetOperations.TypedTuple<String>> tuplas =
                    obterTemplate().opsForZSet().reverseRangeWithScores(CHAVE, 0, -1);
            Map<Integer, Integer> mapa = new HashMap<>();
            if (tuplas != null) {
                for (ZSetOperations.TypedTuple<String> t : tuplas) {
                    if (t.getValue() != null && t.getScore() != null) {
                        mapa.put(Integer.parseInt(t.getValue()), (int) Math.round(t.getScore()));
                    }
                }
            }
            return mapa;
        } catch (RuntimeException e) {
            System.err.println("Redis indisponível (listar): " + e.getMessage());
            return null;
        }
    }

    /** Substitui o Sorted Set inteiro pelos totais informados (vindos do Oracle). */
    public void reconstruir(Map<Integer, Integer> totais) {
        try {
            StringRedisTemplate t = obterTemplate();
            t.delete(CHAVE);
            for (Map.Entry<Integer, Integer> e : totais.entrySet()) {
                t.opsForZSet().add(CHAVE, String.valueOf(e.getKey()), e.getValue());
            }
        } catch (RuntimeException e) {
            System.err.println("Redis indisponível (reconstruir): " + e.getMessage());
        }
    }

    private static StringRedisTemplate obterTemplate() {
        StringRedisTemplate t = template;
        if (t == null) {
            synchronized (RankingRedisDAO.class) {
                t = template;
                if (t == null) {
                    RedisStandaloneConfiguration servidor = new RedisStandaloneConfiguration(
                            System.getenv().getOrDefault("REDIS_HOST", "localhost"),
                            Integer.parseInt(System.getenv().getOrDefault("REDIS_PORT", "6379").trim()));
                    LettuceClientConfiguration cliente = LettuceClientConfiguration.builder()
                            .commandTimeout(Duration.ofSeconds(2)).build();
                    LettuceConnectionFactory fabrica = new LettuceConnectionFactory(servidor, cliente);
                    fabrica.afterPropertiesSet();
                    t = new StringRedisTemplate(fabrica);
                    t.afterPropertiesSet();
                    template = t;
                }
            }
        }
        return t;
    }
}