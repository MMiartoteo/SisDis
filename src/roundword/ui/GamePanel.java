package roundword.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import roundword.Constants;
import roundword.GameTable;
import roundword.Player;
import roundword.Word;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import java.util.List;

public class GamePanel extends JPanel implements GameTable.EventListener {
	private static final long serialVersionUID = 1L;

	volatile boolean isPlaying = false;

	class CharVerifier extends InputVerifier {
		public boolean verify(JComponent input) {
			return true;

		}
	}

	class UppercaseDocumentFilter extends DocumentFilter {
		public void insertString(DocumentFilter.FilterBypass fb, int offset,
								 String text, AttributeSet attr) throws BadLocationException {

			text = text.replaceAll("à", "a");
			text = text.replaceAll("é", "e");
			text = text.replaceAll("è", "e");
			text = text.replaceAll("ì", "i");
			text = text.replaceAll("ò", "o");
			text = text.replaceAll("ù", "u");
			text = text.toUpperCase();
			text = text.replaceAll("[^A-Z]", "");
			fb.insertString(offset, text.toUpperCase(), attr);
		}

		public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
							String text, AttributeSet attrs) throws BadLocationException {

			text = text.replaceAll("à", "a");
			text = text.replaceAll("é", "e");
			text = text.replaceAll("è", "e");
			text = text.replaceAll("ì", "i");
			text = text.replaceAll("ò", "o");
			text = text.replaceAll("ù", "u");
			text = text.toUpperCase();
			text = text.replaceAll("[^A-Z]", "");
			fb.replace(offset, length, text.toUpperCase(), attrs);
		}
	}

	GameTable gameTable;

	TimePanel timePanel;
	JLabel[] lblLastsWord = new JLabel[Constants.WordToDisplay];
	JTextField txtWord;
	List<Word> words;

	public static final int MinPanelWidth = 450;
	public static final int LblWordColorComp = 10;
	public static final Color LblWordColor = new Color(LblWordColorComp,LblWordColorComp,LblWordColorComp);
	public static final int LblWordColorIncrement = (200 - LblWordColorComp)/Constants.WordToDisplay;
	public static final String LblLastWord_InWordSyllableColor = "#0A0A0A";
	public static final Font LblsFont = new Font("Lucida Grande", Font.PLAIN, 30);
	public static final int LblsHeightMin = 40;
	public static final Dimension LblsDimensionMin = new Dimension(MinPanelWidth, LblsHeightMin);
	public static final Dimension LblsDimensionMax = new Dimension(Integer.MAX_VALUE, LblsHeightMin);
	public static final Dimension DimensionMin = new Dimension(MinPanelWidth,
															   LblsHeightMin * (Constants.WordToDisplay + 1) + 50);
	
	/**
	 * Create the panel.
	 */
	public GamePanel(GameTable gameTable, TimePanel timePanel) {
		this.gameTable = gameTable;
		this.timePanel = timePanel;
		this.words = gameTable.getWordsList();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(UIConstants.BackgroundColor);
		setMinimumSize(DimensionMin);
		setPreferredSize(DimensionMin);

		JPanel wordPanel = new JPanel();
		wordPanel.setLayout(new BoxLayout(wordPanel, BoxLayout.Y_AXIS));
		wordPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
		wordPanel.setBackground(UIConstants.BackgroundColor);
		add(wordPanel);

		JLabel lblInformazioniGiocatore = new JLabel("Your word");
		lblInformazioniGiocatore.setForeground(UIConstants.TextInfoColor);
		lblInformazioniGiocatore.setFont(UIConstants.TextInfoFont);
		wordPanel.add(lblInformazioniGiocatore);

		txtWord = new JTextField();
		txtWord.setBorder(null);
		txtWord.setAlignmentX(Component.LEFT_ALIGNMENT);
		txtWord.setHorizontalAlignment(SwingConstants.LEFT);
		txtWord.setBackground(UIConstants.BackgroundColor);
		txtWord.setText("");
		txtWord.setForeground(LblWordColor);
		txtWord.setMaximumSize(LblsDimensionMax);
		txtWord.setMinimumSize(LblsDimensionMin);
		txtWord.setFont(LblsFont);
		txtWord.setEnabled(false);
		txtWord.setInputVerifier(new CharVerifier());
		txtWord.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent keyEvent) {
				if (keyEvent.getKeyChar() == '\n' && txtWord.getText().length() > 0) endPlay(false);
			}
			public void keyPressed(KeyEvent keyEvent) {}
			public void keyReleased(KeyEvent keyEvent) {}
		});
		((AbstractDocument) txtWord.getDocument()).setDocumentFilter(new UppercaseDocumentFilter());
		wordPanel.add(txtWord);

		JLabel lblInformazioniGiocatore2 = new JLabel("Last words");
		lblInformazioniGiocatore2.setForeground(UIConstants.TextInfoColor);
		lblInformazioniGiocatore2.setFont(UIConstants.TextInfoFont);
		add(lblInformazioniGiocatore2);
		
		for (int i = 0; i < Constants.WordToDisplay; i++) {
			lblLastsWord[i] = new JLabel("word " + i);
			lblLastsWord[i].setHorizontalTextPosition(SwingConstants.LEFT);
			lblLastsWord[i].setHorizontalAlignment(SwingConstants.LEFT);
			lblLastsWord[i].setForeground(
					new Color(LblWordColor.getRed() + LblWordColorIncrement * (i + 1),
							LblWordColor.getGreen() + LblWordColorIncrement * (i + 1),
							LblWordColor.getBlue() + LblWordColorIncrement * (i + 1)));
			lblLastsWord[i].setFont(LblsFont);
			lblLastsWord[i].setMaximumSize(LblsDimensionMax);
			lblLastsWord[i].setMinimumSize(LblsDimensionMin);
			//lblLastsWord[i].setPreferredSize(LblsDimensionMin);
			add(lblLastsWord[i]);
		}

		//Set the listener for the timePanel
		timePanel.setEndTimeListener(new Runnable() {
			public void run() { endPlay(true); }
		});

		//Refresh the list depending on the game table
		refresh();

		//Add we to the game table event listener, so we can update the words list graphically
		gameTable.addEventListener(this);


		if (gameTable.getTurnHolder() == gameTable.getLocalPlayer()) startPlay();

	}

	public void refresh() {
		for (int i = 0; i < Constants.WordToDisplay; i++) {
			String t;
			if (words.size() > i) {
				Word w = words.get(i);
				if (i == 0) {
					t = "<html><div style=\"width: 1000\">" + w.getSubWordBeforeLastSyllable() +
						"<span style=\"font-weight:bold; color:" + LblLastWord_InWordSyllableColor + "\">" + 
						w.getLastSyllableSubWord() + "</span></div></html>";
				} else {
					t = "<html><div style=\"width: 1000\">" + w.toString() + "</div><html>";
				}
			} else {
				t = "<html><div style=\"width: 1000\"> </div></html>";
			}
			lblLastsWord[i].setText(t);
		}	
	}

	private void startPlay() {
		isPlaying = true;
		txtWord.setEnabled(true);
		txtWord.requestFocus();
		timePanel.startTimer();
	}

	synchronized public void endPlay(boolean timeoutElapsed) {
		if (isPlaying) {
			isPlaying = false;
			txtWord.setEnabled(false);
			gameTable.addWord(timeoutElapsed ? null : new Word(txtWord.getText()), timePanel.endTimer(), true);
			txtWord.setText("");
		}
	}

	@Override
	public void newWordAdded(Player p, Word w, long milliseconds, WordAddedState state) {
		if (!gameTable.isGameFinished()) refresh();
	}

	@Override
	public void playersPointsUpdate() {

	}

	@Override
	public void turnHolderChanged(Player oldTurnHolder, Player newTurnHolder) {
		if (!gameTable.isGameFinished() && newTurnHolder == gameTable.getLocalPlayer()) startPlay();
	}

	@Override
	public void gameFinished(Player winnerPlayer, List<Player> players) {
		isPlaying = false;
		txtWord.setEnabled(false);
		timePanel.endTimer();
		txtWord.setText("");
	}

	@Override
	public void failure(String msg) {

	}
}
