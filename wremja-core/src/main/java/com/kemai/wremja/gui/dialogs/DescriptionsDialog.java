package com.kemai.wremja.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.kemai.swing.dialog.EscapeDialog;
import com.kemai.swing.text.Html2Text;
import com.kemai.swing.text.SimpleTextPane;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.report.AccumulatedProjectActivity;

public class DescriptionsDialog extends EscapeDialog {

	private static final long serialVersionUID = 1L;
	
	private final AccumulatedProjectActivity activities;

	private JPanel container = new JPanel();;

	public DescriptionsDialog(Frame owner, AccumulatedProjectActivity activities) {
		super(owner);
		this.activities = activities;
		initialize();
	}

    private void initialize() {
        setLocationRelativeTo(getOwner());
        setModalityType(ModalityType.MODELESS);
        
        setMinimumSize(new Dimension(300, 200));
        
        setTitle("Beschreibungen");
        
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        addActivities();
    }

	private void addActivities() {
		
		NumberFormat durationFormat = FormatUtils.getDurationFormat();
		for (ProjectActivity activity : activities.getActivities()) {
			
			String plainDescription = Html2Text.parse(activity.getDescription());
			
			JTextPane pane = new SimpleTextPane();
			
			Document doc = pane.getDocument();

			try {
				SimpleAttributeSet sas = new SimpleAttributeSet();
	            StyleConstants.setBold(sas, true);
				doc.insertString(0, durationFormat.format(activity.getDuration()) + "h", sas);
				
				sas = new SimpleAttributeSet();
	            StyleConstants.setBold(sas, false);
				doc.insertString(doc.getLength(), "\n", sas);
				
				doc.insertString(doc.getLength(), plainDescription, null);
			} catch (BadLocationException e) {
				throw new RuntimeException(e);
			}
			
	        pane.setEditable(false);
	        pane.setBorder(BorderFactory.createLineBorder(GuiConstants.VERY_LIGHT_GREY));
			
			container.add(pane);
		}
	}
}
