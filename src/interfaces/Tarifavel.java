package interfaces;


public interface Tarifavel {


    double VALOR_HORA = 5.00;


    double calcularPrecoEstadia();


    default String formatarValor(double valor) {
        return String.format("R$ %.2f", valor);
    }


    static long calcularHorasCobradas(long minutos) {
        if (minutos <= 0) {
            return 1;
        }
        return (minutos + 59) / 60;
    }
}
