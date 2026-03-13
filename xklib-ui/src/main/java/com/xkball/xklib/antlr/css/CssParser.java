package com.xkball.xklib.antlr.css;

import com.xkball.xklib.ui.css.CascadingStyleSheets;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
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

        css3Parser parser = new css3Parser(new CommonTokenStream(lexer));
        parser.addErrorListener(new LoggingErrorListener());

        css3Parser.StylesheetContext stylesheet = parser.stylesheet();
        CssRuleBuilderListener listener = new CssRuleBuilderListener();
        ParseTreeWalker.DEFAULT.walk(listener, stylesheet);
        return listener.result();
    }

    private static final class LoggingErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            LOGGER.warn("css parse error at {}:{} {}", line, charPositionInLine, msg);
        }
    }
}

