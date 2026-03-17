package com.xkball.xklib.antlr.css;

import com.xkball.xklib.api.gui.css.ISelector;
import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.ui.css.CascadingStyleSheets;
import com.xkball.xklib.ui.css.StyleSheetUnit;
import com.xkball.xklib.ui.css.selector.AndSelector;
import com.xkball.xklib.ui.css.selector.AnySelector;
import com.xkball.xklib.ui.css.selector.ChildSelector;
import com.xkball.xklib.ui.css.selector.ClassNameSelector;
import com.xkball.xklib.ui.css.selector.DescendantSelector;
import com.xkball.xklib.ui.css.selector.FirstChildSelector;
import com.xkball.xklib.ui.css.selector.FocusedSelector;
import com.xkball.xklib.ui.css.selector.HoverSelector;
import com.xkball.xklib.ui.css.selector.IdSelector;
import com.xkball.xklib.ui.css.selector.LastChildSelector;
import com.xkball.xklib.ui.css.selector.TypeSelector;
import com.xkball.xklib.ui.css.selector.UniversalSelector;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class CssParserListener extends css3ParserBaseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CssParserListener.class);
    
    private final CascadingStyleSheets result = new CascadingStyleSheets();
    
    private ISelector rulesetSelector;
    private boolean rulesetError = false;
    private final Map<String, IStyleProperty<?>> rulesetStyles = new LinkedHashMap<>();
    
    private final List<ISelector> orSelectorList = new ArrayList<>();
    private ISelector lastSelector;
    private String lastSelectorCombinator;
    

    private int order = 0;
    
    @Override
    public void enterKnownRuleset(css3Parser.KnownRulesetContext ctx) {
        super.enterKnownRuleset(ctx);
        this.rulesetSelector = null;
        this.rulesetError = false;
        this.rulesetStyles.clear();
    }
    
    @Override
    public void exitKnownRuleset(css3Parser.KnownRulesetContext ctx) {
        super.exitKnownRuleset(ctx);
        if(this.rulesetError) {
            LOGGER.warn("Failed to parseRuleset: {}", ctx.getText());
            return;
        }
        result.add(new StyleSheetUnit(rulesetSelector.weight(),this.order, rulesetSelector, new ArrayList<>(rulesetStyles.values())));
        this.order += 1;
    }
    
    @Override
    public void enterSelectorGroup(css3Parser.SelectorGroupContext ctx) {
        super.enterSelectorGroup(ctx);
        this.orSelectorList.clear();
    }
    
    @Override
    public void exitSelectorGroup(css3Parser.SelectorGroupContext ctx) {
        super.exitSelectorGroup(ctx);
        if(this.orSelectorList.isEmpty()){
            this.rulesetError = true;
        }
        if(this.orSelectorList.size() == 1){
            this.rulesetSelector = this.orSelectorList.getFirst();
        }
        else{
            this.rulesetSelector = new AnySelector(this.orSelectorList);
        }
    }
    
    @Override
    public void exitSelector(css3Parser.SelectorContext ctx) {
        super.exitSelector(ctx);
        if(this.lastSelector != null) this.orSelectorList.add(this.lastSelector);
    }
    
    @Override
    public void enterSimpleSelectorSequence(css3Parser.SimpleSelectorSequenceContext ctx) {
        super.enterSimpleSelectorSequence(ctx);
        ISelector self = null;
        List<ISelector> andList = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            var child = ctx.getChild(i);
            try {
                var s = this.parseSingleSelector(child);
                andList.add(s);
            } catch (Exception e){
                LOGGER.warn("Cannot parse selector.",e);
                this.rulesetError = true;
            }
        }
        if(andList.isEmpty()){
            this.rulesetError = true;
        }
        else if(andList.size() == 1){
            self = andList.getFirst();
        }
        else{
            self = new AndSelector(andList);
        }
        if(this.lastSelector !=  null && this.lastSelectorCombinator != null){
            var c = this.lastSelectorCombinator;
            this.lastSelectorCombinator = null;
            if(" ".equals(c)) this.lastSelector = new DescendantSelector(this.lastSelector,self);
            if(">".equals(c)) this.lastSelector = new ChildSelector(this.lastSelector,self);
            else{
                LOGGER.warn("Unknown Combinator: {}",c);
                this.rulesetError = true;
            }
        }
        else {
            this.lastSelector = self;
        }
    }
    
    @Override
    public void enterCombinator(css3Parser.CombinatorContext ctx) {
        super.enterCombinator(ctx);
        this.lastSelectorCombinator = ctx.getText();
    }
    
    @Override
    public void enterKnownDeclaration(css3Parser.KnownDeclarationContext ctx) {
        super.enterKnownDeclaration(ctx);
        var property = ctx.property_();
        var expr = ctx.expr();
        this.parseDeclaration(property,expr);
    }
    
    private void parseDeclaration(css3Parser.Property_Context property, css3Parser.ExprContext expr){
        String name = "";
        if(property instanceof css3Parser.BadPropertyContext bp){
            name = bp.ident() == null ? "" : bp.ident().getText();
        }
        if(property instanceof css3Parser.GoodPropertyContext gp){
            name = gp.ident() == null ? gp.Variable().getText() : gp.ident().getText();
        }
        var v = CssStyles.INSTANCE.parse(name,expr);
        if(v != null) this.rulesetStyles.put(name, v);
    }
    
    private ISelector parseSingleSelector(ParseTree ctx){
        if(ctx instanceof css3Parser.UniversalContext){
            return new UniversalSelector();
        }
        if(ctx instanceof css3Parser.TypeSelectorContext tsc){
            var type = tsc.elementName().ident().getText();
            return new TypeSelector(type);
        }
        if(ctx instanceof css3Parser.ClassNameContext cc){
            var clazz = cc.ident().getText();
            return new ClassNameSelector(clazz);
        }
        if(ctx instanceof TerminalNode tn){
            var id = tn.getText();
            if(id.startsWith("#")) return new IdSelector(id.substring(1));
        }
        if(ctx instanceof css3Parser.PseudoContext pc){
            var text = pc.ident().getText();
            if("hover".equals(text)) return new HoverSelector();
            if("focus".equals(text)) return new FocusedSelector();
            if("first-child".equals(text)) return new FirstChildSelector();
            if("last-child".equals(text)) return new LastChildSelector();
        }
        throw new IllegalArgumentException("Failed to parse selector: " + ctx.getText());
    }
    
    CascadingStyleSheets result() {
        return this.result;
    }
    
}

