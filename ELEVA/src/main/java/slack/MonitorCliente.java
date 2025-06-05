package slack;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Timer;
import java.util.TimerTask;

public class MonitorCliente {

    private final JdbcTemplate jdbcTemplate;
    private final slackConnection.SlackWebhookSender slackSender;

    public MonitorCliente() {

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/eleva");
        dataSource.setUsername("root");
        dataSource.setPassword("Javeiro7!");

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.slackSender = new slackConnection.SlackWebhookSender();
    }

    public void iniciarMonitoramento() {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {

                    String sqlAtivo = "SELECT isAtivo FROM configuracao_slack WHERE isAtivo = 1 LIMIT 1";
                    Boolean slackAtivo = jdbcTemplate.queryForObject(sqlAtivo, Boolean.class);

                    if (Boolean.TRUE.equals(slackAtivo)) {

                        String sqlCount = "SELECT COUNT(*) FROM leads WHERE foiEnviado = false";
                        Integer naoEnviados = jdbcTemplate.queryForObject(sqlCount, Integer.class);

                        if (naoEnviados != null && naoEnviados > 0) {

                            String mensagem = "📨 Você recebeu " + naoEnviados + " novos leads que ainda não foram processados.";
                            slackSender.enviarMensagem(mensagem);

                            String sqlUpdate = "UPDATE leads SET foiEnviado = true WHERE foiEnviado = false";
                            jdbcTemplate.update(sqlUpdate);
                        }
                    } else {
                        System.out.println("Envio de notificações inativado");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 30000);
    }

    public static void main(String[] args) {
        new MonitorCliente().iniciarMonitoramento();
    }
}
