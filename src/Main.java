import hash.HashSomaCaracteres;
import hash.TabelaHash;
import io.LeitorArquivo;
import modelo.Pet;
import ui.Menu;

/**
 * Ponto de entrada do programa: abre a Clínica Veterinária Patinhas.
 * Carrega os pacientes do arquivo, monta o fichário (tabela hash com M = 17 e soma dos caracteres)
 * e abre o menu da recepção.
 */
public class Main {

    /** Abre a clínica: carrega os pets, monta o fichário e mostra o menu. */
    public static void main(String[] args) {
        Pet[] pets = LeitorArquivo.carregar("dados/pets.txt");
        TabelaHash tabela = new TabelaHash(17, new HashSomaCaracteres());

        int inseridos = 0;
        for (int i = 0; i < pets.length; i++) {
            if (microchipRepetido(pets, i)) {
                System.out.println("Aviso: microchip repetido no arquivo, ficha ignorada: " + pets[i]);
            } else {
                tabela.inserir(pets[i]);
                inseridos++;
            }
        }
        System.out.println("♥ " + inseridos + " pacientes carregados");

        // o Menu recebe também o vetor original, para o modo passo a passo usar a ordem do arquivo
        Menu menu = new Menu(tabela, pets);
        menu.iniciar();
    }

    /** True se o microchip do pet na posição 'posicao' já apareceu antes no vetor (microchip deve ser único). */
    private static boolean microchipRepetido(Pet[] pets, int posicao) {
        for (int i = 0; i < posicao; i++) {
            if (pets[i].getMicrochip() == pets[posicao].getMicrochip()) {
                return true;
            }
        }
        return false;
    }
}
