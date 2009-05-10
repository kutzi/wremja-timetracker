package com.kemai.wremja.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.util.OS;

import com.kemai.util.UiUtilities;
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
    public static final Image NORMAL_ICON;

    /** The icon image when an activity is running. */
    public static final Image ACTIVE_ICON;
    
    
    static {
        BufferedImage normalIcon = null;
        BufferedImage activeIcon = null;
        if (SystemTray.isSupported()) {
            try {
                normalIcon = ImageIO.read(GuiConstants.class.getResource("/icons/Baralga-Tray.gif")); //$NON-NLS-1$
                activeIcon = ImageIO.read(GuiConstants.class.getResource("/icons/Baralga-Tray-Green.png")); //$NON-NLS-1$
                
                if( OS.isWindows() ) {
                    // no changes. For some reason the unscaled 32x32 icons look best on Windows
                    // though the tray size is 16
                } else {
                    Dimension d = SystemTray.getSystemTray().getTrayIconSize();
                
                    normalIcon = UiUtilities.scaleIconToBufferedImage(normalIcon, d.width - 1 , d.height - 1,
                        BufferedImage.TYPE_INT_ARGB);
                    activeIcon = UiUtilities.scaleIconToBufferedImage(activeIcon, d.width - 1 , d.height - 1,
                        BufferedImage.TYPE_INT_ARGB);
                }
            } catch (IOException e) {
                log.error(e, e);
            }
        }
        NORMAL_ICON = normalIcon;
        ACTIVE_ICON = activeIcon;
    }

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
