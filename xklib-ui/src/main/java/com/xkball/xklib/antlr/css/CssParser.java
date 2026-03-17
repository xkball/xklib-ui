package com.xkball.xklib.antlr.css;

import com.xkball.xklib.antlr.ColorMapping;
import com.xkball.xklib.ui.css.CascadingStyleSheets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CssParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(CssParser.class);

    private CssParser() {
    }

    public static CascadingStyleSheets parse(String css) {
        css3Lexer lexer = new css3Lexer(CharStreams.fromString(css));
        lexer.addErrorListener(new LoggingErrorListener());

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        css3Parser parser = new css3Parser(tokenStream);
        parser.addErrorListener(new LoggingErrorListener());

        css3Parser.StylesheetContext stylesheet = parser.stylesheet();
        CssParserListener listener = new CssParserListener();
        ParseTreeWalker.DEFAULT.walk(listener, stylesheet);
        return listener.result();
    }

    public static Int2ObjectMap<ColorMapping> coloring(String css) {
        css3Lexer lexer = new css3Lexer(CharStreams.fromString(css));
        lexer.addErrorListener(new LoggingErrorListener());

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        css3Parser parser = new css3Parser(tokenStream);
        parser.addErrorListener(new LoggingErrorListener());

        css3Parser.StylesheetContext stylesheet = parser.stylesheet();
        CssColoringListener listener = new CssColoringListener();
        ParseTreeWalker.DEFAULT.walk(listener, stylesheet);

        tokenStream.fill();
        Int2ObjectMap<ColorMapping> map = listener.result();
        for (Token token : tokenStream.getTokens()) {
            if (token.getChannel() == css3Lexer.ERROR) {
                map.put(token.getTokenIndex(), ColorMapping.ERROR);
            }
        }
        return map;
    }

    private static final class LoggingErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            LOGGER.warn("css parse error at {}:{} {}", line, charPositionInLine, msg);
        }
    }
}

