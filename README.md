# Lloavece FOOTBALL — Chatbot de Futebol

Projeto da disciplina **PPOO — 2026/2S — Avaliação III (Prova 003 — Chatbot)**.

O tema deste projeto é futebol. O repositório contém uma base textual de conhecimento sobre futebol e uma demonstração em Java do processamento de mensagens. **A demonstração de tokens ainda não é um chatbot completo:** o código atual não carrega os arquivos de `base/` para gerar respostas.

## O que existe

### Demonstração Java

O pacote `src/chatbot/texto/` contém as classes de processamento de texto:

- `ProcessadorTexto`: classe abstrata que define o pipeline comum, usando o padrão Template Method.
- `TokenizadorSimples`: separa o texto em palavras (unigramas).
- `TokenizadorNGramas`: herda do tokenizador simples e acrescenta pares consecutivos de palavras (bigramas), como `copa_mundo`.
- `DemoTokens`: demonstra as etapas do pipeline e compara os tokenizadores.

O pipeline normaliza o texto (minúsculas e sem acentos), separa tokens, remove stopwords, reduz algumas terminações de plural e, no caso de `TokenizadorNGramas`, acrescenta bigramas. Por exemplo, uma pergunta sobre o maior artilheiro das Copas vira tokens de palavras como `maior`, `artilheiro` e `copa`, além de combinações consecutivas no tokenizador de n-gramas.

### Base textual de futebol

A pasta `base/` contém arquivos UTF-8 organizados por assunto: jogadores, clubes, seleções, competições, história, regras, termos, posições, estádios e premiações. Muitos arquivos usam o formato `palavras-chave | resposta`. A pasta `base/texto/` contém dados auxiliares, como stopwords, sinônimos, interações e respostas de fallback.

Esses arquivos são conteúdo para uma futura integração com o chatbot; a demonstração Java atual não os lê.

## Como compilar e executar

É necessário ter o **JDK 17 ou superior**. Execute os comandos a partir da raiz do repositório.

Compile as classes:

```bash
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src -name "*.java")
```

Passe uma pergunta como argumento:

```bash
java -cp bin chatbot.DemoTokens "Quem é o maior artilheiro das Copas?"
```

Ou inicie o modo interativo:

```bash
java -cp bin chatbot.DemoTokens
```

No modo interativo, digite perguntas sobre futebol; escreva `sair` para encerrar. A demonstração mostra as etapas da tokenização, não uma resposta consultada na base de conhecimento.

## Licença

[Apache License 2.0](LICENSE)
