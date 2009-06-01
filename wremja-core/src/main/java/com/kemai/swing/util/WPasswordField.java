package com.kemai.swing.util;

import javax.swing.JPasswordField;
import javax.swing.text.Document;

import com.kemai.util.JavaUtils;

/**
 * A custom {@link JPasswordField} with a workaround for the bug:
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6801620
 * 
 * @author kutzi
 */
public class WPasswordField extends JPasswordField {

	private static final long serialVersionUID = 1L;

	//private static volatile boolean warningDisplayed = false;
	
	public WPasswordField() {
		super();
		checkJPasswordFieldBug();
	}

	public WPasswordField(Document doc, String txt, int columns) {
		super(doc, txt, columns);
		checkJPasswordFieldBug();
	}

	public WPasswordField(int columns) {
		super(columns);
		checkJPasswordFieldBug();
	}

	public WPasswordField(String text, int columns) {
		super(text, columns);
		checkJPasswordFieldBug();
	}

	public WPasswordField(String text) {
		super(text);
		checkJPasswordFieldBug();
	}

	private void checkJPasswordFieldBug() {
		if(JavaUtils.hasJPasswordFieldBug()) {
			enableInputMethods(true);
		}
		
//		addFocusListener(new FocusAdapter() {
//			@Override
//			public void focusGained(FocusEvent e) {
//				if(!warningDisplayed) {
//					//FIXME warningDisplayed = true;
//					
//					String txt = "<html>You seem to be running on a Java version,<br> which has a bug in the password fields,<br>" +
//					" which results in reduced security.<br> Please upgrade to a newer Java version (Java 6 Update 14 or higher)!<br>" +
//					" (This message won't show again until the next restart.)</html>";
//					
//					BalloonTipStyle style = new ModernBalloonStyle(10, 10, Color.WHITE, new Color(230,230,230), Color.BLACK);
//					style = new RoundedBalloonStyle(5, 5, GuiConstants.BEIGE, Color.DARK_GRAY);
//					
//					BalloonTip balloonTip = new BalloonTip(WPasswordField.this, txt, style, true);
//					ToolTipUtils.balloonToToolTip(balloonTip, 500, 7000);
//					//TimingUtils.showTimedBalloon(balloonTip, Integer.valueOf(7000));
//				}
//				
//			}
//			
//		});
	}
}
