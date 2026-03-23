package com.xkball.xklib.antlr.css;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.ui.css.CascadingStyleSheets;
import com.xkball.xklib.ui.css.StyleSheetUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class CssStylesTest {

    @Test
    public void shouldParseCoreTaffyProperties() {
        CascadingStyleSheets sheets = CssParser.parse("""
                .root {
                    display: flex;
                    direction: rtl;
                    item-is-table: true;
                    item-is-replaced: false;
                    box-sizing: content-box;
                    position: absolute;
                    width: 120;
                    height: 48;
                    min-size: 10 20;
                    max-size: 100% 200;
                    aspect-ratio: 16/9;
                    margin: 1 2 3 4;
                    padding: 5 6;
                    border: 7;
                    align-items: center;
                    justify-content: space-between;
                    text-align: justify-all;
                    flex-direction: row-reverse;
                    flex-wrap: wrap-reverse;
                    flex-grow: 2;
                    flex-shrink: 3;
                    flex-basis: 40;
                    scrollbar-width: 12;
                }
                """);

        Map<String, IStyleProperty<?>> properties = toPropertyMap(sheets.sheets().getFirst());

        Assertions.assertTrue(properties.containsKey("display"));
        Assertions.assertTrue(properties.containsKey("direction"));
        Assertions.assertTrue(properties.containsKey("item-is-table"));
        Assertions.assertTrue(properties.containsKey("item-is-replaced"));
        Assertions.assertTrue(properties.containsKey("box-sizing"));
        Assertions.assertTrue(properties.containsKey("position"));
        Assertions.assertTrue(properties.containsKey("width"));
        Assertions.assertTrue(properties.containsKey("height"));
        Assertions.assertTrue(properties.containsKey("min-size"));
        Assertions.assertTrue(properties.containsKey("max-size"));
        Assertions.assertTrue(properties.containsKey("aspect-ratio"));
        Assertions.assertTrue(properties.containsKey("margin"));
        Assertions.assertTrue(properties.containsKey("padding"));
        Assertions.assertTrue(properties.containsKey("border"));
        Assertions.assertTrue(properties.containsKey("align-items"));
        Assertions.assertTrue(properties.containsKey("justify-content"));
        Assertions.assertTrue(properties.containsKey("text-align"));
        Assertions.assertTrue(properties.containsKey("flex-direction"));
        Assertions.assertTrue(properties.containsKey("flex-wrap"));
        Assertions.assertTrue(properties.containsKey("flex-grow"));
        Assertions.assertTrue(properties.containsKey("flex-shrink"));
        Assertions.assertTrue(properties.containsKey("flex-basis"));
        Assertions.assertTrue(properties.containsKey("scrollbar-width"));
    }

    @Test
    public void shouldParseOverflowAndGridProperties() {
        CascadingStyleSheets sheets = CssParser.parse("""
                .grid {
                    overflow: hidden scroll;
                    overflow-x: clip;
                    overflow-y: visible;
                    grid-template-rows: 100 1fr min-content;
                    grid-template-columns: minmax(100, 1fr) auto;
                    grid-auto-rows: 32;
                    grid-auto-columns: fit-content(50%);
                    grid-auto-flow: row-dense;
                    grid-row: 1 / span 2;
                    grid-column: sidebar / 3;
                }
                """);

        Map<String, IStyleProperty<?>> properties = toPropertyMap(sheets.sheets().getFirst());

        Assertions.assertTrue(properties.containsKey("overflow"));
        Assertions.assertTrue(properties.containsKey("overflow-x"));
        Assertions.assertTrue(properties.containsKey("overflow-y"));
        Assertions.assertTrue(properties.containsKey("grid-template-rows"));
        Assertions.assertTrue(properties.containsKey("grid-template-columns"));
        Assertions.assertTrue(properties.containsKey("grid-auto-rows"));
        Assertions.assertTrue(properties.containsKey("grid-auto-columns"));
        Assertions.assertTrue(properties.containsKey("grid-auto-flow"));
        Assertions.assertTrue(properties.containsKey("grid-row"));
        Assertions.assertTrue(properties.containsKey("grid-column"));
    }

    private Map<String, IStyleProperty<?>> toPropertyMap(StyleSheetUnit sheet) {
        Map<String, IStyleProperty<?>> map = new HashMap<>();
        for (IStyleProperty<?> property : sheet.properties()) {
            map.put(property.propertyName(), property);
        }
        return map;
    }
}

