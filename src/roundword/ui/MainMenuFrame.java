package roundword.ui;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JButton;

import roundword.Constants;
import roundword.Starter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MainMenuFrame extends JFrame implements Starter.EventListener, ActionListener {

	private JPanel contentPane;
	private JTextField txtNickname;
	private JTextField txtRegistrarURL;
	private JTextField txtPort;
	private JLabel messagesLabel;
	private JButton btnStartGame;
	private JButton btnExit;

	Starter starter;

	public MainMenuFrame(Starter starter) {
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
		txtNickname.setText(Constants.nickName);
		panelForm.add(txtNickname);
		txtNickname.setColumns(10);
		
		JLabel portLabel = new JLabel("Porta:");
		panelForm.add(portLabel);
		
		txtPort = new JTextField();
		txtPort.setText(String.valueOf(Constants.portNumber));
		panelForm.add(txtPort);
		txtPort.setColumns(10);
		
		JLabel registrarURLLabel = new JLabel("URL Registrar: ");
		panelForm.add(registrarURLLabel);
		
		txtRegistrarURL = new JTextField();
		txtRegistrarURL.setText(Constants.registrarURL);
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

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.setBackground(UIConstants.BackgroundColor);
		statusBarPanel.add(buttonsPanel, BorderLayout.EAST);

		btnExit = new JButton("Esci");
		btnExit.addActionListener(this);
		buttonsPanel.add(btnExit);

		btnStartGame = new JButton("Start!");
		btnStartGame.addActionListener(this);
		buttonsPanel.add(btnStartGame);
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
		if (actionEvent.getSource() == btnStartGame) {
			if (txtNickname.getText().length() == 0) {
				messagesLabel.setText("Inserisci un nickname");
				return;
			} else {
				Constants.nickName = txtNickname.getText();
			}

			if (txtPort.getText().length() == 0) {
				messagesLabel.setText("Inserisci un numero di porta");
				return;
			} else {
				try {
					Constants.portNumber = Integer.valueOf(txtPort.getText());
				} catch (Exception ex) {
					messagesLabel.setText("Inserisci un numero di porta valido");
					return;
				}
			}

			if (txtRegistrarURL.getText().length() == 0) {
				messagesLabel.setText("Inserisci l'URL del registrar");
				return;
			} else {
				Constants.registrarURL = txtRegistrarURL.getText();
			}

			messagesLabel.setText("");
			btnStartGame.setEnabled(false);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					starter.initializeGame(Constants.nickName, Constants.portNumber, Constants.registrarURL, false);
				}
			});
			t.start();
		} else {
			System.exit(0);
		}
	}

}
