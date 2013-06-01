package roundword.ui;

import roundword.Constants;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.border.EmptyBorder;

public class TimePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	long startTime; //Time when the user started to play
	Thread refresher;

	JLabel lblTime;

	Runnable endTimeListener;

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
		
		lblTime = new JLabel(String.valueOf((int) Math.round(Constants.TimeoutMilliseconds / 1000)));
		lblTime.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		lblTime.setFont(UIConstants.TextNormalFont.deriveFont(30f));
		panel.add(lblTime);
		
		JLabel lblTimeSecLabel = new JLabel("sec");
		lblTimeSecLabel.setBorder(new EmptyBorder(0, 0, 4, 0));
		lblTimeSecLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		lblTimeSecLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		lblTimeSecLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(lblTimeSecLabel);

	}

	/**
	 * Start the timer, to count how many time the user takes to write the words
	 */
	public long startTimer() {
		startTime = System.currentTimeMillis();
		refresher = new Thread(new Runnable() {
			public void run() {
				long currentTime;
				double remainingTime;

				while (!Thread.currentThread().isInterrupted()) {
					currentTime = System.currentTimeMillis();
					remainingTime = Constants.TimeoutMilliseconds - (currentTime - startTime);
					if (remainingTime <= 0) {
						if (endTimeListener != null) endTimeListener.run();
						Thread.currentThread().interrupt();
					}
					lblTime.setText(String.valueOf((int) Math.round(remainingTime/1000))
					);
					try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
				}
				lblTime.setText("0");
			}
		});

		lblTime.setText(String.valueOf((int) Math.round(Constants.TimeoutMilliseconds / 1000)));
		refresher.start();
		return startTime;
	}

	/**
	 * end the timer, return the remaining time to reply
	 * */
	public long endTimer() {
		refresher.interrupt();
		return Constants.TimeoutMilliseconds - (System.currentTimeMillis() - startTime);
	}

	public void setEndTimeListener(Runnable listener) {
		if (listener == null) throw new NullPointerException("listener is null");
		endTimeListener = listener;
	}

	public void removeEndTimeListener() {
		endTimeListener = null;
	}

}
