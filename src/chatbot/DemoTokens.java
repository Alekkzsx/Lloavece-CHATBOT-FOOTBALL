package chatbot;

import java.util.List;
import java.util.Scanner;

import chatbot.texto.ProcessadorTexto;
import chatbot.texto.TokenizadorNGramas;
import chatbot.texto.TokenizadorSimples;

/**
 * Demo MÍNIMA para a arguição de 17/11/2026.
 *
 * Mostra, passo a passo, o pipeline da CLASSE BASE de separação dos TOKENs
 * (ProcessadorTexto) e o resultado final das duas subclasses, chamadas
 * polimorficamente através da referência abstrata.
 *
 * Uso:
 *   java -cp bin chatbot.DemoTokens                          (interativo)
 *   java -cp bin chatbot.DemoTokens "Quem é o maior artilheiro das Copas?" (direto)
 */
public class DemoTokens {

    public static void main(String[] args) {
        // POLIMORFISMO: referências do tipo abstrato, objetos de subclasses diferentes.
        ProcessadorTexto[] processadores = {
                new TokenizadorSimples(),
                new TokenizadorNGramas()
        };

        if (args.length > 0) {
            demonstrar(String.join(" ", args), processadores);
            return;
        }

        System.out.println("Demo de tokens para perguntas sobre futebol — digite uma frase (ou 'sair'):");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String linha = scanner.nextLine().trim();
            if (linha.isEmpty()) {
                continue;
            }
            if (linha.equalsIgnoreCase("sair")) {
                break;
            }
            demonstrar(linha, processadores);
        }
        System.out.println("Até logo!");
    }

    /** Executa cada etapa do pipeline separadamente e depois o processar() completo. */
    private static void demonstrar(String frase, ProcessadorTexto[] processadores) {
        ProcessadorTexto base = processadores[0];

        String normalizado = base.normalizar(frase);
        List<String> brutos = base.tokenizar(normalizado);
        List<String> semStopwords = base.removerStopwords(brutos);
        List<String> radicais = base.reduzirRadicais(semStopwords);

        System.out.println("  1. normalizar    : " + normalizado);
        System.out.println("  2. tokenizar     : " + brutos);
        System.out.println("  3. sem stopwords : " + semStopwords);
        System.out.println("  4. radicais      : " + radicais);
        System.out.println("  5. processar() completo (polimorfismo):");
        for (ProcessadorTexto p : processadores) {
            System.out.println("     " + p);
            System.out.println("       -> " + p.processar(frase));
        }
        System.out.println();
    }
}
