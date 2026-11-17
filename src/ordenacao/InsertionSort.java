package ordenacao;

import modelo.Pet;

/**
 * Insertion Sort: como arrumar cartas na mão.
 * Cada pet levanta, e quem tem microchip maior que ele anda uma cadeira para a direita,
 * até abrir o lugar certo para ele sentar.
 *
 * Aqui o contador "trocas" conta DESLOCAMENTOS: cada vez que um pet anda uma cadeira para a direita.
 * Com a fila já ordenada, ninguém anda: 0 deslocamentos e só n-1 comparações → O(n).
 */
public class InsertionSort extends Ordenador {

    /** Ordena v por microchip (crescente) com Insertion Sort. */
    @Override
    public void ordenar(Pet[] v) {
        for (int i = 1; i < v.length; i++) {
            Pet atual = v[i];   // o pet que levanta e procura seu lugar
            if (narrar) {
                narrador.inicioPassagem(getNome(), i);
                narrador.levanta(atual);
            }

            int j = i - 1;
            // enquanto o vizinho da esquerda tiver chip maior, ele anda uma para a direita
            // (se j < 0, o && não chega a chamar maior(), então não conta comparação à toa)
            while (j >= 0 && maior(v[j], atual)) {
                if (narrar) {
                    narrador.cedeCadeira(v[j], atual);
                }
                v[j + 1] = v[j];   // desloca uma cadeira para a direita
                trocas++;          // >>> CONTAGEM DE DESLOCAMENTO (fica no contador de trocas)
                j--;
            }

            v[j + 1] = atual;      // senta na cadeira que sobrou
            if (narrar) {
                Pet vizinho = j >= 0 ? v[j] : null;   // quem ficou à esquerda (null = é o primeiro da fila)
                narrador.senta(atual, vizinho, j + 1);
                narrador.fimPassagem(i, comparacoes, trocas);
                narrador.mostrarFila(v);
            }
        }
    }

    /** Nome do algoritmo, para menus e tabelas. */
    @Override
    public String getNome() {
        return "Insertion Sort";
    }
}
