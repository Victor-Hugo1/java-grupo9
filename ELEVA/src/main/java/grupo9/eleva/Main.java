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
import java.sql.Date;
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

            // Verificação na tabela Consumo de Energia para não inserir duas vezes os dados na tabela

            List<DadosEleva> dadosConsumoEnergia = conexao.query(
                    "SELECT * FROM consumoEnergia WHERE consumo = ? AND data = ? AND classe = ? AND consumidores = ?",
                    new BeanPropertyRowMapper<>(DadosEleva.class),
                    dado.getConsumo(),
                    java.sql.Date.valueOf(dado.getData()),
                    dado.getClasse(),
                    dado.getConsumidores()
            );

            // Verificação na tabela Estados para não inserir duas vezes os dados na tabela

            List<DadosEleva> dadosEstados = conexao.query(
                    "SELECT * FROM estados WHERE uf = ? AND regiao = ?",
                    new BeanPropertyRowMapper<>(DadosEleva.class),
                    dado.getUf(),
                    dado.getRegiao()
            );


            if (!dadosConsumoEnergia.isEmpty()) {
                System.out.println("Dado já existe na tabela consumo com o ID: " + idBanco);
                idBanco ++;
            }
            else {
                    System.out.println("Inserindo: " + dado.getConsumo() + " - " + dado.getData() + " - " + dado.getClasse() + " - " + dado.getConsumidores());

                int sqlInsert = conexao.update(
                        "INSERT INTO consumoEnergia (consumo, data, classe, consumidores) VALUES (?, ?, ?, ?)",
                        dado.getConsumo(),
                        Date.valueOf(dado.getData()),
                        dado.getClasse(),
                        dado.getConsumidores()
                );
                idBanco++;
                }

            if(!dadosEstados.isEmpty()){
                System.out.println("Dado já existe na tabela com o ID: " + idBanco);

            }
            else {
                System.out.println("Inserindo: " + dado.getUf() + " - " + dado.getRegiao());

                int sqlInsert = conexao.update(
                        "INSERT INTO estados (uf, regiao) VALUES (?, ?)",
                        dado.getUf(),
                        dado.getRegiao()
                );

                }
            }
        }
    }
