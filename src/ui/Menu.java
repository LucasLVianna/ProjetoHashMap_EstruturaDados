package ui;

import corrida.GrandeCorrida;
import hash.ExperimentoHash;
import hash.FuncaoHash;
import hash.HashPolinomial;
import hash.HashSomaCaracteres;
import hash.Normalizador;
import hash.TabelaHash;
import modelo.Pet;
import ordenacao.BubbleSort;
import ordenacao.InsertionSort;
import ordenacao.Ordenador;
import ordenacao.QuickSort;

import java.util.Scanner;

/**
 * Menu da recepção da Clínica Veterinária Patinhas.
 *
 * Toda entrada do usuário é validada aqui (opção inválida, microchip errado ou repetido, nome vazio),
 * sempre com mensagem amigável — nenhuma exceção aparece na tela.
 * Um único Scanner é compartilhado por todo o menu.
 */
public class Menu {

    private static final int MAX_PASSO_A_PASSO = 10;   // modo passo a passo: no máximo 10 pets

    private final Scanner entrada = new Scanner(System.in);
    private TabelaHash tabela;
    private Pet[] ordemDoArquivo;            // pets na ordem em que aparecem em dados/pets.txt
    private boolean entradaAcabou = false;   // true se o teclado/arquivo de entrada terminou (EOF)

    /**
     * @param tabela         o fichário já montado
     * @param ordemDoArquivo os pets na ordem do arquivo (usada no modo passo a passo)
     */
    public Menu(TabelaHash tabela, Pet[] ordemDoArquivo) {
        this.tabela = tabela;
        this.ordemDoArquivo = ordemDoArquivo;
    }

    /** Laço principal: mostra o menu, lê a opção e executa, até o usuário escolher 0. */
    public void iniciar() {
        int opcao = -1;
        while (opcao != 0) {
            mostrarMenu();
            opcao = lerOpcao("Escolha uma opção: ", 0, 10);
            if (entradaAcabou) {
                break;
            }
            System.out.println();
            executar(opcao);
        }
        System.out.println("♥ Clínica fechada. Até amanhã, pacientes!");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║          ♥ PET HASH & SORT ♥             ║");
        System.out.println("║        Clínica Veterinária Patinhas      ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║ 1 - Cadastrar paciente                   ║");
        System.out.println("║ 2 - Buscar pacientes por nome            ║");
        System.out.println("║ 3 - Dar alta (remover por microchip)     ║");
        System.out.println("║ 4 - Mostrar fichário (tabela hash)       ║");
        System.out.println("║ 5 - Estatísticas do fichário             ║");
        System.out.println("║ 6 - Organizar a sala de espera           ║");
        System.out.println("║ 7 - Modo passo a passo                   ║");
        System.out.println("║ 8 - A Grande Corrida                     ║");
        System.out.println("║ 9 - Comparar funções hash e tamanhos     ║");
        System.out.println("║10 - Trocar tamanho M / função hash       ║");
        System.out.println("║ 0 - Fechar a clínica                     ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("Fichário: M = " + tabela.getM() + " · " + tabela.getFuncao().getNome()
                + " · " + tabela.getQuantidade() + " pacientes");
    }

    /** Chama a ação correspondente à opção escolhida. */
    private void executar(int opcao) {
        switch (opcao) {
            case 1 -> cadastrar();
            case 2 -> buscar();
            case 3 -> darAlta();
            case 4 -> tabela.exibir();
            case 5 -> tabela.imprimirEstatisticas();
            case 6 -> organizarSalaDeEspera();
            case 7 -> modoPassoAPasso();
            case 8 -> grandeCorrida();
            case 9 -> compararHash();
            case 10 -> trocarTabela();
            default -> { }   // 0: o laço de iniciar() termina sozinho
        }
    }

    // ===================== opções 1 a 3: fichário =====================

    /** Opção 1: lê os dados, valida e guarda a ficha na tabela hash. */
    private void cadastrar() {
        System.out.println("♥ Cadastro de paciente (deixe em branco e tecle Enter para cancelar)");
        int microchip = lerMicrochip("Microchip (8 dígitos): ");
        if (microchip < 0) {
            return;
        }
        if (buscarPorMicrochip(microchip) != null) {
            System.out.println("Já existe um paciente com o microchip " + microchip + ": " + buscarPorMicrochip(microchip));
            return;
        }
        String nome = lerTextoObrigatorio("Nome: ");
        if (nome == null) {
            return;
        }
        String especie = lerTextoObrigatorio("Espécie: ");
        if (especie == null) {
            return;
        }
        String mania = lerLinha("Mania (opcional): ");
        if (mania == null || mania.isEmpty()) {
            mania = "ainda não mostrou nenhuma mania";
        }

        Pet novo = new Pet(microchip, nome, especie, mania);
        int gaveta = tabela.indiceDoBalde(nome);
        boolean gavetaOcupada = tabela.tamanhoDoBalde(gaveta) > 0;   // olhamos ANTES de inserir
        tabela.inserir(novo);

        System.out.println("Ficha guardada na gaveta [" + String.format("%02d", gaveta) + "]: " + novo);
        if (gavetaOcupada) {
            System.out.println("(houve colisão: essa gaveta já tinha outras fichas)");
        }
    }

    /** Opção 2: mostra todos os pacientes com o nome digitado (sem diferenciar acento/maiúscula). */
    private void buscar() {
        String nome = lerTextoObrigatorio("Nome do paciente: ");
        if (nome == null) {
            return;
        }
        Pet[] encontrados = tabela.buscarPorNome(nome);
        String chave = Normalizador.normalizar(nome);
        int gaveta = tabela.indiceDoBalde(nome);

        if (encontrados.length == 0) {
            System.out.println("Nenhum paciente chamado \"" + nome + "\" (procurei na gaveta ["
                    + String.format("%02d", gaveta) + "]).");
            return;
        }
        System.out.println(encontrados.length + " paciente(s) chamado(s) " + chave
                + " na gaveta [" + String.format("%02d", gaveta) + "]:");
        for (int i = 0; i < encontrados.length; i++) {
            System.out.println("  " + encontrados[i]);
        }
    }

    /** Opção 3: remove o paciente pelo microchip. */
    private void darAlta() {
        int microchip = lerMicrochip("Microchip do paciente que vai receber alta: ");
        if (microchip < 0) {
            return;
        }
        Pet pet = buscarPorMicrochip(microchip);
        if (pet != null && tabela.removerPorMicrochip(microchip)) {
            System.out.println("Alta concedida! " + pet.getNome() + " voltou para casa: " + pet);
        } else {
            System.out.println("Nenhum paciente com o microchip " + microchip + ".");
        }
    }

    /**
     * Procura um pet pelo microchip olhando todos os pets da tabela.
     * (A chave da tabela é o nome, então não dá para ir direto ao balde.)
     */
    private Pet buscarPorMicrochip(int microchip) {
        Pet[] todos = tabela.paraVetor();
        for (int i = 0; i < todos.length; i++) {
            if (todos[i].getMicrochip() == microchip) {
                return todos[i];
            }
        }
        return null;
    }

    // ===================== opções 6 e 7: ordenação =====================

    /** Opção 6: ordena uma CÓPIA dos pacientes por microchip e mostra a fila e os contadores. */
    private void organizarSalaDeEspera() {
        Pet[] pacientes = tabela.paraVetor();
        if (pacientes.length == 0) {
            System.out.println("A sala de espera está vazia.");
            return;
        }
        Ordenador algoritmo = escolherAlgoritmo();
        if (algoritmo == null) {
            return;
        }
        Pet[] fila = algoritmo.ordenarCopia(pacientes);   // o fichário não é alterado

        System.out.println("♥ Sala de espera organizada com " + algoritmo.getNome() + " (por microchip):");
        for (int i = 0; i < fila.length; i++) {
            System.out.println(String.format("  cadeira %2d: %s", i + 1, fila[i]));
        }
        imprimirContadores(algoritmo);
    }

    /**
     * Opção 7: narra a ordenação de até 10 pacientes, passo a passo.
     * Usa os pacientes na ordem de chegada (a do arquivo: Luna, Thor, Pipoca, Mel, Zeus...),
     * igual ao exemplo do enunciado.
     */
    private void modoPassoAPasso() {
        Pet[] pacientes = pacientesNaOrdemDeChegada();
        if (pacientes.length < 2) {
            System.out.println("É preciso ter pelo menos 2 pacientes para organizar a fila.");
            return;
        }
        int limite = Math.min(MAX_PASSO_A_PASSO, pacientes.length);
        int quantos = lerOpcao("Quantos pacientes na sala de espera? (2 a " + limite + "): ", 2, limite);
        if (entradaAcabou) {
            return;
        }
        Ordenador algoritmo = escolherAlgoritmo();
        if (algoritmo == null) {
            return;
        }

        // pega os primeiros 'quantos' pacientes do fichário
        Pet[] sala = new Pet[quantos];
        for (int i = 0; i < quantos; i++) {
            sala[i] = pacientes[i];
        }

        algoritmo.setNarrar(true);
        algoritmo.ordenarCopia(sala);
        algoritmo.setNarrar(false);   // desliga para não narrar em outras opções
        System.out.println();
        imprimirContadores(algoritmo);
    }

    /**
     * Os pacientes que estão no fichário AGORA, na ordem de chegada:
     * primeiro os do arquivo (na ordem do arquivo, pulando quem já recebeu alta),
     * depois os cadastrados pelo menu.
     */
    private Pet[] pacientesNaOrdemDeChegada() {
        Pet[] naTabela = tabela.paraVetor();
        Pet[] resultado = new Pet[naTabela.length];
        int posicao = 0;

        // 1) pets do arquivo que ainda estão no fichário
        for (int i = 0; i < ordemDoArquivo.length; i++) {
            if (contem(naTabela, ordemDoArquivo[i])) {
                resultado[posicao] = ordemDoArquivo[i];
                posicao++;
            }
        }
        // 2) pets que estão no fichário mas não vieram do arquivo (cadastrados pela opção 1)
        for (int i = 0; i < naTabela.length; i++) {
            if (!contem(ordemDoArquivo, naTabela[i])) {
                resultado[posicao] = naTabela[i];
                posicao++;
            }
        }
        return resultado;
    }

    /** True se o vetor contém exatamente esse pet (o mesmo objeto). */
    private boolean contem(Pet[] vetor, Pet pet) {
        for (int i = 0; i < vetor.length; i++) {
            if (vetor[i] == pet) {
                return true;
            }
        }
        return false;
    }

    /** Pergunta qual algoritmo usar. Devolve null se a entrada acabou. */
    private Ordenador escolherAlgoritmo() {
        System.out.println("Algoritmo: 1 - Bubble Sort | 2 - Insertion Sort | 3 - Quick Sort");
        int escolha = lerOpcao("Escolha: ", 1, 3);
        if (escolha == 1) {
            return new BubbleSort();
        } else if (escolha == 2) {
            return new InsertionSort();
        } else if (escolha == 3) {
            return new QuickSort();
        }
        return null;
    }

    private void imprimirContadores(Ordenador algoritmo) {
        System.out.println("Total → comparações: " + algoritmo.getComparacoes() + " | trocas: " + algoritmo.getTrocas());
    }

    // ===================== opções 8 a 10: experimentos =====================

    /** Opção 8: roda a Grande Corrida e mostra no console as tabelas de tempo e de comparações/trocas. */
    private void grandeCorrida() {
        System.out.println("A corrida usa pacientes gerados (50, 1.000 e 10.000) e leva alguns segundos...");
        GrandeCorrida corrida = new GrandeCorrida();
        corrida.executar();
        corrida.imprimirTabelas();
    }

    /** Opção 9: compara M = 13, 17, 37 × duas funções; opcionalmente roda o teste de carga. */
    private void compararHash() {
        ExperimentoHash.comparar(tabela.paraVetor());
        System.out.println();
        String resposta = lerLinha("Rodar também o teste de carga (10.000 pacientes, M = 17)? (s/n): ");
        if (resposta != null && resposta.equalsIgnoreCase("s")) {
            System.out.println();
            ExperimentoHash.testarCarga(10000, 17);
        }
    }

    /** Opção 10: reconstrói o fichário com outro M e/ou outra função, reinserindo todos os pacientes. */
    private void trocarTabela() {
        System.out.println("Tamanho M: 1 - 13 | 2 - 17 | 3 - 37 (do grupo)");
        int escolhaM = lerOpcao("Escolha: ", 1, 3);
        if (entradaAcabou) {
            return;
        }
        System.out.println("Função hash: 1 - Soma dos caracteres | 2 - Polinomial (base 31)");
        int escolhaFuncao = lerOpcao("Escolha: ", 1, 2);
        if (entradaAcabou) {
            return;
        }

        int[] tamanhos = {13, 17, 37};
        int novoM = tamanhos[escolhaM - 1];
        FuncaoHash novaFuncao = escolhaFuncao == 1 ? new HashSomaCaracteres() : new HashPolinomial();

        // nova tabela vazia + reinserção de todos os pacientes (os índices mudam, então é preciso reinserir)
        Pet[] todos = tabela.paraVetor();
        TabelaHash nova = new TabelaHash(novoM, novaFuncao);
        for (int i = 0; i < todos.length; i++) {
            nova.inserir(todos[i]);
        }
        tabela = nova;
        System.out.println("Fichário reorganizado!");
        tabela.imprimirEstatisticas();
    }

    // ===================== leitura e validação de entradas =====================

    /**
     * Mostra a pergunta e lê uma linha (sem espaços nas pontas).
     * Devolve null se a entrada acabou (ex.: Ctrl+D ou fim do arquivo redirecionado).
     */
    private String lerLinha(String pergunta) {
        System.out.print(pergunta);
        if (!entrada.hasNextLine()) {
            entradaAcabou = true;
            System.out.println();
            return null;
        }
        return entrada.nextLine().trim();
    }

    /** Lê um número inteiro entre min e max, repetindo a pergunta até ser válido. Devolve -1 se a entrada acabar. */
    private int lerOpcao(String pergunta, int min, int max) {
        while (true) {
            String texto = lerLinha(pergunta);
            if (texto == null) {
                return -1;
            }
            if (ehNumero(texto) && texto.length() <= 9) {   // até 9 dígitos cabe num int sem estourar
                int numero = Integer.parseInt(texto);
                if (numero >= min && numero <= max) {
                    return numero;
                }
            }
            System.out.println("Opção inválida! Digite um número de " + min + " a " + max + ".");
        }
    }

    /**
     * Lê um microchip de exatamente 8 dígitos (não pode começar com 0).
     * Enter vazio cancela. Devolve -1 se cancelou ou se a entrada acabou.
     */
    private int lerMicrochip(String pergunta) {
        while (true) {
            String texto = lerLinha(pergunta);
            if (texto == null || texto.isEmpty()) {
                System.out.println("Operação cancelada.");
                return -1;
            }
            if (texto.length() == 8 && ehNumero(texto) && texto.charAt(0) != '0') {
                return Integer.parseInt(texto);
            }
            System.out.println("Microchip inválido! Precisa ter exatamente 8 dígitos (sem letras e sem começar com 0).");
        }
    }

    /**
     * Lê um texto que não pode ficar vazio. Enter vazio cancela (devolve null).
     * O ';' é proibido porque é o separador do arquivo de pets.
     */
    private String lerTextoObrigatorio(String pergunta) {
        while (true) {
            String texto = lerLinha(pergunta);
            if (texto == null || texto.isEmpty()) {
                System.out.println("Operação cancelada (o campo não pode ficar vazio).");
                return null;
            }
            if (!texto.contains(";")) {
                return texto;
            }
            System.out.println("Não use ';' no texto, por favor.");
        }
    }

    /** True se o texto tem só dígitos de 0 a 9 (e pelo menos um). */
    private boolean ehNumero(String texto) {
        if (texto.isEmpty()) {
            return false;
        }
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
