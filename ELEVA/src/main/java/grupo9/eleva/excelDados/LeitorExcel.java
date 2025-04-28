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

import grupo9.eleva.bdpath.ConexaoBD;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

public class LeitorExcel {

    private final JdbcTemplate jdbcTemplate;
    public LeitorExcel(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
                dadosEleva.setData(converterDate(row.getCell(0).getDateCellValue())); // Criando o set para inserir uma data
                // O campo acima da data esta sendo formatado e manipulado, normalmente o ApachePOI lê ele como uma String
                // Então é necessário usar o converterDate
                dadosEleva.setUf(row.getCell(1).getStringCellValue()); // Criando o set para inserir o UF
                dadosEleva.setRegiao(row.getCell(2).getStringCellValue()); // Criando o set para Regiao
                dadosEleva.setClasse(row.getCell(3).getStringCellValue()); // Criando o set para Classe

                Integer consumoFormatado = (int) row.getCell(4).getNumericCellValue();
                dadosEleva.setConsumo((double) consumoFormatado); // Criando o set para Consumo

                Integer consumidorFormatado = (int) row.getCell(5).getNumericCellValue();
                dadosEleva.setConsumidores((long) consumidorFormatado); // Criando o set para os consumidores
                // Normalmente o getNumericCellValue pega sempre um valor inteiro, então aqui é necessário fazer um casting para transformar ele em LONG



                dadosExtraidos.add(dadosEleva); // Dados extraidos e adicionados na lista
            }

            // Fechando o workbook após a leitura
            workbook.close();
            System.out.println("\nLeitura do arquivo finalizada\n");

            insercaoDados(dadosExtraidos);
            return dadosExtraidos;

        } catch (IOException e) {
            // Caso ocorra algum erro durante a leitura do arquivo uma exceção será lançada
            throw new RuntimeException(e);
        }
    }


    private LocalDate converterDate(Date data) {
        return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void enviarDadosEleva(List<DadosEleva> dadosEleva) {

        String sql = "INSERT INTO consumoEnergia (consumo, data, classe, consumidores, uf, regiao) VALUES (?, ?, ?, ?, ?, ?)";

        ConexaoBD dados = new ConexaoBD();
        JdbcTemplate conexao = dados.getConnection();

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                DadosEleva dados = dadosEleva.get(i);
                ps.setDouble(1, dados.getConsumo());
                ps.setDate(2, java.sql.Date.valueOf(dados.getData()));
                ps.setString(3, dados.getClasse());
                ps.setLong(4, dados.getConsumidores());
                ps.setString(5, dados.getUf());
                ps.setString(6, dados.getRegiao());

                System.out.printf(
                        "Inserindo dados: [Consumo: %.2f | Data: %s | Classe: %s | Consumidores: %d | UF: %s | Região: %s]%n",
                        dados.getConsumo(),
                        dados.getData(),
                        dados.getClasse(),
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
