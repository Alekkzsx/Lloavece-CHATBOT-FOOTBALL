package chatbot.texto;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * CLASSE BASE (abstrata) PARA SEPARAÇÃO DOS TOKENs.
 *
 * Implementa o padrão de projeto "Template Method": o método final
 * {@link #processar(String)} define o pipeline completo de processamento
 * do texto e delega às subclasses apenas a estratégia de separação
 * (método abstrato {@link #tokenizar(String)}) e um gancho opcional
 * ({@link #enriquecer(List)}).
 *
 * Pipeline:
 *   1. normalizar        -> caixa baixa + remoção de acentos
 *   2. tokenizar         -> ABSTRATO (definido pela subclasse)
 *   3. removerStopwords  -> descarta palavras sem valor semântico
 *   4. reduzirRadicais   -> heurística de lematização (plural -> singular)
 *   5. enriquecer        -> gancho (ex.: geração de bigramas)
 */
public abstract class ProcessadorTexto {

    /** Lista padrão de stopwords (caso o arquivo dados/stopwords.txt não exista). */
    public static final Set<String> STOPWORDS_PADRAO = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    "a", "o", "as", "os", "um", "uma", "uns", "umas",
                    "de", "do", "da", "dos", "das", "e", "em", "no", "na",
                    "nos", "nas", "por", "pelo", "pela", "para", "pra", "com",
                    "que", "qual", "quais", "como", "quando", "onde", "se",
                    "eu", "meu", "minha", "seu", "sua", "ele", "ela", "voce",
                    "voces", "me", "te", "lhe", "ao", "aos", "mais", "muito",
                    "muita", "eh", "ser", "estar", "esta", "estao", "sou",
                    "foi", "sao", "tem", "tenho", "ter", "isso", "isto",
                    "esse", "essa", "este", "dele", "dela", "ou", "mas",
                    "tambem", "ja", "la", "aqui", "ai", "bem", "sim", "nao",
                    "pode", "poderia", "quero", "queria", "preciso", "favor",
                    "devo", "entre", "sobre", "fazer", "quanto", "quantos",
                    "quantas", "entao", "ne", "tudo", "coisa", "tipo"
            )));

    private Set<String> stopwords;

    /** Construtor: usa a lista padrão de stopwords. */
    protected ProcessadorTexto() {
        this(null);
    }

    /** Construtor: recebe uma lista de stopwords personalizada (ex.: lida de arquivo). */
    protected ProcessadorTexto(Set<String> stopwords) {
        setStopwords(stopwords);
    }

    public Set<String> getStopwords() {
        return new HashSet<>(stopwords);
    }

    public final void setStopwords(Set<String> stopwords) {
        this.stopwords = new HashSet<>();
        if (stopwords != null) {
            for (String palavra : stopwords) {
                this.stopwords.add(normalizar(palavra));
            }
        } else {
            this.stopwords.addAll(STOPWORDS_PADRAO);
        }
    }

    /**
     * TEMPLATE METHOD: pipeline completo de processamento do texto.
     * É "final" para garantir que todas as subclasses sigam as mesmas etapas.
     */
    public final List<String> processar(String texto) {
        String normalizado = normalizar(texto);
        List<String> tokens = tokenizar(normalizado);
        tokens = removerStopwords(tokens);
        tokens = reduzirRadicais(tokens);
        return enriquecer(tokens);
    }

    /**
     * Passo ABSTRATO: cada subclasse define sua estratégia de separação
     * dos tokens a partir do texto já normalizado.
     */
    public abstract List<String> tokenizar(String textoNormalizado);

    /**
     * Gancho opcional executado ao final do pipeline.
     * A implementação padrão não altera os tokens.
     */
    protected List<String> enriquecer(List<String> tokens) {
        return tokens;
    }

    /** Normalização: remove acentos (decomposição NFD) e converte para caixa baixa. */
    public final String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcentos.toLowerCase(Locale.ROOT);
    }

    /** Remove dos tokens as palavras presentes na lista de stopwords. */
    public List<String> removerStopwords(List<String> tokens) {
        List<String> resultado = new ArrayList<>();
        for (String token : tokens) {
            if (!stopwords.contains(token)) {
                resultado.add(token);
            }
        }
        return resultado;
    }

    /** Aplica a heurística de redução de radical a cada token. */
    public List<String> reduzirRadicais(List<String> tokens) {
        List<String> resultado = new ArrayList<>();
        for (String token : tokens) {
            resultado.add(reduzirRadical(token));
        }
        return resultado;
    }

    /**
     * Heurística simples de lematização para PT-BR (plural -> singular):
     *   "manutencoes" -> "manutencao" | "paes" -> "pao" | "pneus" -> "pneu".
     * Palavras com menos de 4 letras não são alteradas.
     */
    public String reduzirRadical(String palavra) {
        if (palavra == null || palavra.length() < 4) {
            return palavra;
        }
        if (palavra.endsWith("oes")) {
            return palavra.substring(0, palavra.length() - 3) + "ao";
        }
        if (palavra.endsWith("aes")) {
            return palavra.substring(0, palavra.length() - 3) + "ao";
        }
        if (palavra.endsWith("s") && !palavra.endsWith("ss")) {
            return palavra.substring(0, palavra.length() - 1);
        }
        return palavra;
    }

    /**
     * Canonicaliza uma palavra-chave da base de conhecimento com o MESMO
     * tratamento aplicado ao texto do usuário (normalização + radical).
     * Palavras-chave compostas usam "_" (ex.: "troca_oleo").
     */
    public String canonicalizarPalavraChave(String palavraChave) {
        String normalizada = normalizar(palavraChave);
        String[] partes = normalizada.split("_");
        StringBuilder resultado = new StringBuilder();
        for (String parte : partes) {
            if (resultado.length() > 0) {
                resultado.append('_');
            }
            resultado.append(reduzirRadical(parte));
        }
        return resultado.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{stopwords=" + stopwords.size() + "}";
    }
}
