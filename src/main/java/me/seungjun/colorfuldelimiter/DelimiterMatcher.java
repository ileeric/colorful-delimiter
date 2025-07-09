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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.*;

public class DelimiterMatcher {
    
    private static final Map<Character, Character> BRACE_PAIRS = new HashMap<>();
    private static final Set<Character> OPENING_BRACES = new HashSet<>();
    private static final Set<Character> CLOSING_BRACES = new HashSet<>();
    private static final Set<Character> QUOTE_CHARS = new HashSet<>();
    
    static {
        BRACE_PAIRS.put('(', ')');
        BRACE_PAIRS.put('{', '}');
        BRACE_PAIRS.put('[', ']');
        
        OPENING_BRACES.addAll(BRACE_PAIRS.keySet());
        CLOSING_BRACES.addAll(BRACE_PAIRS.values());
        
        QUOTE_CHARS.add('\'');
        QUOTE_CHARS.add('"');
        QUOTE_CHARS.add('`');
    }
    
    public static class DelimiterPair {
        private final int openOffset;
        private final int closeOffset;
        private final char delimiterType;
        private final int colorIndex;
        private final boolean isQuote;
        private final int quoteLength;
        
        public DelimiterPair(int openOffset, int closeOffset, char delimiterType, int colorIndex) {
            this.openOffset = openOffset;
            this.closeOffset = closeOffset;
            this.delimiterType = delimiterType;
            this.colorIndex = colorIndex;
            this.isQuote = false;
            this.quoteLength = 1;
        }
        
        public DelimiterPair(int openOffset, int closeOffset, char delimiterType, int colorIndex, int quoteLength) {
            this.openOffset = openOffset;
            this.closeOffset = closeOffset;
            this.delimiterType = delimiterType;
            this.colorIndex = colorIndex;
            this.isQuote = true;
            this.quoteLength = quoteLength;
        }
        
        public int getOpenOffset() { return openOffset; }
        public int getCloseOffset() { return closeOffset; }
        public char getDelimiterType() { return delimiterType; }
        public int getColorIndex() { return colorIndex; }
        public boolean isQuote() { return isQuote; }
        public int getQuoteLength() { return quoteLength; }
    }
    
    public static List<DelimiterPair> findMatchingDelimiters(PsiFile file) {
        List<DelimiterPair> pairs = new ArrayList<>();
        String text = file.getText();
        
        // Find comment ranges to skip
        List<CommentRange> commentRanges = findCommentRanges(text);
        
        // Find string ranges to skip (but still color the quotes themselves)
        List<CommentRange> stringRanges = findStringRanges(text);
        
        // Color assignment counter
        int[] colorCounter = {0};
        
        // Handle delimiters (skip both comments and strings)
        List<CommentRange> allSkipRanges = new ArrayList<>();
        allSkipRanges.addAll(commentRanges);
        allSkipRanges.addAll(stringRanges);
        pairs.addAll(findMatchingDelimiterTypes(text, allSkipRanges, colorCounter));
        
        // Handle quotes (skip only comments, not strings since quotes define strings)
        pairs.addAll(findMatchingQuotes(text, commentRanges, colorCounter));
        
        return pairs;
    }
    
    private static class CommentRange {
        final int start;
        final int end;
        
        CommentRange(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
    
    private static List<CommentRange> findCommentRanges(String text) {
        List<CommentRange> ranges = new ArrayList<>();
        
        for (int i = 0; i < text.length(); i++) {
            // Single-line comments: // and #
            if (i < text.length() - 1 && text.charAt(i) == '/' && text.charAt(i + 1) == '/') {
                int end = text.indexOf('\n', i);
                if (end == -1) end = text.length();
                ranges.add(new CommentRange(i, end));
                i = end - 1;
            } else if (text.charAt(i) == '#') {
                int end = text.indexOf('\n', i);
                if (end == -1) end = text.length();
                ranges.add(new CommentRange(i, end));
                i = end - 1;
            }
            // Multi-line comments: /* */
            else if (i < text.length() - 1 && text.charAt(i) == '/' && text.charAt(i + 1) == '*') {
                int end = text.indexOf("*/", i + 2);
                if (end != -1) {
                    ranges.add(new CommentRange(i, end + 2));
                    i = end + 1;
                }
            }
        }
        
        return ranges;
    }
    
    private static List<CommentRange> findStringRanges(String text) {
        List<CommentRange> ranges = new ArrayList<>();
        
        for (char quoteChar : QUOTE_CHARS) {
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == quoteChar) {
                    // Check for triple quotes
                    int quoteLength = getQuoteLength(text, i, quoteChar);
                    
                    // Check for empty string (immediate closing quote)
                    if (isEmptyString(text, i, quoteChar, quoteLength)) {
                        // Skip empty strings - they don't contain content to exclude
                        i = i + (quoteLength * 2) - 1;
                    } else {
                        // Find the matching closing quote
                        int closePos = findClosingQuoteSimple(text, i + quoteLength, quoteChar, quoteLength);
                        if (closePos != -1) {
                            // Add the range between quotes (excluding the quote characters themselves)
                            ranges.add(new CommentRange(i + quoteLength, closePos));
                            i = closePos + quoteLength - 1; // Skip past the closing quote
                        }
                    }
                }
            }
        }
        
        return ranges;
    }
    
    private static int findClosingQuoteSimple(String text, int startPos, char quoteChar, int quoteLength) {
        for (int i = startPos; i <= text.length() - quoteLength; i++) {
            if (text.charAt(i) == quoteChar) {
                if (quoteLength == 1) {
                    // Single quote - check for escape
                    if (i > 0 && text.charAt(i - 1) == '\\') {
                        continue; // Skip escaped quotes
                    }
                    return i;
                } else if (quoteLength == 3) {
                    // Triple quote - check if we have three consecutive quotes
                    if (i + 2 < text.length() && 
                        text.charAt(i + 1) == quoteChar && 
                        text.charAt(i + 2) == quoteChar) {
                        return i;
                    }
                }
            }
        }
        return -1; // No matching quote found
    }
    
    private static boolean isInComment(int position, List<CommentRange> commentRanges) {
        for (CommentRange range : commentRanges) {
            if (position >= range.start && position < range.end) {
                return true;
            }
        }
        return false;
    }
    
    private static List<DelimiterPair> findMatchingDelimiterTypes(String text, List<CommentRange> commentRanges, int[] colorCounter) {
        List<DelimiterPair> pairs = new ArrayList<>();
        
        Stack<DelimiterInfo> parentheses = new Stack<>();
        Stack<DelimiterInfo> curlyBraces = new Stack<>();
        Stack<DelimiterInfo> squareBrackets = new Stack<>();
        
        // Track color assignment by depth for each type
        Map<String, Integer> depthColorCounters = new HashMap<>();
        
        for (int i = 0; i < text.length(); i++) {
            // Skip if this position is in a comment
            if (isInComment(i, commentRanges)) {
                continue;
            }
            
            char ch = text.charAt(i);
            
            if (ch == '(') {
                int depth = parentheses.size();
                String key = "(" + depth;
                int colorIndex = depthColorCounters.getOrDefault(key, depth % ColorScheme.getColorCount());
                depthColorCounters.put(key, colorIndex);
                
                parentheses.push(new DelimiterInfo(i, colorIndex));
            } else if (ch == ')' && !parentheses.isEmpty()) {
                DelimiterInfo openInfo = parentheses.pop();
                pairs.add(new DelimiterPair(openInfo.position, i, '(', openInfo.colorIndex));
            } else if (ch == '{') {
                int depth = curlyBraces.size();
                String key = "{" + depth;
                int colorIndex = depthColorCounters.getOrDefault(key, depth % ColorScheme.getColorCount());
                depthColorCounters.put(key, colorIndex);
                
                curlyBraces.push(new DelimiterInfo(i, colorIndex));
            } else if (ch == '}' && !curlyBraces.isEmpty()) {
                DelimiterInfo openInfo = curlyBraces.pop();
                pairs.add(new DelimiterPair(openInfo.position, i, '{', openInfo.colorIndex));
            } else if (ch == '[') {
                int depth = squareBrackets.size();
                String key = "[" + depth;
                int colorIndex = depthColorCounters.getOrDefault(key, depth % ColorScheme.getColorCount());
                depthColorCounters.put(key, colorIndex);
                
                squareBrackets.push(new DelimiterInfo(i, colorIndex));
            } else if (ch == ']' && !squareBrackets.isEmpty()) {
                DelimiterInfo openInfo = squareBrackets.pop();
                pairs.add(new DelimiterPair(openInfo.position, i, '[', openInfo.colorIndex));
            }
        }
        
        return pairs;
    }
    
    private static class DelimiterInfo {
        final int position;
        final int colorIndex;
        
        DelimiterInfo(int position, int colorIndex) {
            this.position = position;
            this.colorIndex = colorIndex;
        }
    }
    
    private static List<DelimiterPair> findMatchingQuotes(String text, List<CommentRange> commentRanges, int[] colorCounter) {
        List<DelimiterPair> pairs = new ArrayList<>();
        
        for (char quoteChar : QUOTE_CHARS) {
            pairs.addAll(findQuotePairs(text, quoteChar, commentRanges, colorCounter));
        }
        
        return pairs;
    }
    
    private static List<DelimiterPair> findQuotePairs(String text, char quoteChar, List<CommentRange> commentRanges, int[] colorCounter) {
        List<DelimiterPair> pairs = new ArrayList<>();
        
        // Track quote depth for consistent coloring
        Map<String, Integer> quoteDepthCounters = new HashMap<>();
        int quoteDepth = 0;
        
        for (int i = 0; i < text.length(); i++) {
            // Skip if this position is in a comment
            if (isInComment(i, commentRanges)) {
                continue;
            }
            
            if (text.charAt(i) == quoteChar) {
                // Check for triple quotes
                int quoteLength = getQuoteLength(text, i, quoteChar);
                
                // Assign color based on quote depth
                String key = quoteChar + String.valueOf(quoteLength) + "_" + quoteDepth;
                int colorIndex = quoteDepthCounters.getOrDefault(key, (quoteDepth + 10) % ColorScheme.getColorCount());
                quoteDepthCounters.put(key, colorIndex);
                
                // Check for empty string (immediate closing quote)
                if (isEmptyString(text, i, quoteChar, quoteLength)) {
                    pairs.add(new DelimiterPair(i, i + (quoteLength * 2) - 1, quoteChar, colorIndex, quoteLength));
                    i = i + (quoteLength * 2) - 1; // Skip past the empty string
                } else {
                    // Find the matching closing quote
                    int closePos = findClosingQuote(text, i + quoteLength, quoteChar, quoteLength, commentRanges);
                    if (closePos != -1) {
                        pairs.add(new DelimiterPair(i, closePos + quoteLength - 1, quoteChar, colorIndex, quoteLength));
                        i = closePos + quoteLength - 1; // Skip past the closing quote
                        quoteDepth++;
                    }
                }
            }
        }
        
        return pairs;
    }
    
    private static int getQuoteLength(String text, int startPos, char quoteChar) {
        int length = 1;
        
        // Check for triple quotes
        if (startPos + 2 < text.length() && 
            text.charAt(startPos + 1) == quoteChar && 
            text.charAt(startPos + 2) == quoteChar) {
            length = 3;
        }
        
        return length;
    }
    
    private static boolean isEmptyString(String text, int startPos, char quoteChar, int quoteLength) {
        // Check if we have enough characters for an empty string
        if (startPos + (quoteLength * 2) > text.length()) {
            return false;
        }
        
        if (quoteLength == 1) {
            // Check for "" or ''
            return text.charAt(startPos + 1) == quoteChar;
        } else if (quoteLength == 3) {
            // Check for '''''' (6 consecutive quotes)
            for (int i = 1; i < 6; i++) {
                if (startPos + i >= text.length() || text.charAt(startPos + i) != quoteChar) {
                    return false;
                }
            }
            return true;
        }
        
        return false;
    }
    
    private static int findClosingQuote(String text, int startPos, char quoteChar, int quoteLength, List<CommentRange> commentRanges) {
        for (int i = startPos; i <= text.length() - quoteLength; i++) {
            // Skip if this position is in a comment
            if (isInComment(i, commentRanges)) {
                continue;
            }
            
            if (text.charAt(i) == quoteChar) {
                if (quoteLength == 1) {
                    // Single quote - check for escape
                    if (i > 0 && text.charAt(i - 1) == '\\') {
                        continue; // Skip escaped quotes
                    }
                    return i;
                } else if (quoteLength == 3) {
                    // Triple quote - check if we have three consecutive quotes
                    if (i + 2 < text.length() && 
                        text.charAt(i + 1) == quoteChar && 
                        text.charAt(i + 2) == quoteChar) {
                        return i;
                    }
                }
            }
        }
        return -1; // No matching quote found
    }
    
    public static boolean isDelimiter(char ch) {
        return OPENING_BRACES.contains(ch) || CLOSING_BRACES.contains(ch);
    }
    
    public static boolean isOpeningDelimiter(char ch) {
        return OPENING_BRACES.contains(ch);
    }
    
    public static boolean isClosingDelimiter(char ch) {
        return CLOSING_BRACES.contains(ch);
    }
}