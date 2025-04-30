package grupo9.eleva;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationVariable {

    private static final Properties properties = new Properties();

    static {
        try {
            InputStream input = ConfigurationVariable.class.getClassLoader().getResourceAsStream("config.properties");
            if (input == null) {
                throw new RuntimeException("Arquivo config.properties não encontrado no classpath.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar config.properties: " + e.getMessage(), e);
        }
    }

    public static String get(String chave) {
        String valor = System.getenv(chave.toUpperCase());
        if (valor != null && !valor.isBlank()) {
            System.out.println("Valor da variável de ambiente " + chave + ": " + valor);
            return valor;
        }

        return properties.getProperty(chave);
    }
}
