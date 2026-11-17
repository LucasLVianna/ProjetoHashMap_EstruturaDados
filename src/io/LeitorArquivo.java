package io;

import modelo.Pet;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Lê o arquivo de pacientes (dados/pets.txt) e devolve um vetor de Pet.
 *
 * Formato de cada linha: microchip;nome;especie;mania
 * Linhas vazias e linhas começando com '#' são ignoradas (comentários).
 * Linhas com erro são avisadas e puladas — o programa não cai por causa de uma linha ruim.
 */
public class LeitorArquivo {

    /**
     * Carrega os pets do arquivo. Faz duas passadas:
     * 1ª conta quantas linhas são válidas (para saber o tamanho do vetor, sem usar ArrayList);
     * 2ª cria o vetor e o preenche.
     * Se o arquivo não existir, devolve um vetor vazio.
     */
    public static Pet[] carregar(String caminho) {
        // 1ª passada: só conta os pets válidos
        int total = 0;
        try (BufferedReader leitor = Files.newBufferedReader(Path.of(caminho), StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (converterLinha(linha) != null) {
                    total++;
                }
            }
        } catch (IOException e) {
            System.out.println("Não consegui abrir o arquivo " + caminho + ": " + e.getMessage());
            return new Pet[0];
        }

        // 2ª passada: preenche o vetor (agora mostrando avisos das linhas com erro)
        Pet[] pets = new Pet[total];
        int posicao = 0;
        int numeroLinha = 0;
        try (BufferedReader leitor = Files.newBufferedReader(Path.of(caminho), StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = leitor.readLine()) != null && posicao < total) {
                numeroLinha++;
                Pet pet = converterLinha(linha);
                if (pet != null) {
                    pets[posicao] = pet;
                    posicao++;
                } else if (!ehIgnoravel(linha)) {
                    System.out.println("Aviso: linha " + numeroLinha + " com formato inválido foi ignorada: " + linha);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler " + caminho + ": " + e.getMessage());
        }
        return pets;
    }

    /** Linha vazia ou comentário (#) não é erro: só deve ser pulada. */
    private static boolean ehIgnoravel(String linha) {
        String limpa = linha.trim();
        return limpa.isEmpty() || limpa.startsWith("#");
    }

    /**
     * Transforma uma linha "microchip;nome;especie;mania" em um Pet.
     * Devolve null se a linha for comentário, vazia ou estiver com formato errado.
     */
    private static Pet converterLinha(String linha) {
        if (ehIgnoravel(linha)) {
            return null;
        }
        String[] partes = linha.split(";");
        if (partes.length != 4) {
            return null;   // faltou ou sobrou campo
        }
        String textoChip = partes[0].trim();
        String nome = partes[1].trim();
        if (textoChip.length() != 8 || nome.isEmpty()) {
            return null;   // microchip precisa ter 8 dígitos e o nome não pode ser vazio
        }
        int microchip;
        try {
            microchip = Integer.parseInt(textoChip);
        } catch (NumberFormatException e) {
            return null;   // microchip com letras
        }
        if (microchip < 10000000) {
            return null;   // negativo ou começando com zero (ex.: "01234567") não vale
        }
        return new Pet(microchip, nome, partes[2].trim(), partes[3].trim());
    }
}
