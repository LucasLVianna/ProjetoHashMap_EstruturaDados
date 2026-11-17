package hash;

import modelo.Pet;

/**
 * Um "elo" da lista encadeada: guarda um pet e aponta para o próximo elo.
 * O último elo da lista aponta para null.
 */
public class No {

    Pet pet;        // a ficha guardada neste elo
    No proximo;     // o próximo elo (ou null se este for o último)

    /** Cria um elo com o pet, ainda sem próximo (null). */
    public No(Pet pet) {
        this.pet = pet;
        this.proximo = null;
    }
}
