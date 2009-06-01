package com.kemai.swing.action;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URL;

import com.kemai.wremja.logging.Logger;

/**
 * Simple action for opening URLs in the Browser.
 * @author remast
 */
public class OpenBrowserAction extends AbstractWAction {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(OpenBrowserAction.class);

	/** The url to be opened. */
	private final String url;

	/**
	 * Creates a new action that opens the given url.
	 * @param url the url to be opened when the action is performed
	 */
	public OpenBrowserAction(final String url) {
		super(url, false);
		this.url = url;
		setTooltip("Open URL in a browser");
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		try {
			Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (Exception ex) {
			LOG.error(ex, ex);
		}
	}

}
