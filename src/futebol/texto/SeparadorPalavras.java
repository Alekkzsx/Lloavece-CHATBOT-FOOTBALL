package futebol.texto; // pasta "texto": tudo que transforma a frase em tokens

import java.util.ArrayList; // lista que cresce sozinha enquanto montamos as palavras

/**
 * SEPARADOR DE PALAVRAS — a filha que faz a separação de verdade.
 *
 * O QUE FAZ:  implementa a etapa 2 do molde (ProcessadorTexto): quebra a
 *             frase normalizada em palavras individuais, uma por token.
 * COMO FAZ:   percorre o texto letra por letra. Letra ou número continua
 *             montando a palavra; qualquer outra coisa (espaço, vírgula,
 *             ponto, interrogação, underline...) é DIVISOR e fecha a palavra.
 *             Ex.: "quem e o rei?" -> ["quem", "e", "o", "rei"] (antes do filtro).
 * ONDE:       pacote futebol.texto. Filha de ProcessadorTexto e mãe de
 *             SeparadorPalavrasEPares. O Dicionario também usa esta classe
 *             para preparar as palavras-chave da base do mesmo jeito.
 * POR QUE:    a base define O ROTEIRO, mas não sabe separar; esta classe
 *             preenche exatamente esse único passo, do jeito mais simples
 *             possível (sem regex, sem split — só charAt e if).
 */
public class SeparadorPalavras extends ProcessadorTexto {

    /**
     * Etapa 2 do roteiro: quebra o texto já normalizado em palavras.
     * Entrada: texto em minúsculas e sem acento (a etapa 1 já passou).
     * Saída:   vetor com uma palavra por posição, na ordem em que aparecem.
     */
    @Override
    protected String[] separarPalavras(String textoNormalizado) {
        ArrayList<String> palavras = new ArrayList<>(); // palavras já fechadas
        String palavraAtual = "";                       // palavra em montagem

        for (int i = 0; i < textoNormalizado.length(); i++) {
            char letra = textoNormalizado.charAt(i);

            if (Character.isLetterOrDigit(letra)) {
                // letra ou número: continua montando a palavra atual
                palavraAtual = palavraAtual + letra;
            } else {
                // divisor: se havia uma palavra sendo montada, fecha e guarda.
                // (divisores seguidos, como "? ", não geram palavra vazia)
                if (!palavraAtual.isEmpty()) {
                    palavras.add(palavraAtual);
                    palavraAtual = ""; // começa a montagem da próxima
                }
            }
        }

        // O texto pode terminar em letra (sem divisor no fim):
        // não esquecer de guardar a última palavra montada.
        if (!palavraAtual.isEmpty()) {
            palavras.add(palavraAtual);
        }

        return paraVetor(palavras); // helper herdado da base: lista -> vetor
    }
}
