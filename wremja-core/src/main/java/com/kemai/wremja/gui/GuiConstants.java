package com.kemai.wremja.gui;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.kemai.wremja.logging.Logger;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * Misc GUI constants for the application.
 */
public abstract class GuiConstants {
    
    private static final Logger log = Logger.getLogger(GuiConstants.class);
    
    /** Hide constructor in utility class. */
    private GuiConstants() { }

    public static final Color BEIGE = new Color(245, 245, 220);

    public static final Color VERY_LIGHT_GREY = new Color(240, 240, 240);

    public static final Color DARK_BLUE = new Color(64, 64, 128);
    
    /** The standard icon image. */
    public static final Image NORMAL_ICON = new ImageIcon(GuiConstants.class.getResource("/icons/Baralga-Tray.gif")).getImage(); //$NON-NLS-1$

    /** The icon image when an activity is running. */
    public static final Image ACTIVE_ICON = new ImageIcon(GuiConstants.class.getResource("/icons/Baralga-Tray-Green.png")).getImage(); //$NON-NLS-1$

    @SuppressWarnings(value="MS_MUTABLE_ARRAY", justification="We trust all callers")
    public static final Highlighter[] HIGHLIGHTERS = new Highlighter[] { 
        HighlighterFactory.createSimpleStriping(BEIGE),
        new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.DARK_GRAY, Color.WHITE)
    };
    
    public static final String WREMJA_VERSION;
    public static final String WREMJA_REVISION;
    
    static {
        String version = "?";
        String revision = "?";
        try {
            InputStream in = GuiConstants.class.getResourceAsStream("/com/kemai/wremja/wremja.properties");
            if(in != null) {
                Properties props = new Properties();
                props.load(in);
                version = props.getProperty("wremja.version", "?");
                revision = props.getProperty("wremja.buildnumber", "?");
            }
        } catch (IOException e) {
            log.warn( "Couldn't access wremja.properties", e );
        }
        WREMJA_VERSION = version;
        WREMJA_REVISION = revision;
    }

}
