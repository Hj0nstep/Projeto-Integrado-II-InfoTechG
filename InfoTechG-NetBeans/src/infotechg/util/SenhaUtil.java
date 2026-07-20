package infotechg.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Hash de senha com SHA-256 + salt aleatorio por usuario (Etapa 4).
 *
 * Nao ha biblioteca de bcrypt/Argon2 disponivel no projeto (Ant/Java SE puro,
 * sem gerenciador de dependencias), entao o hash e feito com
 * {@link MessageDigest} + {@link SecureRandom}, o que evita os algoritmos
 * proibidos (MD5/SHA1) e a senha em texto puro, mesmo nao sendo tao forte
 * quanto bcrypt/Argon2.
 *
 * Formato armazenado: {@code saltHex:hashHex}.
 */
public final class SenhaUtil {

    private static final String ALGORITMO = "SHA-256";
    private static final int TAMANHO_SALT_BYTES = 16;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private SenhaUtil() {
    }

    public static String gerarHash(String senha) {
        byte[] salt = new byte[TAMANHO_SALT_BYTES];
        ALEATORIO.nextBytes(salt);
        String saltHex = HexFormat.of().formatHex(salt);
        return saltHex + ":" + hashComSalt(senha, saltHex);
    }

    public static boolean verificar(String senha, String hashArmazenado) {
        if (senha == null || hashArmazenado == null) {
            return false;
        }
        String[] partes = hashArmazenado.split(":", 2);
        if (partes.length != 2) {
            return false;
        }
        String saltHex = partes[0];
        String hashEsperado = partes[1];
        byte[] calculado = HexFormat.of().parseHex(hashComSalt(senha, saltHex));
        byte[] esperado = HexFormat.of().parseHex(hashEsperado);
        return MessageDigest.isEqual(calculado, esperado);
    }

    private static String hashComSalt(String senha, String saltHex) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITMO);
            digest.update(HexFormat.of().parseHex(saltHex));
            byte[] resultado = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(resultado);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Algoritmo " + ALGORITMO + " indisponivel", ex);
        }
    }
}
