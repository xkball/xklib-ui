package com.xkball.xklib.antlr.css;

import com.xkball.xklib.antlr.ColorMapping;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class CssColoringListener extends css3ParserBaseListener {
    
    private final Int2ObjectMap<ColorMapping> map;

    public CssColoringListener() {
        this(new Int2ObjectLinkedOpenHashMap<>());
    }

    public CssColoringListener(Int2ObjectMap<ColorMapping> map) {
        this.map = map;
    }

    public Int2ObjectMap<ColorMapping> result() {
        return this.map;
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        super.visitTerminal(node);
        Token token = node.getSymbol();
        if (token == null || token.getType() == Token.EOF) {
            return;
        }
        int index = token.getTokenIndex();
        if (index < 0) {
            return;
        }
        if (hasError(node)) {
            map.put(index, ColorMapping.ERROR);
            return;
        }
        int type = token.getType();
        if (isPunctuation(type)) {
            map.putIfAbsent(index, ColorMapping.CSS_PUNCTUATION);
            return;
        }
        if (inPropertyName(node) && isPropertyNameToken(type)) {
            map.putIfAbsent(index, ColorMapping.CSS_PROPERTY_NAME);
            return;
        }
        if (inPropertyValue(node) && isPropertyValueToken(type)) {
            map.putIfAbsent(index, ColorMapping.CSS_PROPERTY_VALUE);
            return;
        }
        if (inSelector(node) && isSelectorToken(type)) {
            map.putIfAbsent(index, ColorMapping.CSS_SELECTOR);
        }
    }

    private static boolean hasError(ParseTree node) {
        ParseTree cursor = node;
        while (cursor != null) {
            if (cursor instanceof ParserRuleContext context && context.exception != null) {
                return true;
            }
            cursor = cursor.getParent();
        }
        return false;
    }

    private static boolean inPropertyName(ParseTree node) {
        return hasParent(node, css3Parser.GoodPropertyContext.class)
                || hasParent(node, css3Parser.BadPropertyContext.class);
    }

    private static boolean inPropertyValue(ParseTree node) {
        return hasParent(node, css3Parser.ExprContext.class)
                || hasParent(node, css3Parser.ValueContext.class)
                || hasParent(node, css3Parser.KnownTermContext.class)
                || hasParent(node, css3Parser.UnknownTermContext.class)
                || hasParent(node, css3Parser.BadTermContext.class)
                || hasParent(node, css3Parser.PrioContext.class);
    }

    private static boolean inSelector(ParseTree node) {
        return hasParent(node, css3Parser.SelectorGroupContext.class)
                || hasParent(node, css3Parser.SelectorContext.class)
                || hasParent(node, css3Parser.SimpleSelectorSequenceContext.class)
                || hasParent(node, css3Parser.TypeSelectorContext.class)
                || hasParent(node, css3Parser.TypeNamespacePrefixContext.class)
                || hasParent(node, css3Parser.ElementNameContext.class)
                || hasParent(node, css3Parser.UniversalContext.class)
                || hasParent(node, css3Parser.ClassNameContext.class)
                || hasParent(node, css3Parser.AttribContext.class)
                || hasParent(node, css3Parser.PseudoContext.class)
                || hasParent(node, css3Parser.FunctionalPseudoContext.class)
                || hasParent(node, css3Parser.ExpressionContext.class)
                || hasParent(node, css3Parser.NegationContext.class)
                || hasParent(node, css3Parser.NegationArgContext.class);
    }

    private static boolean hasParent(ParseTree node, Class<? extends ParseTree> type) {
        ParseTree cursor = node;
        while (cursor != null) {
            if (type.isInstance(cursor)) {
                return true;
            }
            cursor = cursor.getParent();
        }
        return false;
    }

    private static boolean isSelectorToken(int type) {
        return type == css3Lexer.Ident
                || type == css3Lexer.Hash
                || type == css3Lexer.Variable
                || type == css3Lexer.Function_;
    }

    private static boolean isPropertyNameToken(int type) {
        return type == css3Lexer.Ident || type == css3Lexer.Variable;
    }

    private static boolean isPropertyValueToken(int type) {
        return type == css3Lexer.Ident
                || type == css3Lexer.Variable
                || type == css3Lexer.Number
                || type == css3Lexer.Percentage
                || type == css3Lexer.Dimension
                || type == css3Lexer.UnknownDimension
                || type == css3Lexer.String_
                || type == css3Lexer.UnicodeRange
                || type == css3Lexer.Url
                || type == css3Lexer.Url_
                || type == css3Lexer.Hash
                || type == css3Lexer.Function_
                || type == css3Lexer.Calc
                || type == css3Lexer.Var
                || type == css3Lexer.AtKeyword
                || type == css3Lexer.Important;
    }

    private static boolean isPunctuation(int type) {
        return type == css3Lexer.OpenBracket
                || type == css3Lexer.CloseBracket
                || type == css3Lexer.OpenParen
                || type == css3Lexer.CloseParen
                || type == css3Lexer.OpenBrace
                || type == css3Lexer.CloseBrace
                || type == css3Lexer.SemiColon
                || type == css3Lexer.Equal
                || type == css3Lexer.Colon
                || type == css3Lexer.Dot
                || type == css3Lexer.Multiply
                || type == css3Lexer.Divide
                || type == css3Lexer.Pipe
                || type == css3Lexer.Underscore
                || type == css3Lexer.Plus
                || type == css3Lexer.Minus
                || type == css3Lexer.Greater
                || type == css3Lexer.Comma
                || type == css3Lexer.Tilde
                || type == css3Lexer.PrefixMatch
                || type == css3Lexer.SuffixMatch
                || type == css3Lexer.SubstringMatch
                || type == css3Lexer.Includes
                || type == css3Lexer.DashMatch;
    }
}
