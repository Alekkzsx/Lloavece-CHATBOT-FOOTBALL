package futebol.dicionario; // pasta "dicionario": o conhecimento do bot

import java.io.File;             // representa uma pasta ou arquivo do disco
import java.io.IOException;      // erro de leitura de arquivo (disco, permissão...)
import java.nio.file.Files;      // utilitário pronto do Java para ler arquivos
import java.util.ArrayList;      // lista que cresce sozinha

import futebol.texto.SeparadorPalavras; // prepara as chaves igual à pergunta

/**
 * DICIONÁRIO — todo o conhecimento do bot, carregado da pasta "base".
 *
 * O QUE FAZ:  lê os arquivos .txt de assunto e guarda as entradas no formato
 *             "palavras-chave | resposta". Depois responde a duas consultas:
 *             contemToken() (usada pela validação) e buscarResposta()
 *             (usada pelo chatbot para escolher a resposta).
 * COMO FAZ:   na criação, percorre as subpastas de "base", lê linha por linha,
 *             corta cada linha no "|" e prepara as palavras-chave com o MESMO
 *             preparo da pergunta (minúsculas, sem acento, sem palavras comuns,
 *             plural reduzido). Assim pergunta e dicionário "falam a mesma
 *             língua" na hora de comparar.
 * ONDE:       pacote futebol.dicionario. Quem cria é o Chatbot; quem consulta
 *             é o Chatbot (buscarResposta) e o ValidadorDicionario (contemToken).
 * POR QUE:    deixar TODO o conhecimento em arquivos de texto fora do código:
 *             para ensinar algo novo ao bot basta editar um .txt da base,
 *             sem tocar no Java e sem recompilar nada.
 *
 * Guarda três listas (as duas primeiras andam juntas, pelo mesmo índice):
 *   palavrasChave.get(i) = palavras-chave da entrada i;
 *   respostas.get(i)     = resposta da entrada i;
 *   tokensConhecidos     = todas as palavras-chave vistas, sem repetição
 *                          (é o "vocabulário" do bot, usado na validação).
 */
public class Dicionario {

    /** palavrasChave.get(i) = palavras-chave da entrada i (um vetor por entrada). */
    private final ArrayList<String[]> palavrasChave = new ArrayList<>();

    /** respostas.get(i) = resposta da entrada i (mesmo índice da lista acima). */
    private final ArrayList<String> respostas = new ArrayList<>();

    /** Todas as palavras que o bot conhece (usado pela validação de tokens). */
    private final ArrayList<String> tokensConhecidos = new ArrayList<>();

    /**
     * Prepara as palavras-chave do MESMO jeito que a pergunta é preparada.
     * De propósito é a SeparadorPalavras (SEM pares): as chaves do dicionário
     * são só palavras simples. Os pares existem apenas do lado da pergunta;
     * o bônus deles é calculado em buscarResposta() checando as duas metades.
     */
    private final SeparadorPalavras preparador = new SeparadorPalavras();

    /**
     * Cria o dicionário já carregando os arquivos da pasta informada.
     * Entrada: caminho da pasta da base (ex.: "base").
     * Toda a carga acontece AQUI, uma única vez, na criação do objeto.
     */
    public Dicionario(String pastaBase) {
        carregarPastas(pastaBase);
    }

    /**
     * Percorre as subpastas de "base" e manda ler cada arquivo .txt de assunto.
     * listFiles() devolve null quando a pasta não existe — por isso o aviso
     * em vez de deixar o programa quebrar.
     */
    private void carregarPastas(String pastaBase) {
        File[] pastas = new File(pastaBase).listFiles();
        if (pastas == null) {
            System.out.println("Aviso: pasta '" + pastaBase + "' não encontrada.");
            return; // sem base o bot fica vazio, mas o programa continua rodando
        }
        for (File pasta : pastas) {
            // Só interessam DIRETÓRIOS de assunto. A pasta "texto" é pulada
            // porque tem arquivos auxiliares (stopwords, sinônimos...),
            // que não são entradas "chave | resposta".
            if (!pasta.isDirectory() || pasta.getName().equals("texto")) {
                continue;
            }
            File[] arquivos = pasta.listFiles();
            if (arquivos == null) {
                continue; // não deu para listar esta pasta: pula sem erro
            }
            for (File arquivo : arquivos) {
                if (arquivo.getName().endsWith(".txt")) {
                    carregarArquivo(arquivo); // só arquivos .txt são conhecimento
                }
            }
        }
    }

    /**
     * Lê UM arquivo e guarda cada linha válida no formato "chave | resposta".
     * O try/catch existe para um arquivo com problema não derrubar o bot:
     * ele avisa no console e segue carregando os outros.
     */
    private void carregarArquivo(File arquivo) {
        try {
            // readAllLines lê o arquivo inteiro de uma vez (UTF-8 por padrão).
            for (String linha : Files.readAllLines(arquivo.toPath())) {
                linha = linha.trim(); // tira espaços das pontas

                // pula linha vazia e comentário (linha que começa com #)
                if (linha.isEmpty() || linha.startsWith("#")) {
                    continue;
                }

                // acha o "|" que separa as palavras-chave da resposta
                int barra = linha.indexOf('|');
                if (barra < 0) {
                    continue; // sem "|" a linha não é uma entrada válida
                }

                // corta a linha em duas partes: antes do "|" e depois dele
                String parteChaves = linha.substring(0, barra);
                String resposta = linha.substring(barra + 1).trim();

                // prepara as chaves igual à pergunta: minúsculo, sem acento,
                // sem palavra comum e com plural reduzido
                String[] chaves = preparador.processar(parteChaves);

                if (chaves.length == 0 || resposta.isEmpty()) {
                    continue; // entrada sem chave ou sem resposta não serve
                }

                // guarda a entrada: as duas listas crescem juntas,
                // então o índice i aponta para a mesma entrada nas duas
                palavrasChave.add(chaves);
                respostas.add(resposta);
                registrarTokens(chaves); // atualiza o vocabulário do bot
            }
        } catch (IOException erro) {
            System.out.println("Aviso: não consegui ler " + arquivo.getName());
        }
    }

    /**
     * Guarda cada palavra-chave como um token conhecido do bot.
     * O if evita repetição: "copa" aparece em dezenas de entradas,
     * mas precisa existir uma única vez no vocabulário.
     */
    private void registrarTokens(String[] chaves) {
        for (String chave : chaves) {
            if (!tokensConhecidos.contains(chave)) {
                tokensConhecidos.add(chave);
            }
        }
    }

    /**
     * Diz se o token existe no dicionário (consulta ao vocabulário).
     * Quem chama é o ValidadorDicionario, na etapa de validação.
     * Entrada: um token já preparado. Saída: true se o bot conhece a palavra.
     */
    public boolean contemToken(String token) {
        return tokensConhecidos.contains(token);
    }

    /**
     * Procura a resposta que mais combina com os tokens da pergunta.
     *
     * PONTUAÇÃO:
     * - cada palavra-chave da entrada que aparece na pergunta vale 1 ponto;
     * - cada par da pergunta (ex.: "artilheiro_copa") vale 2 pontos EXTRAS
     *   quando as DUAS metades do par são palavras-chave da MESMA entrada.
     *   Vale mais porque um par é mais específico que uma palavra solta;
     * - vence a entrada com mais pontos; em empate fica a PRIMEIRA carregada
     *   (só trocamos o vencedor quando a pontuação é estritamente maior).
     *
     * @param tokensPergunta palavras + pares já preparados da pergunta
     * @return a resposta vencedora, ou null se nenhuma entrada pontuou
     */
    public String buscarResposta(String[] tokensPergunta) {
        String melhorResposta = null; // ninguém venceu ainda
        int melhorPontuacao = 0;      // pontuação a bater

        // percorre TODAS as entradas e pontua cada uma contra a pergunta
        for (int i = 0; i < palavrasChave.size(); i++) {
            String[] chaves = palavrasChave.get(i);
            int pontos = 0;

            // regra 1: +1 ponto por palavra-chave encontrada na pergunta
            for (String chave : chaves) {
                if (contem(tokensPergunta, chave)) {
                    pontos++;
                }
            }

            // regra 2: +2 pontos por par cujas DUAS metades são desta entrada
            for (String token : tokensPergunta) {
                int underline = token.indexOf('_');
                if (underline >= 0) { // tem "_" = é um par, não palavra simples
                    // parte o par nas duas palavras originais
                    String primeira = token.substring(0, underline);
                    String segunda = token.substring(underline + 1);
                    if (contem(chaves, primeira) && contem(chaves, segunda)) {
                        pontos += 2; // as duas palavras juntas nesta entrada: bônus
                    }
                }
            }

            // só troca o vencedor se a pontuação for MAIOR (empate mantém o 1º)
            if (pontos > melhorPontuacao) {
                melhorPontuacao = pontos;
                melhorResposta = respostas.get(i); // mesmo índice da lista de chaves
            }
        }
        return melhorResposta; // null = nenhuma palavra-chave casou com a pergunta
    }

    /**
     * Quantas entradas foram carregadas.
     * Quem chama é o Chatbot, para o Terminal mostrar na tela de boas-vindas.
     */
    public int quantidadeEntradas() {
        return respostas.size();
    }

    /**
     * Procura uma palavra dentro de um vetor (comparação exata, com equals).
     * Vetor puro não tem "contains" pronto, então fazemos na mão com um laço.
     */
    private boolean contem(String[] vetor, String palavra) {
        for (String item : vetor) {
            if (item.equals(palavra)) {
                return true; // achou: pode parar de procurar
            }
        }
        return false; // percorreu tudo e não achou
    }
}
