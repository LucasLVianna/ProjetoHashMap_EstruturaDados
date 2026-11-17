package ordenacao;

import modelo.Pet;

/**
 * Quick Sort com partição de Lomuto: o pivô é SEMPRE o último elemento do trecho.
 *
 * ATENÇÃO (de propósito, não "consertar"): com a fila já ordenada ou invertida, o último elemento
 * é sempre o maior (ou o menor), então cada partição separa só 1 pet. Resultado: O(n²) comparações
 * e recursão com profundidade n (pode estourar a pilha). Essa é a resposta da pergunta 9 do relatório.
 */
public class QuickSort extends Ordenador {

    private int particoes;   // só para numerar as partições na narração

    /** Ordena v por microchip (crescente) com Quick Sort (pivô = último elemento). */
    @Override
    public void ordenar(Pet[] v) {
        particoes = 0;
        quickSort(v, 0, v.length - 1);
    }

    /**
     * Ordena o trecho v[ini..fim]: particiona e depois ordena, recursivamente,
     * o lado esquerdo (chips menores) e o lado direito (chips maiores) do pivô.
     */
    private void quickSort(Pet[] v, int ini, int fim) {
        if (ini < fim) {   // trecho com 0 ou 1 pet já está ordenado
            int posicaoPivo = particionar(v, ini, fim);
            quickSort(v, ini, posicaoPivo - 1);
            quickSort(v, posicaoPivo + 1, fim);
        }
    }

    /**
     * >>> PARTIÇÃO DE LOMUTO
     * Pivô = v[fim]. A variável i marca o fim da "área dos menores que o pivô".
     * Percorremos j de ini até fim-1: se v[j] tem chip menor que o pivô, ele entra na área dos menores
     * (i anda um e v[j] troca com v[i]). No final, o pivô troca com v[i+1] e fica no lugar definitivo.
     * Devolve a posição final do pivô.
     */
    private int particionar(Pet[] v, int ini, int fim) {
        Pet pivo = v[fim];   // pivô = último elemento (Lomuto)
        particoes++;
        if (narrar) {
            narrador.inicioPassagem(getNome(), particoes);
            narrador.pivo(pivo);
        }

        int i = ini - 1;   // ainda não há ninguém na área dos menores
        for (int j = ini; j < fim; j++) {
            if (maior(pivo, v[j])) {   // pivô > v[j]  ⇔  v[j] tem chip menor → vai para a esquerda
                i++;
                // Por que não trocamos (nem contamos troca) quando i == j?
                // Nesse caso v[j] JÁ está na área dos menores: "trocar" seria o pet trocar de cadeira
                // com ele mesmo, e ninguém se mexe. Se contássemos, a fila já ordenada mostraria
                // ~n²/2 "trocas" (cerca de 50 milhões com n = 10.000) sem nenhum pet sair do lugar,
                // o que falsearia a comparação com Bubble e Insertion (que só contam trocas de verdade).
                // As COMPARAÇÕES continuam todas contadas: o O(n²) do pivô ruim aparece nelas.
                if (i != j) {
                    if (narrar) {
                        narrador.trocaQuick(v[j], v[i]);
                    }
                    trocar(v, i, j);
                }
            }
        }

        // coloca o pivô logo depois da área dos menores
        int posicaoPivo = i + 1;
        if (posicaoPivo != fim) {   // mesma regra: se o pivô já está no lugar, não há troca
            trocar(v, posicaoPivo, fim);
        }
        if (narrar) {
            narrador.pivoNoLugar(pivo, posicaoPivo);
            narrador.fimPassagem(particoes, comparacoes, trocas);
            narrador.mostrarFila(v);
        }
        return posicaoPivo;
    }

    /** Nome do algoritmo, para menus e tabelas. */
    @Override
    public String getNome() {
        return "Quick Sort";
    }
}
