package exceptions;

/**
 * Checked Exception: herda diretamente de Exception, entao quem chama
 * o metodo e obrigado a usar try/catch ou declarar throws.
 */
public class VagaNaoDisponivelException extends Exception {

    public VagaNaoDisponivelException(String mensagem) {
        super(mensagem);
    }
}
