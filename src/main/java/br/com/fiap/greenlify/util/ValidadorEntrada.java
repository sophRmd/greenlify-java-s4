package br.com.fiap.greenlify.util;

import java.util.regex.Pattern;

/**
 * Validação de entrada contra injeção (SQL, HTML/script e path traversal).
 * <p>
 * Esta é a <b>segunda</b> linha de defesa. A primeira continua sendo o uso de
 * {@code PreparedStatement} com parâmetros ({@code ?}) em todos os DAOs, que é
 * o que de fato impede SQL injection. Aqui rejeitamos cedo (HTTP 400) qualquer
 * texto com cara de ataque e restringimos campos estruturados (nome, e-mail,
 * CPF, hashes...) a uma lista de caracteres permitidos (whitelist).
 * <p>
 * Todas as falhas lançam {@link IllegalArgumentException} com mensagem
 * genérica: o conteúdo enviado nunca é devolvido na resposta nem impresso.
 */
public final class ValidadorEntrada {

    private ValidadorEntrada() {
    }

    // ---------------------------------------------------------------- whitelists

    private static final Pattern NOME_PESSOA = Pattern.compile("^\\p{L}[\\p{L} .'’-]*$");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");
    private static final Pattern CPF = Pattern.compile("^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$");
    private static final Pattern CODIGO_BARRAS = Pattern.compile("^[0-9][0-9 .-]{9,254}$");
    private static final Pattern HASH = Pattern.compile("^[A-Za-z0-9+/=_-]{1,255}$");
    private static final Pattern EMBEDDING = Pattern.compile("^[0-9eE+\\-.,\\s\\[\\]]{1,4000}$");

    // ---------------------------------------------------------------- blacklists (texto livre)

    private static final int FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNICODE_CASE;

    /** Padrões típicos de SQL injection (Oracle e genéricos). */
    private static final Pattern SQL_PERIGOSO = Pattern.compile(
            "--|/\\*|\\*/"
                    + "|;\\s*(drop|delete|insert|update|select|alter|create|truncate|exec|execute|merge|grant|revoke|begin)\\b"
                    + "|\\bunion\\b[\\s\\S]{0,30}\\bselect\\b"
                    + "|\\bselect\\b[\\s\\S]{0,80}\\bfrom\\b|\\binsert\\s+into\\b|\\bdelete\\s+from\\b|\\bupdate\\b[\\s\\S]{0,40}\\bset\\b"
                    + "|\\|\\|"
                    + "|['\"]\\s*(or|and|union)\\b"
                    + "|\\b(or|and)\\b\\s+['\"]?\\w+['\"]?\\s*(=|<|>|like\\b)\\s*['\"]?\\w*"
                    + "|\\b(drop|truncate|alter)\\s+(table|user|database|view|index|sequence)\\b"
                    + "|\\b(dbms_|utl_|xp_|sp_executesql)\\w*"
                    + "|\\b(waitfor\\s+delay|pg_sleep|sleep\\s*\\(|benchmark\\s*\\()"
                    + "|\\b(all_tables|user_tables|all_users|information_schema|sys\\.\\w+|v\\$\\w+)\\b",
            FLAGS);

    /** HTML/JS embutido (XSS armazenado). */
    private static final Pattern HTML_PERIGOSO = Pattern.compile(
            "[<>]|javascript\\s*:|vbscript\\s*:|data\\s*:\\s*text/html|\\bon[a-z]{3,}\\s*=|&#x?\\d+;?",
            FLAGS);

    // ---------------------------------------------------------------- texto livre

    /** Texto livre obrigatório (descrições, nomes de missão/grupo/selo etc.). */
    public static void textoLivre(String rotulo, String valor, int max) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(rotulo + " é obrigatório.");
        }
        validarTexto(rotulo, valor, max);
    }

    /** Texto livre opcional: {@code null} ou vazio é aceito, mas se vier deve ser seguro. */
    public static void textoLivreOpcional(String rotulo, String valor, int max) {
        if (valor == null || valor.isBlank()) {
            return;
        }
        validarTexto(rotulo, valor, max);
    }

    private static void validarTexto(String rotulo, String valor, int max) {
        if (valor.length() > max) {
            throw new IllegalArgumentException(rotulo + " deve ter no máximo " + max + " caracteres.");
        }
        if (temControle(valor, true)
                || HTML_PERIGOSO.matcher(valor).find()
                || SQL_PERIGOSO.matcher(valor).find()) {
            throw new IllegalArgumentException(rotulo + " contém caracteres ou padrões não permitidos.");
        }
    }

    // ---------------------------------------------------------------- campos estruturados

    public static void nomePessoa(String rotulo, String valor, int max) {
        obrigatorioComLimite(rotulo, valor, max);
        if (!NOME_PESSOA.matcher(valor.trim()).matches()) {
            throw new IllegalArgumentException(rotulo + " contém caracteres não permitidos.");
        }
    }

    public static void email(String valor) {
        obrigatorioComLimite("E-mail", valor, 150);
        if (!EMAIL.matcher(valor.trim()).matches()) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
    }

    public static void cpf(String valor) {
        obrigatorioComLimite("CPF", valor, 14);
        if (!CPF.matcher(valor.trim()).matches()) {
            throw new IllegalArgumentException("CPF inválido: use 11 dígitos, com ou sem pontuação.");
        }
    }

    /**
     * A senha não passa por blacklist (qualquer caractere imprimível é válido,
     * e o PreparedStatement já a trata como dado). Só exigimos presença,
     * tamanho (a coluna comporta 64) e ausência de caracteres de controle.
     */
    public static void senha(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória.");
        }
        if (valor.length() > 64 || temControle(valor, false)) {
            throw new IllegalArgumentException("Senha inválida: use até 64 caracteres, sem caracteres de controle.");
        }
    }

    public static void codigoBarras(String valor) {
        obrigatorioComLimite("O código de barras", valor, 255);
        if (!CODIGO_BARRAS.matcher(valor.trim()).matches()) {
            throw new IllegalArgumentException("O código de barras contém caracteres não permitidos.");
        }
    }

    public static void hash(String rotulo, String valor) {
        obrigatorioComLimite(rotulo, valor, 255);
        if (!HASH.matcher(valor.trim()).matches()) {
            throw new IllegalArgumentException(rotulo + " contém caracteres não permitidos.");
        }
    }

    public static void embedding(String valor) {
        obrigatorioComLimite("O embedding", valor, 4000);
        if (!EMBEDDING.matcher(valor).matches()) {
            throw new IllegalArgumentException("O embedding contém caracteres não permitidos.");
        }
    }

    /** Nome/caminho de arquivo enviado: barra invertida e ".." (path traversal) não são aceitos. */
    public static void nomeArquivo(String valor) {
        textoLivre("O arquivo", valor, 255);
        if (valor.contains("..") || valor.contains("\\") || valor.contains("\u0000")) {
            throw new IllegalArgumentException("O arquivo contém caracteres ou padrões não permitidos.");
        }
    }

    /**
     * Converte um texto em um valor de enum aceitando somente os nomes
     * existentes (útil para parâmetros de query que não passam por DTO).
     */
    public static <E extends Enum<E>> E enumValor(Class<E> tipo, String rotulo, String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(rotulo + " é obrigatório.");
        }
        try {
            return Enum.valueOf(tipo, valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(rotulo + " inválido.");
        }
    }

    // ---------------------------------------------------------------- internos

    private static void obrigatorioComLimite(String rotulo, String valor, int max) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(rotulo + " é obrigatório.");
        }
        if (valor.length() > max) {
            throw new IllegalArgumentException(rotulo + " deve ter no máximo " + max + " caracteres.");
        }
    }

    /** Detecta caracteres de controle (inclui NUL). Quebras de linha/tab podem ser toleradas em texto livre. */
    private static boolean temControle(String valor, boolean toleraQuebras) {
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            if (Character.isISOControl(c)) {
                if (toleraQuebras && (c == '\n' || c == '\r' || c == '\t')) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }
}
