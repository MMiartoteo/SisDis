package roundword.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class PlayersListElement extends JLabel implements ListCellRenderer {
	private static final long serialVersionUID = 1L;
	
	public static final Color BackgroundColor1 = new Color(243, 243, 243);
	public static final Color BackgroundColor2 = new Color(233, 233, 233);

	public PlayersListElement() {
		setOpaque(true);
		setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setText(value.toString());

		//change the colors with the position
		if (index % 2 == 0) setBackground(BackgroundColor1);
		else setBackground(BackgroundColor2);

		return this;
	}
}