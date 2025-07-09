/*
 * Copyright (C) 2025 Seungjun Lee
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package me.seungjun.colorfuldelimiter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorScheme {
    
    private static final Color[] COLORS = {
        new JBColor(new Color(255, 69, 0), new Color(255, 100, 50)),     // Red Orange
        new JBColor(new Color(50, 205, 50), new Color(100, 255, 100)),   // Lime Green
        new JBColor(new Color(30, 144, 255), new Color(70, 180, 255)),   // Dodger Blue
        new JBColor(new Color(255, 20, 147), new Color(255, 60, 180)),   // Deep Pink
        new JBColor(new Color(255, 215, 0), new Color(255, 235, 50)),    // Gold
        new JBColor(new Color(138, 43, 226), new Color(170, 80, 255)),   // Blue Violet
        new JBColor(new Color(255, 140, 0), new Color(255, 170, 50)),    // Dark Orange
        new JBColor(new Color(0, 255, 255), new Color(50, 255, 255)),    // Cyan
        new JBColor(new Color(255, 105, 180), new Color(255, 130, 200)), // Hot Pink
        new JBColor(new Color(124, 252, 0), new Color(150, 255, 50)),    // Lawn Green
        new JBColor(new Color(255, 0, 255), new Color(255, 50, 255)),    // Magenta
        new JBColor(new Color(255, 165, 0), new Color(255, 190, 50)),    // Orange
        new JBColor(new Color(127, 255, 212), new Color(150, 255, 220)), // Aqua Marine
        new JBColor(new Color(255, 99, 71), new Color(255, 120, 100)),   // Tomato
        new JBColor(new Color(154, 205, 50), new Color(180, 230, 80)),   // Yellow Green
        new JBColor(new Color(255, 20, 147), new Color(255, 60, 180)),   // Deep Pink
        new JBColor(new Color(0, 191, 255), new Color(50, 210, 255)),    // Deep Sky Blue
        new JBColor(new Color(255, 69, 0), new Color(255, 100, 50)),     // Red Orange
        new JBColor(new Color(148, 0, 211), new Color(180, 50, 240)),    // Dark Violet
        new JBColor(new Color(255, 255, 0), new Color(255, 255, 100))    // Yellow
    };
    
    private static final Map<String, TextAttributesKey> ATTRIBUTE_KEYS = new HashMap<>();
    
    static {
        for (int i = 0; i < COLORS.length; i++) {
            String keyName = "COLORFUL_DELIMITER_" + i;
            TextAttributesKey key = TextAttributesKey.createTextAttributesKey(
                keyName,
                DefaultLanguageHighlighterColors.BRACKETS
            );
            ATTRIBUTE_KEYS.put(keyName, key);
        }
    }
    
    public static TextAttributesKey getAttributesKey(int colorIndex) {
        String keyName = "COLORFUL_DELIMITER_" + (colorIndex % COLORS.length);
        return ATTRIBUTE_KEYS.get(keyName);
    }
    
    public static TextAttributes getTextAttributes(int colorIndex) {
        Color color = COLORS[colorIndex % COLORS.length];
        
        TextAttributes attributes = new TextAttributes();
        attributes.setForegroundColor(color);
        attributes.setFontType(Font.BOLD);
        attributes.setEffectType(null);
        return attributes;
    }
    
    public static int getColorCount() {
        return COLORS.length;
    }
    
    public static Color[] getColors() {
        return COLORS.clone();
    }
}