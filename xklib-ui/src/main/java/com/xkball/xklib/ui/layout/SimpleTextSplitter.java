package com.xkball.xklib.ui.layout;

import com.ibm.icu.text.BreakIterator;
import com.xkball.xklib.api.gui.layout.ITextSplitter;
import com.xkball.xklib.ui.render.IFont;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SimpleTextSplitter implements ITextSplitter {
    
    private static final String CJK_LINE_START_FORBIDDEN = "，。！？、；：）」』】〉》\"〕％‰";
    private static final String CJK_LINE_END_FORBIDDEN = "（「『【〈《\"〔";
    
    @Override
    public List<String> split(IFont font, String text, float width) {
        if (text == null || text.isEmpty() || width <= 0) {
            return List.of();
        }
        
        List<String> result = new ArrayList<>();
        String normalizedText = text.replace("\r\n", "\n");
        String[] paragraphs = normalizedText.split("\n", -1);
        
        for (String paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                result.add("");
            } else {
                result.addAll(splitParagraph(font, paragraph, width));
            }
        }
        
        return result;
    }
    
    private List<String> splitParagraph(IFont font, String text, float width) {
        List<String> result = new ArrayList<>();
        BreakIterator boundary = BreakIterator.getLineInstance(Locale.getDefault());
        boundary.setText(text);
        
        StringBuilder currentLine = new StringBuilder();
        int start = boundary.first();
        int end = boundary.next();
        
        while (end != BreakIterator.DONE) {
            String segment = text.substring(start, end);
            String testLine = currentLine + segment;
            float testWidth = font.width(testLine);
            
            if (testWidth <= width) {
                currentLine.append(segment);
            } else {
                if (!currentLine.isEmpty()) {
                    String lineStr = currentLine.toString();
                    
                    if (shouldAvoidBreak(lineStr, segment)) {
                        result.add(lineStr);
                        currentLine = new StringBuilder();
                        
                        if (font.width(segment) > width) {
                            List<String> brokenSegments = breakLongSegmentCompletely(font, segment, width);
                            if (!brokenSegments.isEmpty()) {
                                for (int i = 0; i < brokenSegments.size() - 1; i++) {
                                    result.add(brokenSegments.get(i));
                                }
                                currentLine.append(brokenSegments.getLast());
                            }
                        } else {
                            currentLine.append(segment);
                        }
                    } else {
                        result.add(lineStr);
                        currentLine = new StringBuilder();
                        
                        if (font.width(segment) > width) {
                            List<String> brokenSegments = breakLongSegmentCompletely(font, segment, width);
                            if (!brokenSegments.isEmpty()) {
                                for (int i = 0; i < brokenSegments.size() - 1; i++) {
                                    result.add(brokenSegments.get(i));
                                }
                                currentLine.append(brokenSegments.getLast());
                            }
                        } else {
                            currentLine.append(segment);
                        }
                    }
                } else {
                    if (font.width(segment) > width) {
                        List<String> brokenSegments = breakLongSegmentCompletely(font, segment, width);
                        if (!brokenSegments.isEmpty()) {
                            for (int i = 0; i < brokenSegments.size() - 1; i++) {
                                result.add(brokenSegments.get(i));
                            }
                            currentLine.append(brokenSegments.getLast());
                        }
                    } else {
                        currentLine.append(segment);
                    }
                }
            }
            
            start = end;
            end = boundary.next();
        }
        
        if (!currentLine.isEmpty()) {
            result.add(currentLine.toString());
        }
        
        return result;
    }
    
    private boolean shouldAvoidBreak(String lineStr, String segment) {
        if (lineStr.isEmpty() || segment.isEmpty()) {
            return false;
        }
        
        char lastChar = lineStr.charAt(lineStr.length() - 1);
        char firstChar = segment.charAt(0);
        
        boolean lineEndsWithForbidden = CJK_LINE_END_FORBIDDEN.indexOf(lastChar) != -1;
        boolean segmentStartsWithForbidden = CJK_LINE_START_FORBIDDEN.indexOf(firstChar) != -1;
        
        return lineEndsWithForbidden || segmentStartsWithForbidden;
    }
    
    private List<String> breakLongSegmentCompletely(IFont font, String segment, float width) {
        List<String> result = new ArrayList<>();
        String remaining = segment;
        
        while (!remaining.isEmpty()) {
            if (font.width(remaining) <= width) {
                result.add(remaining);
                break;
            }
            
            int breakPoint = 0;
            for (int i = 1; i <= remaining.length(); i++) {
                String sub = remaining.substring(0, i);
                if (font.width(sub) <= width) {
                    breakPoint = i;
                } else {
                    break;
                }
            }
            
            if (breakPoint > 0) {
                result.add(remaining.substring(0, breakPoint));
                remaining = remaining.substring(breakPoint);
            } else {
                result.add(remaining.substring(0, 1));
                remaining = remaining.substring(1);
            }
        }
        
        return result;
    }
}

