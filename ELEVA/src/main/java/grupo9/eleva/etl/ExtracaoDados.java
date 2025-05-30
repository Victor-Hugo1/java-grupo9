package grupo9.eleva.etl;

import grupo9.eleva.logs.Categoria;
import grupo9.eleva.logs.Log;
import grupo9.eleva.logs.Origem;
import grupo9.eleva.s3.ConexaoS3;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExtracaoDados {
    private List<Registro> registros = new ArrayList<>();
    private JdbcTemplate jdbcTemplate;
    private static List<Log> logs = new ArrayList<>(ConexaoS3.getLogList());

    public ExtracaoDados(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Registro> getRegistros() {
        return registros;
    }

    public void setRegistros(List<Registro> registros) {
        this.registros = registros;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    private LocalDate conversorData(Date data) {
        return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public List<Registro> extrairDados(String nomeArquivo, InputStream arquivo) {

        try {
            Workbook workbook;

            if (nomeArquivo.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(arquivo);
            } else {
                workbook = new HSSFWorkbook(arquivo);
            }

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    for (int i = 0; i < 6; i++) {
                        String coluna = row.getCell(i).getStringCellValue(); // aqui pegamos as colunas
                    }
                    continue;
                }
                Registro registroDados = new Registro();
                registroDados.setData(conversorData(row.getCell(0).getDateCellValue()));
                registroDados.setUf(row.getCell(1).getStringCellValue());
                registroDados.setRegiao(row.getCell(2).getStringCellValue());
                registroDados.setClasse(row.getCell(3).getStringCellValue());
                Integer consumoFormatado = (int) row.getCell(4).getNumericCellValue();
                registroDados.setConsumo((double) consumoFormatado);
                Integer consumidorFormatado = (int) row.getCell(5).getNumericCellValue();
                registroDados.setConsumidores((long) consumidorFormatado);



                Log log = new Log(LocalDateTime.now(), Origem.EXTRACAO, Categoria.INFO, "Lendo linha: " + row.getRowNum());
                System.out.println("Extraindo dado do arquivo: " + log);

                logs.add(log);
                registros.add(registroDados);

            }
            return registros;

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

}