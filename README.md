# ⚽ Lloavece Futebol — Chatbot de Futebol

Projeto da disciplina **PPOO — 2026/2S — Avaliação III (Prova 003 — Assistente virtual)**.

Um chatbot que roda **no terminal** e responde perguntas sobre futebol consultando uma base de conhecimento em arquivos de texto (**410 entradas**: jogadores, clubes, seleções, competições, regras, história, termos, posições, estádios e premiações).

É **Java puro (17 ou superior)**: sem Maven/Gradle, sem nenhuma biblioteca externa. Só `javac` e `java`. O código foi escrito de propósito do jeito mais simples possível (laços `for`, `if`, vetores `String[]` e `ArrayList`) e **todos os arquivos são comentados em português**, explicando o que cada coisa faz, como, onde e por quê.

**Em uma frase:** a pergunta vira *tokens* (palavras úteis + pares de palavras vizinhas), os tokens são validados contra o vocabulário da base, e a entrada com mais palavras-chave em comum vence; se nada for reconhecido, o bot devolve uma mensagem padrão (*fallback*).

---

## Como rodar

Execute a partir da **raiz do projeto** (a pasta que contém `base/` e `src/`):

```bash
# 1) compilar todos os .java para a pasta bin
javac -encoding UTF-8 -d bin $(find src -name "*.java")

# 2) abrir o terminal de conversa
java -cp bin futebol.Terminal
```

Exemplo de conversa real:

```
==============================================
   LLOAVECE FUTEBOL — seu chatbot de futebol
==============================================
Conhecimento carregado: 410 entradas.
Pergunte sobre jogadores, clubes, regras, Copas...
Digite 'sair' para encerrar a conversa.

Você: quem é o maior artilheiro das copas?
Bot: Miroslav Klose (Alemanha) é o maior artilheiro da história das Copas, com 16 gols.
     Ronaldo (Brasil) tem 15 e Gerd Müller 14.

Você: flamengo
Bot: Flamengo: clube do Rio de Janeiro, o mais popular do Brasil. Ídolo maior: Zico. (...)

Você: xyzabc banana nada
Bot: Desculpe, não sei responder isso ainda. Tente perguntar sobre jogadores, clubes, regras ou Copas.

Você: sair
Bot: Até logo!
```

- Digite **`sair`** (ou aperte **Ctrl+D**) para encerrar.
- Linha vazia é ignorada.
- Acentos e maiúsculas não atrapalham: `quem é pelé?` funciona igual a `QUEM E PELE`.

---

## Estrutura do projeto

```
Lloavece-CHATBOT-FOOTBAL/
├── README.md                  ← este arquivo
├── LICENSE                    ← licença Apache 2.0
├── base/                      ← TODO o conhecimento do bot (arquivos .txt)
│   ├── LEIA-ME.txt            ← explicação do formato da base
│   ├── clubes/                ← times do Brasil, Europa e América do Sul
│   ├── competicoes/           ← Copa do Mundo, Champions, Libertadores, Brasileirão...
│   ├── estadios/              ← estádios famosos
│   ├── historia/              ← origem do futebol, marcos, recordes, futebol feminino
│   ├── jogadores/             ← lendas, geração atual, técnicos, futebol feminino
│   ├── posicoes/              ← posições e formações táticas
│   ├── premiacoes/            ← Bola de Ouro, prêmios e afins
│   ├── regras/                ← impedimento, pênaltis, cartões, VAR...
│   ├── selecoes/              ← seleções principais e a do Brasil
│   ├── termos/                ← glossário, dribles, táticas
│   └── texto/                 ← arquivos AUXILIARES (ainda não lidos pelo código,
│                                ver a seção "A base de conhecimento")
├── src/futebol/               ← o código Java (8 arquivos)
│   ├── Terminal.java          ← 🖥️ a "tela": o arquivo que o usuário roda
│   ├── Chatbot.java           ← 🧠 o "cérebro": coordena as três peças
│   ├── texto/                 ← preparo da pergunta
│   │   ├── ProcessadorTexto.java        ← classe BASE (roteiro fixo de 5 etapas)
│   │   ├── SeparadorPalavras.java       ← quebra a frase em palavras
│   │   └── SeparadorPalavrasEPares.java ← palavras + pares ("copa_mundo")
│   ├── dicionario/            ← o conhecimento em memória
│   │   └── Dicionario.java              ← carrega a base/ e busca a resposta
│   └── validacao/             ← a conferência dos tokens
│       ├── ValidadorTokens.java         ← classe BASE (roteiro da validação)
│       └── ValidadorDicionario.java     ← confere o token no dicionário
└── bin/                       ← .class gerados na compilação (ignorado pelo git)
```

**Por que pastas assim:** cada pasta tem UMA responsabilidade (`texto` prepara, `validacao` confere, `dicionario` conhece). Na raiz ficam só os dois arquivos que amarram tudo: o `Terminal` (entrada/saída) e o `Chatbot` (coordenação).

---

## O caminho de uma pergunta (passo a passo)

```
Você digita a pergunta
   ↓
Terminal        → lê a linha e entrega ao Chatbot (ele não pensa, só conversa)
   ↓
Chatbot         → passo 1: ProcessadorTexto.processar(pergunta)
   ↓                       1. normalizar: minúsculas + sem acento
   ↓                       2. separar:    quebrar em palavras
   ↓                       3. filtrar:    tirar palavras comuns ("de", "o"...)
   ↓                       4. reduzir:    plural → singular aproximado
   ↓                       5. pares:      acrescentar palavras vizinhas com "_"
   ↓
Chatbot         → passo 2: ValidadorTokens.validar(tokens)
   ↓                       nenhum token conhecido? → devolve o FALLBACK
   ↓
Chatbot         → passo 3: Dicionario.buscarResposta(tokens)
   ↓                       pontua cada entrada; a maior pontuação vence
   ↓                       ninguém pontuou? → devolve o FALLBACK
   ↓
Terminal        → imprime "Bot: " + a resposta
```

Exemplo real, com `"Quem é o maior artilheiro das Copas?"`:

| Etapa | Resultado |
|---|---|
| 1. normalizar | `quem e o maior artilheiro das copas?` |
| 2. separar | `[quem, e, o, maior, artilheiro, das, copas]` |
| 3. filtrar comuns | `[quem, maior, artilheiro, copas]` |
| 4. reduzir plural | `[quem, maior, artilheiro, copa]` |
| 5. acrescentar pares | `[quem, maior, artilheiro, copa, quem_maior, maior_artilheiro, artilheiro_copa]` |
| validação | `artilheiro` existe no vocabulário da base → pergunta válida ✔ |
| busca | a entrada sobre artilheiros das Copas tem as chaves `artilheiro` e `copa` (2 pontos) **+ 2 de bônus** pelo par `artilheiro_copa` → vence → resposta do Klose |

**Os dois pontos de fallback:** (a) nenhum token conhecido — o bot nem procura; (b) tokens conhecidos mas nenhuma entrada pontuou. Nos dois casos a resposta é a mensagem `FALLBACK` definida em `Chatbot.java`.

---

## O que cada arquivo faz (o quê / como / onde / por quê)

### `src/futebol/Terminal.java` — a tela
- **O que faz:** mostra as boas-vindas, lê cada pergunta e imprime a resposta, até `sair`.
- **Como faz:** laço `while (true)` com `Scanner`: imprime `Você: `, lê a linha (`nextLine().trim()`), ignora linha vazia, encerra com `sair`/Ctrl+D e imprime `Bot: ` + o que o Chatbot devolver.
- **Onde:** é o **ponto de entrada** (`main`). Único arquivo que o usuário executa.
- **Por que:** separar a TELA do CÉREBRO — aqui só existe entrada/saída, nenhuma regra de resposta. Cada classe com um único trabalho.

### `src/futebol/Chatbot.java` — o cérebro
- **O que faz:** recebe a pergunta e devolve a resposta (método `responder()`).
- **Como faz:** chama as três peças na ordem fixa: processar → validar → buscar. Guarda a mensagem `FALLBACK` e a usa nos dois pontos de falha.
- **Onde:** raiz do `src`. Chamado pelo Terminal; chama `texto/`, `validacao/` e `dicionario/`. É o único lugar que conhece as três peças juntas.
- **Por que:** coordenar sem fazer o trabalho — se uma peça for trocada (outro separador, outro validador), basta mudar uma linha do construtor. Os atributos usam os **tipos base** (`ProcessadorTexto`, `ValidadorTokens`): polimorfismo.

### `src/futebol/texto/ProcessadorTexto.java` — o molde do preparo
- **O que faz:** define o roteiro de 5 etapas que transforma qualquer frase em tokens comparáveis com a base.
- **Como faz:** `processar()` é **`final`** (nenhuma filha muda a ordem): normalizar → separar → filtrar → reduzir plural → gancho dos pares. A etapa 2 é **`abstract`** (a filha é obrigada a implementar); a etapa 5 é um **gancho** que por padrão não faz nada. A lista de palavras comuns fica na própria classe, já normalizada. A remoção de acento usa duas strings "irmãs" (`á→a`, `ç→c`...), letra por letra.
- **Onde:** mãe de `SeparadorPalavras` (que é mãe de `SeparadorPalavrasEPares`). Usada pelo Chatbot (pergunta) e pelo Dicionario (chaves) — **os dois preparam texto do mesmo jeito**, senão a comparação não funcionaria.
- **Por que:** padrão **Template Method** — roteiro fixo na base, detalhes variáveis nas filhas. Todo separador prepara igual, mudando só o que precisa.

### `src/futebol/texto/SeparadorPalavras.java` — a separação de verdade
- **O que faz:** implementa a etapa 2: quebra a frase em palavras, uma por token.
- **Como faz:** percorre o texto com `charAt`; letra/número (`Character.isLetterOrDigit`) continua montando a palavra, qualquer outra coisa é divisor e fecha a palavra. No fim, guarda a última palavra (o texto pode terminar sem divisor).
- **Onde:** filha de `ProcessadorTexto`, mãe de `SeparadorPalavrasEPares`. O `Dicionario` usa esta classe (sem pares) para preparar as chaves da base.
- **Por que:** preencher o único passo que a base não sabe fazer, do jeito mais simples possível — sem regex, sem `split`.

### `src/futebol/texto/SeparadorPalavrasEPares.java` — palavras + pares
- **O que faz:** tudo da mãe **e** acrescenta pares de vizinhas: `maior artilheiro` → token extra `maior_artilheiro`.
- **Como faz:** herda a separação pronta e sobrescreve só o gancho `acrescentarPares()`: copia as palavras e, com N palavras, cria os N−1 pares `palavra_i + "_" + palavra_i+1`.
- **Onde:** fecha a cadeia de herança de 3 níveis. É a classe escolhida pelo Chatbot para as perguntas.
- **Por que:** pares reconhecem **expressões de duas palavras** (`copa_mundo`, `artilheiro_copa`), que são mais específicas que palavras soltas — por isso valem bônus na pontuação.

### `src/futebol/dicionario/Dicionario.java` — o conhecimento
- **O que faz:** carrega toda a `base/` para a memória e responde a duas consultas: `contemToken()` (validação) e `buscarResposta()` (escolha da resposta).
- **Como faz:** na criação, percorre as subpastas (pulando `texto/`), lê cada `.txt` linha por linha, ignora vazias e comentários `#`, corta no `|`, prepara as chaves com `SeparadorPalavras` e guarda em três listas: `palavrasChave` e `respostas` (índices alinhados) e `tokensConhecidos` (vocabulário sem repetição). Na busca, pontua cada entrada: **+1** por palavra-chave presente na pergunta, **+2** por par cujas duas metades são chaves da mesma entrada; vence a maior pontuação, empate mantém a primeira carregada; zero pontos devolve `null`.
- **Onde:** criado pelo Chatbot (uma única carga); consultado pelo Chatbot e pelo ValidadorDicionario.
- **Por que:** conhecimento em `.txt` **fora do código** — para ensinar algo novo basta editar um arquivo da base, sem recompilar. Arquivo com erro de leitura gera aviso, não derruba o bot.

### `src/futebol/validacao/ValidadorTokens.java` — o molde da validação
- **O que faz:** responde "a pergunta tem pelo menos UMA palavra conhecida?".
- **Como faz:** `validar()` percorre os tokens e chama o método **`abstract`** `tokenValido()`; basta um `true` para parar e aprovar (curto-circuito).
- **Onde:** mãe de `ValidadorDicionario`; usada pelo Chatbot no passo 2.
- **Por que:** mesmo Template Method do processador, em versão menor: o **laço** fica travado na base, o **lugar da consulta** fica com a filha. Se nenhum token é conhecido, o bot responde o fallback sem gastar busca.

### `src/futebol/validacao/ValidadorDicionario.java` — a consulta concreta
- **O que faz:** define o detalhe que faltava: token válido = token que existe no vocabulário da base.
- **Como faz:** herda o laço e implementa `tokenValido()` em uma linha: `dicionario.contemToken(token)`.
- **Onde:** criada pelo Chatbot, que **entrega o dicionário pronto** pelo construtor.
- **Por que:** receber o dicionário em vez de criar outro evita carregar a base do disco duas vezes e garante que validação e busca usam os mesmos dados.

---

## Conceitos de POO usados (e onde olhar)

| Conceito | Onde está | Para que serve aqui |
|---|---|---|
| Classe abstrata | `ProcessadorTexto`, `ValidadorTokens` | São moldes: não viram objeto, obrigam as filhas a completar os passos |
| Herança | `SeparadorPalavras` → `ProcessadorTexto`; `SeparadorPalavrasEPares` → `SeparadorPalavras`; `ValidadorDicionario` → `ValidadorTokens` | Reaproveitar o roteiro pronto e escrever só o detalhe novo |
| Polimorfismo | atributos do `Chatbot` com tipos base | O cérebro usa as peças pelo contrato da base; trocar a filha não muda o Chatbot |
| Template Method + `final` | `ProcessadorTexto.processar()` | Trava a ordem das 5 etapas para todas as filhas |
| Gancho (hook) | `acrescentarPares()` | Etapa opcional: a filha sobrescreve só se quiser acrescentar algo |
| Sobrescrita (`@Override`) | `separarPalavras()`, `acrescentarPares()`, `tokenValido()` | As filhas preenchem os detalhes do molde |
| Encapsulamento | campos `private`/`final`, métodos internos `private`, ganchos `protected` | Esconder o "como"; de fora só se usa `processar()`, `validar()`, `responder()`... |
| Composição | `Chatbot` TEM UM dicionário, um processador e um validador | O bot é montado de peças, cada uma com um único trabalho |
| Injeção pelo construtor | `ValidadorDicionario(Dicionario)` | Receber a dependência pronta, sem carregar a base duas vezes |

---

## A base de conhecimento (`base/`)

Cada linha útil dos arquivos de assunto é uma entrada no formato:

```
palavras-chave | resposta
```

Exemplo real (de `base/competicoes/copa_do_mundo.txt`):

```
copa do mundo artilheiro historia | Miroslav Klose (Alemanha) é o maior artilheiro da história das Copas, com 16 gols. Ronaldo (Brasil) tem 15 e Gerd Müller 14.
```

Regras do formato:
- linhas começando com `#` são **comentários**; linhas vazias são ignoradas;
- linha sem `|` é ignorada; entrada sem chave ou sem resposta também;
- as chaves podem ter acento/maiúscula/plural: elas passam pelo **mesmo preparo** da pergunta antes de serem guardadas;
- tudo em **UTF-8**.

**Para ensinar algo novo ao bot:** adicione uma linha `chave | resposta` em qualquer `.txt` de assunto e abra o Terminal de novo. **Não precisa recompilar** — a base é lida toda vez que o programa inicia.

### Os arquivos auxiliares de `base/texto/` — status atual

| Arquivo | Conteúdo | Situação hoje |
|---|---|---|
| `stopwords.txt` | lista de palavras comuns | **Não é lido.** A lista usada de verdade está no código, em `ProcessadorTexto.java` (decisão de simplicidade) |
| `sinonimos.txt` | apelido → nome principal (`rei \| pele`) | **Ainda não é lido** — sinônimos não são aplicados |
| `interacoes.txt` | saudações, ajuda, despedidas | **Ainda não é lido** |
| `fallback.txt` | respostas alternativas de "não entendi" | **Ainda não é lido** — a mensagem de fallback é fixa em `Chatbot.java` |

A pasta `texto/` é **pulada de propósito** na carga do dicionário, porque esses arquivos não são entradas de conhecimento.

---

## Por que o código é tão simples (decisões do projeto)

- **Sem Maven/Gradle:** compila com um comando `javac`; nenhuma dependência para instalar.
- **Sem regex, sem `split`, sem `Normalizer`:** a separação é letra por letra com `if`; a remoção de acento usa duas strings "irmãs". Mais linhas, porém cada linha é óbvia.
- **`ArrayList` + `for` + `String[]`:** as estruturas mais básicas possíveis; a conversão lista→vetor é feita na mão (`paraVetor`).
- **Palavras comuns no código:** evita leitura de arquivo dentro do processador; a comparação é um `contem()` com laço.
- **Plural na mão:** três regrinhas (`oes/aes → ao`, tira `s` final, não mexe em palavra curta). É um quebra-galho assumido, não um lematizador.
- **Fallback fixo:** uma constante em `Chatbot.java`, o mínimo que funciona.
- **Cada arquivo se basta:** helpers pequenos (como o `contem()`) se repetem em duas classes de propósito, para uma não depender da outra.

## Limitações atuais (o que ele ainda NÃO faz)

- Não aplica **sinônimos** (`sinonimos.txt` não é lido).
- Não trata **saudações/interações** como conversa social (`interacoes.txt` não é lido).
- O fallback é **uma mensagem fixa**, não sorteada do `fallback.txt`.
- A redução de plural é heurística e **pode errar** (ex.: `luis` viraria `lui`).
- A busca é por **palavra exata**: erro de digitação (`flamengoo`) não é reconhecido.
- Em empate de pontuação, vence a **primeira entrada carregada** (ordem dos arquivos).

## Licença

[Licença Apache 2.0](LICENSE)
