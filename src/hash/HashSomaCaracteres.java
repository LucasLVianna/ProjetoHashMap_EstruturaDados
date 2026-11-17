package hash;

/**
 * Função hash do material de apoio: soma os códigos de todos os caracteres e tira o resto por m.
 *
 * Ponto fraco: a ordem das letras não importa. "AMOR" e "ROMA" têm a mesma soma → mesmo balde.
 */
public class HashSomaCaracteres implements FuncaoHash {

    /** Soma os códigos dos caracteres e tira o resto por m; devolve um índice em [0, m). */
    @Override
    public int calcular(String chave, int m) {
        int soma = 0;
        for (int i = 0; i < chave.length(); i++) {
            soma = soma + chave.charAt(i);   // charAt devolve o código do caractere (ex.: 'A' = 65)
        }
        return soma % m;                     // resto da divisão: sempre entre 0 e m-1
    }

    /** Nome mostrado nas tabelas comparativas. */
    @Override
    public String getNome() {
        return "Soma dos caracteres";
    }
}
