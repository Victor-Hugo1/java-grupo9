package school.sptech;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
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
    }
}