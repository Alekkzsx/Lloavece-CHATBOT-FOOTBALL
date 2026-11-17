package futebol.texto;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

/**
 * Classe-base com as etapas que todos os separadores usam.
 *
 * A ordem do preparo é: normalizar o texto, separar as palavras, remover as
 * palavras comuns, reduzir alguns plurais e, por fim, permitir que uma
 * subclasse acrescente informações, como pares de palavras.
 */
public abstract class ProcessadorTexto {

    /**
     * Lista usada quando não é fornecida outra lista de palavras ignoradas.
     * A lista fica privada para impedir que outras classes alterem seu conteúdo.
     */
    private static final String[] PALAVRAS_COMUNS = {
            "a", "o", "as", "os", "um", "uma", "uns", "umas",
            "de", "do", "da", "dos", "das", "e", "em", "no", "na",
            "nos", "nas", "por", "pelo", "pela", "para", "pra", "com",
            "que", "qual", "quais", "como", "quando", "onde", "se",
            "eu", "meu", "minha", "seu", "sua", "ele", "ela", "voce",
            "voces", "me", "te", "lhe", "ao", "aos", "mais", "muito",
            "muita", "eh", "ser", "estar", "esta", "estao", "sou",
            "foi", "sao", "tem", "tenho", "ter", "isso", "isto",
            "esse", "essa", "este", "dele", "dela", "ou", "mas",
            "tambem", "ja", "la", "aqui", "ai", "bem", "sim", "nao",
            "pode", "poderia", "quero", "queria", "preciso", "favor",
            "devo", "entre", "sobre", "fazer", "quanto", "quantos",
            "quantas", "entao", "ne", "tudo", "coisa", "tipo"
    };

    /** Palavras que serão retiradas antes da comparação do conteúdo. */
    private String[] palavrasIgnoradas;

    /**
     * Cria um processador com a lista padrão de palavras comuns.
     *
     * Este construtor é usado pelas classes que herdam desta classe.
     */
    protected ProcessadorTexto() {
        this(null);
    }

    /**
     * Cria um processador com uma lista própria de palavras ignoradas.
     *
     * @param palavras lista que substituirá a lista padrão; se for nula,
     *                 será usada a lista padrão
     */
    protected ProcessadorTexto(String[] palavras) {
        definirPalavrasIgnoradas(palavras);
    }

    /**
     * Devolve uma cópia da lista atual de palavras ignoradas.
     *
     * A cópia evita que quem chamar este método altere a lista interna.
     *
     * @return vetor com as palavras ignoradas
     */
    public String[] obterPalavrasIgnoradas() {
        return palavrasIgnoradas.clone();
    }

    /**
     * Define quais palavras serão ignoradas.
     *
     * Cada palavra é normalizada para que, por exemplo, "VOCÊ" e "voce"
     * sejam comparadas da mesma forma.
     *
     * @param palavras palavras que devem ser ignoradas; nulo seleciona a lista padrão
     */
    public final void definirPalavrasIgnoradas(String[] palavras) {
        if (palavras == null) {
            // Copiamos a lista padrão para que cada objeto tenha sua própria lista.
            palavrasIgnoradas = PALAVRAS_COMUNS.clone();
            return;
        }

        // Mantém a quantidade recebida e normaliza cada palavra antes de guardá-la.
        palavrasIgnoradas = new String[palavras.length];
        int indice = 0;
        while (indice < palavras.length) {
            palavrasIgnoradas[indice] = normalizar(palavras[indice]);
            indice++;
        }
    }

    /**
     * Executa todas as etapas de preparo do texto, na ordem definida.
     *
     * Este método é final para que as classes filhas usem sempre a mesma ordem.
     *
     * @param texto frase original digitada pela pessoa
     * @return palavras preparadas e, se aplicável, seus pares consecutivos
     */
    public final String[] processar(String texto) {
        String[] palavras = separarPalavras(normalizar(texto));
        palavras = removerPalavrasIgnoradas(palavras);
        palavras = reduzirPlurais(palavras);
        return acrescentarPares(palavras);
    }

    /**
     * Separa o texto normalizado em palavras individuais.
     *
     * Cada classe filha escolhe como separar o texto.
     *
     * @param textoNormalizado texto em minúsculas e sem acentos
     * @return vetor com as palavras encontradas
     */
    public abstract String[] separarPalavras(String textoNormalizado);

    /**
     * Permite acrescentar informações depois do preparo das palavras.
     *
     * A implementação padrão não altera o vetor. A classe que cria pares
     * substitui este método para acrescentar as combinações.
     *
     * @param palavras palavras já filtradas e reduzidas
     * @return palavras recebidas, sem alterações
     */
    protected String[] acrescentarPares(String[] palavras) {
        return palavras;
    }

    /**
     * Coloca o texto em minúsculas e remove os sinais de acentuação.
     *
     * Um texto nulo é tratado como texto vazio para que as etapas seguintes
     * possam continuar sem tentar processar uma referência inexistente.
     *
     * @param texto frase original
     * @return frase em minúsculas e sem acentos, ou texto vazio se for nulo
     */
    public final String normalizar(String texto) {
        if (texto == null) {
            return "";
        }

        // A decomposição separa os acentos das letras; a expressão remove os acentos.
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Retira do vetor as palavras que estão na lista de palavras ignoradas.
     *
     * O vetor temporário começa com o tamanho máximo possível. Ao final,
     * Arrays.copyOf reduz o vetor ao número de palavras que foram mantidas.
     *
     * @param palavras palavras separadas do texto
     * @return novo vetor sem as palavras ignoradas
     */
    public String[] removerPalavrasIgnoradas(String[] palavras) {
        String[] resultado = new String[palavras.length];
        int quantidade = 0;
        int indice = 0;

        while (indice < palavras.length) {
            // Só guardamos a palavra quando ela não aparece na lista de exclusão.
            if (!contem(palavrasIgnoradas, palavras[indice])) {
                resultado[quantidade] = palavras[indice];
                quantidade++;
            }
            indice++;
        }

        return Arrays.copyOf(resultado, quantidade);
    }

    /**
     * Aplica a redução de plural a todas as palavras do vetor.
     *
     * @param palavras palavras que serão analisadas
     * @return novo vetor com as formas reduzidas
     */
    public String[] reduzirPlurais(String[] palavras) {
        String[] resultado = new String[palavras.length];
        int indice = 0;

        // Cada posição de saída corresponde à mesma posição de entrada.
        while (indice < palavras.length) {
            resultado[indice] = reduzirPlural(palavras[indice]);
            indice++;
        }
        return resultado;
    }

    /**
     * Tenta transformar uma palavra no plural em uma forma singular simples.
     *
     * Esta regra é apenas uma aproximação: palavras muito curtas não mudam;
     * terminações "oes" e "aes" viram "ao"; nos demais casos, remove-se um
     * "s" final, exceto quando a palavra termina em "ss".
     *
     * @param palavra palavra que será analisada
     * @return forma reduzida ou a palavra original quando nenhuma regra se aplica
     */
    public String reduzirPlural(String palavra) {
        if (palavra == null || palavra.length() < 4) {
            return palavra;
        }
        if (palavra.endsWith("oes") || palavra.endsWith("aes")) {
            return palavra.substring(0, palavra.length() - 3) + "ao";
        }
        if (palavra.endsWith("s") && !palavra.endsWith("ss")) {
            return palavra.substring(0, palavra.length() - 1);
        }
        return palavra;
    }

    /**
     * Normaliza e reduz as partes de uma palavra-chave composta.
     *
     * O sublinhado separa as partes da expressão e é mantido na resposta.
     * Por exemplo, "COPAS_DO_MUNDO" vira "copa_do_mundo".
     *
     * @param palavraChave expressão que será preparada
     * @return expressão em minúsculas, sem acentos e com plurais reduzidos
     */
    public String prepararPalavraChave(String palavraChave) {
        String[] partes = normalizar(palavraChave).split("_");
        int indice = 0;

        // Reduz cada trecho sem remover os sublinhados que formam a expressão.
        while (indice < partes.length) {
            partes[indice] = reduzirPlural(partes[indice]);
            indice++;
        }
        return String.join("_", partes);
    }

    /**
     * Procura uma palavra em um vetor usando comparação exata.
     *
     * @param lista vetor que será consultado
     * @param palavra palavra procurada
     * @return verdadeiro se a palavra estiver no vetor; falso caso contrário
     */
    private boolean contem(String[] lista, String palavra) {
        int indice = 0;

        while (indice < lista.length) {
            if (lista[indice].equals(palavra)) {
                return true;
            }
            indice++;
        }
        return false;
    }

    /**
     * Cria uma descrição simples deste processador para a demonstração.
     *
     * @return nome da classe e quantidade de palavras ignoradas
     */
    @Override
    public String toString() {
        return getClass().getSimpleName()
                + " (palavras ignoradas: " + palavrasIgnoradas.length + ")";
    }
}
