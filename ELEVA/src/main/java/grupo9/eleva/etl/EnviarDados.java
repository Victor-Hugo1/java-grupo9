package grupo9.eleva.etl;

import grupo9.eleva.logs.Categoria;
import grupo9.eleva.logs.Log;
import grupo9.eleva.logs.Origem;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class EnviarDados {

    private JdbcTemplate jdbcTemplate;
    private List<Log> logsEnviarDados;

    public EnviarDados(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.logsEnviarDados = TransformarDados.getLogs();
    }

    public void enviarDadosConsumo(List<Registro> registros) {

        Log log = new Log(LocalDateTime.now(), Origem.ENVIAR, Categoria.INFO, "Iniciando envio de lote de %d registros para o banco de dados".formatted(registros.size()));
        System.out.println("Realizando o envio dos registros para o banco " + log);
        logsEnviarDados.add(log);

        String sql = "INSERT INTO energia_historico (dataHora, classe, consumo, consumidores, uf, regiao) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Registro dados = registros.get(i);
                    // Otimização: Evite conversões desnecessárias. Use java.sql.Date.valueOf diretamente do LocalDate.
                    ps.setDate(1, java.sql.Date.valueOf(dados.getData()));
                    ps.setString(2, dados.getClasse());
                    ps.setDouble(3, dados.getConsumo());
                    ps.setLong(4, dados.getConsumidores());
                    ps.setString(5, dados.getUf());
                    ps.setString(6, dados.getRegiao());
                }

                @Override
                public int getBatchSize() {
                    return registros.size();
                }
            });
            Log successLog = new Log(LocalDateTime.now(), Origem.ENVIAR, Categoria.INFO, "Lote de %d registros enviado com sucesso!".formatted(registros.size()));
            System.out.println(successLog);
            logsEnviarDados.add(successLog);

        } catch (Exception e) {
            Log errorLog = new Log(LocalDateTime.now(), Origem.ENVIAR, Categoria.ERRO, "Erro ao enviar lote de registros: %s".formatted(e.getMessage()));
            System.err.println(errorLog);
            logsEnviarDados.add(errorLog);
            throw new RuntimeException("Falha ao enviar dados para o banco: " + e.getMessage(), e);
        }
    }

    public void enviarLogs() {
        if (logsEnviarDados.isEmpty()) {
            System.out.println("Nenhum log para enviar para o banco de dados.");
            return;
        }

        String sql = "INSERT INTO log (dataHora, origem, categoria, mensagem) VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Log logsEnviados = logsEnviarDados.get(i);
                    ps.setTimestamp(1, java.sql.Timestamp.valueOf(logsEnviados.getData()));
                    ps.setString(2, logsEnviados.getOrigem().name());
                    ps.setString(3, logsEnviados.getCategoria().name());
                    ps.setString(4, logsEnviados.getMensagem());
                }

                @Override
                public int getBatchSize() {
                    return logsEnviarDados.size();
                }
            });
            System.out.println("Todos os logs enviados para o banco de dados com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao enviar logs para o banco de dados: " + e.getMessage());
        }
    }
}