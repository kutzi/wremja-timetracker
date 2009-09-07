package com.kemai.wremja.gui.panels;

import info.clearthought.layout.TableLayout;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.infonode.tabbedpanel.TabDropDownListVisiblePolicy;
import net.infonode.tabbedpanel.TabLayoutPolicy;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.theme.ShapedGradientTheme;
import net.infonode.tabbedpanel.theme.TabbedPanelTitledTabTheme;
import net.infonode.tabbedpanel.titledtab.TitledTab;

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
public class FilteredActivitiesPane extends JXPanel {

    private static final long serialVersionUID = 1L;

	/** The bundle for internationalized texts. */
    private static final TextResourceBundle TEXT_BUNDLE = TextResourceBundle.getBundle(GuiConstants.class);

	/** The model. */
	private final PresentationModel model;
	
	private final TabbedPanelTitledTabTheme theme = new ShapedGradientTheme();
	
	public FilteredActivitiesPane(final PresentationModel model, IUserSettings settings) {
		super();
		this.model = model;

		initialize();
	}

	/**
	 * Set up GUI components.
	 */
	private void initialize() {
		double size[][] = {
				{ TableLayout.FILL}, // Columns
				{ TableLayout.FILL } }; // Rows
		this.setLayout(new TableLayout(size));

		JPanel accummulatedActitvitiesPanel = new AccummulatedActitvitiesPanel(model.getFilteredReport());
		Tab accummulatedActitvitiesTab = new Tab(
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.AccumulatedActivities"),  //$NON-NLS-1$
				null,
				accummulatedActitvitiesPanel, 
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.AccumulatedActivities.Tooltip") //$NON-NLS-1$
		);

		JPanel filteredActitvitiesPanel = new AllActitvitiesPanel(model);
		Tab filteredActitvitiesTab = new Tab(
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.AllActivities"),  //$NON-NLS-1$
				null,
				filteredActitvitiesPanel, 
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.AllActivities.Tooltip") //$NON-NLS-1$
		);

		JPanel descriptionPanel = new DescriptionPanel(model);
		Tab descriptionTab = new Tab(
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.Descriptions"),  //$NON-NLS-1$
				null,
				descriptionPanel, 
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.Descriptions.Tooltip") //$NON-NLS-1$
		);
		
		//JTabbedPane generalTabpane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		TabbedPanel generalTabpane = new TabbedPanel();
		generalTabpane.getProperties().setTabLayoutPolicy(TabLayoutPolicy.SCROLLING);
		generalTabpane.getProperties().setTabDropDownListVisiblePolicy(TabDropDownListVisiblePolicy.TABS_NOT_VISIBLE);
		generalTabpane.getProperties().setTabReorderEnabled(true);
		
		generalTabpane.getProperties().addSuperObject(theme.getTabbedPanelProperties());
		
		addTabToPane(generalTabpane, accummulatedActitvitiesTab);
		addTabToPane(generalTabpane, filteredActitvitiesTab);
		addTabToPane(generalTabpane, descriptionTab);

		JPanel hoursByWeekPanel = new HoursByWeekPanel(model.getHoursByWeekReport());
		Tab hoursByWeekTab = new Tab(
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByWeek"),  //$NON-NLS-1$
				null,
				hoursByWeekPanel, 
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByWeek.Tooltip") //$NON-NLS-1$
		);

		JPanel hoursByDayPanel = new HoursByDayPanel(model.getHoursByDayReport());
		Tab hoursByDayTab = new Tab(
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByDay"),  //$NON-NLS-1$
				null,
				hoursByDayPanel, 
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByDay.Tooltip") //$NON-NLS-1$
		);
		
		addTabToPane(generalTabpane, hoursByWeekTab);
		addTabToPane(generalTabpane, hoursByDayTab);
		//this.category2Tabpane.put("Time", timeTabpane);

		JPanel hoursByProjectPanel = new HoursByProjectPanel(model.getHoursByProjectReport());
		Tab hoursByProjectTab = new Tab(
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByProject"),  //$NON-NLS-1$
				null,
				hoursByProjectPanel, 
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByProject.Tooltip") //$NON-NLS-1$
		);

		JPanel hoursByProjectChartPanel = new HoursByProjectChartPanel(model.getHoursByProjectReport());
		Tab hoursByProjectChartTab = new Tab(
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByProjectChart"),  //$NON-NLS-1$
				null,
				hoursByProjectChartPanel, 
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByProjectChart.Tooltip") //$NON-NLS-1$
		);

        //JTabbedPane projectTabpane = new JTabbedPane();
        addTabToPane(generalTabpane, hoursByProjectTab);
        addTabToPane(generalTabpane, hoursByProjectChartTab);
        //this.category2Tabpane.put("Project", projectTabpane);
		
		//this.initToggleButtons();

		this.add(generalTabpane, "0, 0"); //$NON-NLS-1$
		generalTabpane.setVisible(true);
	}

	/**
	 * Initializes the toggle buttons for the categories from the settings.
	 */
//	private void initToggleButtons() {
//		// 1. Deselect all buttons
//		generalButton.setSelected(false);
//		timeButton.setSelected(false);
//		projectButton.setSelected(false);
//
//		// 2. Select shown button
//		if (StringUtils.equals("General", shownCategory)) { //$NON-NLS-1$
//			generalButton.setSelected(true);
//		} else if (StringUtils.equals("Time", shownCategory)) { //$NON-NLS-1$
//			timeButton.setSelected(true);
//		} else if (StringUtils.equals("Project", shownCategory)) { //$NON-NLS-1$
//			projectButton.setSelected(true);
//		}
//	}

	/**
	 * Processes the action that the user toggles a category button.
	 * @param newCategory the toggled category
	 * @param toggledCategoryButton the toggled button
	 */
//	private void toggleCategory(final String newCategory) {
//	    if(StringUtils.equals(this.shownCategory, newCategory)) {
//	        //initToggleButtons();
//	        return;
//	    }
//
//		// 1. Store category
//		//  a) internally
//		shownCategory = newCategory;
//
//		//  b) in user settings
//		this.settings.setShownCategory(newCategory);
//
//		// 2. Set tab visibility
//		JTabbedPane newPane = this.category2Tabpane.get(this.shownCategory);
//	    this.currentPane.setVisible(false);
//	    remove(this.currentPane);
//	    this.currentPane = newPane;
//	    add(currentPane, "0, 1");
//	    this.currentPane.setVisible(true);
//		
//		// 3.  Deselect all categoryToggleButtons except the one toggled
//		//initToggleButtons();
//	}

	private void addTabToPane(TabbedPanel pane, Tab tab) {
		if (tab == null) {
			return;
		}

		//pane.addTab(tab.getTitle(), tab.getIcon(), tab.getComponent(), tab.getTooltip());
		pane.addTab(makeInfoNodeTab(tab));
	}

	private net.infonode.tabbedpanel.Tab makeInfoNodeTab(Tab tab) {
		TitledTab t = new TitledTab(tab.getTitle(), tab.getIcon(), tab.getComponent(), null);
		t.getProperties().getNormalProperties().setToolTipEnabled(true);
		t.getProperties().getNormalProperties().setToolTipText(tab.getTooltip());
		t.getProperties().getHighlightedProperties().setToolTipEnabled(true);
		t.getProperties().getHighlightedProperties().setToolTipText(tab.getTooltip());
		t.getProperties().addSuperObject(theme.getTitledTabProperties());
		return t;
	}
	
	/**
	 * Convenience class for working with tabs.
	 */
	private static class Tab {

		/** The title of the tab. */
		private final String title;

		/** The icon of the tab. */
		private Icon icon;

		/** The tooltip of the tab. */
		private final String tooltip;

		/** The component displayed in the tab. */
		private final JComponent component;

		private Tab(String title, Icon icon, JComponent component, String tooltip) {
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
		 private JComponent getComponent() {
			 return component;
		 }

		 /**
		  * @return the tooltip
		  */
		 private String getTooltip() {
			 return tooltip;
		 }
	}

}
