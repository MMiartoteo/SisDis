package roundword.ui;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.Box;
import roundword.Starter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class ArgumentsChooser extends JFrame implements Starter.EventListener, ActionListener {

	private static final String[] nomiCasuali = {
			"Ramazzore", "Strapaccioni", "Bomba", "Sorpreso", "Stilografo", "Trasandato", "Tremolo"};

	private JPanel contentPane;
	private JTextField txtNickname;
	private JTextField txtRegistrarURL;
	private JTextField txtPort;
	private JLabel messagesLabel;
	private JButton btnStartGame;

	Starter starter;

	public ArgumentsChooser(Starter starter) {
		Random rnd = new Random();

		this.starter = starter;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(UIConstants.BackgroundColor);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(UIConstants.BackgroundColor);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel titleLabel = new JLabel("<html><span style=\"color:#666666;\">The</span> Round Word <span style=\"color:#666666;\">Game</span></html>");
		titleLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
		titleLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 27));
		contentPane.add(titleLabel, BorderLayout.NORTH);
		
		JPanel panelFormContainer = new JPanel();
		panelFormContainer.setBackground(UIConstants.BackgroundColor);
		panelFormContainer.setBorder(new EmptyBorder(5, 5, 5, 5));
		panelFormContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentPane.add(panelFormContainer, BorderLayout.CENTER);
		panelFormContainer.setLayout(new BorderLayout(0, 0));
		
		JPanel panelForm = new JPanel();
		panelForm.setBackground(UIConstants.BackgroundColor);
		panelFormContainer.add(panelForm, BorderLayout.NORTH);
		panelForm.setLayout(new GridLayout(3, 2, 0, 0));
		
		JLabel nicknameLabel = new JLabel("Nickname: ");

		panelForm.add(nicknameLabel);
		
		txtNickname = new JTextField();
		txtNickname.setText(ArgumentsChooser.nomiCasuali[rnd.nextInt(ArgumentsChooser.nomiCasuali.length)]);
		panelForm.add(txtNickname);
		txtNickname.setColumns(10);
		
		JLabel portLabel = new JLabel("Porta:");
		panelForm.add(portLabel);
		
		txtPort = new JTextField();
		txtPort.setText(String.valueOf(6000 + rnd.nextInt(1000)));
		panelForm.add(txtPort);
		txtPort.setColumns(10);
		
		JLabel registrarURLLabel = new JLabel("URL Registrar: ");
		panelForm.add(registrarURLLabel);
		
		txtRegistrarURL = new JTextField();
		txtRegistrarURL.setText("http://localhost:8080");
		panelForm.add(txtRegistrarURL);
		txtRegistrarURL.setColumns(10);
		
		JPanel statusBarPanel = new JPanel();
		statusBarPanel.setBackground(UIConstants.BackgroundColor);
		statusBarPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.add(statusBarPanel, BorderLayout.SOUTH);
		statusBarPanel.setLayout(new BorderLayout(0, 0));
		
		messagesLabel = new JLabel("");
		statusBarPanel.add(messagesLabel, BorderLayout.CENTER);
		starter.setMessageUpdateListener(this);
		
		btnStartGame = new JButton("Start!");
		btnStartGame.addActionListener(this);
		statusBarPanel.add(btnStartGame, BorderLayout.EAST);
	}

	@Override
	public void messageUpdate(String msg) {
		System.out.println(msg);
		messagesLabel.setText(msg);
	}

	@Override
	public void gameStarted() {
		this.setVisible(false);
		this.dispose();
	}

	@Override
	public void gameFailedToStart(String msg) {
		System.err.println(msg);
		messagesLabel.setText(msg);
		btnStartGame.setEnabled(true);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				starter.startGame(txtNickname.getText(), Integer.valueOf(txtPort.getText()), txtRegistrarURL.getText());
			}
		});
		t.start();
		btnStartGame.setEnabled(false);
	}

}
