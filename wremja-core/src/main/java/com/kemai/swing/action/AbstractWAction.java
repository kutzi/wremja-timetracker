package com.kemai.swing.action;

import javax.swing.AbstractAction;

public abstract class AbstractWAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private boolean mnemonicSet = false;

	public AbstractWAction() {
		super();
	}
	
    public AbstractWAction(String name, boolean mnemonicAutodetect) {
    	this();
    	if(mnemonicAutodetect) {
    		setName(name);
    	} else {
    		super.putValue(NAME, name);
    		this.mnemonicSet = true; // prevent autodetection
    	}
	}

	@Override
    public void putValue(String key, Object newValue) {
        super.putValue(key, newValue);
        
        if( NAME.equals(key) ) {
        	//System.err.println();
            // set up mnemonic key if there is no other, yet
            if( getValue(MNEMONIC_KEY) == null ) {
                final String name = (String) getValue(NAME);
                try {
                    putValue(MNEMONIC_KEY, Integer.valueOf(name.codePointAt(0)) );
                } catch (StringIndexOutOfBoundsException e) {
                    // Ignore
                }
            }
        }
    }
    
    protected void setName(String name) {
    	int index = name.indexOf('_');
    	if(index == -1) {
    		super.putValue(NAME, name);
    	} else {
    		name = name.substring(0, index) + name.substring(index+1, name.length());
    		super.putValue(NAME, name);
    		if( getValue(MNEMONIC_KEY) == null ) {
    			super.putValue(MNEMONIC_KEY, Integer.valueOf(name.codePointAt(index)));
    			super.putValue(DISPLAYED_MNEMONIC_INDEX_KEY, Integer.valueOf(index));
    		}
    		mnemonicSet = true;
    	}
    }
    
    public void setTooltip(final String tooltip) {
        putValue(SHORT_DESCRIPTION, tooltip);
    }
    
    public boolean hasMnemonicSet() {
    	return mnemonicSet;
    }
    
    public Integer getMnemonic() {
    	Object o = getValue(MNEMONIC_KEY);
    	if(o instanceof Integer) {
    		return (Integer)o;
    	} else {
    		return Integer.valueOf('\0');
    	}
    }
    
    public Integer getDisplayedMnemonicIndex() {
    	Object o = getValue(DISPLAYED_MNEMONIC_INDEX_KEY);
    	if(o instanceof Integer) {
    		return (Integer)o;
    	} else {
    		return Integer.valueOf(-1);
    	}
    }
}
