package grupo9.eleva;

import grupo9.eleva.bdpath.ConexaoBD;
import grupo9.eleva.reader.DadosEleva;
import grupo9.eleva.reader.LeitorExcel;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        LeitorExcel linhas = new LeitorExcel();

        String nomeArquivo = "Dados (Grupo 9).xlsx";

        // Carregando o arquivo excel
        Path caminho = Path.of(nomeArquivo);
        InputStream arquivo = Files.newInputStream(caminho);

        // Extraindo os livros do arquivo
        LeitorExcel leitorDados= new LeitorExcel();
        List<DadosEleva> dadosExtraidos = leitorDados.extrairDados(nomeArquivo, arquivo);

        // Fechando o arquivo após a extração
        arquivo.close();

        System.out.println("Dados extraidos:");
        for (DadosEleva dadosEleva : dadosExtraidos) {
            System.out.println(dadosEleva);
        }

        ConexaoBD dados = new ConexaoBD();
        JdbcTemplate connection = dados.getConnection();

        connection.execute("""
                CREATE TABLE dados_eleva (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    data DATE,
                    uf VARCHAR(2),
                    regiao VARCHAR(50),
                    classe VARCHAR(50),
                    consumo DOUBLE,
                    consumidores BIGINT
                );
                """);

        DadosEleva novoDado = new ArrayList<>();

        Integer qtdLinhas = linhas.getQtdLinhas();

        for (int i = 0; i < qtdLinhas; i++) {

            connection.update("INSERT INTO dados_eleva (data, uf, regiao, classe, consumo, consumidores) VALUES (?, ?, ?, ?, ?, ?)",
                  novoDado.getData(), novoDado.getUf(), novoDado.(), novoDado.getDiretor());
        }

    }
}