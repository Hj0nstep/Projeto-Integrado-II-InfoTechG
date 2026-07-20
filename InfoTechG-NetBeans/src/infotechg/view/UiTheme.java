package infotechg.view;

import java.awt.Color;
import java.awt.Font;

/**
 * Paleta de cores e tipografia definidas no documento de UX/UI da Etapa 2,
 * reaproveitadas aqui para manter a identidade visual entre o protótipo e
 * a implementação Swing.
 */
public final class UiTheme {

    public static final Color PRIMARIO = new Color(0x2C, 0x5F, 0x8A);
    public static final Color PRIMARIO_ESCURO = new Color(0x1E, 0x43, 0x63);
    public static final Color FUNDO = new Color(0xF5, 0xF7, 0xFA);
    public static final Color TEXTO = new Color(0x1B, 0x1E, 0x23);
    public static final Color TEXTO_SECUNDARIO = new Color(0x6B, 0x72, 0x80);
    public static final Color SUCESSO = new Color(0x2E, 0x7D, 0x32);
    public static final Color ALERTA = new Color(0xD9, 0x76, 0x06);
    public static final Color ERRO = new Color(0xB9, 0x1C, 0x1C);
    public static final Color BRANCO = Color.WHITE;

    public static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONTE_PADRAO = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONTE_ROTULO = new Font("Segoe UI", Font.BOLD, 13);

    private UiTheme() {
    }
}
