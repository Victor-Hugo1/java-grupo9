package grupo9.eleva;

import grupo9.eleva.bdpath.ConexaoBD;
import grupo9.eleva.reader.DadosEleva;
import grupo9.eleva.reader.LeitorExcel;
import grupo9.eleva.s3connection.ConnectorS3;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

//        S3
//        S3Client s3Client = new ConnectorS3().getS3Client();
//        String bucketName = "eleva-s3";

        String nomeArquivo = "Dados (Grupo 9).xlsx";
        // Carregando o arquivo excel
        Path caminho = Path.of(nomeArquivo);
        InputStream arquivo = Files.newInputStream(caminho);

        // Extraindo os livros do arquivo
        LeitorExcel leitorDados = new LeitorExcel();
        List<DadosEleva> dadosExtraidos = leitorDados.extrairDados(nomeArquivo, arquivo);

        // Fechando o arquivo após a extração
        arquivo.close();

        System.out.println("Dados extraidos:");
        for (DadosEleva dadosEleva : dadosExtraidos) {
            System.out.println(dadosEleva);
        }

        ConexaoBD dados = new ConexaoBD();
        JdbcTemplate conexao = dados.getConnection();
        Integer idBanco = 1;


        for (DadosEleva dado : dadosExtraidos) {
            // Verificação para não inserir duas vezes no banco
            List<DadosEleva> dadosNoBanco = conexao.query(
                    "SELECT * FROM dados_eleva WHERE data = ? AND uf = ? AND regiao = ? AND classe = ? AND consumo = ? AND consumidores = ?",
                    new BeanPropertyRowMapper<>(DadosEleva.class),
                    java.sql.Date.valueOf(dado.getData()),
                    dado.getUf(),
                    dado.getRegiao(),
                    dado.getClasse(),
                    dado.getConsumo(),
                    dado.getConsumidores()
            );
            if (!dadosNoBanco.isEmpty()) {
                System.out.println("Dado já existe no banco localizado com o ID: " + idBanco);
                idBanco ++;
            } else {
                    System.out.println("Inserindo: " + dado.getData() + " - " + dado.getUf() + " - " + dado.getRegiao() + " - "
                            + dado.getClasse() + " - " + dado.getConsumo() + " - " + dado.getConsumidores());

                    conexao.update(
                            "INSERT INTO dados_eleva (data, uf, regiao, classe, consumo, consumidores) VALUES (?, ?, ?, ?, ?, ?)",
                            java.sql.Date.valueOf(dado.getData()),
                            dado.getUf(),
                            dado.getRegiao(),
                            dado.getClasse(),
                            dado.getConsumo(),
                            dado.getConsumidores()
                    );
                    idBanco++;
                }
            }
        }
    }
