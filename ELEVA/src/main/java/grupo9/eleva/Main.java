package grupo9.eleva;

import grupo9.eleva.logs.LogsProcessor;
import grupo9.eleva.bdpath.ConexaoBD;
import grupo9.eleva.excelDados.DadosEleva;
import grupo9.eleva.excelDados.LeitorExcel;
import grupo9.eleva.s3connection.ConnectorS3;
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

    private static final ConexaoBD conexaoBD = new ConexaoBD();
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final JdbcTemplate jdbcTemplate = conexaoBD.getConnection();
    private static final LogsProcessor logs = new LogsProcessor(jdbcTemplate);


    public static void main(String[] args) throws IOException {
        try {

            LocalDateTime inicio = LocalDateTime.now();
            Integer totalDeRegistros = 0;
            Integer logsSucesso = 0;
            Integer logsErro = 0;



            S3Client s3Client = new ConnectorS3().getS3Client();
            String bucketName = "eleva-s3";
            String key = "dados-excel/Dados (Grupo 9).xlsx";
            String nomeArquivo = "Dados(Grupo 9).xlsx";

            InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());


            logger.info("Iniciando leitura e inserção de dados do arquivo: %s".formatted(nomeArquivo));
            LeitorExcel leitorDados = new LeitorExcel(jdbcTemplate);
            List<DadosEleva> dadosExtraidos = leitorDados.extrairDados(key, inputStream);

            // Fecha o arquivo
            inputStream.close();

            logger.info("Leitura completa de dados do arquivo: %s".formatted(nomeArquivo));
            for (DadosEleva dadosEleva : dadosExtraidos) {
                logger.info(dadosEleva);
                totalDeRegistros++;
                logsSucesso = totalDeRegistros;
            }


            logs.registrarLog(
                    nomeArquivo,
                    totalDeRegistros,
                    logsSucesso,
                    logsErro,
                    "Arquivo processado",
                    inicio
            );

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            logger.error("Erro ao processar arquivo");
        }
    }
}
