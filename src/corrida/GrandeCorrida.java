package corrida;

import io.GeradorPets;
import modelo.Pet;
import ordenacao.BubbleSort;
import ordenacao.InsertionSort;
import ordenacao.Ordenador;
import ordenacao.QuickSort;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * A Grande Corrida: os três algoritmos ordenam as mesmas filas e comparamos tempo, comparações e trocas.
 *
 * 3 tamanhos (50, 1.000, 10.000) × 3 situações (aleatório, já ordenado, ordem inversa) × 3 algoritmos
 * = 27 resultados.
 *
 * Implementa Runnable para poder rodar dentro de uma Thread com pilha GRANDE: o Quick Sort com
 * pivô no último elemento, em fila já ordenada, faz uma recursão com profundidade n (10.000 chamadas),
 * o que pode estourar a pilha padrão do Java.
 */
public class GrandeCorrida implements Runnable {

    private static final int[] TAMANHOS = {50, 1000, 10000};
    private static final String[] SITUACOES = {"aleatório", "já ordenado", "ordem inversa"};
    private static final int REPETICOES = 5;                 // média de 5 execuções para n = 1.000 e 10.000
    private static final int REPETICOES_FILA_PEQUENA = 1000; // para n = 50: cada execução leva microssegundos
                                                             // e oscila muito, então tiramos a média de 1.000
    private static final int LIMITE_FILA_PEQUENA = 50;
    private static final long TAMANHO_PILHA = 1L << 28;      // 256 MB de pilha para a thread da corrida

    private Ordenador[] algoritmos = {new BubbleSort(), new InsertionSort(), new QuickSort()};
    private ResultadoCorrida[] resultados = new ResultadoCorrida[TAMANHOS.length * SITUACOES.length * 3];
    private int totalResultados = 0;

    /**
     * Roda a corrida inteira numa thread separada com pilha grande e espera ela terminar (join).
     */
    public void executar() {
        totalResultados = 0;
        Thread corrida = new Thread(null, this, "corrida", TAMANHO_PILHA);
        corrida.start();
        try {
            corrida.join();   // espera a thread da corrida terminar antes de continuar
        } catch (InterruptedException e) {
            System.out.println("A corrida foi interrompida.");
        }
    }

    /** O que a thread da corrida executa (chamado pela Thread, não diretamente). */
    @Override
    public void run() {
        System.out.println("♥ Começou a Grande Corrida! (1 execução de aquecimento; depois média de "
                + REPETICOES_FILA_PEQUENA + " execuções para n = " + LIMITE_FILA_PEQUENA
                + " e de " + REPETICOES + " para os demais)");
        for (int t = 0; t < TAMANHOS.length; t++) {
            for (int s = 0; s < SITUACOES.length; s++) {
                int n = TAMANHOS[t];
                // UMA fila por (n, situação); cada algoritmo recebe uma CÓPIA dela (ordenarCopia)
                Pet[] fila = gerarFila(SITUACOES[s], n);
                for (int a = 0; a < algoritmos.length; a++) {
                    resultados[totalResultados] = medir(algoritmos[a], fila, SITUACOES[s], n);
                    totalResultados++;
                }
            }
        }
        System.out.println("♥ Fim da corrida! " + totalResultados + " resultados.");
    }

    /** Cria a fila de acordo com a situação. */
    private Pet[] gerarFila(String situacao, int n) {
        if (situacao.equals("já ordenado")) {
            return GeradorPets.gerarOrdenado(n);
        } else if (situacao.equals("ordem inversa")) {
            return GeradorPets.gerarInverso(n);
        }
        return GeradorPets.gerarAleatorio(n);
    }

    /**
     * Mede um algoritmo numa fila:
     * 1 execução de aquecimento (o Java "esquenta" o código, o JIT compila os métodos mais usados)
     * e depois a média de várias execuções cronometradas com System.nanoTime()
     * (1.000 para n = 50, 5 para os demais).
     */
    private ResultadoCorrida medir(Ordenador algoritmo, Pet[] fila, String situacao, int n) {
        System.out.print(String.format("  %-14s · %-13s · n = %6d ... ", algoritmo.getNome(), situacao, n));
        int repeticoes = n <= LIMITE_FILA_PEQUENA ? REPETICOES_FILA_PEQUENA : REPETICOES;
        try {
            algoritmo.ordenarCopia(fila);   // aquecimento: não entra na média

            long somaTempos = 0;
            for (int r = 0; r < repeticoes; r++) {
                long inicio = System.nanoTime();
                algoritmo.ordenarCopia(fila);   // a cópia é sempre da fila original, desordenada
                long fim = System.nanoTime();
                somaTempos = somaTempos + (fim - inicio);
            }
            long media = somaTempos / repeticoes;
            System.out.println(String.format("%.3f ms", media / 1_000_000.0));
            // os contadores são da última execução (iguais em todas, pois a fila é a mesma)
            return new ResultadoCorrida(algoritmo.getNome(), situacao, n, media,
                    algoritmo.getComparacoes(), algoritmo.getTrocas(), false);
        } catch (StackOverflowError e) {
            // recursão funda demais: registramos em vez de derrubar o programa
            System.out.println("estourou a pilha!");
            return new ResultadoCorrida(algoritmo.getNome(), situacao, n, -1, -1, -1, true);
        }
    }

    /** Procura o resultado de um algoritmo, numa situação, com um n. Devolve null se não houver. */
    private ResultadoCorrida buscar(String algoritmo, String situacao, int n) {
        for (int i = 0; i < totalResultados; i++) {
            ResultadoCorrida r = resultados[i];
            if (r.algoritmo.equals(algoritmo) && r.situacao.equals(situacao) && r.n == n) {
                return r;
            }
        }
        return null;
    }

    /**
     * Imprime as duas tabelas da corrida:
     * 1) tempo médio em ms; 2) comparações / trocas.
     * Linhas = algoritmo × situação; colunas = n = 50 | 1.000 | 10.000.
     */
    public void imprimirTabelas() {
        if (totalResultados == 0) {
            System.out.println("A corrida ainda não foi executada.");
            return;
        }

        System.out.println();
        System.out.println("♥ Tabela 1 — Tempo médio (ms)");
        imprimirCabecalho(14);
        for (int a = 0; a < algoritmos.length; a++) {
            for (int s = 0; s < SITUACOES.length; s++) {
                String linha = String.format("%-14s | %-13s", algoritmos[a].getNome(), SITUACOES[s]);
                for (int t = 0; t < TAMANHOS.length; t++) {
                    ResultadoCorrida r = buscar(algoritmos[a].getNome(), SITUACOES[s], TAMANHOS[t]);
                    String celula = r.estourou ? "estourou a pilha" : String.format("%.3f", r.getTempoMs());
                    linha = linha + String.format(" | %14s", celula);
                }
                System.out.println(linha);
            }
        }

        System.out.println();
        System.out.println("♥ Tabela 2 — Comparações / trocas (no Insertion, trocas = deslocamentos)");
        imprimirCabecalho(25);
        for (int a = 0; a < algoritmos.length; a++) {
            for (int s = 0; s < SITUACOES.length; s++) {
                String linha = String.format("%-14s | %-13s", algoritmos[a].getNome(), SITUACOES[s]);
                for (int t = 0; t < TAMANHOS.length; t++) {
                    ResultadoCorrida r = buscar(algoritmos[a].getNome(), SITUACOES[s], TAMANHOS[t]);
                    String celula = r.estourou
                            ? "estourou a pilha"
                            : String.format("%,d / %,d", r.comparacoes, r.trocas);
                    linha = linha + String.format(" | %25s", celula);
                }
                System.out.println(linha);
            }
        }
    }

    /** Cabeçalho das tabelas, com a largura de coluna pedida. */
    private void imprimirCabecalho(int larguraColuna) {
        String cabecalho = String.format("%-14s | %-13s", "Algoritmo", "Situação");
        String separador = "---------------+--------------";
        for (int t = 0; t < TAMANHOS.length; t++) {
            cabecalho = cabecalho + String.format(" | %" + larguraColuna + "s", String.format("n = %,d", TAMANHOS[t]));
            separador = separador + "-+-" + "-".repeat(larguraColuna);
        }
        System.out.println(cabecalho);
        System.out.println(separador);
    }

    /**
     * Salva os resultados num CSV (para montar gráficos e tabelas do relatório).
     * Usa ponto como separador decimal (Locale.ROOT), para planilhas lerem sem confusão.
     * Cria a pasta (ex.: resultados/) se ela ainda não existir.
     */
    public void exportarCsv(String caminho) {
        if (totalResultados == 0) {
            System.out.println("A corrida ainda não foi executada: nada para exportar.");
            return;
        }
        try {
            Path arquivo = Path.of(caminho);
            if (arquivo.getParent() != null) {
                Files.createDirectories(arquivo.getParent());
            }
            try (PrintWriter escritor = new PrintWriter(Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8))) {
                escritor.println("algoritmo,situacao,n,tempo_ms,comparacoes,trocas,estourou_pilha");
                for (int i = 0; i < totalResultados; i++) {
                    ResultadoCorrida r = resultados[i];
                    escritor.println(r.algoritmo + "," + r.situacao + "," + r.n + ","
                            + String.format(Locale.ROOT, "%.4f", r.getTempoMs()) + ","
                            + r.comparacoes + "," + r.trocas + "," + (r.estourou ? "sim" : "nao"));
                }
            }
            System.out.println("Resultados salvos em " + caminho);
        } catch (IOException e) {
            System.out.println("Não consegui salvar o CSV: " + e.getMessage());
        }
    }
}
