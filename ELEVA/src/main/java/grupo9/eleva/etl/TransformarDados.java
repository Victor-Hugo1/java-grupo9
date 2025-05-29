package grupo9.eleva.etl;

import grupo9.eleva.logs.Log;

import java.util.ArrayList;
import java.util.List;

public class TransformarDados {
    private static List <Log> logs =  new ArrayList<>(ExtracaoDados.getLogs());
    private EnviarDados enviarDados = new EnviarDados();

    public TransformarDados() {
    }

    public static List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public void transformarDados(List<Registro> registrosExtraidos){
        final int BATCH_SIZE = 100;
        for (int i = 0; i < registrosExtraidos.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, registrosExtraidos.size());
            List<Registro> subList = registrosExtraidos.subList(i, end);
            enviarDados.enviarDadosConsumo(subList);
        }

    }
}
