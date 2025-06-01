package grupo9.eleva;

import grupo9.eleva.etl.EnviarDados;
import grupo9.eleva.etl.ExtracaoDados;
import grupo9.eleva.etl.TransformarDados;
import grupo9.eleva.logs.Categoria;
import grupo9.eleva.logs.Log;
import grupo9.eleva.logs.Origem;
import grupo9.eleva.mysql.ConexaoMySQL;
import grupo9.eleva.s3.ConexaoS3;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {

    private static final ConexaoMySQL CONEXAO_MY_SQL = new ConexaoMySQL();
    private static final JdbcTemplate jdbcTemplate = CONEXAO_MY_SQL.getConnection();

    public static void main(String[] args) throws IOException {

        try {

            // Processo de conexao S3
            S3Client s3Client = new ConexaoS3().getS3Client(); // Commita essa linha para teste local
            ConexaoS3 conexaoS3 = new ConexaoS3(); // Commita essa linha para teste local
            Log log = new Log(LocalDateTime.now(), Origem.CONEXAO_S3, Categoria.INFO, "Conexão realizada com sucesso");
            System.out.println("Conexão com bucket S3 " + log); // Commita essa linha para teste local

           conexaoS3.adicionarLog(log);// Commita essa linha para teste local

           String bucketName = "eleva-s3";// Commita essa linha para teste local
           String key = "dados-excel/Dados(Grupo09).xlsx";// Commita essa linha para teste local
           String nomeArquivo = "Dados(Grupo09).xlsx";

           InputStream inputStream = s3Client.getObject(GetObjectRequest.builder() // Commita essa linha para teste local
                   .bucket(bucketName) // Commita essa linha para teste local
                   .key(key) // Commita essa linha para teste local
                   .build()); // Commita essa linha para teste local

            Path caminho = Path.of(nomeArquivo);
            Files.copy(inputStream, caminho); // Commita essa linha para teste local

            InputStream arquivo = Files.newInputStream(caminho);

            ExtracaoDados extracaoDados = new ExtracaoDados(jdbcTemplate);
            TransformarDados dataTransform = new TransformarDados(jdbcTemplate);
            System.out.println("Realizando a conexão com o MYSQL: " + log );

            extracaoDados.extrairDadosEmBatch(nomeArquivo, arquivo, batch -> {
                dataTransform.transformarDados(batch); // transforma e envia o batch para o banco
            });

            // Fecha o arquivo
            arquivo.close();

            EnviarDados enviarDados = new EnviarDados(jdbcTemplate);
            enviarDados.enviarLogs();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
    }
}
