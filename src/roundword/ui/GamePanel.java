package roundword.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.Font;
import java.awt.Component;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JTextField;
import java.awt.Dimension;

import roundword.GameTable;
import roundword.Word;
import java.util.List;

public class GamePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final Color LblWordColor = new Color(10,10,10);
	public static final int LblWordColorIncrement = 50;
	public static final String LblLastWord_InWordSyllableColor = "#0A0A0A";
	
	public static final Font LblsFont = new Font("Lucida Grande", Font.PLAIN, 30);
	
	public static final Dimension LblsDimensionMin = new Dimension(100, 40);
	public static final Dimension LblsDimensionMax = new Dimension(Integer.MAX_VALUE, 40);

	public static final int LAST_WORDS_NUMBER = 3;
	
	JLabel[] lblLastsWord = new JLabel[LAST_WORDS_NUMBER];
	private JTextField txtWord;
	
	List<Word> words;
	
	/**
	 * Create the panel.
	 */
	public GamePanel(GameTable gameTable) {
		this.words = gameTable.getWordsList();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(UIConstants.BackgroundColor);
		
		for (int i = LAST_WORDS_NUMBER-1; i >= 0; i--) {
			lblLastsWord[i] = new JLabel("word " + i);
			lblLastsWord[i].setHorizontalTextPosition(SwingConstants.LEFT);
			lblLastsWord[i].setHorizontalAlignment(SwingConstants.LEFT);
			lblLastsWord[i].setForeground(
					new Color(LblWordColor.getRed() + LblWordColorIncrement*(i+1), 
							  LblWordColor.getGreen() + LblWordColorIncrement*(i+1),
							  LblWordColor.getBlue() + LblWordColorIncrement*(i+1)));
			lblLastsWord[i].setFont(LblsFont);
			lblLastsWord[i].setMaximumSize(LblsDimensionMax);
			lblLastsWord[i].setMinimumSize(LblsDimensionMin);
			add(lblLastsWord[i]);
		}
		
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
		add(txtWord);
		
		//Refresh the list depending on the game table
		refresh();
		
		//Set the callback to the gametable, in this way is the the game table 
		//itself to call the refresh
		gameTable.setOnNewWordAddedListener(new Runnable() {
			public void run() { refresh(); }
		});
		
	}
	
	public void refresh() {
		for (int i = 0; i < LAST_WORDS_NUMBER; i++) {
			if (words.size() > i) {
				Word w = words.get(i);
				String t;
				if (i == 0) {
					t = "<html>" + w.getSubWordBeforeLastSyllable() + 
						"<span style=\"font-weight:bold; color:" + LblLastWord_InWordSyllableColor + "\">" + 
						w.getLastSyllableSubWord() + "</span></html>";
				} else {
					t = w.toString();
				}
				lblLastsWord[i].setText(t);
			} 
		}	
	}

}
