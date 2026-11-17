package chatbot.texto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * HERANÇA: especializa a classe abstrata ProcessadorTexto.
 *
 * Estratégia simples de separação de TOKENs (unigramas):
 * qualquer sequência de caracteres que não seja letra ou dígito
 * é tratada como delimitador (espaços, vírgulas, pontos, etc.).
 *
 * Exemplo: "Quem marcou o gol da final?" -> [quem, marcou, o, gol, da, final]
 */
public class TokenizadorSimples extends ProcessadorTexto {

    /** Construtor padrão (stopwords padrão). */
    public TokenizadorSimples() {
        super();
    }

    /** Construtor com stopwords personalizadas. */
    public TokenizadorSimples(Set<String> stopwords) {
        super(stopwords);
    }

    @Override
    public List<String> tokenizar(String textoNormalizado) {
        List<String> tokens = new ArrayList<>();
        for (String parte : textoNormalizado.split("[^a-z0-9]+")) {
            if (!parte.isEmpty()) {
                tokens.add(parte);
            }
        }
        return tokens;
    }

    @Override
    public String toString() {
        return "TokenizadorSimples{estrategia=unigramas, stopwords=" + getStopwords().size() + "}";
    }
}
