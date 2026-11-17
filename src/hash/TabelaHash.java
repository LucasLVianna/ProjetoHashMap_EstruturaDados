package hash;

import modelo.Pet;

/**
 * O fichário da recepção: tabela hash com encadeamento separado.
 *
 * A chave é o nome normalizado do pet. A função hash diz em qual balde (gaveta) a ficha vai.
 * Cada balde é uma ListaEncadeada; pets que caem no mesmo balde ficam enfileirados nela.
 */
public class TabelaHash {

    private ListaEncadeada[] baldes;
    private int m;               // número de baldes
    private FuncaoHash funcao;   // função usada para escolher o balde
    private int quantidade;      // quantos pets estão na tabela agora
    // As colisões NÃO são contadas durante o inserir: são calculadas a partir do estado atual da tabela
    // (getColisoes e getColisoesReais). Assim o resultado não depende da ordem de inserção
    // e continua certo depois de uma alta (remoção).

    /** Cria a tabela com m baldes vazios e a função hash escolhida. */
    public TabelaHash(int m, FuncaoHash funcao) {
        this.m = m;
        this.funcao = funcao;
        this.baldes = new ListaEncadeada[m];
        for (int i = 0; i < m; i++) {
            baldes[i] = new ListaEncadeada();   // cada balde começa como uma lista vazia
        }
    }

    /** Insere o pet no balde indicado pela função hash (no início da lista desse balde). */
    public void inserir(Pet p) {
        int indice = funcao.calcular(p.getChave(), m); // recebe o indice pela soma ou conta polinomial das funções de algoritmo
        baldes[indice].inserir(p); // utiliza o metodo inserir da classe lista encadeada
        quantidade++; // aumenta a quantidade de pet na tabela hash
    }

    /** Índice do balde (gaveta) onde fica esse nome. */
    public int indiceDoBalde(String nome) {
        return funcao.calcular(Normalizador.normalizar(nome), m); // procura o indice da gaveta usando o metodo das funções de algoritmo com base na chave normalizada
    }

    /** Quantos pets há no balde de índice informado (0 = balde vazio). */
    public int tamanhoDoBalde(int indice) {
        return baldes[indice].tamanho(); // utiliza o metodo da classe lista encadeada para verificar a gaveta
    }

    /**
     * Busca todos os pets com esse nome. Só precisa olhar UM balde:
     * calcula o índice do nome e procura dentro daquela lista.
     */
    public Pet[] buscarPorNome(String nome) {
        String chave = Normalizador.normalizar(nome); // normaliza a chave
        int indice = funcao.calcular(chave, m); // calcula e encontra o indice da gaveta pelo metodo das funções de algoritmo
        return baldes[indice].buscarPorChave(chave); // retorna a lista de pets com essa chave (pode ter mais de uma Luna por exemplo, retorna todas as Lunas)
    }

    /**
     * Remove (dá alta) o pet com esse microchip.
     * Como a chave da tabela é o NOME, o microchip não diz em qual balde o pet está:
     * por isso é preciso percorrer todos os baldes.
     * obs: melhoria futura: receber também o nome do pet. Com o nome, calculamos o balde
     * e removemos só nele, sem percorrer a tabela inteira (o microchip sozinho não indica o balde), reduz operações.
     */
    public boolean removerPorMicrochip(int microchip) {
        for (int i = 0; i < m; i++) {
            if (baldes[i].removerPorMicrochip(microchip)) { // usa o metodo da classe lista encadeada para remover o pet pelo chip, percorre todas as gavetas até achar aquele microchip
                quantidade--; // diminui quantidade de pets
                return true;
            }
        }
        return false;
    }

    /** Mostra o fichário balde por balde. Ex.: [03] LUNA(60218841) → LUNA(13870255) → null */
    public void exibir() {
        System.out.println("Fichário: M = " + m + " | função: " + funcao.getNome()); // fala tambem o nome da função que está sendo utilizada
        for (int i = 0; i < m; i++) {
            System.out.print(String.format("[%02d] ", i)); // formatação q adiciona 0 antes de digito unico
            baldes[i].imprimir(); // usa função da lista encadeada para imprimir o balde
        }
    }

    /** Fator de carga α = quantidade / m (em média, quantos pets por balde). */
    public double fatorCarga() {
        return (double) quantidade / m;
    }

    /** Quantos baldes estão sem nenhum pet. */
    public int baldesVazios() {
        int vazios = 0;
        for (int i = 0; i < m; i++) {
            if (baldes[i].estaVazia()) { // usa metodo da classe lista encadeada
                vazios++;
            }
        }
        return vazios;
    }

    /** Tamanho da maior lista: o pior caso de uma busca nesta tabela. */
    public int maiorLista() {
        int maior = 0;
        for (int i = 0; i < m; i++) {
            if (baldes[i].tamanho() > maior) { // usa metodo da classe lista encadeada
                maior = baldes[i].tamanho(); // usa metodo da classe lista encadeada
            }
        }
        return maior;
    }

    /**
     * >>> CÁLCULO DAS COLISÕES
     * A tabela tem M gavetas e a função hash diz em qual gaveta cada nome vai.
     * Colisão é quando dois nomes diferentes recebem a mesma gaveta.
     * Ela é inevitável, por dois motivos:
     *
     * Há mais pets do que gavetas: 51 pets em 17 gavetas.
     * Alguma gaveta vai ter mais de um, como pôr 51 cartas em 17 envelopes.
     *
     * A função hash “aperta” muitos nomes em poucos números: LUNA dá 304 e PACOCA dá 423, mas os dois têm resto 15 na divisão por 17.
     * Nomes diferentes, mesma gaveta.
     *
     * Num balde com k pets, o primeiro "chegou sem colidir" e os outros k - 1 colidiram.
     * Somando (k - 1) de cada balde ocupado: colisões = quantidade - baldes ocupados.
     */
    public int getColisoes() {
        int ocupados = m - baldesVazios(); // baldes ocupados = total de baldes (m) - baldes vazios
        return quantidade - ocupados; // colisões = pets - baldes ocupados (inclui nomes repetidos, como as Lunas)
    }

    /**
     * >>> CÁLCULO DAS COLISÕES REAIS (nomes DIFERENTES no mesmo balde)
     * Duas Lunas juntas não contam: mesma chave sempre gera o mesmo índice.
     * Num balde com d nomes distintos, o primeiro nome é o "dono" e os outros d - 1 colidiram de verdade.
     * Ex.: balde com LUNA, LUNA, AMORA, THOR → 3 nomes distintos → 2 colisões reais.
     */
    public int getColisoesReais() {
        int total = 0;
        for (int i = 0; i < m; i++) {
            int distintos = baldes[i].contarChavesDistintas(); // usa metodo da classe lista encadeada para verificar quantas chaves são diferentes
            if (distintos > 1) {
                total = total + (distintos - 1); // calcula colisoes reais com base nas chaves distintas
            }
        }
        return total; // se nenhuma gaveta tiver 2 ou mais nomes diferentes, o total é 0
    }

    /** Quantos pets estão no fichário agora. */
    public int getQuantidade() {
        return quantidade;
    }

    /** Número de baldes (gavetas) da tabela. */
    public int getM() {
        return m;
    }

    /** Função hash usada por esta tabela. */
    public FuncaoHash getFuncao() {
        return funcao;
    }

    /** Imprime um resumo dos números do fichário. */
    public void imprimirEstatisticas() {
        System.out.println("Estatísticas do fichário");
        System.out.println("  Função hash ........ " + funcao.getNome());
        System.out.println("  M (baldes) ......... " + m);
        System.out.println("  Pacientes .......... " + quantidade);
        System.out.println("  Fator de carga ..... " + String.format("%.2f", fatorCarga()));
        System.out.println("  Baldes vazios ...... " + baldesVazios());
        System.out.println("  Maior lista ........ " + maiorLista());
        System.out.println("  Colisões ........... " + getColisoes() + "  (pets além do primeiro em cada balde)");
        System.out.println("  Colisões reais ..... " + getColisoesReais() + "  (nomes diferentes no mesmo balde)");
    }

    /**
     * Mostra os baldes onde há nomes DIFERENTES juntos (exemplos de colisão real).
     * Para cada balde, monta a lista de nomes sem repetir; se tiver mais de um nome, é colisão real.
     * Devolve quantos baldes desse tipo existem.
     * Diferente do getColisoesReais() (que só calcula o número de colisões reais para as estatísticas),
     * este método MOSTRA na tela os nomes de cada gaveta com colisão real e devolve quantas gavetas são.
     */
    public int imprimirColisoesReais() {
        int baldesComColisaoReal = 0;
        for (int i = 0; i < m; i++) { // 1) passa por cada GAVETA
            Pet[] pets = baldes[i].paraVetor(); // copia os pets da gaveta para um vetor usando metodo da classe lista encadeada
            String nomes = "";
            int nomesDiferentes = 0;
            for (int a = 0; a < pets.length; a++) { // 2) passa por cada PET da gaveta
                // o nome já apareceu antes neste balde? se sim, não conta de novo
                boolean jaVisto = false; // a bandeira começa abaixada para cada pet
                for (int b = 0; b < a; b++) { // 3) olha os pets ANTES do pet "a"
                    if (pets[b].getChave().equals(pets[a].getChave())) {
                        jaVisto = true; // laço b: se achar nome igual antes, jaVisto = true
                    }
                }
                if (!jaVisto) { // nome apareceu pela 1 vez nesta gaveta
                    if (nomesDiferentes == 0) {
                        nomes = pets[a].getChave();                  // 1 nome: só o nome → "PACOCA"
                    } else {
                        nomes = nomes + ", " + pets[a].getChave();   // próximos: vírgula + nome → "PACOCA, LUNA"
                    }
                    nomesDiferentes++;
                }
            }
            if (nomesDiferentes > 1) { // 2 ou mais nomes = colisão real
                System.out.println(String.format("    [%02d] %s", i, nomes)); // imprime "[15] PACOCA, LUNA"
                baldesComColisaoReal++; // conta essa gaveta
            }
        }
        return baldesComColisaoReal;
    }

    /** Copia todos os pets da tabela para um único vetor (usado pela ordenação).
     * O vetor sai na ordem das gavetas, que não é a ordem do arquivo nem a do microchip. Por isso o modo passo a passo usa a ordem do arquivo, e a ordenação é quem arruma por microchip.
     * Ele copia referências e não duplica pets. Os mesmos objetos Pet ficam na tabela e no vetor, e ordenar o vetor não mexe nas gavetas.
     */
    public Pet[] paraVetor() {
        Pet[] todos = new Pet[quantidade];   // vetor do tamanho exato: quantos pets há na tabela
        int posicao = 0;                     // próxima posição livre em "todos"

        for (int i = 0; i < m; i++) {                    // para cada gaveta
            Pet[] doBalde = baldes[i].paraVetor();       // pega os pets dela (vetor vazio se a gaveta está vazia)
            for (int j = 0; j < doBalde.length; j++) {   // para cada pet dessa gaveta
                todos[posicao] = doBalde[j];             // coloca na próxima posição livre
                posicao++;
            }
        }
        return todos;
    }
}
