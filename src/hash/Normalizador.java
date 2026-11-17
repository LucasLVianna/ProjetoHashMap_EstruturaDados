package hash;

import java.text.Normalizer;

/**
 * Padroniza nomes para que sirvam de chave na tabela hash.
 *
 * Sem isso, "Paçoca" e "PACOCA" teriam hashes diferentes e cairiam em baldes diferentes, mesmo sendo o mesmo nome
 */
public class Normalizador {

    /** Remove acentos, tira espaços das pontas e passa para maiúsculas. Ex: "Paçoca" → "PACOCA". */
    public static String normalizar(String nome) {
        if (nome == null) {
            return "";
        }
        // NFD separa a letra do acento: ç vira c + cedilha (marca separada)
        String decomposto = Normalizer.normalize(nome, Normalizer.Form.NFD);
        // \p{M} = qualquer marca de acentuação trocamos por nada
        String semAcento = decomposto.replaceAll("\\p{M}", "");
        return semAcento.trim().toUpperCase();
    }
}
