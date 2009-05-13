/*
 * TV-Browser
 * Copyright (C) 04-2003 Martin Oberhauser (martin_oat@yahoo.de)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * CVS information:
 *  $RCSfile$
 *   $Source$
 *     $Date$
 *   $Author$
 * $Revision$
 */

package com.kemai.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

import com.kemai.wremja.logging.Logger;

/**
 * Provides utilities for UI stuff.
 * 
 * @author Til Schneider, www.murfman.de
 */
/**
 * @author MadMan
 * 
 */
public class UiUtilities {

    private static final Logger log = Logger.getLogger(UiUtilities.class);
    
    /**
     * Scale Icons to a specific width. The aspect ratio is kept.
     * 
     * @param icon
     *            The icon to scale.
     * @param newWidth
     *            The new width of the icon.
     * @return The scaled Icon.
     */
    public static Icon scaleIcon(Icon icon, int newWidth) {
        return scaleIcon(icon, newWidth, (int) ((newWidth / (float) icon
                .getIconWidth()) * icon.getIconHeight()));
    }

    /**
     * Scales Icons to a specific size
     * 
     * @param icon
     *            Icon that should be scaled
     * @param x
     *            new X-Value
     * @param y
     *            new Y-Value
     * @return Scaled Icon
     */
    public static Icon scaleIcon(Icon icon, int x, int y) {
        int currentWidth = icon.getIconWidth();
        int currentHeight = icon.getIconHeight();
        if ((currentWidth == x) && (currentHeight == y)) {
            return icon;
        }
        try {
            // Create Image with Icon
            BufferedImage iconimage = new BufferedImage(x, y,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = iconimage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            AffineTransform z = g2.getTransform();
            z.scale((double) x / currentWidth, (double) y / currentHeight);
            g2.setTransform(z);
            icon.paintIcon(null, g2, 0, 0);
            g2.dispose();

            // Return new Icon
            return new ImageIcon(iconimage);

        } catch (Exception e) {
            log.error(e, e);
        }

        return icon;
    }

    /**
     * Scales an image to a specific size and returns an BufferedImage
     * 
     * @param img
     *            Scale this Image
     * @param x
     *            new X-Value
     * @param y
     *            new Y-Value
     * @return Scaled BufferedImage
     * 
     * @since 2.5
     */
    public static BufferedImage scaleIconToBufferedImage(BufferedImage img,
            int x, int y) {
        return scaleIconToBufferedImage(img, x, y, img.getType());
    }

    /**
     * Scales an image to a specific size and returns an BufferedImage
     * 
     * @param img
     *            Scale this IMage
     * @param x
     *            new X-Value
     * @param y
     *            new Y-Value
     * @param type
     *            The type of the image.
     * @return Scaled BufferedImage
     * 
     * @since 2.7
     */
    public static BufferedImage scaleIconToBufferedImage(Image img, int x,
            int y, int type) {
        // Scale Image
        Image image = img.getScaledInstance(x, y, Image.SCALE_SMOOTH);

        BufferedImage im = new BufferedImage(x, y, type);

        Graphics2D g2 = im.createGraphics();
        g2.drawImage(image, null, null);
        g2.dispose();

        im.flush();
        return im;
    }

    public static int getMnemonic(String key, Locale l) {
        String value = (String)UIManager.get(key, l);

        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException nfe) { }
        return 0;
    }
}
