package roundword.ui;

import roundword.Player;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import roundword.ui.PlayersListModel.PlayerInfo;

public class PlayersListElement extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = 1L;
	
	public static final Color BackgroundColor1 = new Color(243, 243, 243);
	public static final Color BackgroundColor2 = new Color(233, 233, 233);

	public static final Color PlayingBackgroundColor = new Color(188, 188, 188);

	JLabel lblNickname;
	JLabel lblPoints;
	JPanel panelPoints;
	JLabel lblPointslabel;

	public PlayersListElement() {
		setOpaque(true);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout());

		lblNickname = new JLabel("nome");
		lblNickname.setForeground(UIConstants.TextColor);
		add(lblNickname, BorderLayout.WEST);

		Component horizontalStrut = Box.createHorizontalStrut(30);
		add(horizontalStrut);

		panelPoints = new JPanel();
		add(panelPoints, BorderLayout.EAST);
		panelPoints.setLayout(new BoxLayout(panelPoints, BoxLayout.X_AXIS));

		lblPoints = new JLabel("0");
		lblPoints.setForeground(UIConstants.TextColor);
		panelPoints.add(lblPoints);

		lblPointslabel = new JLabel("pt");
		lblPointslabel.setForeground(UIConstants.TextColor);
		panelPoints.add(lblPointslabel);

	}

	public Component getListCellRendererComponent(JList list, Object value, int index,
												  boolean isSelected, boolean cellHasFocus) {
		PlayerInfo pInfo = (PlayerInfo) value;
		lblNickname.setText(value.toString());
		lblPoints.setText(String.valueOf(pInfo.getPlayer().getPoints()));

		if (pInfo.isPlaying()) {
			setBackground(PlayingBackgroundColor);
			panelPoints.setBackground(PlayingBackgroundColor);
		} else {
			//change the colors with the position
			if (index % 2 == 0) {
				setBackground(BackgroundColor1);
				panelPoints.setBackground(BackgroundColor1);
			} else {
				setBackground(BackgroundColor2);
				panelPoints.setBackground(BackgroundColor2);
			}
		}

		return this;
	}
}