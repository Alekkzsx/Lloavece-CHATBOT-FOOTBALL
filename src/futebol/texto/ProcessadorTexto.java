package futebol.texto; // pasta "texto": tudo que transforma a frase em tokens

import java.util.ArrayList; // lista que cresce sozinha (mais simples que redimensionar vetor)

/**
 * PROCESSADOR DE TEXTO — a CLASSE BASE do preparo de texto (o "molde").
 *
 * O QUE FAZ:  transforma qualquer frase em tokens prontos para comparar com
 *             as palavras-chave do dicionário (minúsculos, sem acento, sem
 *             palavras comuns e com plural reduzido).
 * COMO FAZ:   o método processar() executa um roteiro FIXO de 5 etapas:
 *
 *               1. normalizar  -> tudo em minúsculo e sem acento;
 *               2. separar     -> quebrar a frase em palavras (a FILHA decide como);
 *               3. filtrar     -> jogar fora palavras comuns ("de", "o", "qual"...);
 *               4. reduzir     -> tirar o plural de forma simples ("copas" -> "copa");
 *               5. acrescentar -> gancho opcional para a filha adicionar algo (pares).
 *
 * ONDE:       pacote futebol.texto. É a mãe de SeparadorPalavras (que é mãe
 *             de SeparadorPalavrasEPares). Quem usa é o Chatbot e o Dicionario
 *             (os dois preparam texto do MESMO jeito, para poder comparar).
 * POR QUE:    padrão TEMPLATE METHOD: o roteiro fica pronto e travado aqui
 *             (processar é "final", nenhuma filha pode mudar a ordem), e só
 *             os passos variáveis ficam com as filhas. Assim todo separador
 *             prepara o texto igual, mudando apenas o que precisa mudar.
 *
 * Por ser "abstract", esta classe NÃO pode virar objeto (new ProcessadorTexto()
 * dá erro de compilação): ela é só o molde; quem trabalha são as filhas.
 */
public abstract class ProcessadorTexto {

    /**
     * Etapa 3 usa esta lista: palavras comuns (stopwords) que NÃO ajudam a
     * descobrir o assunto da pergunta. Ex.: em "qual é o rei do futebol?"
     * só interessam "rei" e "futebol".
     *
     * Já estão em minúsculo e sem acento, igual ao texto depois de normalizar
     * (senão a comparação nunca encontraria "e" dentro de "é", por exemplo).
     *
     * Por que a lista está no código e não em arquivo: simplicidade — assim
     * esta classe não precisa ler nada do disco. O arquivo base/texto/
     * stopwords.txt existe como material para uma integração futura.
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

    /**
     * O ROTEIRO COMPLETO: prepara o texto e devolve os tokens.
     * É o único método que quem usa a classe precisa chamar.
     *
     * "final" = nenhuma classe filha pode sobrescrever este método.
     * A ordem das 5 etapas é uma regra da base e vale para TODAS as filhas.
     *
     * @param texto a frase digitada (pode ter acento, maiúscula, pontuação...)
     * @return vetor com as palavras úteis (e os pares, se a filha criar)
     */
    public final String[] processar(String texto) {
        String normalizado = normalizar(texto);           // etapa 1: minúsculo + sem acento
        String[] palavras = separarPalavras(normalizado); // etapa 2: a FILHA separa
        palavras = removerPalavrasComuns(palavras);       // etapa 3: tira stopwords
        palavras = reduzirPlurais(palavras);              // etapa 4: singular aproximado
        return acrescentarPares(palavras);                // etapa 5: gancho da filha
    }

    /**
     * Etapa 2 — o "buraco" do molde: a base NÃO sabe separar palavras.
     * Por ser "abstract", toda filha concreta é OBRIGADA a implementar.
     * "protected" = só a própria classe e as filhas enxergam este método;
     * quem usa o bot de fora só conhece o processar().
     */
    protected abstract String[] separarPalavras(String textoNormalizado);

    /**
     * Etapa 5 — GANCHO (hook): por padrão não faz nada, só devolve as palavras
     * como chegaram. É opcional: a filha só sobrescreve se quiser acrescentar
     * algo. A SeparadorPalavrasEPares sobrescreve para adicionar os pares;
     * a SeparadorPalavras nem mexe aqui e fica só com as palavras.
     */
    protected String[] acrescentarPares(String[] palavras) {
        return palavras;
    }

    /**
     * Etapa 1: deixa tudo em minúsculo e troca letras acentuadas por simples.
     * Ex.: "Copas É CAMPEÕES" -> "copas e campeoes".
     * Por que na mão (sem Normalizer/regex): para o código ficar o mais
     * simples possível — só charAt, indexOf e concatenação.
     */
    private String normalizar(String texto) {
        if (texto == null) {
            return ""; // texto nulo vira texto vazio, para não dar erro adiante
        }
        texto = texto.toLowerCase(); // primeiro, tudo em minúsculo

        // Duas strings "irmãs": cada letra acentuada tem a versão simples
        // na MESMA posição da outra string (á->a, ç->c, ñ->n...).
        String comAcento = "áàâãäéèêëíìîïóòôõöúùûüçñ";
        String semAcento = "aaaaaeeeeiiiiooooouuuucn";

        // Percorre letra por letra montando o texto novo.
        String resultado = "";
        for (int i = 0; i < texto.length(); i++) {
            char letra = texto.charAt(i);
            int posicao = comAcento.indexOf(letra); // -1 = não é acentuada
            if (posicao >= 0) {
                letra = semAcento.charAt(posicao); // troca pela simples equivalente
            }
            resultado = resultado + letra; // letras comuns passam sem mudança
        }
        return resultado;
    }

    /**
     * Etapa 3: monta um novo vetor só com as palavras que NÃO são comuns.
     * Usa ArrayList porque não sabemos quantas palavras vão sobrar —
     * a lista cresce sozinha e no fim vira vetor com paraVetor().
     */
    private String[] removerPalavrasComuns(String[] palavras) {
        ArrayList<String> uteis = new ArrayList<>();
        for (String palavra : palavras) {
            if (!contem(PALAVRAS_COMUNS, palavra)) {
                uteis.add(palavra); // só guarda quem não está na lista de comuns
            }
        }
        return paraVetor(uteis);
    }

    /** Etapa 4: aplica a redução de plural em cada palavra do vetor. */
    private String[] reduzirPlurais(String[] palavras) {
        String[] resultado = new String[palavras.length]; // mesmo tamanho: 1 por 1
        for (int i = 0; i < palavras.length; i++) {
            resultado[i] = reduzirPlural(palavras[i]);
        }
        return resultado;
    }

    /**
     * Redução de plural bem simples — é um "quebra-galho", não um dicionário:
     * - menos de 4 letras: não mexe (protege palavras como "gol", "rei", "país");
     * - termina em "oes"/"aes": troca por "ao" (campeoes -> campeao);
     * - termina em "s" (mas não em "ss"): tira o "s" (copas -> copa).
     *
     * Por que tão simples: cobre a maioria dos plurais do futebol sem
     * complicar. Pode errar em palavra que termina em "s" sem ser plural
     * (ex.: "luis" viraria "lui") — limitação assumida do projeto.
     */
    private String reduzirPlural(String palavra) {
        if (palavra.length() < 4) {
            return palavra; // curta demais: melhor não arriscar
        }
        if (palavra.endsWith("oes") || palavra.endsWith("aes")) {
            // tira as 3 últimas letras ("oes"/"aes") e põe "ao" no lugar
            return palavra.substring(0, palavra.length() - 3) + "ao";
        }
        if (palavra.endsWith("s") && !palavra.endsWith("ss")) {
            // tira só o "s" final ("ss" fica: ex. "glass" não é plural)
            return palavra.substring(0, palavra.length() - 1);
        }
        return palavra; // não parece plural: devolve igual
    }

    /**
     * Procura uma palavra dentro de um vetor (comparação exata, com equals).
     * Existe porque vetor puro não tem um "contains" pronto — fazemos na mão.
     */
    private boolean contem(String[] vetor, String palavra) {
        for (String item : vetor) {
            if (item.equals(palavra)) {
                return true; // achou: pode parar de procurar
            }
        }
        return false; // percorreu tudo e não achou
    }

    /**
     * Copia uma lista para um vetor String[] (na mão, com um laço).
     * É "protected" de propósito: as classes filhas também precisam dele
     * para devolver seus vetores (SeparadorPalavras e SeparadorPalavrasEPares).
     */
    protected String[] paraVetor(ArrayList<String> lista) {
        String[] vetor = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            vetor[i] = lista.get(i);
        }
        return vetor;
    }
}
