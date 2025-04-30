package grupo9.eleva.bdpath;


import grupo9.eleva.ConfigurationVariable;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ConexaoBD {

    private final DataSource dataSource;
    private String url = ConfigurationVariable.get("URL");
    private String password = ConfigurationVariable.get("PASSWORD");

    public ConexaoBD() {


        //Aqui usamos métodos do JDBC para criar o nosso caminho de conexão com o banco
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(url);
        basicDataSource.setUsername("root");
        basicDataSource.setPassword(password);

        this.dataSource = basicDataSource;
    }


    // Aqui adicionamos tudo isso em um método para que possamos usar adiante
    public JdbcTemplate getConnection() {
        return new JdbcTemplate(dataSource);
    }
}
