package ordenacao;

import modelo.Pet;

/**
 * Base comum dos três algoritmos de ordenação (Bubble, Insertion, Quick).
 *
 * Todos ordenam a sala de espera em ordem CRESCENTE de microchip e usam os mesmos
 * métodos maior() e trocar(), que contam comparações e trocas. Assim a contagem é igual para todos
 * e a comparação entre algoritmos fica justa.
 */
public abstract class Ordenador {

    protected long comparacoes;   // quantas vezes perguntamos "a > b?"
    protected long trocas;        // quantas vezes alguém trocou/andou de cadeira
    protected boolean narrar;     // true = modo passo a passo (imprime cada etapa)
    protected NarradorClinica narrador = new NarradorClinica();

    /** Ordena o vetor v (ele próprio é alterado). Cada algoritmo implementa do seu jeito. */
    public abstract void ordenar(Pet[] v);

    /** Nome do algoritmo, para menus e tabelas. */
    public abstract String getNome();

    /**
     * Ordena uma CÓPIA do vetor e devolve a cópia ordenada. O original nunca é alterado,
     * então dá para entregar exatamente a mesma fila para os três algoritmos.
     * A cópia é feita com um laço simples, posição por posição.
     */
    public Pet[] ordenarCopia(Pet[] original) {
        Pet[] copia = new Pet[original.length];
        for (int i = 0; i < original.length; i++) {
            copia[i] = original[i];
        }
        resetarContadores();
        if (narrar) {
            narrador.comecar(getNome(), copia);   // mostra a fila antes de começar
        }
        ordenar(copia);
        return copia;
    }

    /**
     * Pergunta se a tem microchip maior que b.
     * >>> CONTAGEM DE COMPARAÇÃO: toda comparação entre pets passa por aqui.
     */
    protected boolean maior(Pet a, Pet b) {
        comparacoes++;
        return a.getMicrochip() > b.getMicrochip();
    }

    /**
     * Troca os pets das posições i e j (troca de cadeira).
     * >>> CONTAGEM DE TROCA: toda troca passa por aqui.
     */
    protected void trocar(Pet[] v, int i, int j) {
        trocas++;
        Pet temporario = v[i];
        v[i] = v[j];
        v[j] = temporario;
    }

    /** Zera os contadores antes de uma nova ordenação. */
    public void resetarContadores() {
        comparacoes = 0;
        trocas = 0;
    }

    /** Comparações feitas na última ordenação. */
    public long getComparacoes() {
        return comparacoes;
    }

    /** Trocas feitas na última ordenação (no Insertion, deslocamentos). */
    public long getTrocas() {
        return trocas;
    }

    /** Liga/desliga o modo passo a passo. */
    public void setNarrar(boolean narrar) {
        this.narrar = narrar;
    }
}
