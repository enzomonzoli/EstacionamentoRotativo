package model;

import interfaces.Tarifavel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TicketEstacionamento implements Comparable<TicketEstacionamento>, Tarifavel {
    //Atributos
    private String placa;
    private int vaga;
    private LocalDateTime entrada;
    private LocalDateTime saida; //fica null enquanto o veiculo esta no patio

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    //Construtor: a entrada e registrada na hora em que o ticket e criado
    public TicketEstacionamento(String placa, int vaga) {
        this.placa = placa.trim().toUpperCase();
        this.vaga = vaga;
        this.entrada = LocalDateTime.now();
    }

    //Getters e setters
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

    //Tempo de permanencia em minutos (ate a saida ou, se ainda esta no patio, ate agora)
    public long getMinutosPermanencia() {
        LocalDateTime fim = (saida != null) ? saida : LocalDateTime.now();
        return Duration.between(entrada, fim).toMinutes();
    }

    //Implementacao do metodo da interface Tarifavel
    @Override
    public double calcularPrecoEstadia() {
        long horas = Tarifavel.calcularHorasCobradas(getMinutosPermanencia());
        return horas * VALOR_HORA;
    }

    //Ordenacao natural: por horario de entrada (quem chegou primeiro aparece antes)
    @Override
    public int compareTo(TicketEstacionamento outro) {
        int resultado = this.entrada.compareTo(outro.entrada);
        if (resultado == 0) {
            resultado = this.placa.compareTo(outro.placa);
        }
        return resultado;
    }

    //Dois tickets sao iguais se tiverem a mesma placa
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
