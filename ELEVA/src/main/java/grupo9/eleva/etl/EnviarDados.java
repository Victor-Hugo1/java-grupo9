package grupo9.eleva.etl;

import grupo9.eleva.logs.Log;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnviarDados {

   private List<Log> logsEnviarDados  = new ArrayList<>(TransformarDados.getLogs());

    public void enviarDadosConsumo(List<Registro> registro) {

        String sql = "INSERT INTO energia_historico (dataHora, classe, consumo, consumidores, uf, regiao) VALUES (?, ?, ?, ?, ?, ?)";

        ExtracaoDados.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Registro dados = registro.get(i);
                ps.setDate(1, java.sql.Date.valueOf(dados.getData()));
                ps.setString(2, dados.getClasse());
                ps.setDouble(3, dados.getConsumo());
                ps.setLong(4, dados.getConsumidores());
                ps.setString(5, dados.getUf());
                ps.setString(6, dados.getRegiao());

            }
            public int getBatchSize() {
                return registro.size();
            }
        });
    }

    // ENVIANDO LOGS
        public void enviarLogs() {
            String sql = "INSERT INTO log (dataHora, origem, categoria, mensagem) VALUES (?, ?, ?, ?)";

            ExtracaoDados.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Log logsEnviados = logsEnviarDados.get(i);
                    ps.setTimestamp(1, java.sql.Timestamp.valueOf(logsEnviados.getData()));
                    ps.setString(2, logsEnviados.getOrigem().name());
                    ps.setString(3, logsEnviados.getCategoria().name());
                    ps.setString(4, logsEnviados.getMensagem());
                }
                public int getBatchSize() {
                    return logsEnviarDados.size();
                }
            });
        }
    }

