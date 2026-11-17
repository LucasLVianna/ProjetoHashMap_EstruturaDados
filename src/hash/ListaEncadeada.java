package hash;

import modelo.Pet;

/**
 * Lista encadeada feita por nós (sem LinkedList do Java).
 * Cada balde da tabela hash é uma dessas listas: é ali que ficam os pets que caíram no mesmo índice.
 */
public class ListaEncadeada {

    private No inicio;      // primeiro elo da lista (null = lista vazia)
    private int tamanho;    // quantos pets há na lista

    /**
     * Insere o pet no INÍCIO da lista: O(1), não precisa percorrer nada.
     * O novo nó aponta para o antigo início e passa a ser o início.
     */
    public void inserir(Pet p) {
        No novo = new No(p);
        novo.proximo = inicio;
        inicio = novo;
        tamanho++;
    }

    /**
     * Devolve TODOS os pets com a chave informada (ex.: todas as Lunas).
     * Primeiro conta quantos são, depois cria o vetor do tamanho certo e preenche.
     * Se não houver nenhum, devolve um vetor vazio (nunca null).
     */
    public Pet[] buscarPorChave(String chave) {
        // 1ª volta: contar
        int quantos = 0;
        No atual = inicio;
        while (atual != null) {
            if (atual.pet.getChave().equals(chave)) {
                quantos++;
            }
            atual = atual.proximo;
        }

        // 2ª volta: preencher
        Pet[] encontrados = new Pet[quantos];
        int posicao = 0;
        atual = inicio;
        while (atual != null) {
            if (atual.pet.getChave().equals(chave)) {
                encontrados[posicao] = atual.pet;
                posicao++;
            }
            atual = atual.proximo;
        }
        return encontrados;
    }

    /**
     * Remove o pet com esse microchip. Devolve true se achou e removeu.
     * Usa o ponteiro "anterior" para "pular" o nó removido: anterior.proximo = atual.proximo.
     */
    public boolean removerPorMicrochip(int microchip) {
        No anterior = null;
        No atual = inicio;
        while (atual != null) {
            if (atual.pet.getMicrochip() == microchip) {
                if (anterior == null) {
                    inicio = atual.proximo;            // era o primeiro: o início anda um
                } else {
                    anterior.proximo = atual.proximo;  // estava no meio/fim: o anterior pula ele
                }
                tamanho--;
                return true;
            }
            anterior = atual;
            atual = atual.proximo;
        }
        return false;
    }

    /** Quantos pets há na lista. */
    public int tamanho() {
        return tamanho;
    }

    /** True se a lista não tem nenhum pet. */
    public boolean estaVazia() {
        return inicio == null;
    }

    /**
     * True se já existe na lista algum pet com chave DIFERENTE da informada
     * (ex.: perguntar por LUNA num balde que tem AMORA → true).
     */
    public boolean possuiChaveDiferente(String chave) {
        No atual = inicio;
        while (atual != null) {
            if (!atual.pet.getChave().equals(chave)) {
                return true;
            }
            atual = atual.proximo;
        }
        return false;
    }

    /**
     * Conta quantos nomes (chaves) DIFERENTES existem na lista.
     * Para cada nó, olha se a mesma chave já apareceu num nó anterior; se não, é um nome novo.
     * Ex.: LUNA → LUNA → AMORA → null tem 2 nomes distintos.
     */
    public int contarChavesDistintas() {
        int distintas = 0;
        No atual = inicio;
        while (atual != null) {
            // procura a mesma chave entre os nós que vêm ANTES do atual
            boolean jaApareceu = false;
            No anterior = inicio;
            while (anterior != atual) {
                if (anterior.pet.getChave().equals(atual.pet.getChave())) {
                    jaApareceu = true;
                }
                anterior = anterior.proximo;
            }
            if (!jaApareceu) {
                distintas++;
            }
            atual = atual.proximo;
        }
        return distintas;
    }

    /** Imprime a lista. Ex.: LUNA(60218841) → THOR(31057720) → null */
    public void imprimir() {
        No atual = inicio;
        while (atual != null) {
            System.out.print(atual.pet.getChave() + "(" + atual.pet.getMicrochip() + ") → ");
            atual = atual.proximo;
        }
        System.out.println("null");
    }

    /** Copia os pets da lista para um vetor (na ordem da lista). */
    public Pet[] paraVetor() {
        Pet[] vetor = new Pet[tamanho];
        int posicao = 0;
        No atual = inicio;
        while (atual != null) {
            vetor[posicao] = atual.pet;
            posicao++;
            atual = atual.proximo;
        }
        return vetor;
    }
}
