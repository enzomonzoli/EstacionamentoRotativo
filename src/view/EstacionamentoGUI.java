package view;

import exceptions.VagaNaoDisponivelException;
import model.TicketEstacionamento;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EstacionamentoGUI extends JFrame implements ActionListener, WindowListener {
    //Atributos
    private static final int CAPACIDADE = 10; //total de vagas do estacionamento

    private JLabel lblPlaca, lblHoras;
    private JTextField txtPlaca, txtHoras;
    private JButton btnCadastrar, btnPesquisar, btnProcessar, btnOrdenar, btnSair;
    private JTextArea txtRelatorio;
    private JScrollPane scroll;
    private Dimension dimFrame, dimLabel, dimTextField, dimButton, dimScroll;

    //Dados 100% em memoria
    private List<TicketEstacionamento> patio;
    private double totalArrecadado;

    //Construtor
    public EstacionamentoGUI() {
        patio = new ArrayList<>();
        totalArrecadado = 0;

        //Tamanho dos elementos
        dimFrame = new Dimension(600, 540);
        dimLabel = new Dimension(190, 20);
        dimTextField = new Dimension(150, 20);
        dimButton = new Dimension(165, 25);
        dimScroll = new Dimension(540, 290);

        //Aparencia da janela
        this.setTitle("Estacionamento Rotativo");
        this.setSize(dimFrame);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //quem fecha e o WindowListener
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.addWindowListener(this);

        //Elementos
        lblPlaca = new JLabel("Placa (7 caracteres):");
        lblPlaca.setSize(dimLabel);
        lblPlaca.setLocation(25, 25);
        this.add(lblPlaca);

        txtPlaca = new JTextField("");
        txtPlaca.setSize(dimTextField);
        txtPlaca.setLocation(220, 25);
        this.add(txtPlaca);

        lblHoras = new JLabel("Permanência simulada (h):");
        lblHoras.setSize(dimLabel);
        lblHoras.setLocation(25, 55);
        this.add(lblHoras);

        txtHoras = new JTextField("");
        txtHoras.setSize(dimTextField);
        txtHoras.setLocation(220, 55);
        txtHoras.setToolTipText("Opcional. Use so na saida para simular horas de estadia.");
        this.add(txtHoras);

        btnCadastrar = criarBotao("Cadastrar entrada", 25, 95);
        btnPesquisar = criarBotao("Pesquisar placa", 200, 95);
        btnProcessar = criarBotao("Processar saída", 375, 95);
        btnOrdenar = criarBotao("Ordenar por entrada", 25, 130);
        btnSair = criarBotao("Sair", 200, 130);

        txtRelatorio = new JTextArea("");
        txtRelatorio.setEditable(false);
        txtRelatorio.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        scroll = new JScrollPane(txtRelatorio);
        scroll.setSize(dimScroll);
        scroll.setLocation(25, 175);
        this.add(scroll);

        atualizarRelatorio();
    }

    //Cria o botao ja com tamanho, posicao e listener (evita repetir codigo)
    private JButton criarBotao(String texto, int x, int y) {
        JButton botao = new JButton(texto);
        botao.setSize(dimButton);
        botao.setLocation(x, y);
        botao.addActionListener(this);
        this.add(botao);
        return botao;
    }

    //Eventos dos botoes
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSair) {
            sair();
            return;
        }
        if (e.getSource() == btnCadastrar) {
            cadastrarEntrada();
        }
        if (e.getSource() == btnPesquisar) {
            pesquisarPlaca();
        }
        if (e.getSource() == btnProcessar) {
            processarSaida();
        }
        if (e.getSource() == btnOrdenar) {
            Collections.sort(patio); //usa o compareTo do TicketEstacionamento
            atualizarRelatorio();
        }
        txtPlaca.requestFocus();
    }

    //Operacao 1: registrar entrada de um veiculo
    private void cadastrarEntrada() {
        String placa = lerPlaca();
        if (placa == null) {
            return;
        }
        try {
            TicketEstacionamento ticket = estacionar(placa);
            patio.add(ticket);
            txtPlaca.setText("");
            atualizarRelatorio();
            JOptionPane.showMessageDialog(this,
                    "Entrada registrada!\nPlaca: " + ticket.getPlaca() + "\nVaga: " + ticket.getVaga(),
                    "Entrada", JOptionPane.INFORMATION_MESSAGE);
        } catch (VagaNaoDisponivelException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Vaga indisponível", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Cria o ticket ou lanca a exception se nao der para estacionar
    private TicketEstacionamento estacionar(String placa) throws VagaNaoDisponivelException {
        if (buscarPorPlaca(placa) != null) {
            throw new VagaNaoDisponivelException("O veículo " + placa + " já está no pátio.");
        }
        if (patio.size() >= CAPACIDADE) {
            throw new VagaNaoDisponivelException("Estacionamento lotado! Todas as " + CAPACIDADE + " vagas estão ocupadas.");
        }
        return new TicketEstacionamento(placa, proximaVagaLivre());
    }

    //Procura o menor numero de vaga que nao esta em uso
    private int proximaVagaLivre() {
        for (int vaga = 1; vaga <= CAPACIDADE; vaga++) {
            boolean ocupada = false;
            for (TicketEstacionamento t : patio) {
                if (t.getVaga() == vaga) {
                    ocupada = true;
                }
            }
            if (!ocupada) {
                return vaga;
            }
        }
        return -1; //nao chega aqui porque estacionar() ja confere a lotacao
    }

    //Operacao 2: pesquisar um veiculo pela placa
    private void pesquisarPlaca() {
        String placa = lerPlaca();
        if (placa == null) {
            return;
        }
        TicketEstacionamento ticket = buscarPorPlaca(placa);
        if (ticket == null) {
            JOptionPane.showMessageDialog(this, "Veículo " + placa + " não está no pátio.",
                    "Pesquisa", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, ticket.toString(),
                    "Veículo encontrado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //Operacao 3: calcular, cobrar e liberar a vaga
    private void processarSaida() {
        String placa = lerPlaca();
        if (placa == null) {
            return;
        }
        TicketEstacionamento ticket = buscarPorPlaca(placa);
        if (ticket == null) {
            JOptionPane.showMessageDialog(this, "Veículo " + placa + " não está no pátio.",
                    "Saída", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDateTime saida = LocalDateTime.now();
        //Se preencheu as horas simuladas, a saida acontece N horas depois da entrada
        if (!txtHoras.getText().trim().isEmpty()) {
            try {
                int horas = Integer.parseInt(txtHoras.getText().trim());
                if (horas < 0) {
                    JOptionPane.showMessageDialog(this, "As horas não podem ser negativas.",
                            "Valor inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                saida = ticket.getEntrada().plusHours(horas);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Erro: digite um número inteiro de horas.",
                        "Aconteceu um erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        ticket.setSaida(saida);
        double valor = ticket.calcularPrecoEstadia();
        totalArrecadado += valor;
        patio.remove(ticket); //libera a vaga
        txtPlaca.setText("");
        txtHoras.setText("");
        atualizarRelatorio();
        JOptionPane.showMessageDialog(this,
                "Saída registrada!\nPlaca: " + ticket.getPlaca()
                        + "\nPermanência: " + ticket.getMinutosPermanencia() + " min"
                        + "\nTotal a pagar: " + ticket.formatarValor(valor)
                        + "\nVaga " + ticket.getVaga() + " liberada.",
                "Saída", JOptionPane.INFORMATION_MESSAGE);
    }

    //Le e valida a placa digitada. Devolve null se for invalida.
    private String lerPlaca() {
        String placa = txtPlaca.getText().trim().toUpperCase();
        if (!placa.matches("[A-Z0-9]{7}")) {
            JOptionPane.showMessageDialog(this,
                    "A placa deve ter 7 letras ou números (ex.: ABC1D23).",
                    "Placa inválida", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return placa;
    }

    private TicketEstacionamento buscarPorPlaca(String placa) {
        for (TicketEstacionamento t : patio) {
            if (t.getPlaca().equals(placa)) {
                return t;
            }
        }
        return null;
    }

    //Mostra o extrato dos veiculos que estao no patio
    private void atualizarRelatorio() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== EXTRATO DO PÁTIO ===\n");
        sb.append("Vagas ocupadas: ").append(patio.size()).append(" de ").append(CAPACIDADE).append("\n");
        sb.append(String.format("Total arrecadado: R$ %.2f\n", totalArrecadado));
        sb.append("------------------------\n");
        if (patio.isEmpty()) {
            sb.append("Nenhum veículo no pátio.\n");
        }
        for (TicketEstacionamento t : patio) {
            sb.append(t).append("\n");
        }
        txtRelatorio.setText(sb.toString());
        txtRelatorio.setCaretPosition(0);
    }

    private void sair() {
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente sair?",
                "Sair", JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    //WindowListener: so o windowClosing faz algo, os outros ficam vazios
    @Override
    public void windowClosing(WindowEvent e) {
        sair();
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
