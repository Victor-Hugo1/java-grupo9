package grupo9.eleva.etl;

import grupo9.eleva.logs.Categoria;
import grupo9.eleva.logs.Log;
import grupo9.eleva.logs.Origem;
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
import java.util.function.Consumer;

public class ExtracaoDados {
    private List<Registro> registros = new ArrayList<>();
    private JdbcTemplate jdbcTemplate;
    private static List<Log> logs = new ArrayList<>();

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
        return data.toInstant().atZone(ZoneId.of("America/Sao_Paulo")).toLocalDate();
    }

    public void extrairDadosEmBatch(String nomeArquivo, InputStream arquivo, Consumer<List<Registro>> processadorLista) {
        Log log = new Log(LocalDateTime.now(), Origem.EXTRACAO, Categoria.INFO,
                "Iniciando processo de extração de dados do arquivo %s".formatted(nomeArquivo));
        System.out.println(log);
        logs.add(log);

        try {
            Workbook workbook = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo);
            Sheet sheet = workbook.getSheetAt(0);

            final int BATCH_SIZE = 2000;
            List<Registro> listaRegistros = new ArrayList<>(BATCH_SIZE);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Registro registroDados = new Registro();
                registroDados.setData(conversorData(row.getCell(0).getDateCellValue()));
                registroDados.setUf(row.getCell(1).getStringCellValue());
                registroDados.setRegiao(row.getCell(2).getStringCellValue());
                registroDados.setClasse(row.getCell(3).getStringCellValue());
                registroDados.setConsumo((int) row.getCell(4).getNumericCellValue());
                registroDados.setConsumidores((long) row.getCell(5).getNumericCellValue());

                listaRegistros.add(registroDados);

                if (listaRegistros.size() >= BATCH_SIZE) {
                    processadorLista.accept(new ArrayList<>(listaRegistros));
                    listaRegistros.clear();
                }

                if (row.getRowNum() % 1000 == 0) {
                    log = new Log(LocalDateTime.now(), Origem.EXTRACAO, Categoria.INFO,
                            "Lidas até a linha: " + row.getRowNum());
                    System.out.println(log);
                    logs.add(log);
                }
            }


            if (!listaRegistros.isEmpty()) {
                processadorLista.accept(listaRegistros);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}