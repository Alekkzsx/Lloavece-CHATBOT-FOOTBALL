package futebol; // pacote raiz: aqui ficam a "tela" (Terminal) e o "cérebro" (Chatbot)

import java.util.Scanner; // Scanner = leitor pronto do Java para ler o que o usuário digita

/**
 * TERMINAL — o arquivo que o usuário roda para conversar com o bot.
 *
 * O QUE FAZ:  mostra as boas-vindas, lê cada pergunta digitada e imprime a
 *             resposta, até o usuário escrever "sair".
 * COMO FAZ:   um laço infinito: imprime "Você: ", lê uma linha com o Scanner,
 *             entrega a linha ao Chatbot e imprime "Bot: " + a resposta.
 * ONDE:       é o ponto de entrada do programa (o método main). Fica na raiz
 *             do src porque é quem "liga" o sistema.
 * POR QUE:    separar a TELA do CÉREBRO. Aqui só existe entrada e saída
 *             (ler do teclado / imprimir na tela); nenhuma regra de resposta
 *             mora neste arquivo. Quem pensa é o Chatbot — assim cada classe
 *             tem um único trabalho e fica fácil de entender e mudar.
 *
 * COMO RODAR (na raiz do projeto, a pasta que contém "base" e "src"):
 *   javac -encoding UTF-8 -d bin $(find src -name "*.java")
 *   java -cp bin futebol.Terminal
 */
public class Terminal {

    /**
     * Ponto de entrada do programa: monta o chatbot e mantém a conversa.
     * Entrada: nenhuma (as perguntas chegam pelo teclado durante a execução).
     * Saída:   as respostas impressas na tela.
     */
    public static void main(String[] argumentos) {
        // PASSO 1: cria o chatbot. O construtor dele já carrega toda a base
        // de conhecimento da pasta "base". Por isso o programa precisa ser
        // rodado a partir da raiz do projeto, onde essa pasta existe.
        Chatbot chatbot = new Chatbot("base");

        // PASSO 2: mostra o cabeçalho com as instruções e quantas entradas
        // de conhecimento foram carregadas (prova de que a base foi lida).
        mostrarBoasVindas(chatbot.quantidadeEntradas());

        // PASSO 3: a conversa. System.in representa o teclado;
        // o Scanner transforma o que vem dele em linhas de texto.
        Scanner leitor = new Scanner(System.in);
        while (true) { // só sai do laço com "sair" ou com o fim da entrada (Ctrl+D)
            System.out.print("\nVocê: "); // print sem "ln": o cursor fica na mesma linha

            // hasNextLine() avisa se ainda existe linha para ler. Se não existe
            // (usuário apertou Ctrl+D, por exemplo), encerra sem dar erro.
            if (!leitor.hasNextLine()) {
                break;
            }

            // nextLine() lê a linha inteira; trim() corta espaços das pontas
            // (assim "   sair   " também funciona).
            String pergunta = leitor.nextLine().trim();

            if (pergunta.isEmpty()) {
                continue; // linha vazia: ignora e volta a esperar outra pergunta
            }
            if (pergunta.equalsIgnoreCase("sair")) {
                break; // "sair", "SAIR", "Sair"... encerram a conversa
            }

            // O Terminal NÃO pensa: só repassa a pergunta ao Chatbot
            // e imprime na tela o que ele devolver.
            System.out.println("Bot: " + chatbot.responder(pergunta));
        }
        leitor.close(); // libera o leitor (boa prática, mesmo no fim do programa)
        System.out.println("Bot: Até logo!");
    }

    /**
     * Imprime o cabeçalho do terminal com as instruções de uso.
     * Entrada: quantas entradas de conhecimento foram carregadas da base.
     * Por que recebe esse número: para o usuário conferir que a base carregou.
     * É private porque só este arquivo usa (ninguém de fora precisa chamar).
     */
    private static void mostrarBoasVindas(int entradasCarregadas) {
        System.out.println("==============================================");
        System.out.println("   LLOAVECE FUTEBOL — seu chatbot de futebol");
        System.out.println("==============================================");
        System.out.println("Conhecimento carregado: " + entradasCarregadas + " entradas.");
        System.out.println("Pergunte sobre jogadores, clubes, regras, Copas...");
        System.out.println("Digite 'sair' para encerrar a conversa.");
    }
}
