package grupo9.eleva.etl;

import grupo9.eleva.logs.Log;

import java.util.ArrayList;
import java.util.List;

public class TransformarDados {
    private List <Log> logs = new ArrayList<>(ExtracaoDados.getLogs());

    public List<ExtracaoDados> transformarDados(List<ExtracaoDados> registrosExtraidos){
        List<ExtracaoDados> loteLista = new ArrayList<>();
        final int BATCH_SIZE = 500;
        for (int i = 0; i < registrosExtraidos.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, registrosExtraidos.size());
            List<ExtracaoDados> subList = registrosExtraidos.subList(i, end);
            loteLista.add(subList);
        }
        return(subList);

    }
}
