package ordenacao;

import modelo.Pet;

/**
 * Narra a ordenação na linguagem da clínica (modo passo a passo, até 10 pets).
 * Os algoritmos só chamam o narrador quando narrar == true; na Grande Corrida ele fica calado.
 *
 * Exemplo:
 *   ♥ Bubble Sort · passagem 1
 *     Luna (60218841) > Thor (31057720)?   SIM → trocam de cadeira!
 *     Luna (60218841) > Zeus (70334512)?   NÃO → Luna continua cochilando.
 *     fim da passagem 1 → comparações: 4 | trocas: 3
 */
public class NarradorClinica {

    private static final int PETS_POR_LINHA = 5;   // quantos pets mostrar por linha na fila

    // largura da pergunta "a > b?", para alinhar as respostas SIM/NÃO numa coluna (calculada em comecar)
    private int larguraPergunta = 36;

    // no Quick Sort cada etapa é uma "partição"; nos outros, uma "passagem"
    private String etapa = "passagem";

    /**
     * Chamado antes de ordenar: calcula a largura da coluna de perguntas
     * (pelo maior "Nome (microchip)" da fila) e mostra a sala de espera inicial.
     */
    public void comecar(String algoritmo, Pet[] v) {
        int maiorTexto = 0;
        for (int i = 0; i < v.length; i++) {
            if (nomeChip(v[i]).length() > maiorTexto) {
                maiorTexto = nomeChip(v[i]).length();
            }
        }
        larguraPergunta = maiorTexto * 2 + 4;   // "a" + " > " + "b" + "?"
        System.out.println("♥ " + algoritmo + " — organizando a sala de espera por microchip (do menor para o maior)");
        mostrarFila(v);
    }

    /** Cabeçalho da etapa. Ex.: "♥ Bubble Sort · passagem 1". */
    public void inicioPassagem(String algoritmo, int numero) {
        etapa = algoritmo.startsWith("Quick") ? "partição" : "passagem";
        System.out.println();
        System.out.println("♥ " + algoritmo + " · " + etapa + " " + numero);
    }

    /** Uma comparação do Bubble Sort: a > b? Se sim, trocam; se não, a continua no lugar. */
    public void comparacao(Pet a, Pet b, boolean trocou) {
        String resposta = trocou
                ? "SIM → trocam de cadeira!"
                : "NÃO → " + a.getNome() + " continua cochilando.";
        imprimirPergunta(a, b, resposta);
    }

    /** Fim da etapa, com os contadores acumulados até aqui. */
    public void fimPassagem(int numero, long comparacoes, long trocas) {
        System.out.println("  fim da " + etapa + " " + numero + " → comparações: " + comparacoes + " | trocas: " + trocas);
    }

    /** Mostra a fila atual da sala de espera, cadeira por cadeira. */
    public void mostrarFila(Pet[] v) {
        System.out.print("  sala de espera:");
        for (int i = 0; i < v.length; i++) {
            if (i > 0 && i % PETS_POR_LINHA == 0) {
                System.out.print("\n                 ");   // quebra a linha para não ficar comprida demais
            }
            System.out.print(" [" + (i + 1) + "] " + v[i].getNome() + " (" + v[i].getMicrochip() + ")");
        }
        System.out.println();
    }

    // ---------- Insertion Sort ----------

    /** O pet levanta para procurar seu lugar. */
    public void levanta(Pet p) {
        System.out.println("  " + nomeChip(p) + " levanta e procura seu lugar…");
    }

    /** O vizinho tem chip maior: cede a cadeira e anda uma para a direita. */
    public void cedeCadeira(Pet vizinho, Pet quemProcura) {
        imprimirPergunta(vizinho, quemProcura,
                "SIM → " + vizinho.getNome() + " cede a cadeira e anda uma para a direita.");
    }

    /** O pet encontrou o lugar. vizinho = quem ficou à esquerda (null se chegou à primeira cadeira). */
    public void senta(Pet p, Pet vizinho, int posicao) {
        if (vizinho != null) {
            imprimirPergunta(vizinho, p, "NÃO → " + p.getNome() + " senta na cadeira " + (posicao + 1) + ".");
        } else {
            System.out.println("  " + p.getNome() + " chega à primeira cadeira e senta.");
        }
    }

    // ---------- Quick Sort ----------

    /** Anuncia o pivô da partição. */
    public void pivo(Pet p) {
        System.out.println("  " + nomeChip(p) + " é o pivô — quem tem chip menor vai para a esquerda");
    }

    /** Um pet com chip menor que o pivô troca de cadeira para ir para a esquerda. */
    public void trocaQuick(Pet menor, Pet outro) {
        System.out.println("  " + nomeChip(menor) + " tem chip menor que o pivô → troca de cadeira com "
                + nomeChip(outro) + "!");
    }

    /** O pivô senta no lugar definitivo. */
    public void pivoNoLugar(Pet p, int posicao) {
        System.out.println("  O pivô " + p.getNome() + " senta na cadeira " + (posicao + 1)
                + ": à esquerda só chips menores, à direita só maiores.");
    }

    // ---------- auxiliares ----------

    /** Ex.: "Luna (60218841)". */
    private String nomeChip(Pet p) {
        return p.getNome() + " (" + p.getMicrochip() + ")";
    }

    /** Imprime "a > b?" com a resposta alinhada. */
    private void imprimirPergunta(Pet a, Pet b, String resposta) {
        String pergunta = nomeChip(a) + " > " + nomeChip(b) + "?";
        System.out.println("  " + String.format("%-" + larguraPergunta + "s", pergunta) + " " + resposta);
    }
}
