package ordenacao;

import modelo.Pet;

/**
 * Bubble Sort: compara vizinhos e troca se estiverem fora de ordem.
 * A cada passagem, o maior microchip que ainda não estava no lugar "borbulha" até o fim da fila.
 *
 * Tem parada antecipada: se uma passagem inteira não trocar ninguém, a fila já está ordenada e paramos.
 */
public class BubbleSort extends Ordenador {

    /** Ordena v por microchip (crescente) com Bubble Sort, parando cedo se uma passagem não trocar ninguém. */
    @Override
    public void ordenar(Pet[] v) {
        int n = v.length;
        boolean houveTroca = true;
        int passagem = 0;

        // cada passagem coloca mais um pet no lugar certo, no final da fila
        while (houveTroca && passagem < n - 1) {
            passagem++;
            houveTroca = false;   // ainda não trocou ninguém nesta passagem
            if (narrar) {
                narrador.inicioPassagem(getNome(), passagem);
            }

            // os últimos (passagem - 1) pets já estão no lugar, não precisa olhar para eles
            for (int j = 0; j < n - passagem; j++) {
                Pet a = v[j];
                Pet b = v[j + 1];
                boolean foraDeOrdem = maior(a, b);   // conta 1 comparação
                if (foraDeOrdem) {
                    trocar(v, j, j + 1);              // conta 1 troca
                    houveTroca = true;
                }
                if (narrar) {
                    narrador.comparacao(a, b, foraDeOrdem);
                }
            }

            if (narrar) {
                narrador.fimPassagem(passagem, comparacoes, trocas);
                narrador.mostrarFila(v);
            }
        }
        // se saiu do laço com houveTroca == false, foi a parada antecipada
    }

    /** Nome do algoritmo, para menus e tabelas. */
    @Override
    public String getNome() {
        return "Bubble Sort";
    }
}
