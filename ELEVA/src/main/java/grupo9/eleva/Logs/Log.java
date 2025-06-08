package grupo9.eleva.logs;

import java.time.LocalDateTime;

public class Log {
    private LocalDateTime data;
    private Origem origem;
    private Categoria categoria;
    private String mensagem;

    public Log(LocalDateTime data, Origem origem, Categoria categoria, String mensagem) {
        this.data = data;
        this.origem = origem;
        this.categoria = categoria;
        this.mensagem = mensagem;
    }

    public Log() {
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Origem getOrigem() {
        return origem;
    }

    public void setOrigem(Origem origem) {
        this.origem = origem;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    @Override
    public String toString() {
        return "Log{" +
                "data=" + data +
                ", origem=" + origem +
                ", categoria=" + categoria +
                ", mensagem='" + mensagem + '\'' +
                '}';
    }
}
