# Lloavece Futebol — Assistente de Futebol

Projeto da disciplina **PPOO — 2026/2S — Avaliação III (Prova 003 — Assistente virtual)**.

O tema deste projeto é futebol. O repositório contém uma base textual de conhecimento sobre futebol e uma demonstração em Java que prepara perguntas. **A demonstração ainda não é um assistente virtual completo:** o código atual não carrega os arquivos de `base/` para gerar respostas.

## O que existe

### Demonstração Java

O pacote `src/futebol/texto/` contém as classes de preparação do texto:

- `ProcessadorTexto`: classe abstrata que define as etapas comuns.
- `SeparadorPalavras`: separa o texto em palavras individuais.
- `SeparadorPalavrasEPares`: herda do separador simples e também cria pares de palavras vizinhas, como `maior_artilheiro`.
- `DemonstracaoTexto`: mostra cada etapa e compara os dois separadores.

O programa coloca o texto em minúsculas, remove acentos, separa as palavras, remove palavras comuns e reduz alguns plurais. O separador de pares também junta palavras vizinhas com `_`. Por exemplo, uma pergunta sobre o maior artilheiro das Copas resulta em `maior`, `artilheiro`, `copa` e pares como `maior_artilheiro`.

O código usa vetores simples (`String[]`) e laços `while` para manter os exemplos acessíveis a quem está começando.

Os comentários das classes explicam a finalidade de cada método, seus dados de entrada e saída e os trechos cuja lógica pode não ser evidente à primeira vista. Os comentários dos arquivos de `base/` explicam o formato e o assunto dos dados; eles não significam que esses arquivos já sejam lidos pelo programa.

### Base textual de futebol

A pasta `base/` contém arquivos UTF-8 organizados por assunto: jogadores, clubes, seleções, competições, história, regras, termos, posições, estádios e premiações. Muitos arquivos usam o formato `palavras-chave | resposta`. A pasta `base/texto/` contém dados auxiliares, como palavras comuns, sinônimos, interações e respostas alternativas.

Esses arquivos são conteúdo para uma futura integração com o assistente virtual; a demonstração Java atual não os lê.

## Como compilar e executar

É necessário ter o **Java 17 ou superior**. Execute os comandos a partir da raiz do repositório.

Compile as classes:

```bash
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src -name "*.java")
```

Passe uma pergunta como argumento:

```bash
java -cp bin futebol.DemonstracaoTexto "Quem é o maior artilheiro das Copas?"
```

Ou inicie o modo interativo:

```bash
java -cp bin futebol.DemonstracaoTexto
```

No modo interativo, digite perguntas sobre futebol; escreva `sair` para encerrar. A demonstração mostra as etapas de preparação das palavras, mas não consulta a base de conhecimento para responder.

## Licença

[Licença Apache 2.0](LICENSE)
