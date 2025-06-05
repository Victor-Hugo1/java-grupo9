package slackConnection;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SlackWebhookSender {


    private static final String WEBHOOK_URL = "https://hooks.slack.com/services/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";

    public void enviarMensagem(String mensagem) {
        try {
            String jsonPayload = String.format("{\"text\":\"%s\"}", mensagem);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WEBHOOK_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("[Slack] Mensagem enviada com sucesso!");
            } else {
                System.err.println("[Slack] Erro ao enviar mensagem. Código: " + response.statusCode());
                System.err.println("Resposta: " + response.body());
            }

        } catch (Exception e) {
            System.err.println("[Slack] Falha ao enviar mensagem.");
            e.printStackTrace();
        }
    }
}
