package com.kemai.wremja.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXPanel;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.panels.report.AccummulatedActitvitiesPanel;
import com.kemai.wremja.gui.panels.report.AllActitvitiesPanel;
import com.kemai.wremja.gui.panels.report.DescriptionPanel;
import com.kemai.wremja.gui.panels.report.HoursByDayPanel;
import com.kemai.wremja.gui.panels.report.HoursByProjectChartPanel;
import com.kemai.wremja.gui.panels.report.HoursByProjectPanel;
import com.kemai.wremja.gui.panels.report.HoursByWeekPanel;
import com.kemai.wremja.gui.settings.IUserSettings;

/**
 * The panel containing the "category" buttons and the corresponding tabs.
 *
 * @author remast
 * @author kutzi
 */
@SuppressWarnings("serial")
public class FilteredActivitiesPane extends JXPanel {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(GuiConstants.class);

	/** The model. */
	private final PresentationModel model;
	
    private final IUserSettings settings;

	/** The category that's shown right now. */
	private String shownCategory;

	/** The currently displayed tabpane. */
	private JTabbedPane currentPane;
	
	/** Mapping from category to tabpane. */
	private final Map<String, JTabbedPane> category2Tabpane = new HashMap<String, JTabbedPane>();

	//------------------------------------------------
	// Tabs with their panels
	//------------------------------------------------

	private AccummulatedActitvitiesPanel accummulatedActitvitiesPanel;
	private CategorizedTab accummulatedActitvitiesTab;

	private HoursByWeekPanel hoursByWeekPanel;
	private CategorizedTab hoursByWeekTab;

	private HoursByDayPanel hoursByDayPanel;
	private CategorizedTab hoursByDayTab;

	private HoursByProjectPanel hoursByProjectPanel;
	private CategorizedTab hoursByProjectTab;

	private HoursByProjectChartPanel hoursByProjectChartPanel;
	private CategorizedTab hoursByProjectChartTab;

	private AllActitvitiesPanel filteredActitvitiesPanel;
	private CategorizedTab filteredActitvitiesTab;

	private DescriptionPanel descriptionPanel;
	private CategorizedTab descriptionTab;

	//------------------------------------------------
	// Toggle buttons for tab categories
	//------------------------------------------------

	private final JXPanel categoryButtonPanel = new JXPanel();

	private final JToggleButton generalButton = new JToggleButton(new AbstractAction(textBundle.textFor("Category.General"), new ImageIcon(getClass().getResource("/icons/gtk-dnd-multiple.png"))) {

		@Override
		public void actionPerformed(final ActionEvent e) {
			FilteredActivitiesPane.this.toggleCategory("General"); //$NON-NLS-1$
		}

	});
	{
		generalButton.setToolTipText(textBundle.textFor("Category.General.ToolTipText"));
	}

	private final JToggleButton timeButton = new JToggleButton(new AbstractAction(textBundle.textFor("Category.Time"), new ImageIcon(getClass().getResource("/icons/stock_calendar-view-day.png"))) {

		@Override
		public void actionPerformed(final ActionEvent e) {
			FilteredActivitiesPane.this.toggleCategory("Time"); //$NON-NLS-1$
		}

	});
	{
		timeButton.setToolTipText(textBundle.textFor("Category.Time.ToolTipText"));
	}

	private final JToggleButton projectButton = new JToggleButton(new AbstractAction(textBundle.textFor("Category.Project"), new ImageIcon(getClass().getResource("/icons/stock_calendar-view-day.png"))) {

		@Override
		public void actionPerformed(final ActionEvent e) {
			FilteredActivitiesPane.this.toggleCategory("Project"); //$NON-NLS-1$
		}

	});
	{
		projectButton.setToolTipText(textBundle.textFor("Category.Project.ToolTipText"));
	}

	public FilteredActivitiesPane(final PresentationModel model, IUserSettings settings) {
		super();
		this.model = model;
        this.settings = settings;

		initialize();
	}

	/**
	 * Set up GUI components.
	 */
	private void initialize() {
		double size[][] = {
				{ TableLayout.FILL}, // Columns
				{ TableLayout.PREFERRED, TableLayout.FILL } }; // Rows
		this.setLayout(new TableLayout(size));

		int border = 5;
		categoryButtonPanel.setLayout(new TableLayout(new double [][] {{0, TableLayout.FILL, border, TableLayout.FILL, border, TableLayout.FILL},{border, TableLayout.PREFERRED, border-3}}));
		categoryButtonPanel.add(generalButton, "1, 1"); //$NON-NLS-1$
		categoryButtonPanel.add(timeButton, "3, 1"); //$NON-NLS-1$
		categoryButtonPanel.add(projectButton, "5, 1"); //$NON-NLS-1$
		this.add(categoryButtonPanel, "0, 0"); //$NON-NLS-1$

		//tabs.setTabShape(JideTabbedPane.SHAPE_WINDOWS);
		//tabs.setTabColorProvider(JideTabbedPane.ONENOTE_COLOR_PROVIDER);

		shownCategory = this.settings.getShownCategory();

		accummulatedActitvitiesTab = new CategorizedTab(); 
		accummulatedActitvitiesPanel = new AccummulatedActitvitiesPanel(model.getFilteredReport());
		accummulatedActitvitiesTab.setComponent(
				textBundle.textFor("FilteredActivitiesPane.Tab.AccumulatedActivities"),  //$NON-NLS-1$
				null,
//				new ImageIcon(getClass().getResource("/icons/gnome-calculator.png")),  //$NON-NLS-1$
				accummulatedActitvitiesPanel, 
				textBundle.textFor("FilteredActivitiesPane.Tab.AccumulatedActivities.Tooltip") //$NON-NLS-1$
		);

		filteredActitvitiesTab = new CategorizedTab();
		filteredActitvitiesPanel = new AllActitvitiesPanel(model);
		filteredActitvitiesTab.setComponent(
				textBundle.textFor("FilteredActivitiesPane.Tab.AllActivities"),  //$NON-NLS-1$
				null,
//				new ImageIcon(getClass().getResource("/icons/gtk-dnd-multiple.png")),  //$NON-NLS-1$
				filteredActitvitiesPanel, 
				textBundle.textFor("FilteredActivitiesPane.Tab.AllActivities.Tooltip") //$NON-NLS-1$
		);

		descriptionTab = new CategorizedTab();
		descriptionPanel = new DescriptionPanel(model);
		descriptionTab.setComponent(
				textBundle.textFor("FilteredActivitiesPane.Tab.Descriptions"),  //$NON-NLS-1$
				null,
//				new ImageIcon(getClass().getResource("/icons/gnome-mime-text-x-readme.png")), 
				descriptionPanel, 
				textBundle.textFor("FilteredActivitiesPane.Tab.Descriptions.Tooltip") //$NON-NLS-1$
		);
		
		JTabbedPane generalTabpane = new JTabbedPane();
		addCategorizedTab(generalTabpane, accummulatedActitvitiesTab);
		addCategorizedTab(generalTabpane, filteredActitvitiesTab);
		addCategorizedTab(generalTabpane, descriptionTab);
		this.category2Tabpane.put("General", generalTabpane);

		hoursByWeekTab = new CategorizedTab();
		hoursByWeekPanel = new HoursByWeekPanel(model.getHoursByWeekReport());
		hoursByWeekTab.setComponent(
				textBundle.textFor("FilteredActivitiesPane.Tab.HoursByWeek"),  //$NON-NLS-1$
				null,
//				new ImageIcon(getClass().getResource("/icons/stock_calendar-view-work-week.png")),  //$NON-NLS-1$
				hoursByWeekPanel, 
				textBundle.textFor("FilteredActivitiesPane.Tab.HoursByWeek.Tooltip") //$NON-NLS-1$
		);

		hoursByDayTab = new CategorizedTab();
		hoursByDayPanel = new HoursByDayPanel(model.getHoursByDayReport());
		hoursByDayTab.setComponent(
				textBundle.textFor("FilteredActivitiesPane.Tab.HoursByDay"),  //$NON-NLS-1$
				null,
//				new ImageIcon(getClass().getResource("/icons/stock_calendar-view-day.png")),  //$NON-NLS-1$
				hoursByDayPanel, 
				textBundle.textFor("FilteredActivitiesPane.Tab.HoursByDay.Tooltip") //$NON-NLS-1$
		);
		
		JTabbedPane timeTabpane = new JTabbedPane();
		addCategorizedTab(timeTabpane, hoursByWeekTab);
		addCategorizedTab(timeTabpane, hoursByDayTab);
		this.category2Tabpane.put("Time", timeTabpane);

		hoursByProjectTab = new CategorizedTab();
		hoursByProjectPanel = new HoursByProjectPanel(model.getHoursByProjectReport());
		hoursByProjectTab.setComponent(
				textBundle.textFor("FilteredActivitiesPane.Tab.HoursByProject"),  //$NON-NLS-1$
				null,
//				new ImageIcon(getClass().getResource("/icons/stock_calendar-view-day.png")),  //$NON-NLS-1$
				hoursByProjectPanel, 
				textBundle.textFor("FilteredActivitiesPane.Tab.HoursByProject.Tooltip") //$NON-NLS-1$
		);

		hoursByProjectChartTab = new CategorizedTab();
		hoursByProjectChartPanel = new HoursByProjectChartPanel(model.getHoursByProjectReport());
		hoursByProjectChartTab.setComponent(
				textBundle.textFor("FilteredActivitiesPane.Tab.HoursByProjectChart"),  //$NON-NLS-1$
				null,
//				new ImageIcon(getClass().getResource("/icons/stock_calendar-view-day.png")),  //$NON-NLS-1$
				hoursByProjectChartPanel, 
				textBundle.textFor("FilteredActivitiesPane.Tab.HoursByProjectChart.Tooltip") //$NON-NLS-1$
		);

        JTabbedPane projectTabpane = new JTabbedPane();
        addCategorizedTab(projectTabpane, hoursByProjectTab);
        addCategorizedTab(projectTabpane, hoursByProjectChartTab);
        this.category2Tabpane.put("Project", projectTabpane);
		
		this.initToggleButtons();

		this.currentPane = this.category2Tabpane.get(this.shownCategory);
		
		this.add(currentPane, "0, 1"); //$NON-NLS-1$
		currentPane.setVisible(true);
	}

	/**
	 * Initializes the toggle buttons for the categories from the settings.
	 */
	private void initToggleButtons() {
		// 1. Deselect all buttons
		generalButton.setSelected(false);
		timeButton.setSelected(false);
		projectButton.setSelected(false);

		// 2. Select shown button
		if (StringUtils.equals("General", shownCategory)) { //$NON-NLS-1$
			generalButton.setSelected(true);
		} else if (StringUtils.equals("Time", shownCategory)) { //$NON-NLS-1$
			timeButton.setSelected(true);
		} else if (StringUtils.equals("Project", shownCategory)) { //$NON-NLS-1$
			projectButton.setSelected(true);
		}
	}

	/**
	 * Processes the action that the user toggles a category button.
	 * @param newCategory the toggled category
	 * @param toggledCategoryButton the toggled button
	 */
	private void toggleCategory(final String newCategory) {
	    if(StringUtils.equals(this.shownCategory, newCategory)) {
	        initToggleButtons();
	        return;
	    }

		// 1. Store category
		//  a) internally
		shownCategory = newCategory;

		//  b) in user settings
		this.settings.setShownCategory(newCategory);

		// 2. Set tab visibility
		JTabbedPane newPane = this.category2Tabpane.get(this.shownCategory);
	    this.currentPane.setVisible(false);
	    remove(this.currentPane);
	    this.currentPane = newPane;
	    add(currentPane, "0, 1");
	    this.currentPane.setVisible(true);
		
		// 3.  Deselect all categoryToggleButtons except the one toggled
		initToggleButtons();
	}

	/**
	 * Add a categorized tab to the tabs.
	 * @param tab the tab to add
	 */
	private void addCategorizedTab(JTabbedPane pane, CategorizedTab tab) {
		if (tab == null) {
			return;
		}

		pane.addTab(tab.getTitle(), tab.getIcon(), tab.getComponent(), tab.getTooltip());
	}

	/**
	 * A tab belonging to a category.
	 */
	private static class CategorizedTab {

		/** The title of the tab. */
		private String title;

		/** The icon of the tab. */
		private Icon icon;

		/** The tooltip of the tab. */
		private String tooltip;

		/** The component displayed in the tab. */
		private Component component;

		private CategorizedTab() {
		}

		private void setComponent(final String title, final Icon icon, final Component component, final String tooltip) {
			this.title = title;
			this.icon = icon;
			this.component = component;
			this.tooltip = tooltip;
		}

		 /**
		  * @return the title
		  */
		 private String getTitle() {
			 return title;
		 }

		 /**
		  * @return the icon
		  */
		 private Icon getIcon() {
			 return icon;
		 }

		 /**
		  * @return the component
		  */
		 private Component getComponent() {
			 return component;
		 }

		 /**
		  * @return the tip
		  */
		 private String getTooltip() {
			 return tooltip;
		 }
	}

}
