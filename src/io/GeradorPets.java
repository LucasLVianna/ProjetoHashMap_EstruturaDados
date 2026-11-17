package io;

import modelo.Pet;

import java.util.Random;

/**
 * Gera muitos pets de mentira para os testes de desempenho (Grande Corrida e teste de carga).
 *
 * Os microchips são criados em sequência (10000000, 10000007, 10000014, ...), por isso
 * nunca se repetem — não precisamos de HashSet para garantir isso.
 * A semente fixa (42) faz o Random sortear sempre a mesma sequência: resultados reproduzíveis.
 */
public class GeradorPets {

    private static final long SEMENTE = 42;

    // nomes repetidos de propósito: Luna, Thor, Bob e Mel aparecem duas vezes, então saem com mais frequência
    private static final String[] NOMES = {
            "Luna", "Thor", "Bob", "Mel", "Luna", "Thor", "Bob", "Mel",
            "Pipoca", "Zeus", "Amora", "Lola", "Paçoca", "Frida", "Simba", "Nina",
            "Pandora", "Chico", "Fred", "Toddy", "Bidu", "Biscoito", "Cacau", "Dengo"
    };

    private static final String[] ESPECIES = {
            "cão", "gato", "cachorra", "gata", "coelho", "hamster", "calopsita", "jabuti"
    };

    private static final String[] MANIAS = {
            "late para o aspirador", "dorme em cima do teclado", "rouba meias",
            "mia para a geladeira", "come a ração do irmão", "caça o ponteiro laser",
            "tem medo de pepino", "assobia quando alguém chega", "abre portas sozinho",
            "persegue o próprio rabo"
    };

    /** n pets com microchips em ordem CRESCENTE (o melhor caso para alguns algoritmos). */
    public static Pet[] gerarOrdenado(int n) {
        Random sorteio = new Random(SEMENTE);
        Pet[] pets = new Pet[n];
        for (int i = 0; i < n; i++) {
            pets[i] = criarPet(10000000 + i * 7, sorteio);
        }
        return pets;
    }

    /** n pets com microchips em ordem DECRESCENTE (o pior caso para Bubble e Insertion). */
    public static Pet[] gerarInverso(int n) {
        Random sorteio = new Random(SEMENTE);
        Pet[] pets = new Pet[n];
        for (int i = 0; i < n; i++) {
            pets[i] = criarPet(10000000 + (n - 1 - i) * 7, sorteio);
        }
        return pets;
    }

    /**
     * n pets em ordem aleatória. Gera ordenado e embaralha com Fisher-Yates:
     * de trás para frente, cada posição i troca com uma posição sorteada entre 0 e i.
     * Como só trocamos de lugar, os microchips continuam únicos.
     */
    public static Pet[] gerarAleatorio(int n) {
        Pet[] pets = gerarOrdenado(n);
        Random sorteio = new Random(SEMENTE);
        for (int i = n - 1; i > 0; i--) {
            int j = sorteio.nextInt(i + 1);   // posição sorteada em [0, i]
            Pet temporario = pets[i];
            pets[i] = pets[j];
            pets[j] = temporario;
        }
        return pets;
    }

    /** Monta um pet com o microchip dado e nome/espécie/mania sorteados. */
    private static Pet criarPet(int microchip, Random sorteio) {
        String nome = NOMES[sorteio.nextInt(NOMES.length)];
        String especie = ESPECIES[sorteio.nextInt(ESPECIES.length)];
        String mania = MANIAS[sorteio.nextInt(MANIAS.length)];
        return new Pet(microchip, nome, especie, mania);
    }
}
