package roundword.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import roundword.GameTable;
import roundword.Player;
import roundword.Word;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public class GamePanel extends JPanel implements GameTable.EventListener {
	private static final long serialVersionUID = 1L;

	class CharVerifier extends InputVerifier {
		public boolean verify(JComponent input) {
			return true;

		}
	}

	class UppercaseDocumentFilter extends DocumentFilter {
		public void insertString(DocumentFilter.FilterBypass fb, int offset,
								 String text, AttributeSet attr) throws BadLocationException {

			text = text.toUpperCase();
			text = text.replaceAll("à", "A");
			text = text.replaceAll("é", "E");
			text = text.replaceAll("è", "E");
			text = text.replaceAll("ì", "I");
			text = text.replaceAll("ò", "O");
			text = text.replaceAll("ù", "U");
			text = text.replaceAll("[^A-Z]", "");
			fb.insertString(offset, text.toUpperCase(), attr);
		}

		public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
							String text, AttributeSet attrs) throws BadLocationException {

			text = text.toUpperCase();
			text = text.replaceAll("à", "A");
			text = text.replaceAll("é", "E");
			text = text.replaceAll("è", "E");
			text = text.replaceAll("ì", "I");
			text = text.replaceAll("ò", "O");
			text = text.replaceAll("ù", "U");
			text = text.replaceAll("[^A-Z]", "");
			fb.replace(offset, length, text.toUpperCase(), attrs);
		}
	}

	GameTable gameTable;

	public static final int LAST_WORDS_NUMBER = 3;

	JLabel[] lblLastsWord = new JLabel[LAST_WORDS_NUMBER];
	JTextField txtWord;

	List<Word> words;

	public static final int MinPanelWidth = 250;
	public static final Color LblWordColor = new Color(10,10,10);
	public static final int LblWordColorIncrement = 50;
	public static final String LblLastWord_InWordSyllableColor = "#0A0A0A";
	public static final Font LblsFont = new Font("Lucida Grande", Font.PLAIN, 30);
	public static final int LblsHeightMin = 40;
	public static final Dimension LblsDimensionMin = new Dimension(MinPanelWidth, LblsHeightMin);
	public static final Dimension LblsDimensionMax = new Dimension(Integer.MAX_VALUE, LblsHeightMin);
	public static final Dimension DimensionMin = new Dimension(MinPanelWidth,
															   LblsHeightMin * (LAST_WORDS_NUMBER + 1) + 50);
	
	/**
	 * Create the panel.
	 */
	public GamePanel(GameTable gameTable) {
		this.gameTable = gameTable;

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

		JLabel lblInformazioniGiocatore = new JLabel("La tua parola");
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
				if (keyEvent.getKeyChar() == '\n') endPlay();
			}
			public void keyPressed(KeyEvent keyEvent) {}
			public void keyReleased(KeyEvent keyEvent) {}
		});
		((AbstractDocument) txtWord.getDocument()).setDocumentFilter(new UppercaseDocumentFilter());
		wordPanel.add(txtWord);

		JLabel lblInformazioniGiocatore2 = new JLabel("Le ultime parole");
		lblInformazioniGiocatore2.setForeground(UIConstants.TextInfoColor);
		lblInformazioniGiocatore2.setFont(UIConstants.TextInfoFont);
		add(lblInformazioniGiocatore2);
		
		for (int i = 0; i < LAST_WORDS_NUMBER; i++) {
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

		//Refresh the list depending on the game table
		refresh();

		//Add we to the game table event listener, so we can update the words list graphically
		gameTable.addEventListener(this);


		if (gameTable.getPlayingPlayer() == gameTable.getLocalPlayer()) startPlay();

	}

	public void refresh() {
		for (int i = 0; i < LAST_WORDS_NUMBER; i++) {
			String t;
			if (words.size() > i) {
				Word w = words.get(i);
				if (i == 0) {
					t = "<html>" + w.getSubWordBeforeLastSyllable() + 
						"<span style=\"font-weight:bold; color:" + LblLastWord_InWordSyllableColor + "\">" + 
						w.getLastSyllableSubWord() + "</span></html>";
				} else {
					t = "<html>" + w.toString() + "<html>";
				}
			} else {
				t = "<html> </html>";
			}
			lblLastsWord[i].setText(t);
		}	
	}

	private void startPlay() {
		txtWord.setEnabled(true);
		txtWord.requestFocus();
	}

	private void endPlay() {
		txtWord.setEnabled(false);
		//TODO: check se la parola fa errore (non è presente nel dizionario, o non è conforme alla parola precedente, ecc...)
		gameTable.addWord(new Word(txtWord.getText()));
		txtWord.setText("");
		gameTable.nextTurn();
	}

	@Override
	public void newWordAdded(Word w) {
		refresh();
	}

	@Override
	public void playersPointsUpdate() {

	}

	@Override
	public void playingPlayerChanged(Player oldPlayingPlayer, Player newPlayingPlayer) {
		if (newPlayingPlayer == gameTable.getLocalPlayer()) {
			startPlay();
		}
	}

}
