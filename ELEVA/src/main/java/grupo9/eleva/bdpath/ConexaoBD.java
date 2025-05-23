package grupo9.eleva.bdpath;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ConexaoBD {

    private final DataSource dataSource;

    public ConexaoBD() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://mysql:3306/eleva?useSSL=false&serverTimezone=GMT-3&allowPublicKeyRetrieval=true");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("sptech");

        System.out.println("Tentando conectar ao banco de dados...");
        this.dataSource = basicDataSource;
    }

    public JdbcTemplate getConnection() {
        return new JdbcTemplate(dataSource);
    }
}