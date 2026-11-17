package hash;

/**
 * Função hash criada pelo grupo: polinomial (método de Horner) com base 31.
 *
 * A cada caractere: h = (h * 31 + c) % m.
 * Diferente da soma simples, a POSIÇÃO de cada letra muda o resultado ("AMOR" ≠ "ROMA"),
 * então os nomes se espalham melhor pelos baldes.
 */
public class HashPolinomial implements FuncaoHash {

    private static final int BASE = 31;   // número primo pequeno, o mesmo usado pelo String.hashCode() do Java

    /** Percorre a chave aplicando Horner com base 31; devolve um índice em [0, m). */
    @Override
    public int calcular(String chave, int m) {
        int h = 0;
        for (int i = 0; i < chave.length(); i++) {
            // o "% m" DENTRO do laço mantém h sempre menor que m, então h * 31 nunca estoura o int
            h = (h * BASE + chave.charAt(i)) % m;
        }
        return h;
    }

    /** Nome mostrado nas tabelas comparativas. */
    @Override
    public String getNome() {
        return "Polinomial (base 31)";
    }
}
