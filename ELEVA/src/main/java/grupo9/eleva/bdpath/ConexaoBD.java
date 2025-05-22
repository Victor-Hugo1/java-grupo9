package grupo9.eleva.bdpath;

import grupo9.eleva.ConfigurationVariable;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConexaoBD {

    private final DataSource dataSource;
    private final String url = ConfigurationVariable.get("URL");
    private final String password = ConfigurationVariable.get("PASSWORD");

    public ConexaoBD() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(url);
        basicDataSource.setUsername("root");
        basicDataSource.setPassword(password);

        System.out.println("Tentando conectar ao banco de dados...");


        while (true) {
            try (Connection connection = basicDataSource.getConnection()) {
                System.out.println(" Conexão bem-sucedida com o banco!");
                break;
            } catch (SQLException e) {
                System.out.println("Banco não está pronto. Tentando novamente em 5 segundos...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        this.dataSource = basicDataSource;
    }

    public JdbcTemplate getConnection() {
        return new JdbcTemplate(dataSource);
    }
}