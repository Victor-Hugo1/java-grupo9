package grupo9.eleva.slack;

import grupo9.eleva.mysql.ConexaoMySQL;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MonitorCliente {

    private static final JdbcTemplate jdbcTemplate = new ConexaoMySQL().getConnection();
    private final SlackWebhookSender slackSender;

    public MonitorCliente() {
        this.slackSender = new SlackWebhookSender();
    }

    public void iniciarMonitoramento() {
       final Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {

                    String sqlConfig = "SELECT isAtivo, processo_etl, processo_solicitacao FROM configuracao_slack WHERE idUsuario = 1 LIMIT 1";

                    Map<String, Object> configuracoes = jdbcTemplate.queryForMap(sqlConfig);


                    if (configuracoes.isEmpty()) {
                        System.out.println("Nenhuma notificação foi encontrada para o usuário.");
                        return;
                    }

                    Integer isAtivoInt = (Integer) configuracoes.get("isAtivo");
                    Boolean slackAtivo = isAtivoInt != null && isAtivoInt == 1;

                    Integer processoEtlInt = (Integer) configuracoes.get("processo_etl");
                    Boolean processoEtlAtivo = processoEtlInt != null && processoEtlInt == 1;

                    Integer processoSolicitacaoInt = (Integer) configuracoes.get("processo_solicitacao");
                    Boolean processoSolicitacao = processoSolicitacaoInt != null && processoSolicitacaoInt == 1;

                    if (Boolean.TRUE.equals(slackAtivo)) {

                        if (Boolean.TRUE.equals(processoSolicitacao)) {
                            String sqlCount = "SELECT COUNT(*) FROM leads WHERE foiEnviado = false";
                            Integer naoEnviados = jdbcTemplate.queryForObject(sqlCount, Integer.class);

                            if (naoEnviados != null && naoEnviados > 0) {

                                String mensagem = "\uD83D\uDCE2 Atenção: você tem " + naoEnviados + " novos leads aguardando processamento. Acesse o sistema para gerenciá-los.";
                                slackSender.enviarMensagem(mensagem);
                                System.out.println("Enviando mensagem para o SLACK do processo LEADS");
                                String sqlUpdate = "UPDATE leads SET foiEnviado = true WHERE foiEnviado = false";
                                jdbcTemplate.update(sqlUpdate);
                            }
                        } else {
                            System.out.println("O envio de notificações de novos LEADS está desligado ");
                        }

                        if (Boolean.TRUE.equals(processoEtlAtivo)) {
                            System.out.println("Enviando mensagem para o SLACK do processo ETL");
                            String mensagem = "\uD83D\uDD5B O processo de ETL programado para hoje foi finalizado com sucesso. Acesse o sistema para conferir os dados mais recentes.";
                            slackSender.enviarMensagem(mensagem);
                        } else {
                            System.out.println("O envio de notificações do processo de ETL está desligado");
                        }

                    }else {
                        System.out.println("Envio de notificações inativado");
                    }
                    timer.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                    timer.cancel();
                }
            }
        }, 0, 30000);
    }
}

