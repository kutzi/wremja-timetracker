package com.kemai.swing.util;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.swing.DefaultEventTableModel;

/**
 * This class extends SwingX' {@link JXTable} and
 * fixes a compatibility problem between {@link JXTable} (SwingX 1.6)
 * and Glazed Lists models (1.8.0).
 *
 * @author kutzi
 * @author $Author$
 */
public class WTable extends JXTable {

    private static final long serialVersionUID = 1L;

    public WTable(DefaultEventTableModel<?> glazedListsModel) {
        super(glazedListsModel);
        setAutoCreateRowSorter(false);
        setRowSorter(null);
    }
}
