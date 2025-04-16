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
    private Integer qtdLinhas;
    public List<DadosEleva> extrairDados(String nomeArquivo, InputStream arquivo) {
        try {
            System.out.println("\nIniciando leitura do arquivo %s\n".formatted(nomeArquivo));

            // Criando um objeto Workbook a partir do arquivo recebido
            Workbook workbook;
            if (nomeArquivo.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(arquivo);
            } else {
                workbook = new HSSFWorkbook(arquivo);
            }

            Sheet sheet = workbook.getSheetAt(0);

            List<DadosEleva> dadosExtraidos = new ArrayList<>();

            // Iterando sobre as linhas da planilha
            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    System.out.println("\nLendo cabeçalho");

                    for (int i = 0; i < 6; i++) {
                        String coluna = row.getCell(i).getStringCellValue();
                        System.out.println("Coluna " + i + ": " + coluna);
                    }

                    System.out.println("--------------------");
                    continue;
                }

                // Extraindo valor das células e criando objeto DadosEleva
                System.out.println("Lendo linha " + row.getRowNum());

                DadosEleva dadosEleva = new DadosEleva();
                dadosEleva.setData(converterDate (row.getCell(0).getDateCellValue()));
                dadosEleva.setUf(row.getCell(1).getStringCellValue());
                dadosEleva.setRegiao(row.getCell(2).getStringCellValue());
                dadosEleva.setClasse(row.getCell(3).getStringCellValue());
                dadosEleva.setConsumo(row.getCell(4).getNumericCellValue());
                dadosEleva.setConsumidores((long)row.getCell(5).getNumericCellValue());

                dadosExtraidos.add(dadosEleva);
                qtdLinhas++;
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

    public Integer getQtdLinhas() {
        return qtdLinhas;
    }

    public void setQtdLinhas(Integer qtdLinhas) {
        this.qtdLinhas = qtdLinhas;
    }

    private LocalDate converterDate(Date data) {
        return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
