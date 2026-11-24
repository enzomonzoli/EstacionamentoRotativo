package interfaces;

/**
 * Contrato de qualquer coisa que possa ter o preco de uma estadia calculado.
 * Tem um metodo abstrato, um metodo default e um metodo static (Java moderno).
 */
public interface Tarifavel {

    // Constante da interface: valor cobrado por hora
    double VALOR_HORA = 5.00;

    // Metodo abstrato: cada classe implementa do seu jeito
    double calcularPrecoEstadia();

    // Metodo default: comportamento pronto que as classes ja herdam
    default String formatarValor(double valor) {
        return String.format("R$ %.2f", valor);
    }

    // Metodo static: pertence a interface (Tarifavel.calcularHorasCobradas(...))
    // Regra: cobra hora cheia (61 minutos = 2 horas) e o minimo e 1 hora
    static long calcularHorasCobradas(long minutos) {
        if (minutos <= 0) {
            return 1;
        }
        return (minutos + 59) / 60;
    }
}
