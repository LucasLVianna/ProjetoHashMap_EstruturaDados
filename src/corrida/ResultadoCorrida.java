package corrida;

/**
 * Resultado de UM algoritmo em UMA situação da Grande Corrida
 * (ex.: Quick Sort · já ordenado · n = 10.000).
 */
public class ResultadoCorrida {

    String algoritmo;     // "Bubble Sort", "Insertion Sort" ou "Quick Sort"
    String situacao;      // "aleatório", "já ordenado" ou "ordem inversa"
    int n;                // quantos pets na fila
    long tempoNs;         // tempo MÉDIO de uma ordenação, em nanossegundos
    long comparacoes;
    long trocas;
    boolean estourou;     // true se deu StackOverflowError (a recursão ficou funda demais)

    /** Guarda os números medidos de um algoritmo numa situação. */
    public ResultadoCorrida(String algoritmo, String situacao, int n,
                            long tempoNs, long comparacoes, long trocas, boolean estourou) {
        this.algoritmo = algoritmo;
        this.situacao = situacao;
        this.n = n;
        this.tempoNs = tempoNs;
        this.comparacoes = comparacoes;
        this.trocas = trocas;
        this.estourou = estourou;
    }

    /** Tempo médio convertido para milissegundos (1 ms = 1.000.000 ns). */
    public double getTempoMs() {
        return tempoNs / 1_000_000.0;
    }

    /** Nome do algoritmo (ex.: "Quick Sort"). */
    public String getAlgoritmo() {
        return algoritmo;
    }

    /** Situação da fila: "aleatório", "já ordenado" ou "ordem inversa". */
    public String getSituacao() {
        return situacao;
    }

    /** Quantos pets havia na fila. */
    public int getN() {
        return n;
    }

    /** Tempo médio de uma ordenação, em nanossegundos. */
    public long getTempoNs() {
        return tempoNs;
    }

    /** Comparações feitas numa ordenação. */
    public long getComparacoes() {
        return comparacoes;
    }

    /** Trocas (ou deslocamentos, no Insertion) feitas numa ordenação. */
    public long getTrocas() {
        return trocas;
    }

    /** True se a recursão estourou a pilha nesta medição. */
    public boolean isEstourou() {
        return estourou;
    }
}
