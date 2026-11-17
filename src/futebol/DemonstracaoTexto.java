package futebol;

import java.util.Arrays;
import java.util.Scanner;

import futebol.texto.ProcessadorTexto;
import futebol.texto.SeparadorPalavras;
import futebol.texto.SeparadorPalavrasEPares;

/**
 * Programa de demonstração do preparo de perguntas sobre futebol.
 *
 * A classe compara duas formas de separar as palavras: uma mantém somente
 * palavras individuais e a outra também cria pares de palavras vizinhas.
 * Ela demonstra o preparo do texto, mas ainda não consulta a pasta "base".
 */
public class DemonstracaoTexto {

    /**
     * Inicia o programa.
     *
     * Se a pessoa informar palavras ao executar o programa, elas serão tratadas
     * como uma única frase. Sem palavras informadas, o programa permite digitar
     * várias frases até receber o comando "sair".
     *
     * @param argumentos palavras informadas depois do nome do programa
     */
    public static void main(String[] argumentos) {
        // Os dois objetos compartilham as mesmas etapas básicas de preparo.
        ProcessadorTexto[] separadores = {
                new SeparadorPalavras(),
                new SeparadorPalavrasEPares()
        };

        // Quando há argumentos, prepara a frase e termina sem abrir o modo de digitação.
        if (argumentos.length > 0) {
            demonstrar(juntarArgumentos(argumentos), separadores);
            return;
        }

        // Sem argumentos, lê uma frase por vez até o fim da entrada ou o comando de saída.
        Scanner leitor = new Scanner(System.in);
        System.out.println("Digite uma pergunta sobre futebol ou 'sair' para terminar:");

        while (true) {
            System.out.print("> ");
            if (!leitor.hasNextLine()) {
                break;
            }

            String frase = leitor.nextLine().trim();
            // Ignora linhas vazias para não mostrar um resultado sem conteúdo.
            if (frase.isEmpty()) {
                continue;
            }
            // Compara sem diferenciar letras maiúsculas e minúsculas.
            if (frase.equalsIgnoreCase("sair")) {
                break;
            }

            demonstrar(frase, separadores);
        }

        System.out.println("Até logo!");
    }

    /**
     * Junta os argumentos em uma frase, separando-os por espaços.
     *
     * O terminal entrega cada trecho separado por espaço como um elemento
     * diferente do vetor. Este método reconstrói a frase para o processamento.
     *
     * @param argumentos trechos de texto recebidos ao iniciar o programa
     * @return todos os trechos unidos em uma única frase
     */
    private static String juntarArgumentos(String[] argumentos) {
        String frase = "";
        int indice = 0;

        while (indice < argumentos.length) {
            // O primeiro trecho não precisa de espaço antes; os seguintes precisam.
            if (!frase.isEmpty()) {
                frase += " ";
            }
            frase += argumentos[indice];
            indice++;
        }
        return frase;
    }

    /**
     * Mostra as etapas do preparo e compara os resultados dos separadores.
     *
     * O primeiro separador é usado para exibir cada etapa básica. Depois, todos
     * os separadores recebem a frase original para mostrar o resultado completo.
     *
     * @param frase pergunta ou texto que será demonstrado
     * @param separadores objetos que processam o texto de maneiras diferentes
     */
    private static void demonstrar(String frase, ProcessadorTexto[] separadores) {
        // Usamos o primeiro objeto para mostrar as etapas sem acrescentar pares.
        ProcessadorTexto separador = separadores[0];
        String textoNormalizado = separador.normalizar(frase);
        String[] palavrasOriginais = separador.separarPalavras(textoNormalizado);
        String[] palavrasUteis = separador.removerPalavrasIgnoradas(palavrasOriginais);
        String[] palavrasReduzidas = separador.reduzirPlurais(palavrasUteis);

        System.out.println("  1. Texto sem acentos e em minúsculas: " + textoNormalizado);
        System.out.println("  2. Palavras separadas: " + Arrays.toString(palavrasOriginais));
        System.out.println("  3. Palavras depois de remover as comuns: " + Arrays.toString(palavrasUteis));
        System.out.println("  4. Plurais reduzidos: " + Arrays.toString(palavrasReduzidas));
        System.out.println("  5. Resultado de cada separador:");

        // Percorre os dois objetos sem repetir o código de impressão.
        int indice = 0;
        while (indice < separadores.length) {
            System.out.println("     " + separadores[indice]);
            System.out.println("       " + Arrays.toString(separadores[indice].processar(frase)));
            indice++;
        }
        System.out.println();
    }
}
