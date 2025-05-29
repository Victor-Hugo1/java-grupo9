package grupo9.eleva;

import grupo9.eleva.etl.EnviarDados;
import grupo9.eleva.etl.ExtracaoDados;
import grupo9.eleva.etl.TransformarDados;
import grupo9.eleva.mysql.ConexaoMySQL;
import grupo9.eleva.etl.Registro;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    private static final ConexaoMySQL CONEXAO_MY_SQL = new ConexaoMySQL();
    private static final JdbcTemplate jdbcTemplate = CONEXAO_MY_SQL.getConnection();

    public static void main(String[] args) throws IOException {

        try {

            // Processo de conexao S3
//            S3Client s3Client = new ConexaoS3().getS3Client();
//
//            String bucketName = "eleva-s3";
//            String key = "dados-excel/Dados (Grupo 9).xlsx";
              String nomeArquivo = "src/main/Dados (Grupo 9).xlsx";
//
//            InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(key)
//                    .build());
            // Conexao S3 já realizada


            // CONEXAO COM ARQUIVO LOCAL
            // Carregando o arquivo excel
            Path caminho = Path.of(nomeArquivo);
            InputStream arquivo = Files.newInputStream(caminho);

            ExtracaoDados extracaoDados = new ExtracaoDados(jdbcTemplate);

            List<Registro> registrosExtraidos = extracaoDados.extrairDados(nomeArquivo, arquivo);

            TransformarDados dataTransform = new TransformarDados();

            dataTransform.transformarDados(registrosExtraidos);

            // Fecha o arquivo
            arquivo.close();

            EnviarDados enviarDados = new EnviarDados();
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
