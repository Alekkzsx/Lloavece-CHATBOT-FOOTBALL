package futebol.texto; // pasta "texto": tudo que transforma a frase em tokens

import java.util.ArrayList; // lista que cresce sozinha enquanto juntamos palavras + pares

/**
 * SEPARADOR DE PALAVRAS E PARES — o separador completo, usado pelo Chatbot.
 *
 * O QUE FAZ:  tudo que a SeparadorPalavras faz E, no fim, acrescenta pares
 *             de palavras vizinhas: "maior artilheiro" gera os tokens
 *             "maior", "artilheiro" e também "maior_artilheiro".
 * COMO FAZ:   herda a separação pronta da mãe e sobrescreve APENAS o gancho
 *             da etapa 5 (acrescentarPares): copia as palavras e adiciona,
 *             para cada vizinhança, palavra_i + "_" + palavra_i+1.
 * ONDE:       pacote futebol.texto. Fecha a cadeia de herança de 3 níveis:
 *             ProcessadorTexto (molde) -> SeparadorPalavras (separa)
 *             -> SeparadorPalavrasEPares (separa + pares).
 *             É a classe que o Chatbot escolhe para preparar as perguntas.
 * POR QUE:    pares reconhecem EXPRESSÕES de duas palavras, que são mais
 *             específicas que palavras soltas. "copa" e "mundo" separadas
 *             podem falar de qualquer coisa; "copa_mundo" aponta direto
 *             para o assunto — por isso o Dicionario dá pontos extras a eles.
 */
public class SeparadorPalavrasEPares extends SeparadorPalavras {

    /**
     * Gancho da etapa 5: mantém as palavras originais e acrescenta os pares.
     * Entrada: palavras já filtradas e no singular (etapas 3 e 4 passaram).
     * Saída:   as mesmas palavras + os pares no fim do vetor.
     * Com N palavras existem N-1 pares consecutivos (cada palavra com a
     * vizinha da direita; a última não tem vizinha, por isso não gera par).
     */
    @Override
    protected String[] acrescentarPares(String[] palavras) {
        ArrayList<String> resultado = new ArrayList<>();

        // 1) copia todas as palavras originais, sem mudar nada nelas
        for (String palavra : palavras) {
            resultado.add(palavra);
        }

        // 2) cria os pares: junta cada palavra com a vizinha da direita.
        // A condição "i + 1 < length" garante que sempre existe uma vizinha
        // (para quando chega na última palavra, que não tem par).
        for (int i = 0; i + 1 < palavras.length; i++) {
            resultado.add(palavras[i] + "_" + palavras[i + 1]);
        }

        return paraVetor(resultado); // helper herdado da base: lista -> vetor
    }
}
