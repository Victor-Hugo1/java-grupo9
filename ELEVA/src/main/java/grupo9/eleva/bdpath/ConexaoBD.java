package grupo9.eleva.bdpath;


import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ConexaoBD {

    private final DataSource dataSource;

    public ConexaoBD() {

        //Aqui usamos métodos do JDBC para criar o nosso caminho de conexão com o banco
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://204.236.198.140:3306/eleva?useSSL=false&serverTimezone=GMT-3");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("sptech");

        this.dataSource = basicDataSource;
    }


    // Aqui adicionamos tudo isso em um método para que possamos usar adiante
    public JdbcTemplate getConnection() {
        return new JdbcTemplate(dataSource);
    }
}
