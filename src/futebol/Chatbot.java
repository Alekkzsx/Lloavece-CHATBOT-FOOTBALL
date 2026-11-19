package futebol; // pacote raiz: o "cérebro" que amarra as peças do assistente

// Cada import é uma peça do bot, guardada na sua própria pasta:
import futebol.dicionario.Dicionario;         // o conhecimento (carrega a base/)
import futebol.texto.ProcessadorTexto;        // tipo BASE do preparo de texto
import futebol.texto.SeparadorPalavrasEPares; // preparo concreto: palavras + pares
import futebol.validacao.ValidadorDicionario; // validação concreta: no dicionário
import futebol.validacao.ValidadorTokens;     // tipo BASE da validação de tokens

/**
 * CHATBOT — a estrutura central do assistente (o "cérebro").
 *
 * O QUE FAZ:  recebe uma pergunta em português e devolve uma resposta.
 * COMO FAZ:   chama três peças, sempre nesta ordem:
 *
 *               pergunta
 *                  |
 *                  v
 *             ProcessadorTexto -> prepara os tokens (palavras + pares)
 *                  |
 *                  v
 *             ValidadorTokens  -> existe pelo menos 1 token conhecido?
 *                  |               nao -> devolve o FALLBACK
 *                  v
 *             Dicionario       -> procura a entrada com mais palavras em comum
 *                  |               nao achou -> devolve o FALLBACK
 *                  v
 *               resposta
 *
 * ONDE:       raiz do src. É chamado pelo Terminal e chama as peças de
 *             texto/, validacao/ e dicionario/. É o único lugar do projeto
 *             que conhece as três peças juntas.
 * POR QUE:    cada peça faz UMA coisa (preparar, validar, responder) e o
 *             Chatbot só coordena a ordem. Se uma peça for trocada por outra
 *             (ex.: outro separador), o resto do sistema nem fica sabendo.
 */
public class Chatbot {

    /**
     * FALLBACK: a mensagem padrão de "não entendi".
     * Usada em dois casos: nenhum token conhecido OU nenhuma entrada casou.
     * Por que fixa no código: é o jeito mais simples possível. O arquivo
     * base/texto/fallback.txt existe, mas ainda não é lido pelo programa.
     */
    private static final String FALLBACK =
            "Desculpe, não sei responder isso ainda. "
            + "Tente perguntar sobre jogadores, clubes, regras ou Copas.";

    // As três peças do bot. "final" = depois de montadas no construtor,
    // nunca mais são trocadas. Os tipos são os mais genéricos possíveis
    // (Dicionario é classe concreta; as outras duas são tipos BASE).
    private final Dicionario dicionario;        // guarda e procura o conhecimento
    private final ProcessadorTexto processador; // transforma a pergunta em tokens
    private final ValidadorTokens validador;    // confere se os tokens são conhecidos

    /**
     * Monta o chatbot: carrega a base e escolhe a estratégia de cada peça.
     * Entrada: o caminho da pasta da base (ex.: "base").
     * Por que recebe o caminho: para não deixar o nome da pasta fixo no código.
     */
    public Chatbot(String pastaBase) {
        // 1) carrega TODO o conhecimento dos .txt da base para a memória
        dicionario = new Dicionario(pastaBase);

        // 2) POLIMORFISMO: o objeto criado é um SeparadorPalavrasEPares, mas
        // a variável é do tipo base ProcessadorTexto. O Chatbot só conhece o
        // contrato da base (o método processar); não sabe nem precisa saber
        // como a filha separa as palavras por dentro.
        processador = new SeparadorPalavrasEPares();

        // 3) a validação concreta consulta ESTE dicionário que acabamos de
        // carregar (entregamos ele pronto, para não carregar a base 2 vezes).
        validador = new ValidadorDicionario(dicionario);
    }

    /**
     * Recebe a pergunta em linguagem natural e devolve a resposta.
     * Este é o ÚNICO método que o Terminal precisa chamar.
     *
     * @param pergunta a frase digitada pelo usuário (com acento, maiúscula...)
     * @return a resposta encontrada na base, ou o FALLBACK se não entender
     */
    public String responder(String pergunta) {
        // PASSO 1: prepara a pergunta. Sai daqui um vetor de tokens:
        // palavras úteis + pares vizinhos (ex.: "artilheiro_copa").
        String[] tokens = processador.processar(pergunta);

        // PASSO 2: valida. Se NENHUM token existe no dicionário, a pergunta
        // não é sobre nada que o bot conhece: devolve o fallback direto,
        // sem gastar tempo procurando resposta.
        if (!validador.validar(tokens)) {
            return FALLBACK;
        }

        // PASSO 3: procura a entrada com mais palavras-chave em comum.
        // O dicionário devolve null quando nenhuma entrada pontuou.
        String resposta = dicionario.buscarResposta(tokens);
        if (resposta == null) {
            return FALLBACK;
        }
        return resposta;
    }

    /**
     * Quantas entradas a base carregou.
     * Existe só para o Terminal mostrar o número na tela de boas-vindas.
     * Quem conta de verdade é o Dicionario; aqui só repassamos o valor.
     */
    public int quantidadeEntradas() {
        return dicionario.quantidadeEntradas();
    }
}
