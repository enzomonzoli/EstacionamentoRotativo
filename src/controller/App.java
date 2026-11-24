package controller;

import view.EstacionamentoGUI;

public class App {
    public static void main(String[] args) {
        //responsavel por instanciar a janela unica do programa
        EstacionamentoGUI tela = new EstacionamentoGUI();
        tela.setVisible(true);
    }
}
