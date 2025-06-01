package grupo9.eleva;

import grupo9.eleva.etl.EnviarDados;
import grupo9.eleva.etl.ExtracaoDados;
import grupo9.eleva.etl.TransformarDados;
import grupo9.eleva.logs.Categoria;
import grupo9.eleva.logs.Log;
import grupo9.eleva.logs.Origem;
import grupo9.eleva.mysql.ConexaoMySQL;
import grupo9.eleva.etl.Registro;
import grupo9.eleva.s3.ConexaoS3;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRotY;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    private static final ConexaoMySQL CONEXAO_MY_SQL = new ConexaoMySQL();
    private static final JdbcTemplate jdbcTemplate = CONEXAO_MY_SQL.getConnection();

    public static void main(String[] args) throws IOException {

        try {

            // Processo de conexao S3
            S3Client s3Client = new ConexaoS3().getS3Client();
            ConexaoS3 conexaoS3 = new ConexaoS3();
            Log log = new Log(LocalDateTime.now(), Origem.CONEXAO_S3, Categoria.INFO, "Conexão realizada com sucesso");
            System.out.println("Conexão com bucket S3 " + log);

            conexaoS3.adicionarLog(log);

            String bucketName = "eleva-s3";
            String key = "dados-excel/Dados(Grupo09).xlsx";
            String nomeArquivo = "Dados(Grupo09).xlsx";

            InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());

            Path caminho = Path.of(nomeArquivo);
            Files.copy(inputStream, caminho);

            InputStream arquivo = Files.newInputStream(caminho);

            ExtracaoDados extracaoDados = new ExtracaoDados(jdbcTemplate);

            System.out.println("Realizando a conexão com o MYSQL: " + log );

            List<Registro> registrosExtraidos = extracaoDados.extrairDados(nomeArquivo, arquivo);

            TransformarDados dataTransform = new TransformarDados(jdbcTemplate);

            dataTransform.transformarDados(registrosExtraidos);

            // Fecha o arquivo
            arquivo.close();

            EnviarDados enviarDados = new EnviarDados(jdbcTemplate);
            enviarDados.enviarLogs();
//
//            logger.info("Leitura completa de dados do arquivo: %s".formatted(nomeArquivo));
//            for (Registro registro : registrosExtraidos) {
//                logger.info(registro);
//            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
    }
}
