# Lloavece CAR — Chatbot 🚗

Projeto da disciplina **PPOO — 2026/2S — Avaliação III (Prova 003 — Chatbot)**.

## 🎯 Etapa atual: arguição de 17/11/2026

O requisito desta data é apresentar **o tema**, **a estrutura do chatbot** e **a classe base para separação dos TOKENs**. Este repositório contém exatamente isso:

1. **Tema** definido (abaixo);
2. **Estrutura planejada** (diagrama abaixo);
3. **Classe base de TOKENs implementada e demonstrável** (`src/chatbot/texto/`).

> A versão completa do projeto (chatbot funcional + relatório), já desenvolvida, está guardada em [`_versao-completa/`](_versao-completa/) para a entrega final.

## 1. Tema

Chatbot **tradicional** (baseado em regras e palavras-chave, sem IA/bibliotecas de chatbot) de **assistência automotiva em português**: responde dúvidas sobre troca de óleo, pneus, bateria, freios, superaquecimento, consumo, revisão, compra de usado, IPVA, seguro, etc.

Exemplos de perguntas previstas: *"Quando trocar o óleo do motor?"*, *"Meu carro não liga, o que pode ser?"*, *"Com que frequência calibrar os pneus?"*.

## 2. Estrutura planejada

```
chatbot/
├── DemoTokens                 ✅ implementado (demo da arguição)
├── Main / Chatbot / Conversa / Mensagem        🔜 entrega final
├── texto/                     ✅ IMPLEMENTADO (foco da arguição)
│   ├── ProcessadorTexto       ← CLASSE ABSTRATA BASE dos TOKENs
│   ├── TokenizadorSimples     ← herda da base (unigramas)
│   └── TokenizadorNGramas     ← herda de Simples (unigramas + bigramas)
├── conhecimento/              🔜 entrega final
│   ├── Resposta (abstrata) ← RespostaFixa / RespostaSorteada
│   ├── Regra (palavras-chave + pesos + limiar)
│   └── BaseConhecimento (seleção por pontuação + fallback)
└── io/                        🔜 entrega final
    ├── GerenciadorArquivos (leitura/escrita UTF-8)
    └── CarregadorBase (parser da base em arquivo)
```

Fluxo: `mensagem → ProcessadorTexto (tokens) → BaseConhecimento (pontua regras) → Resposta.gerar() → histórico em arquivo`.

## 3. Classe base para separação dos TOKENs (foco da arguição)

**`ProcessadorTexto`** (abstrata) usa o padrão **Template Method**: o método `final processar(String)` fixa o pipeline e delega às subclasses apenas a estratégia de separação:

| Etapa | Método | Quem define |
|---|---|---|
| 1. Normalizar (caixa baixa, sem acentos via NFD) | `normalizar` | base |
| 2. **Separar tokens** | `tokenizar` — **abstrato** | subclasse |
| 3. Remover stopwords (~100 palavras PT-BR) | `removerStopwords` | base |
| 4. Reduzir radicais (plural → singular: `pneus→pneu`) | `reduzirRadicais` | base |
| 5. Enriquecer (gancho opcional) | `enriquecer` | subclasse (opcional) |

- **`TokenizadorSimples`**: separa por qualquer caractere que não seja letra/dígito → unigramas.
- **`TokenizadorNGramas`**: herda de `TokenizadorSimples` e sobrescreve o gancho `enriquecer()` para acrescentar **bigramas** (`trocar_oleo`, `carro_liga`), permitindo palavras-chave compostas na base.
- **Por que abstrata?** O pipeline é sempre o mesmo; só a estratégia de tokenização varia. A base garante o contrato e as subclasses especializam — isso dá **herança em cadeia** e **polimorfismo** (`ProcessadorTexto p = new TokenizadorNGramas(); p.processar(frase)`).

Exemplo real: `"Quando trocar o óleo do motor?"` → normaliza `quando trocar o oleo do motor?` → tokens `[quando, trocar, o, oleo, do, motor]` → stopwords `[trocar, oleo, motor]` → bigramas `[trocar, oleo, motor, trocar_oleo, oleo_motor]`.

Conceitos da disciplina já visíveis nestas 4 classes: classe abstrata, herança, polimorfismo, construtores (com sobrecarga), get/set, sobreposição de `toString`.

## 4. Como demonstrar (JDK 17+)

```bash
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp bin chatbot.DemoTokens "Quando trocar o óleo do motor?"
java -cp bin chatbot.DemoTokens        # modo interativo
```

A demo imprime cada etapa do pipeline e o resultado final dos dois tokenizadores via polimorfismo.

## 5. Roteiro rápido de arguição (~2 min)

1. **Tema**: assistente automotivo PT-BR, chatbot tradicional por regras (sem libs de chatbot).
2. **Estrutura**: 4 pacotes — núcleo, `texto` (tokens), `conhecimento` (regras/respostas), `io` (arquivos).
3. **Classe base de TOKENs**: `ProcessadorTexto` abstrata, Template Method, método abstrato `tokenizar`, gancho `enriquecer`; subclasses Simples e NGramas.
4. **Demo ao vivo**: rodar `DemoTokens` com uma frase e explicar as 5 etapas.

## Licença

[Apache License 2.0](LICENSE)
