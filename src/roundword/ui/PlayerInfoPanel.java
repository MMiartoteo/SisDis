package roundword.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;
import java.awt.Component;
import javax.swing.Box;

public class PlayerInfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public PlayerInfoPanel() {
		setBackground(UIConstants.InfoBarBackgroundColor);
		setLayout(new BorderLayout(0, 0));
		
		JLabel lblInformazioniGiocatore = new JLabel("Informazioni Giocatore");
		lblInformazioniGiocatore.setForeground(UIConstants.TextInfoColor);
		lblInformazioniGiocatore.setFont(UIConstants.TextInfoFont);
		add(lblInformazioniGiocatore, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		panel.setBackground(UIConstants.InfoBarBackgroundColor);
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel lblNickname = new JLabel("Nickname");
		lblNickname.setForeground(UIConstants.TextColor);
		lblNickname.setFont(UIConstants.TextNormalFont.deriveFont(30f));
		panel.add(lblNickname);
		
		JPanel panelPoints = new JPanel();
		panelPoints.setBackground(UIConstants.InfoBarBackgroundColor);
		panelPoints.setBorder(new EmptyBorder(8, 0, 0, 0));
		panel.add(panelPoints);
		panelPoints.setLayout(new BoxLayout(panelPoints, BoxLayout.X_AXIS));
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		panelPoints.add(horizontalStrut);
		
		JLabel lblPoints = new JLabel("1000 ");
		lblPoints.setForeground(UIConstants.TextColor);
		lblPoints.setFont(UIConstants.TextNormalFont.deriveFont(16f));
		panelPoints.add(lblPoints);
		
		JLabel lblPointslabel = new JLabel("pt");
		lblPointslabel.setFont(UIConstants.TextNormalFont.deriveFont(15f));
		lblPointslabel.setForeground(UIConstants.TextColor);
		panelPoints.add(lblPointslabel);

	}

}
