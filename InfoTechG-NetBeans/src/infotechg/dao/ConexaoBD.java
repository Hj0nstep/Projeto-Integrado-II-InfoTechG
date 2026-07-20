package infotechg.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fabrica de conexoes JDBC com o MySQL (Etapa 4).
 *
 * As credenciais ficam em {@code db.properties} (classpath, pacote
 * infotechg.dao), nunca hardcoded no codigo-fonte.
 */
public final class ConexaoBD {

    private static final Properties CONFIGURACAO = carregarConfiguracao();

    private ConexaoBD() {
    }

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(
                CONFIGURACAO.getProperty("db.url"),
                CONFIGURACAO.getProperty("db.user"),
                CONFIGURACAO.getProperty("db.password"));
    }

    private static Properties carregarConfiguracao() {
        Properties propriedades = new Properties();
        try (InputStream entrada = ConexaoBD.class.getResourceAsStream("db.properties")) {
            if (entrada == null) {
                throw new IllegalStateException("Arquivo db.properties nao encontrado no pacote infotechg.dao");
            }
            propriedades.load(entrada);
        } catch (IOException ex) {
            throw new UncheckedIOException("Falha ao carregar db.properties", ex);
        }
        return propriedades;
    }
}
