package grupo9.eleva;

import grupo9.eleva.logs.LogsProcessor;
import grupo9.eleva.mysql.ConexaoMySQL;
import grupo9.eleva.etl.Registro;
import grupo9.eleva.etl.LeitorExcel;
import grupo9.eleva.s3.ConexaoS3;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    private static final ConexaoMySQL CONEXAO_MY_SQL = new ConexaoMySQL();
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final JdbcTemplate jdbcTemplate = CONEXAO_MY_SQL.getConnection();
    private static final LogsProcessor logs = new LogsProcessor(jdbcTemplate);


    public static void main(String[] args) throws IOException {
        try {

            // Processo de conexao S3
            S3Client s3Client = new ConexaoS3().getS3Client();

            String bucketName = "eleva-s3";
            String key = "dados-excel/Dados (Grupo 9).xlsx";
            String nomeArquivo = "Dados(Grupo 9).xlsx";

            InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            // Finalizando conexao

            logger.info("Iniciando leitura e inserção de dados do arquivo: %s".formatted(nomeArquivo));
            LeitorExcel leitorDados = new LeitorExcel(jdbcTemplate);
            List<Registro> dadosExtraidos = leitorDados.extrairDados(key, inputStream);

            // Fecha o arquivo
            inputStream.close();

            logger.info("Leitura completa de dados do arquivo: %s".formatted(nomeArquivo));
            for (Registro registro : dadosExtraidos) {
                logger.info(registro);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
    }
}
