package grupo9.eleva.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ConexaoMySQL {

    private final DataSource conexaoBD;


    public ConexaoMySQL() {
        BasicDataSource configuracaoBD = new BasicDataSource();
        configuracaoBD.setUrl("jdbc:mysql://localhost:3306/eleva?useSSL=false&serverTimezone=GMT-3&allowPublicKeyRetrieval=true");
        configuracaoBD.setUsername("root");
        configuracaoBD.setPassword("2808");

        this.conexaoBD = configuracaoBD;
    }

    public JdbcTemplate getConnection() {
        return new JdbcTemplate(conexaoBD);
    }
}