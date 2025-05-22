package grupo9.eleva.logs;

import java.time.LocalDateTime;

public class LogsCarga {
    private String nomeArquivo;
    private LocalDateTime inicioLog;
    private LocalDateTime fimLog;
    private Integer registrosTotais;
    private Integer registrosErros;
    private Integer registrosSucesso;
    private String mensagem;


    public LogsCarga() {
    }


    public LogsCarga(String nomeArquivo, LocalDateTime inicioLog, LocalDateTime fimLog, Integer registrosTotais, Integer registrosErros, Integer registrosSucesso, String mensagem) {
        this.nomeArquivo = nomeArquivo;
        this.inicioLog = inicioLog;
        this.fimLog = fimLog;
        this.registrosTotais = registrosTotais;
        this.registrosErros = registrosErros;
        this.registrosSucesso = registrosSucesso;
        this.mensagem = mensagem;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public LocalDateTime getInicioLog() {
        return inicioLog;
    }

    public void setInicioLog(LocalDateTime inicioLog) {
        this.inicioLog = inicioLog;
    }

    public LocalDateTime getFimLog() {
        return fimLog;
    }

    public void setFimLog(LocalDateTime fimLog) {
        this.fimLog = fimLog;
    }

    public Integer getRegistrosTotais() {
        return registrosTotais;
    }

    public void setRegistrosTotais(Integer registrosTotais) {
        this.registrosTotais = registrosTotais;
    }

    public Integer getRegistrosErros() {
        return registrosErros;
    }

    public void setRegistrosErros(Integer registrosErros) {
        this.registrosErros = registrosErros;
    }

    public Integer getRegistrosSucesso() {
        return registrosSucesso;
    }

    public void setRegistrosSucesso(Integer registrosSucesso) {
        this.registrosSucesso = registrosSucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }


}
