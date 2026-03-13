package com.xkball.xklib.antlr.css;

import com.xkball.xklib.ui.css.CascadingStyleSheets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class CssRuleBuilderListener extends css3ParserBaseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CssRuleBuilderListener.class);

    private final CascadingStyleSheets result = new CascadingStyleSheets();
    private int order = 0;
    
    @Override
    public void enterKnownRuleset(css3Parser.KnownRulesetContext ctx) {
        super.enterKnownRuleset(ctx);
    }
    
    @Override
    public void exitKnownRuleset(css3Parser.KnownRulesetContext ctx) {
        super.exitKnownRuleset(ctx);
    }
    
    @Override
    public void enterUnknownRuleset(css3Parser.UnknownRulesetContext ctx) {
        super.enterUnknownRuleset(ctx);
    }
    
    @Override
    public void exitUnknownRuleset(css3Parser.UnknownRulesetContext ctx) {
        super.exitUnknownRuleset(ctx);
    }
    
    CascadingStyleSheets result() {
        return this.result;
    }

    public enum RulesetState{
        KNOWN,
        UNKNOWN
    }
}

