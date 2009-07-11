package com.kemai.wremja.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.swing.EventTableModel;

import com.kemai.swing.dialog.EscapeDialog;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.dialogs.table.ProjectListTableFormat;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.Project;

/**
 * The dialog to manage the available projects.
 * @author remast
 * @author kutzi
 */
@SuppressWarnings("serial")
public class ManageProjectsDialog extends EscapeDialog implements Observer {

	private static final Logger log = Logger.getLogger(ManageProjectsDialog.class);
	
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ManageProjectsDialog.class);

    private JPanel jContentPane = null;

    private JXTable projectList = null;

    private JTextField newProjectTextField = null;

    private JPanel projectsPanel = null;

    private JButton addProjectButton = null;

    private JButton removeProjectButton = null;

    private JPanel newProjectNamePanel = null;

    private JLabel lableProjectTitle = null;

    private final PresentationModel model;

    private EventTableModel<Project> projectListTableModel;

    /**
     * @param owner
     */
    public ManageProjectsDialog(final Frame owner, final PresentationModel model) {
        super(owner);

        this.model = model;
        this.model.addObserver(this);

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        setLocationRelativeTo(getOwner());
        this.setIconImage(new ImageIcon(getClass().getResource("/icons/gtk-edit.png")).getImage()); //$NON-NLS-1$

        this.setModal(true);
        this.setTitle(textBundle.textFor("ManageProjectsDialog.Title")); //$NON-NLS-1$
        this.setContentPane(getJContentPane());

        // Set default Button to AddProjectsButton.
        this.getRootPane().setDefaultButton(addProjectButton);
    }

    /**
     * This method initializes jContentPane.
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getNewProjectNamePanel(), BorderLayout.NORTH);
            jContentPane.add(getProjectsPanel(), BorderLayout.EAST);

            JScrollPane projectListScrollPane = new JScrollPane(getProjectList());
            jContentPane.add(projectListScrollPane, BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * This method initializes projectList.
     * @return javax.swing.JList	
     */
    private JXTable getProjectList() {
        if (projectList == null) {
            projectList = new JXTable();
            projectList.setSortable(false);
            projectList.getTableHeader().setVisible(false);

            projectListTableModel = new EventTableModel<Project>(model.getProjectList(), new ProjectListTableFormat(model));
            projectList.setModel(projectListTableModel);
            projectList.setToolTipText(textBundle.textFor("ManageProjectsDialog.ProjectList.ToolTipText")); //$NON-NLS-1$
        }
        return projectList;
    }

    /**
     * This method initializes newProjectTextField.
     * @return javax.swing.JTextField	
     */
    // TODO: disallow leading/trailing whitespace (or simply trim it)?
    private JTextField getNewProjectTextField() {
        if (newProjectTextField == null) {
            newProjectTextField = new JTextField();
            newProjectTextField.setName(textBundle.textFor("ManageProjectsDialog.4")); //$NON-NLS-1$
            newProjectTextField.setText(textBundle.textFor("ManageProjectsDialog.NewProjectTitle.DefaultNewProjectName")); //$NON-NLS-1$
            newProjectTextField.setToolTipText(textBundle.textFor("ManageProjectsDialog.NewProjectTitle.ToolTipText")); //$NON-NLS-1$
            newProjectTextField.setPreferredSize(new Dimension(224, 19));
            
            newProjectTextField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					checkValidProjectName( e.getDocument() );
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					checkValidProjectName( e.getDocument() );
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					checkValidProjectName( e.getDocument() );
				}
				
				/**
				 * Checks that the document contains a valid project name and
				 * subsequently enables/disables the add button.
				 * 
				 * Currently just checks that the name is not blank
				 */
				private void checkValidProjectName(Document document) {
					try {
						String name = document.getText(0, document.getLength());
						if( Project.validateProjectName(name)) {
							addProjectButton.setEnabled(true);
						} else {
							addProjectButton.setEnabled(false);
						}
					} catch (BadLocationException e) {
						log.error(e, e);
					}
				}
            });
        }
        return newProjectTextField;
    }

    /**
     * This method initializes projectsPanel.
     * @return javax.swing.JPanel	
     */
    private JPanel getProjectsPanel() {
        if (projectsPanel == null) {
            final GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(2);
            projectsPanel = new JPanel();
            projectsPanel.setLayout(gridLayout);
            projectsPanel.add(getAddProjectButton(), null);
            projectsPanel.add(getRemoveProjectButton(), null);
        }

        return projectsPanel;
    }

    /**
     * This method initializes addProjectButton	.
     * @return javax.swing.JButton	
     */
    private JButton getAddProjectButton() {
        if (addProjectButton == null) {
            addProjectButton = new JButton(new ImageIcon(getClass().getResource("/icons/gtk-add.png")));
            addProjectButton.setText(textBundle.textFor("ManageProjectsDialog.AddProjectButton.Title")); //$NON-NLS-1$
            addProjectButton.setToolTipText(textBundle.textFor("ManageProjectsDialog.AddProjectButton.ToolTipText")); //$NON-NLS-1$
            addProjectButton.addActionListener(new ActionListener() {   
                public void actionPerformed(final ActionEvent e) {
                    String projectName = getNewProjectTextField().getText().trim();
                    model.addProject(new Project(model.nextProjectId(), projectName, projectName), ManageProjectsDialog.this);
                    getNewProjectTextField().setText(""); //$NON-NLS-1$
                }

            });
            
            // initially disable the button until some meaningful name has been entered
            addProjectButton.setEnabled(false);
            addProjectButton.setDefaultCapable(true);
        }
        return addProjectButton;
    }

    /**
     * This method initializes removeProjectButton.
     * @return javax.swing.JButton	
     */
    private JButton getRemoveProjectButton() {
        if (removeProjectButton == null) {
            removeProjectButton = new JButton(new ImageIcon(getClass().getResource("/icons/gtk-stop.png")));
            removeProjectButton.setText(textBundle.textFor("ManageProjectsDialog.RemoveProjectButton.Title")); //$NON-NLS-1$
            removeProjectButton.setToolTipText(textBundle.textFor("ManageProjectsDialog.RemoveProjectButton.ToolTipText")); //$NON-NLS-1$
            removeProjectButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    for (int index : getProjectList().getSelectedRows()) {
                        model.removeProject(
                                model.getProjectList().get(index), 
                                ManageProjectsDialog.this
                        );
                    }
                }
            });
        }
        return removeProjectButton;
    }

    /**
     * This method initializes the Panel with the name of the new 
     * project.
     * @return javax.swing.JPanel	
     */
    private JPanel getNewProjectNamePanel() {
        if (newProjectNamePanel == null) {
            final FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            flowLayout.setVgap(3);
            flowLayout.setHgap(3);
            lableProjectTitle = new JLabel();
            lableProjectTitle.setText(textBundle.textFor("ManageProjectsDialog.ProjectSelector.Title")); //$NON-NLS-1$
            lableProjectTitle.setBackground(Color.lightGray);
            newProjectNamePanel = new JPanel();
            newProjectNamePanel.setLayout(flowLayout);
            newProjectNamePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.gray));
            newProjectNamePanel.add(lableProjectTitle, null);
            newProjectNamePanel.add(getNewProjectTextField(), null);
        }
        return newProjectNamePanel;
    }

    @Override
    public void update(Observable source, Object eventObject) {
        if (source == null || !(eventObject instanceof WremjaEvent)) {
            return;
        }

        final WremjaEvent event = (WremjaEvent) eventObject;

        switch (event.getType()) {
        case PROJECT_CHANGED:
            projectListTableModel.fireTableDataChanged();
            break;
        }
    }
}
