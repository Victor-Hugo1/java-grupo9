package grupo9.eleva.etl;

import java.time.LocalDate;

public class DadosEleva {
    private Double consumo;
    private LocalDate data;
    private String classe;
    private Long consumidores;
    private String uf;
    private String regiao;

    public DadosEleva() {
    }

    public DadosEleva(LocalDate data, String uf, String regiao, String classe, Double consumo, Long consumidores) {
        this.data = data;
        this.uf = uf;
        this.regiao = regiao;
        this.classe = classe;
        this.consumo = consumo;
        this.consumidores = consumidores;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public Double getConsumo() {
        return consumo;
    }

    public void setConsumo(Double consumo) {
        this.consumo = consumo;
    }

    public Long getConsumidores() {
        return consumidores;
    }

    public void setConsumidores(Long consumidores) {
        this.consumidores = consumidores;
    }

    @Override
    public String toString() {
        return "DadosEleva{" +
                "data=" + data +
                ", uf='" + uf + '\'' +
                ", regiao='" + regiao + '\'' +
                ", classe='" + classe + '\'' +
                ", consumo=" + consumo +
                ", consumidores=" + consumidores +
                '}';
    }
}


