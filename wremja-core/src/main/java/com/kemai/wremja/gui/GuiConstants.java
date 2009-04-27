package com.kemai.wremja.gui;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * Misc GUI constants for the application.
 */
public abstract class GuiConstants {
    
    /** Hide constructor in utility class. */
    private GuiConstants() { }

    public static final Color BEIGE = new Color(245, 245, 220);

    public static final Color VERY_LIGHT_GREY = new Color(240, 240, 240);

    public static final Color DARK_BLUE = new Color(64, 64, 128);
    
    /** The standard icon image. */
    public static final Image NORMAL_ICON = new ImageIcon(Launcher.class.getResource("/icons/Baralga-Tray.gif")).getImage(); //$NON-NLS-1$

    /** The icon image when an activity is running. */
    public static final Image ACTIVE_ICON = new ImageIcon(Launcher.class.getResource("/icons/Baralga-Tray-Green.png")).getImage(); //$NON-NLS-1$

    @SuppressWarnings(value="MS_MUTABLE_ARRAY", justification="We trust all callers")
    public static final Highlighter[] HIGHLIGHTERS = new Highlighter[] { 
        HighlighterFactory.createSimpleStriping(BEIGE),
        new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.DARK_GRAY, Color.WHITE)
    };

}
