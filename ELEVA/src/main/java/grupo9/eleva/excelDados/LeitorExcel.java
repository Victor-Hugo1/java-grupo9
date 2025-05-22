package grupo9.eleva.excelDados;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

public class LeitorExcel {

    private static final Logger logger = LogManager.getLogger(LeitorExcel.class);
    private final JdbcTemplate jdbcTemplate;
    public LeitorExcel(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DadosEleva> extrairDados(String nomeArquivo, InputStream arquivo) {

        try {
            logger.info("\nIniciando leitura do arquivo %s\n".formatted(nomeArquivo));

            Workbook workbook;

            if (nomeArquivo.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(arquivo);
            } else {
                workbook = new HSSFWorkbook(arquivo);
            }

            Sheet sheet = workbook.getSheetAt(0);
            List<DadosEleva> dadosExtraidos = new ArrayList<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    logger.info("\nLendo cabeçalho");

                    for (int i = 0; i < 6; i++) {
                        String coluna = row.getCell(i).getStringCellValue(); // aqui pegamos as colunas
                        logger.info("Coluna " + i + ": " + coluna);
                    }

                    System.out.println("------------------------------------------------------------");
                    continue;
                }

                logger.info("Lendo linha " + row.getRowNum());

                DadosEleva dadosEleva = new DadosEleva();
                dadosEleva.setData(converterDate(row.getCell(0).getDateCellValue()));
                dadosEleva.setUf(row.getCell(1).getStringCellValue());
                dadosEleva.setRegiao(row.getCell(2).getStringCellValue());
                dadosEleva.setClasse(row.getCell(3).getStringCellValue());

                Integer consumoFormatado = (int) row.getCell(4).getNumericCellValue();
                dadosEleva.setConsumo((double) consumoFormatado);

                Integer consumidorFormatado = (int) row.getCell(5).getNumericCellValue();
                dadosEleva.setConsumidores((long) consumidorFormatado);


                dadosExtraidos.add(dadosEleva);
            }


            workbook.close();
            logger.info("\nLeitura do arquivo finalizada\n");

            insercaoDados(dadosExtraidos);
            return dadosExtraidos;

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    private LocalDate converterDate(Date data) {
        return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void enviarDadosEleva(List<DadosEleva> dadosEleva) {

        String sql = "INSERT INTO consumo_energia (data, classe, consumo, consumidores, uf, regiao) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                DadosEleva dados = dadosEleva.get(i);
                ps.setDate(1, java.sql.Date.valueOf(dados.getData()));
                ps.setString(2, dados.getClasse());
                ps.setDouble(3, dados.getConsumo());
                ps.setLong(4, dados.getConsumidores());
                ps.setString(5, dados.getUf());
                ps.setString(6, dados.getRegiao());


                //TODO ANALISAR POSSIBILIDADE DE REMOÇÃO
                System.out.printf(
                        "Inserindo dados: [Data: %s | Classe: %s | Consumo: %.2f | Consumidores: %d | UF: %s | Região: %s]%n",
                        dados.getData(),
                        dados.getClasse(),
                        dados.getConsumo(),
                        dados.getConsumidores(),
                        dados.getUf(),
                        dados.getRegiao()
                );
            }

            public int getBatchSize() {
                return dadosEleva.size();
            }
        });
    }


    public void insercaoDados(List<DadosEleva> dadosElevaList){
        final int BATCH_SIZE = 500;
        for (int i = 0; i < dadosElevaList.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, dadosElevaList.size());
            List<DadosEleva> subList = dadosElevaList.subList(i, end);
            enviarDadosEleva(subList);
        }
    }
}
