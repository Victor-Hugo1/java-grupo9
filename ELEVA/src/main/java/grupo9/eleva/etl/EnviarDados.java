package grupo9.eleva.etl;

import grupo9.eleva.logs.Categoria;
import grupo9.eleva.logs.Log;
import grupo9.eleva.logs.Origem;
import grupo9.eleva.slack.MonitorCliente;
import grupo9.eleva.slack.SlackWebhookSender;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class EnviarDados {

    private final JdbcTemplate jdbcTemplate;
    private final List<Log> logsEnviarDados;

    public EnviarDados(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.logsEnviarDados = TransformarDados.getLogs();
    }

    public void enviarDadosConsumo(List<Registro> registros) {
        Log log = new Log(LocalDateTime.now(), Origem.ENVIAR, Categoria.INFO,
                "Iniciando envio de lote de %d registros para o banco de dados (transação manual)".formatted(registros.size()));
        System.out.println(log);
        logsEnviarDados.add(log);

        String sql = "INSERT INTO energia_historico (dataHora, classe, consumo, consumidores, uf, regiao) VALUES (?, ?, ?, ?, ?, ?)";

        DataSource dataSource = jdbcTemplate.getDataSource();
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Registro dados : registros) {
                    ps.setDate(1, java.sql.Date.valueOf(dados.getData()));
                    ps.setString(2, dados.getClasse());
                    ps.setDouble(3, dados.getConsumo());
                    ps.setLong(4, dados.getConsumidores());
                    ps.setString(5, dados.getUf());
                    ps.setString(6, dados.getRegiao());
                    ps.addBatch();
                }

                ps.executeBatch();
                conn.commit();
            }

            Log envioComSucesso = new Log(LocalDateTime.now(), Origem.ENVIAR, Categoria.INFO,
                    "Lote de %d registros enviado com sucesso!".formatted(registros.size()));
            System.out.println(envioComSucesso);
            logsEnviarDados.add(envioComSucesso);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Rollback executado devido a erro.");
                } catch (SQLException ex) {
                    System.err.println("Erro durante rollback: " + ex.getMessage());
                }
            }

            Log envioComErro = new Log(LocalDateTime.now(), Origem.ENVIAR, Categoria.ERRO,
                    "Erro ao enviar lote de registros: %s".formatted(e.getMessage()));
            System.err.println(envioComErro);
            logsEnviarDados.add(envioComErro);
            throw new RuntimeException("Falha ao enviar dados para o banco", e);

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Erro ao fechar conexão: " + ex.getMessage());
                }
            }
        }
    }

    public void enviarLogs() {
        if (logsEnviarDados.isEmpty()) {
            System.out.println("Nenhum log para enviar para o banco de dados.");
            return;
        }

        String sql = "INSERT INTO log (dataHora, origem, categoria, mensagem) VALUES (?, ?, ?, ?)";

        try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Log log : logsEnviarDados) {
                    ps.setTimestamp(1, java.sql.Timestamp.valueOf(log.getData()));
                    ps.setString(2, log.getOrigem().name());
                    ps.setString(3, log.getCategoria().name());
                    ps.setString(4, log.getMensagem());
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
                System.out.println("Todos os logs enviados para o banco de dados com sucesso!");
            }
        } catch (Exception e) {
            System.err.println("Erro ao enviar logs para o banco de dados: " + e.getMessage());
        }
    }
}
