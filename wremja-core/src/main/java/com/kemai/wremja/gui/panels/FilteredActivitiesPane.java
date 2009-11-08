package com.kemai.wremja.gui.panels;

import info.clearthought.layout.TableLayout;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.infonode.tabbedpanel.Tab;
import net.infonode.tabbedpanel.TabAdapter;
import net.infonode.tabbedpanel.TabDropDownListVisiblePolicy;
import net.infonode.tabbedpanel.TabEvent;
import net.infonode.tabbedpanel.TabLayoutPolicy;
import net.infonode.tabbedpanel.TabStateChangedEvent;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.theme.ShapedGradientTheme;
import net.infonode.tabbedpanel.theme.TabbedPanelTitledTabTheme;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.infonode.tabbedpanel.titledtab.TitledTabSizePolicy;

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
	private final IUserSettings settings;
	
	/**
	 * This object acts as a template for concrete {@link TitledTab}s.
	 * I.e. properties of this theme are inherited to tabs.
	 */
	private static final TabbedPanelTitledTabTheme theme = new ShapedGradientTheme();
	static {
		theme.getTitledTabProperties().setSizePolicy(TitledTabSizePolicy.EQUAL_SIZE);
		theme.getTitledTabProperties().getHighlightedProperties().setToolTipEnabled(true);
		theme.getTitledTabProperties().getNormalProperties().setToolTipEnabled(true);
	}
	
	private int[] tabOrder = { 0, 1, 2, 3, 4, 5, 6 };

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
				{ TableLayout.FILL } }; // Rows
		this.setLayout(new TableLayout(size));
		
		final TabbedPanel tabpane = new TabbedPanel();
		tabpane.getProperties().addSuperObject(theme.getTabbedPanelProperties());

		tabpane.getProperties().setTabLayoutPolicy(TabLayoutPolicy.SCROLLING);
		tabpane.getProperties().setTabDropDownListVisiblePolicy(TabDropDownListVisiblePolicy.TABS_NOT_VISIBLE);
		tabpane.getProperties().setTabReorderEnabled(true);
		
		tabpane.getProperties().getButtonProperties().getTabDropDownListButtonProperties().setToolTipText(
		        TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tabpane.Dropdownlist.Tooltip"));
		
		JPanel accummulatedActitvitiesPanel = new AccummulatedActitvitiesPanel(model.getFilteredReport());
		WTab accummulatedActitvitiesTab = new WTab(
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.AccumulatedActivities"),  //$NON-NLS-1$
				null,
				accummulatedActitvitiesPanel, 
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.AccumulatedActivities.Tooltip") //$NON-NLS-1$
		);

		JPanel filteredActitvitiesPanel = new AllActitvitiesPanel(model);
		WTab filteredActitvitiesTab = new WTab(
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.AllActivities"),  //$NON-NLS-1$
				null,
				filteredActitvitiesPanel, 
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.AllActivities.Tooltip") //$NON-NLS-1$
		);

		JPanel descriptionPanel = new DescriptionPanel(model);
		WTab descriptionTab = new WTab(
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.Descriptions"),  //$NON-NLS-1$
				null,
				descriptionPanel, 
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.Descriptions.Tooltip") //$NON-NLS-1$
		);
		
		JPanel hoursByWeekPanel = new HoursByWeekPanel(model.getHoursByWeekReport());
		WTab hoursByWeekTab = new WTab(
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByWeek"),  //$NON-NLS-1$
				null,
				hoursByWeekPanel, 
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByWeek.Tooltip") //$NON-NLS-1$
		);

		JPanel hoursByDayPanel = new HoursByDayPanel(model.getHoursByDayReport());
		WTab hoursByDayTab = new WTab(
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByDay"),  //$NON-NLS-1$
				null,
				hoursByDayPanel, 
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByDay.Tooltip") //$NON-NLS-1$
		);
		
		JPanel hoursByProjectPanel = new HoursByProjectPanel(model.getHoursByProjectReport());
		WTab hoursByProjectTab = new WTab(
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByProject"),  //$NON-NLS-1$
				null,
				hoursByProjectPanel, 
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByProject.Tooltip") //$NON-NLS-1$
		);

		JPanel hoursByProjectChartPanel = new HoursByProjectChartPanel(model.getHoursByProjectReport());
		WTab hoursByProjectChartTab = new WTab(
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByProjectChart"),  //$NON-NLS-1$
				null,
				hoursByProjectChartPanel, 
				TEXT_BUNDLE.textFor("FilteredActivitiesPane.Tab.HoursByProjectChart.Tooltip") //$NON-NLS-1$
		);

        final Tab[] tabs = new Tab[] {
                makeInfoNodeTab(accummulatedActitvitiesTab),
                makeInfoNodeTab(filteredActitvitiesTab),
                makeInfoNodeTab(descriptionTab),
                makeInfoNodeTab(hoursByWeekTab),
                makeInfoNodeTab(hoursByDayTab),
                makeInfoNodeTab(hoursByProjectTab),
                makeInfoNodeTab(hoursByProjectChartTab)
        };
        
        int[] order = this.settings.getMainTabpaneOrder();
        if (order.length == this.tabOrder.length) {
            this.tabOrder = order;
        } else {
            // ignore; use default order
        }
        
        for (int o : this.tabOrder) {
            tabpane.addTab(tabs[o]);
        }
        
        tabpane.addTabListener(new TabAdapter() {
            @Override
            public void tabSelected(TabStateChangedEvent event) {
                tabpane.scrollTabToVisibleArea(event.getTab());
            }

            @Override
            public void tabMoved(TabEvent event) {
            	// remember tab order in settings
                for (int i=0; i < tabs.length; i++) {
                    int index = tabpane.getTabIndex(tabs[i]);
                    tabOrder[index] = i;
                }
                settings.setMainTabpaneOrder(tabOrder);
            }
        });

		this.add(tabpane, "0, 0"); //$NON-NLS-1$
		tabpane.setVisible(true);
	}

	private Tab makeInfoNodeTab(WTab tab) {
		TitledTab t = new TitledTab(tab.getTitle(), tab.getIcon(), tab.getComponent(), null);
		t.getProperties().addSuperObject(theme.getTitledTabProperties());
		t.getProperties().getNormalProperties().setToolTipText(tab.getTooltip());
		t.getProperties().getHighlightedProperties().setToolTipText(tab.getTooltip());
		return t;
	}
	
	/**
	 * Convenience class for working with tabs.
	 */
	private static class WTab {

		/** The title of the tab. */
		private final String title;

		/** The icon of the tab. */
		private Icon icon;

		/** The tooltip of the tab. */
		private final String tooltip;

		/** The component displayed in the tab. */
		private final JComponent component;

		private WTab(String title, Icon icon, JComponent component, String tooltip) {
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
