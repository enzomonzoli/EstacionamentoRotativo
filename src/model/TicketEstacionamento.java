package model;

import interfaces.Tarifavel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TicketEstacionamento implements Comparable<TicketEstacionamento>, Tarifavel {

    private String placa;
    private int vaga;
    private LocalDateTime entrada;
    private LocalDateTime saida;

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


    public TicketEstacionamento(String placa, int vaga) {
        this.placa = placa.trim().toUpperCase();
        this.vaga = vaga;
        this.entrada = LocalDateTime.now();
    }


    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa.trim().toUpperCase();
    }

    public int getVaga() {
        return vaga;
    }

    public void setVaga(int vaga) {
        this.vaga = vaga;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public LocalDateTime getSaida() {
        return saida;
    }

    public void setSaida(LocalDateTime saida) {
        this.saida = saida;
    }


    public long getMinutosPermanencia() {
        LocalDateTime fim = (saida != null) ? saida : LocalDateTime.now();
        return Duration.between(entrada, fim).toMinutes();
    }


    @Override
    public double calcularPrecoEstadia() {
        long horas = Tarifavel.calcularHorasCobradas(getMinutosPermanencia());
        return horas * VALOR_HORA;
    }


    @Override
    public int compareTo(TicketEstacionamento outro) {
        int resultado = this.entrada.compareTo(outro.entrada);
        if (resultado == 0) {
            resultado = this.placa.compareTo(outro.placa);
        }
        return resultado;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketEstacionamento outro = (TicketEstacionamento) o;
        return Objects.equals(placa, outro.placa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placa);
    }

    @Override
    public String toString() {
        return String.format("Vaga %02d | %s | Entrada: %s | %d min | %s",
                vaga, placa, entrada.format(FORMATO), getMinutosPermanencia(),
                formatarValor(calcularPrecoEstadia()));
    }
}
