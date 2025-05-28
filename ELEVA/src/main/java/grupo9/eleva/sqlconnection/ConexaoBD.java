package grupo9.eleva.sqlconnection;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ConexaoBD {

    private final DataSource conexaoBD;

    public ConexaoBD() {
        BasicDataSource configuracaoBD = new BasicDataSource();
        configuracaoBD.setUrl("jdbc:mysql://mysql:3306/eleva?useSSL=false&serverTimezone=GMT-3&allowPublicKeyRetrieval=true");
        configuracaoBD.setUsername("root");
        configuracaoBD.setPassword("sptech");

        this.conexaoBD = configuracaoBD;
    }

    public JdbcTemplate getConnection() {
        return new JdbcTemplate(conexaoBD);
    }
}