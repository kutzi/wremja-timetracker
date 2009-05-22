package com.kemai.swing.text;

import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXPanel;

/**
 * Simple editor for html formatted text.
 * @author remast
 * @author kutzi
 */
@SuppressWarnings("serial")
public class TextEditor extends JXPanel {

    /**
     * A interface that allows to listen for text changes to the card side text panes. Use
     * {@link TextEditor#addTextObserver} method to hook it to the CardPanel.
     */
    public interface TextChangeObserver {
        void onTextChange();
    }


    private static final StyledEditorKit.BoldAction BOLD_ACTION = new StyledEditorKit.BoldAction();
    private static final StyledEditorKit.ItalicAction ITALIC_ACTION = new StyledEditorKit.ItalicAction();
    private static final DefaultEditorKit.CopyAction COPY_ACTION = new DefaultEditorKit.CopyAction();
    private static final DefaultEditorKit.PasteAction PASTE_ACTION = new DefaultEditorKit.PasteAction();
    static {
    	BOLD_ACTION.putValue(SMALL_ICON, new ImageIcon(TextEditor.class.getResource("/icons/text_bold.png"))); //$NON-NLS-1$
        BOLD_ACTION.putValue(SHORT_DESCRIPTION, "Bold Font");
        BOLD_ACTION.setEnabled(false);
        
        ITALIC_ACTION.putValue(SMALL_ICON, new ImageIcon(TextEditor.class.getResource("/icons/text_italic.png"))); //$NON-NLS-1$
        ITALIC_ACTION.putValue(SHORT_DESCRIPTION, "Italic Font");
        ITALIC_ACTION.setEnabled(false);
        
        COPY_ACTION.putValue(SMALL_ICON, new ImageIcon(TextEditor.class.getResource("/icons/edit-copy.png"))); //$NON-NLS-1$ 
        COPY_ACTION.putValue(SHORT_DESCRIPTION, "Copy");
        COPY_ACTION.setEnabled(false);

        PASTE_ACTION.putValue(SMALL_ICON, new ImageIcon(TextEditor.class.getResource("/icons/edit-paste.png"))); //$NON-NLS-1$ 
        PASTE_ACTION.putValue(SHORT_DESCRIPTION, "Paste");
        PASTE_ACTION.setEnabled(false);
    }
    
    private final List<TextChangeObserver> textObservers = new CopyOnWriteArrayList<TextChangeObserver>();
    
    private final JTextPane textPane = new JTextPane();
    {
    	textPane.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				if(e.getMark() != e.getDot()) {
					// there's something selected in the textpane
					BOLD_ACTION.setEnabled(true);
					ITALIC_ACTION.setEnabled(true);
					COPY_ACTION.setEnabled(true);
				} else {
					BOLD_ACTION.setEnabled(false);
					ITALIC_ACTION.setEnabled(false);
					COPY_ACTION.setEnabled(false);
				}
			}
    	});
    }

    private JToolBar toolbar;

    private JXCollapsiblePane collapsiblePane;

    private boolean collapseEditToolbar = true;

    private boolean scrollable = false;

    private final List<Action> actions;
    {
        List<Action> tmp = new ArrayList<Action>();
        tmp.add(BOLD_ACTION);
        tmp.add(ITALIC_ACTION);
        tmp.add(COPY_ACTION);
        tmp.add(PASTE_ACTION);
        actions = Collections.unmodifiableList(tmp);
    }

    private void notifyTextObservers() {
        for (TextChangeObserver txtObserver : textObservers) {
            txtObserver.onTextChange();
        }
    }

    public void addTextObserver(final TextChangeObserver txtObserver) {
        textObservers.add(txtObserver);
    }

    public TextEditor() {
        initialize();
    }

    public TextEditor(final boolean scrollable) {
        this.scrollable = scrollable;
        initialize();
    }

    public TextEditor(final boolean scrollable, final boolean collapseEditToolbar) {
        this.scrollable = scrollable;
        this.collapseEditToolbar = collapseEditToolbar;
        initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());

        StyleSheet styleSheet = new StyleSheet();
        styleSheet.addRule("body {font-family: Tahoma; font-size: 11pt; font-style: normal; font-weight: normal;}");

        HTMLEditorKit editorKit = new HTMLEditorKit();
        editorKit.setStyleSheet(styleSheet);
        textPane.setEditorKit(editorKit);

        textPane.setEnabled(true);
        textPane.setEditable(true);
        textPane.setPreferredSize(new Dimension(120, 50));

        setTabBehavior();
        textPane.addFocusListener(new FocusListener() {

            public void focusGained(final FocusEvent e) {
                if (collapseEditToolbar) {
                    collapsiblePane.setCollapsed(false);
                }
            }

            public void focusLost(final FocusEvent e) {
                if (collapseEditToolbar) {
                    if (e.getOppositeComponent() != null && e.getOppositeComponent().getParent() != toolbar) {
                        collapsiblePane.setCollapsed(true);
                    }
                }
            }

        });

        textPane.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(final DocumentEvent e) {
                notifyTextObservers();
            }

            public void insertUpdate(final DocumentEvent e) {
                notifyTextObservers();
            }

            public void removeUpdate(final DocumentEvent e) {
                notifyTextObservers();
            }
        });

        createToolbar();

        collapsiblePane = new JXCollapsiblePane();
        collapsiblePane.add(toolbar);

        if (!collapseEditToolbar) {
            collapsiblePane.setCollapsed(false);
        } else {
            collapsiblePane.setCollapsed(true);
        }

        this.add(collapsiblePane, BorderLayout.NORTH);
        if (scrollable) {
            this.add(new JScrollPane(textPane), BorderLayout.CENTER);
        } else {
            this.add(textPane, BorderLayout.CENTER);
        }
    }

    private void setTabBehavior() {
        // focus next pane with TAB instead of CTRL+TAB
        Set<KeyStroke> key = new HashSet<KeyStroke>();
        key.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));

        int forwardTraversal = KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS;
        textPane.setFocusTraversalKeys(forwardTraversal, key);

        // focus previous pane with SHIFT+TAB instead of SHIFT+CTRL+TAB
        key = new HashSet<KeyStroke>();
        key.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK));

        final int backwardTraversal = KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS;
        textPane.setFocusTraversalKeys(backwardTraversal, key);

        final int shortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        final KeyStroke ctrlTab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, shortcutKey);
        // insert tab with CTRL+TAB instead of TAB
        textPane.getInputMap(JComponent.WHEN_FOCUSED).put(ctrlTab, DefaultEditorKit.insertTabAction);
    }

    /**
     * Creates the toolbar with actions for editing text.
     */
    private void createToolbar() {
        toolbar = new JToolBar();
        toolbar.setFloatable(false);

        // Add edit actions
        for (Action action : actions) {
            toolbar.add(action);
        }
    }

    public String getText() {
        return textPane.getText();
    }

    public void setText(final String description) {
        textPane.setText(description);
    }

    public void setEditable(final boolean editable) {
        textPane.setEnabled(editable);
        textPane.setEditable(editable);
        toolbar.setEnabled(editable);

//        for (Action action : actions) {
//            action.setEnabled(editable);
//        }
        PASTE_ACTION.setEnabled(true);
    }

    public void setCollapseEditToolbar(final boolean collapseEditToolbar) {
        this.collapseEditToolbar = collapseEditToolbar;

        if (!collapseEditToolbar) {
            collapsiblePane.setCollapsed(false);
        }
    }

}
