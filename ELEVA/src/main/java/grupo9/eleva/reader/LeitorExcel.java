package grupo9.eleva.reader;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class LeitorExcel {

    public List<DadosEleva> extrairDados(String nomeArquivo, InputStream arquivo) {
        try {
            System.out.println("\nIniciando leitura do arquivo %s\n".formatted(nomeArquivo));

            Workbook workbook; // Criando um Workbook(arquivo)
            //Workbook é um leitor de arquivo, ele representa um arquivo inteiro da planilha do Excel


            if (nomeArquivo.endsWith(".xlsx")) { // Aqui verificamos se o arquivo é do tipo .xlsx, caso seja passamos um tipo de leitura específica para o arquivo
                workbook = new XSSFWorkbook(arquivo);
            } else {
                workbook = new HSSFWorkbook(arquivo);
            }

            Sheet sheet = workbook.getSheetAt(0); // O sheet representa as folhas do nosso excel, aquela parte abaixo que mostra quantas planilhas temos dentro de um arquivo excel

            List<DadosEleva> dadosExtraidos = new ArrayList<>();

            for (Row row : sheet) { // Row são as linhas, então aqui ele está lendo cada linha da folha

                if (row.getRowNum() == 0) {
                    System.out.println("\nLendo cabeçalho");

                    for (int i = 0; i < 6; i++) {
                        String coluna = row.getCell(i).getStringCellValue(); // aqui pegamos as colunas
                        System.out.println("Coluna " + i + ": " + coluna);
                    }

                    System.out.println("--------------------");
                    continue;
                }

               // Aqui extraímos os valores das células para inserir uma a uma nos atributos criados na classe DadosEleva
                System.out.println("Lendo linha " + row.getRowNum());

                DadosEleva dadosEleva = new DadosEleva(); // Criando o objeto
                dadosEleva.setData(converterDate (row.getCell(0).getDateCellValue())); // Criando o set para inserir uma data
                // O campo acima da data esta sendo formatado e manipulado, normalmente o ApachePOI lê ele como uma String
                // Então é necessário usar o converterDate

                dadosEleva.setUf(row.getCell(1).getStringCellValue()); // Criando o set para inserir o UF
                dadosEleva.setRegiao(row.getCell(2).getStringCellValue()); // Criando o set para Regiao
                dadosEleva.setClasse(row.getCell(3).getStringCellValue()); // Criando o set para Classe
                dadosEleva.setConsumo(row.getCell(4).getNumericCellValue()); // Criando o set para Consumo
                dadosEleva.setConsumidores((long)row.getCell(5).getNumericCellValue()); // Criando o set para os consumidores
                // Normalmente o getNumericCellValue pega sempre um valor inteiro, então aqui é necessário fazer um casting para transformar ele em LONG

                dadosExtraidos.add(dadosEleva); // Dados extraidos e adicionados na lista

            }

            // Fechando o workbook após a leitura
            workbook.close();

            System.out.println("\nLeitura do arquivo finalizada\n");

            return dadosExtraidos;
        } catch (IOException e) {
            // Caso ocorra algum erro durante a leitura do arquivo uma exceção será lançada
            throw new RuntimeException(e);
        }
    }

    private LocalDate converterDate(Date data) {
        return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
