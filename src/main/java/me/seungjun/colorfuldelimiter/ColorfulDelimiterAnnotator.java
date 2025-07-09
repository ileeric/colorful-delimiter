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

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ColorfulDelimiterAnnotator implements Annotator {
    
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        PsiFile file = element.getContainingFile();
        if (file == null) {
            return;
        }
        
        // Only process at the file level to avoid duplicate processing
        if (element != file) {
            return;
        }
        
        List<DelimiterMatcher.DelimiterPair> delimiterPairs = DelimiterMatcher.findMatchingDelimiters(file);
        
        for (DelimiterMatcher.DelimiterPair pair : delimiterPairs) {
            int openOffset = pair.getOpenOffset();
            int closeOffset = pair.getCloseOffset();
            
            // Process opening delimiter/quote
            if (pair.isQuote()) {
                // Color only the opening quote characters
                annotateQuoteChars(holder, openOffset, pair.getColorIndex(), pair.getQuoteLength());
            } else {
                annotateDelimiter(holder, openOffset, pair.getColorIndex());
            }
            
            // Process closing delimiter/quote
            if (pair.isQuote()) {
                // Color only the closing quote characters
                annotateQuoteChars(holder, closeOffset - pair.getQuoteLength() + 1, pair.getColorIndex(), pair.getQuoteLength());
            } else {
                annotateDelimiter(holder, closeOffset, pair.getColorIndex());
            }
        }
    }
    
    private void annotateDelimiter(AnnotationHolder holder, int offset, int colorIndex) {
        TextRange range = new TextRange(offset, offset + 1);
        TextAttributes attributes = ColorScheme.getTextAttributes(colorIndex);
        
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(range)
                .enforcedTextAttributes(attributes)
                .create();
    }
    
    private void annotateQuoteChars(AnnotationHolder holder, int offset, int colorIndex, int quoteLength) {
        // Only color the quote characters themselves, not the content
        TextRange range = new TextRange(offset, offset + quoteLength);
        TextAttributes attributes = ColorScheme.getTextAttributes(colorIndex);
        
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(range)
                .enforcedTextAttributes(attributes)
                .create();
    }
}