package futebol.validacao; // pasta "validacao": a conferência dos tokens

/**
 * VALIDADOR DE TOKENS — a CLASSE BASE da validação (o "molde").
 *
 * O QUE FAZ:  responde a uma pergunta simples: "a pergunta tem pelo menos
 *             UMA palavra que o bot conhece?". Se não tiver nenhuma, o
 *             chatbot devolve o fallback direto, sem procurar resposta.
 * COMO FAZ:  o método validar() percorre os tokens um por um e pergunta
 *             "este token é válido?" ao método abstrato tokenValido().
 *             Basta UM válido para devolver true (e parar de procurar).
 * ONDE:       pacote futebol.validacao. É a mãe da ValidadorDicionario.
 *             Quem usa é o Chatbot, no passo 2 do método responder().
 * POR QUE:    mesmo espírito do ProcessadorTexto (Template Method), só que
 *             menor: o ROTEIRO (o laço) fica pronto e travado na base, e o
 *             DETALHE (onde conferir cada token) fica com a filha. Se um dia
 *             a conferência for em outro lugar (uma lista de sinônimos, por
 *             exemplo), basta criar outra filha — o Chatbot não muda.
 *
 * Por ser "abstract", não dá para dar new ValidadorTokens(): ela é só o
 * molde; quem trabalha de verdade são as filhas concretas.
 */
public abstract class ValidadorTokens {

    /**
     * O ROTEIRO: percorre os tokens e devolve true se pelo menos um for válido.
     * Basta um token conhecido para a pergunta fazer sentido para o bot.
     *
     * @param tokens as palavras já preparadas da pergunta (palavras + pares)
     * @return true se existe ao menos um token conhecido; false se nenhum
     */
    public boolean validar(String[] tokens) {
        for (String token : tokens) {
            if (tokenValido(token)) {
                return true; // achou um conhecido: já pode parar (curto-circuito)
            }
        }
        return false; // percorreu todos e nenhum é conhecido
    }

    /**
     * O DETALHE que falta: COMO conferir um token? A base não sabe.
     * Cada filha concreta é obrigada a implementar (ex.: consultar o
     * dicionário). "protected" = só a base e as filhas enxergam este método;
     * quem usa de fora só chama o validar().
     */
    protected abstract boolean tokenValido(String token);
}
