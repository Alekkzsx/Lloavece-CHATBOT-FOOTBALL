package chatbot.texto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * HERANÇA EM CADEIA: TokenizadorNGramas estende TokenizadorSimples,
 * que por sua vez estende a classe abstrata ProcessadorTexto.
 *
 * Além dos unigramas, gera BIGRAMAS (pares de palavras consecutivas
 * unidas por "_") através do gancho enriquecer(), executado APÓS a
 * remoção de stopwords e a redução de radicais.
 *
 * Exemplo: "Quando trocar o óleo do motor?"
 *   unigramas úteis: [trocar, oleo, motor]
 *   bigramas:        [trocar_oleo, oleo_motor]
 *
 * Isso permite que a base de conhecimento use palavras-chave compostas
 * como "troca_oleo" e "carro_liga", capturando expressões de duas palavras.
 */
public class TokenizadorNGramas extends TokenizadorSimples {

    /** Construtor padrão (stopwords padrão). */
    public TokenizadorNGramas() {
        super();
    }

    /** Construtor com stopwords personalizadas. */
    public TokenizadorNGramas(Set<String> stopwords) {
        super(stopwords);
    }

    /**
     * POLIMORFISMO: sobrescreve o gancho da classe base para acrescentar
     * os bigramas ao final da lista de tokens.
     */
    @Override
    protected List<String> enriquecer(List<String> tokens) {
        List<String> resultado = new ArrayList<>(tokens);
        for (int i = 0; i < tokens.size() - 1; i++) {
            resultado.add(tokens.get(i) + "_" + tokens.get(i + 1));
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "TokenizadorNGramas{estrategia=unigramas+bigramas, stopwords=" + getStopwords().size() + "}";
    }
}
