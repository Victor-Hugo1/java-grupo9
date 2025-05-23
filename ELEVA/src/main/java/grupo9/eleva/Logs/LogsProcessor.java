package grupo9.eleva.Logs;

import grupo9.eleva.Logs.LogsService;
import grupo9.eleva.Logs.LogsCarga;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

public class LogsProcessor {

    private static final Logger logger = LogManager.getLogger(LogsProcessor.class);

    private final LogsService logService;

        public LogsProcessor(JdbcTemplate jdbcTemplate) {
            this.logService = new LogsService(jdbcTemplate);
        }

        public void registrarLog(String nomeArquivo, int total, int sucesso, int falha, String mensagem, LocalDateTime inicio) {
            LogsCarga log = new LogsCarga();
            log.setNomeArquivo(nomeArquivo);
            log.setInicioLog(inicio);
            log.setFimLog(LocalDateTime.now());
            log.setRegistrosTotais(total);
            log.setRegistrosSucesso(sucesso);
            log.setRegistrosErros(falha);
            log.setMensagem(mensagem);

            logService.salvar(log);
            logger.info("Log registrado para arquivo: %s".formatted(nomeArquivo));

        }
    }

