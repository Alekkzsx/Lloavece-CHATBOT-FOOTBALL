package futebol.texto;

import java.util.Arrays;

/**
 * Separa uma frase em palavras individuais.
 *
 * Espaços, pontuação e quaisquer outros caracteres que não sejam letras
 * sem acento ou números funcionam como divisores.
 */
public class SeparadorPalavras extends ProcessadorTexto {

    /**
     * Cria um separador que usa a lista padrão de palavras comuns.
     */
    public SeparadorPalavras() {
        super();
    }

    /**
     * Cria um separador com uma lista própria de palavras ignoradas.
     *
     * @param palavrasIgnoradas palavras que serão retiradas das perguntas
     */
    public SeparadorPalavras(String[] palavrasIgnoradas) {
        super(palavrasIgnoradas);
    }

    /**
     * Divide o texto quando encontra algo que não seja letra ou número.
     *
     * A expressão "[^a-z0-9]+" significa "um ou mais caracteres que não sejam
     * letras de a a z ou números". Como os acentos já foram removidos antes
     * desta chamada, as letras ficam dentro do intervalo esperado.
     *
     * @param textoNormalizado texto em minúsculas e sem acentos
     * @return vetor com as palavras não vazias encontradas
     */
    @Override
    public String[] separarPalavras(String textoNormalizado) {
        String[] partes = textoNormalizado.split("[^a-z0-9]+");
        // Reservamos espaço para todas as partes; algumas podem estar vazias.
        String[] palavras = new String[partes.length];
        int quantidade = 0;
        int indice = 0;

        while (indice < partes.length) {
            // Ignoramos partes vazias que podem surgir junto à pontuação.
            if (!partes[indice].isEmpty()) {
                palavras[quantidade] = partes[indice];
                quantidade++;
            }
            indice++;
        }

        return Arrays.copyOf(palavras, quantidade);
    }

    /**
     * Devolve uma descrição deste separador para ser mostrada na demonstração.
     *
     * @return nome simples da estratégia de separação
     */
    @Override
    public String toString() {
        return "Separador de palavras individuais";
    }
}
