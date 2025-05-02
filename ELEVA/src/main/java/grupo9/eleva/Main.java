package grupo9.eleva;

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
import java.util.List;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        try {

            S3Client s3Client = new ConnectorS3().getS3Client();
            String bucketName = "s3-eleva";
            String key = "dados-excel/Dados (Grupo 9).xlsx";

            InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());

            // Cria a conexão com o banco
            ConexaoBD conexaoBD = new ConexaoBD();

            JdbcTemplate jdbcTemplate = conexaoBD.getConnection();

            // Passa a conexão para o LeitorExcel
            LeitorExcel leitorDados = new LeitorExcel(jdbcTemplate);
            List<DadosEleva> dadosExtraidos = leitorDados.extrairDados(key, inputStream);

            // Fecha o arquivo
            inputStream.close();

            logger.info("Dado extraído com sucesso");
            for (DadosEleva dadosEleva : dadosExtraidos) {
                System.out.println(dadosEleva);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Erro ao processar o arquivo: " + e.getMessage());
        }
    }
}
