package modelo;

import hash.Normalizador;

/**
 * O microchip é único (identifica o pet sem ambiguidade) e é usado para ORDENAR a sala de espera
 * O nome pode se repetir e é usado como CHAVE da tabela hash
 **/
public class Pet {

    private int microchip;   // 8 dígitos, único para cada pet
    private String nome;     // nome como foi cadastrado (com acentos, ex: "Paçoca")
    private String especie;
    private String mania;    // a mania do pet

    /** Cria um pet com os 4 dados da ficha */
    public Pet(int microchip, String nome, String especie, String mania) {
        this.microchip = microchip;
        this.nome = nome;
        this.especie = especie;
        this.mania = mania;
    }

    /** Microchip de 8 dígitos (único): usado para ordenar a sala de espera. */
    public int getMicrochip() {
        return microchip;
    }

    /** Nome como foi cadastrado (com acentos). */
    public String getNome() {
        return nome;
    }

    /** Espécie do pet (gata, cão, coelha...). */
    public String getEspecie() {
        return especie;
    }

    /** A mania engraçada do pet. */
    public String getMania() {
        return mania;
    }

    /**
     * Chave usada na tabela hash: o nome normalizado (sem acento, sem espaços nas pontas, maiúsculo).
     * "Paçoca", "pacoca" e " PAÇOCA " viram a mesma chave "PACOCA".
     */
    public String getChave() {
        return Normalizador.normalizar(nome);
    }

    /** Ex: "Luna (60218841) · gata · dorme em cima do teclado". */
    @Override
    public String toString() {
        return nome + " (" + microchip + ") · " + especie + " · " + mania;
    }
}
