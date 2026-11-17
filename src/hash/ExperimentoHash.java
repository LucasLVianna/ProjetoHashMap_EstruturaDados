package hash;

import io.GeradorPets;
import modelo.Pet;

/**
 * Experimentos com o fichário, para responder as perguntas 1 a 5 do relatório:
 * comparar tamanhos M e funções hash, achar colisões reais e testar a tabela muito cheia.
 */
public class ExperimentoHash {

    // tamanhos testados: 13 e 17 (do enunciado) e 37 (escolhido pelo grupo). Todos primos.
    // Evitamos 31 de propósito: é a base da HashPolinomial (ver casoPatologico).
    private static final int[] TAMANHOS = {13, 17, 37};

    /**
     * Monta 6 tabelas (3 tamanhos × 2 funções) com os mesmos pets e imprime a comparação.
     * Também mostra onde caem as Lunas e exemplos de colisão real.
     */
    public static void comparar(Pet[] pets) {
        FuncaoHash[] funcoes = {new HashSomaCaracteres(), new HashPolinomial()};

        // monta as 6 tabelas: para cada M, uma com cada função
        TabelaHash[] tabelas = new TabelaHash[TAMANHOS.length * funcoes.length];
        int t = 0;
        for (int i = 0; i < TAMANHOS.length; i++) {
            for (int f = 0; f < funcoes.length; f++) {
                tabelas[t] = new TabelaHash(TAMANHOS[i], funcoes[f]);
                for (int p = 0; p < pets.length; p++) {
                    tabelas[t].inserir(pets[p]);
                }
                t++;
            }
        }

        // tabela comparativa
        System.out.println("♥ Comparação das funções hash (" + pets.length + " pacientes)");
        System.out.println("  M  | Função               | Carga | Vazios | Maior lista | Colisões | Colisões reais");
        System.out.println("-----+----------------------+-------+--------+-------------+----------+---------------");
        for (int i = 0; i < tabelas.length; i++) {
            TabelaHash tab = tabelas[i];
            System.out.println(String.format(" %3d | %-20s | %5.2f | %6d | %11d | %8d | %14d",
                    tab.getM(), tab.getFuncao().getNome(), tab.fatorCarga(), tab.baldesVazios(),
                    tab.maiorLista(), tab.getColisoes(), tab.getColisoesReais()));
        }

        // pergunta 1: mesma chave → mesmo índice, para qualquer M e qualquer função
        System.out.println();
        System.out.println("♥ Onde ficam as Lunas? (mesmo nome = mesma chave = mesmo balde, sempre)");
        for (int i = 0; i < tabelas.length; i++) {
            TabelaHash tab = tabelas[i];
            int indice = tab.getFuncao().calcular("LUNA", tab.getM());
            int quantasLunas = tab.buscarPorNome("Luna").length;
            System.out.println(String.format("  M = %2d | %-20s → todas as %d Lunas no balde [%02d]",
                    tab.getM(), tab.getFuncao().getNome(), quantasLunas, indice));
        }

        // pergunta 2: exemplos de colisão real (nomes diferentes dividindo o balde)
        System.out.println();
        System.out.println("♥ Colisões reais (nomes diferentes no mesmo balde)");
        for (int i = 0; i < tabelas.length; i++) {
            TabelaHash tab = tabelas[i];
            System.out.println("  M = " + tab.getM() + " | " + tab.getFuncao().getNome() + ":");
            int quantos = tab.imprimirColisoesReais();
            if (quantos == 0) {
                System.out.println("    nenhum balde com nomes diferentes");
            }
        }

        System.out.println();
        casoPatologico(pets);
    }

    /**
     * Caso patológico: M = 31 com a HashPolinomial, que também usa base 31.
     *
     * Em h = (h * 31 + c) % 31, a parte h * 31 é múltiplo de 31, então some no resto.
     * Sobra só h = c % 31: o índice depende APENAS da última letra do nome.
     * Todos os nomes terminados em 'A' ('A' = 65, 65 % 31 = 3) caem no balde [03].
     * Lição: M não pode ter fator comum com a base da função.
     */
    public static void casoPatologico(Pet[] pets) {
        FuncaoHash polinomial = new HashPolinomial();
        int m = 31;
        int baldeDoA = 'A' % m;   // 65 % 31 = 3

        System.out.println("♥ Caso patológico: M = 31 com base 31");
        System.out.println("  h = (h * 31 + c) % 31  →  h * 31 some no resto  →  só sobra a última letra!");
        System.out.println("  Última letra 'A' = 65 → 65 % 31 = " + baldeDoA + ". Nomes que caem no balde ["
                + String.format("%02d", baldeDoA) + "]:");

        // percorre os pets e lista (sem repetir) os nomes que caem no balde do 'A'
        String nomes = "";
        int quantosNomes = 0;
        for (int i = 0; i < pets.length; i++) {
            String chave = pets[i].getChave();
            if (polinomial.calcular(chave, m) == baldeDoA && !jaApareceu(pets, i, chave)) {
                nomes = quantosNomes == 0 ? chave : nomes + ", " + chave;
                quantosNomes++;
            }
        }
        System.out.println("    " + nomes);
        System.out.println("  → " + quantosNomes + " nomes diferentes no mesmo balde. Com M = 37 isso não acontece.");
    }

    /** True se a chave já apareceu em algum pet antes da posição 'ate' (para não repetir nomes). */
    private static boolean jaApareceu(Pet[] pets, int ate, String chave) {
        for (int i = 0; i < ate; i++) {
            if (pets[i].getChave().equals(chave)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Teste de carga (pergunta 5): coloca n pets gerados numa tabela com m baldes
     * e mostra como as listas ficam compridas. Depois repete com um M bem maior (rehash).
     *
     * Faz duas versões:
     * 1) nomes repetidos (só ~20 nomes): aumentar M NÃO adianta, porque mesmo nome = mesmo balde;
     * 2) nomes únicos (LUNA0001, LUNA0002...): aumentar M funciona, as listas ficam curtinhas.
     */
    public static void testarCarga(int n, int m) {
        Pet[] repetidos = GeradorPets.gerarAleatorio(n);
        Pet[] unicos = comNomesUnicos(repetidos);
        int mGrande = proximoPrimo(n);   // a "solução" clássica: aumentar M para um primo perto de n

        System.out.println("♥ Teste de carga: " + n + " pacientes, M = " + m + " e depois M = " + mGrande + " (rehash)");
        System.out.println("  1) Nomes repetidos (Luna, Thor, Bob...):");
        imprimirCarga(repetidos, m, new HashSomaCaracteres());
        imprimirCarga(repetidos, m, new HashPolinomial());
        imprimirCarga(repetidos, mGrande, new HashSomaCaracteres());
        imprimirCarga(repetidos, mGrande, new HashPolinomial());

        System.out.println("  2) Nomes únicos (LUNA0001, THOR0002...):");
        imprimirCarga(unicos, m, new HashSomaCaracteres());
        imprimirCarga(unicos, m, new HashPolinomial());
        imprimirCarga(unicos, mGrande, new HashSomaCaracteres());
        imprimirCarga(unicos, mGrande, new HashPolinomial());
    }

    /**
     * Cria uma cópia dos pets trocando o nome por nome + número (ex.: "Luna" → "Luna0001").
     * Assim nenhuma chave se repete. O microchip, a espécie e a mania continuam iguais.
     */
    private static Pet[] comNomesUnicos(Pet[] pets) {
        Pet[] unicos = new Pet[pets.length];
        for (int i = 0; i < pets.length; i++) {
            Pet p = pets[i];
            String nomeUnico = p.getNome() + String.format("%04d", i + 1);
            unicos[i] = new Pet(p.getMicrochip(), nomeUnico, p.getEspecie(), p.getMania());
        }
        return unicos;
    }

    /** Monta uma tabela com os pets e imprime fator de carga, média e maior lista. */
    private static void imprimirCarga(Pet[] pets, int m, FuncaoHash funcao) {
        TabelaHash tab = new TabelaHash(m, funcao);
        for (int i = 0; i < pets.length; i++) {
            tab.inserir(pets[i]);
        }
        int ocupados = m - tab.baldesVazios();
        double mediaOcupados = (double) tab.getQuantidade() / ocupados;
        System.out.println(String.format(
                "    M = %5d | %-20s | carga α = %8.2f | baldes ocupados = %5d | média por balde ocupado = %8.1f | maior lista = %d",
                m, funcao.getNome(), tab.fatorCarga(), ocupados, mediaOcupados, tab.maiorLista()));
    }

    /** Menor número primo maior ou igual a n (testa divisores até a raiz). */
    private static int proximoPrimo(int n) {
        int candidato = n < 2 ? 2 : n;
        while (true) {
            boolean primo = true;
            for (int d = 2; d * d <= candidato; d++) {
                if (candidato % d == 0) {
                    primo = false;
                    break;
                }
            }
            if (primo) {
                return candidato;
            }
            candidato++;
        }
    }
}
