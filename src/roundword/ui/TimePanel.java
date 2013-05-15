package roundword.ui;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.border.EmptyBorder;

public class TimePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public TimePanel() {
		setAlignmentX(Component.RIGHT_ALIGNMENT);
		setBackground(UIConstants.InfoBarBackgroundColor);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel container = new JPanel();
		container.setBackground(UIConstants.InfoBarBackgroundColor);
		container.setAlignmentX(Component.RIGHT_ALIGNMENT);
		add(container);
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		JLabel lblTimeLabel = new JLabel("Tempo");
		container.add(lblTimeLabel);
		lblTimeLabel.setForeground(UIConstants.TextInfoColor);
		lblTimeLabel.setFont(UIConstants.TextInfoFont);
		
		JPanel panel = new JPanel();
		container.add(panel);
		panel.setBackground(UIConstants.InfoBarBackgroundColor);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel lblNewLabel_1 = new JLabel("26");
		lblNewLabel_1.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		lblNewLabel_1.setFont(UIConstants.TextNormalFont.deriveFont(30f));
		panel.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("sec");
		lblNewLabel_2.setBorder(new EmptyBorder(0, 0, 4, 0));
		lblNewLabel_2.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		lblNewLabel_2.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		lblNewLabel_2.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(lblNewLabel_2);

	}

}
