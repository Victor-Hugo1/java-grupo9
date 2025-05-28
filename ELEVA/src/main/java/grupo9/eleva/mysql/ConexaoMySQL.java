package grupo9.eleva.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ConexaoMySQL {

    private final DataSource conexaoBD;
    private BasicDataSource configuracaoBD = new BasicDataSource();

    public ConexaoMySQL() {
        configuracaoBD.setUrl("jdbc:mysql://mysql:3306/eleva?useSSL=false&serverTimezone=GMT-3&allowPublicKeyRetrieval=true");
        configuracaoBD.setUsername("root");
        configuracaoBD.setPassword("sptech");

        this.conexaoBD = configuracaoBD;
    }

    public JdbcTemplate getConnection() {
        return new JdbcTemplate(conexaoBD);
    }
}