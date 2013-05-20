package roundword.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.*;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import roundword.GameTable;
import roundword.Word;
import java.util.List;

public class GamePanel extends JPanel implements GameTable.EventListener {
	private static final long serialVersionUID = 1L;

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
	public static final Dimension LblsDimensionMin = new Dimension(MinPanelWidth, 40);
	public static final Dimension LblsDimensionMax = new Dimension(Integer.MAX_VALUE, 40);
	public static final Dimension DimensionMin = new Dimension(MinPanelWidth, 40 * LAST_WORDS_NUMBER);
	
	/**
	 * Create the panel.
	 */
	public GamePanel(GameTable gameTable) {
		this.gameTable = gameTable;

		this.words = gameTable.getWordsList();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(UIConstants.BackgroundColor);
		setMinimumSize(DimensionMin);

		JPanel wordPanel = new JPanel();
		wordPanel.setLayout(new BoxLayout(wordPanel, BoxLayout.Y_AXIS));
		wordPanel.setBorder(new EmptyBorder(0,0,10,0));
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
		txtWord.setText("nuova parola");
		txtWord.setForeground(LblWordColor);
		txtWord.setMaximumSize(LblsDimensionMax);
		txtWord.setMinimumSize(LblsDimensionMin);
		txtWord.setFont(LblsFont);
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
			lblLastsWord[i].setPreferredSize(LblsDimensionMin);
			add(lblLastsWord[i]);
		}



		//Refresh the list depending on the game table
		refresh();

		//Add we to the game table event listener, so we can update the words list graphically
		gameTable.addEventListener(this);

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
					t = w.toString();
				}
			} else {
				t = "<html></html>";
			}
			lblLastsWord[i].setText(t);
		}	
	}

	@Override
	public void newWordAdded(Word w) {
		refresh();
	}

	@Override
	public void playersPointsUpdate() {

	}
}
