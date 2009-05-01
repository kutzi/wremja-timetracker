package com.kemai.swing.action;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;

import com.kemai.wremja.logging.Logger;

/**
 * Simple action for opening URLs in the Browser.
 * @author remast
 */
@SuppressWarnings("serial")
public class OpenBrowserAction extends AbstractAction {

	/** The logger. */
	private static final Logger log = Logger.getLogger(OpenBrowserAction.class);

	/** The url to be opened. */
	private String url;

	/**
	 * Creates a new action that opens the given url.
	 * @param url the url to be opened when the action is performed
	 */
	public OpenBrowserAction(final String url) {
		super(url);
		this.url = url;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		try {
			Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (Exception ex) {
			log.error(ex, ex);
		}

	}

}
