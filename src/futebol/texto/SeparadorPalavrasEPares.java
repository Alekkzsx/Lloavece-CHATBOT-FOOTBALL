package futebol.texto;

import java.util.Arrays;

/**
 * Separa palavras e também cria pares de palavras vizinhas.
 *
 * Primeiro usa todas as etapas de SeparadorPalavras. Depois, acrescenta pares
 * consecutivos, como "maior_artilheiro", ao final do vetor.
 */
public class SeparadorPalavrasEPares extends SeparadorPalavras {

    /**
     * Cria um separador de palavras e pares com a lista padrão de palavras comuns.
     */
    public SeparadorPalavrasEPares() {
        super();
    }

    /**
     * Cria um separador com uma lista própria de palavras ignoradas.
     *
     * @param palavrasIgnoradas palavras que serão retiradas das perguntas
     */
    public SeparadorPalavrasEPares(String[] palavrasIgnoradas) {
        super(palavrasIgnoradas);
    }

    /**
     * Acrescenta um par para cada dupla de palavras vizinhas.
     *
     * Se houver zero ou uma palavra, não existe nenhum par. Caso contrário,
     * o vetor final terá as palavras originais seguidas de todos os pares.
     *
     * @param palavras palavras já preparadas pelo processo principal
     * @return vetor original seguido dos pares consecutivos
     */
    @Override
    protected String[] acrescentarPares(String[] palavras) {
        // Um vetor com N palavras tem N - 1 pares consecutivos.
        int quantidadePares = palavras.length - 1;
        if (quantidadePares < 0) {
            quantidadePares = 0;
        }

        // Copia as palavras para o começo do vetor e reserva espaço para os pares.
        String[] resultado = Arrays.copyOf(palavras, palavras.length + quantidadePares);
        int indice = 0;

        // Cada par junta a palavra atual à palavra que vem logo depois dela.
        while (indice < quantidadePares) {
            resultado[palavras.length + indice] =
                    palavras[indice] + "_" + palavras[indice + 1];
            indice++;
        }

        return resultado;
    }

    /**
     * Devolve uma descrição desta estratégia para a demonstração.
     *
     * @return nome simples da estratégia de palavras e pares
     */
    @Override
    public String toString() {
        return "Separador de palavras individuais e pares";
    }
}
