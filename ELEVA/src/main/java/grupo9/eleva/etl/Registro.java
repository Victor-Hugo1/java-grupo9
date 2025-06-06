package grupo9.eleva.etl;


import java.time.LocalDate;

public class Registro {
    private LocalDate data;
    private String classe;
    private Integer consumo;
    private Long consumidores;
    private String uf;
    private String regiao;

    public Registro() {
    }

    public Registro(LocalDate data , String classe, Integer consumo, Long consumidores, String uf, String regiao) {
        this.data = data;
        this.classe = classe;
        this.consumo = consumo;
        this.consumidores = consumidores;
        this.uf = uf;
        this.regiao = regiao;
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

    public Integer getConsumo() {
        return consumo;
    }

    public void setConsumo(Integer consumo) {
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


