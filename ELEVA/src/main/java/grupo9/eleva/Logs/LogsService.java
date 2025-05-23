package grupo9.eleva.Logs;
import org.springframework.jdbc.core.JdbcTemplate;

public class LogsService {

    private final JdbcTemplate jdbcTemplate;

    public LogsService(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        public void salvar(LogsCarga log) {
            String sql = "INSERT INTO logs_service (nomeArquivo, inicio, fim, logs_registrados, erros, sucesso, mensagem) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(
                    sql,
                    log.getNomeArquivo(),
                    log.getInicioLog(),
                    log.getFimLog(),
                    log.getRegistrosTotais(),
                    log.getRegistrosErros(),
                    log.getRegistrosSucesso(),
                    log.getMensagem()
            );
        }
    }

