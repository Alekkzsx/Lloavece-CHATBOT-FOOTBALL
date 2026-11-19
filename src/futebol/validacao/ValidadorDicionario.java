package futebol.validacao; // pasta "validacao": a conferência dos tokens

import futebol.dicionario.Dicionario; // a consulta concreta é feita aqui dentro

/**
 * VALIDADOR NO DICIONÁRIO — a filha concreta da validação.
 *
 * O QUE FAZ:  define o detalhe que faltava na base: um token é válido quando
 *             existe entre as palavras-chave da base de conhecimento.
 * COMO FAZ:   herda o laço pronto de ValidadorTokens e implementa o método
 *             tokenValido() com UMA linha: dicionario.contemToken(token).
 * ONDE:       pacote futebol.validacao. Filha de ValidadorTokens. Quem cria
 *             é o Chatbot (no construtor), entregando o dicionário pronto.
 * POR QUE:    a base sabe PERCORRER os tokens, mas não sabe ONDE conferir;
 *             esta classe preenche só esse detalhe. Assim a regra de
 *             "onde o token é válido" fica isolada em um único lugar.
 */
public class ValidadorDicionario extends ValidadorTokens {

    /** Dicionário onde os tokens serão procurados (o vocabulário do bot). */
    private final Dicionario dicionario;

    /**
     * Recebe o dicionário PRONTO pelo construtor.
     * Por que receber em vez de criar: o Chatbot já carregou a base; se esta
     * classe criasse outro Dicionario, a base seria lida do disco 2 vezes
     * à toa. Receber pronto também garante que os dois usam os MESMOS dados.
     */
    public ValidadorDicionario(Dicionario dicionario) {
        this.dicionario = dicionario;
    }

    /**
     * O detalhe da validação: um token é válido quando o dicionário conhece
     * essa palavra (ela apareceu em alguma palavra-chave da base).
     * Este método é chamado pelo validar() da base, uma vez por token.
     */
    @Override
    protected boolean tokenValido(String token) {
        return dicionario.contemToken(token);
    }
}
