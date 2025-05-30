package grupo9.eleva.etl;

import grupo9.eleva.logs.Categoria;
import grupo9.eleva.logs.Log;
import grupo9.eleva.logs.Origem;
import org.springframework.jdbc.core.JdbcTemplate; // Importe JdbcTemplate para passar para EnviarDados

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransformarDados {
    private static List<Log> logs;
    private EnviarDados enviarDados;


    public TransformarDados(JdbcTemplate jdbcTemplate) {
        this.logs = new ArrayList<>(ExtracaoDados.getLogs());
        this.enviarDados = new EnviarDados(jdbcTemplate);
    }

    public static List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public void transformarDados(List<Registro> registrosExtraidos){
        Log log = new Log(LocalDateTime.now(), Origem.TRANSFORMAR, Categoria.INFO, "Inicializando processo de transformação dos dados");
        System.out.println(log);
        logs.add(log);

        final int BATCH_SIZE = 2000; // Tamanho do lote. Teste diferentes valores para otimização.
        for (int i = 0; i < registrosExtraidos.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, registrosExtraidos.size());
            List<Registro> subList = registrosExtraidos.subList(i, end);
            enviarDados.enviarDadosConsumo(subList);

            Log batchLog = new Log(LocalDateTime.now(), Origem.TRANSFORMAR, Categoria.INFO, "Transformados e enviados %d registros (lote de %d)".formatted(subList.size(), (i / BATCH_SIZE) + 1));
            System.out.println(batchLog);
            logs.add(batchLog);
        }

        Log finalLog = new Log(LocalDateTime.now(), Origem.TRANSFORMAR, Categoria.INFO, "Processo de transformação e envio concluído para todos os dados.");
        System.out.println(finalLog);
        logs.add(finalLog);
    }
}