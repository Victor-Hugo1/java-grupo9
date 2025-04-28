package grupo9.eleva;

import grupo9.eleva.bdpath.ConexaoBD;
import grupo9.eleva.excelDados.DadosEleva;
import grupo9.eleva.excelDados.LeitorExcel;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            // 1. Cria a conexão com o banco
            ConexaoBD conexaoBD = new ConexaoBD();
            JdbcTemplate jdbcTemplate = conexaoBD.getConnection();

            // 2. Carrega o arquivo Excel
            String nomeArquivo = "Dados (Grupo 9).xlsx";
            Path caminho = Path.of(nomeArquivo);
            InputStream arquivo = Files.newInputStream(caminho);

            // 3. Passa a conexão para o LeitorExcel
            LeitorExcel leitorDados = new LeitorExcel(jdbcTemplate);
            List<DadosEleva> dadosExtraidos = leitorDados.extrairDados(nomeArquivo, arquivo);

            // 4. Fecha o arquivo
            arquivo.close();

            System.out.println("Dados extraídos:");
            for (DadosEleva dadosEleva : dadosExtraidos) {
                System.out.println(dadosEleva);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao processar o arquivo: " + e.getMessage());
        }
    }
}
