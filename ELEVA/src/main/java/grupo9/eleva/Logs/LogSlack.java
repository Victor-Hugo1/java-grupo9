package grupo9.eleva.logs;

import grupo9.eleva.slack.MonitorCliente;

import java.time.LocalDateTime;

public class LogSlack extends Log{

    private MonitorCliente monitorCliente = new MonitorCliente();

    public LogSlack(LocalDateTime data, Origem origem, Categoria categoria, String mensagem) {
        super(data, origem, categoria, mensagem);
    }

    public LogSlack() {
    }

    public void enviarNotificacaoParaSlack(){
         monitorCliente.iniciarMonitoramento();
    }
}
