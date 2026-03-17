package com.xkball.xklib.ui.widget;

import com.xkball.xklib.antlr.ColorMapping;
import com.xkball.xklib.antlr.css.CssParser;
import com.xkball.xklib.antlr.css.css3Lexer;
import com.xkball.xklib.ui.WidgetTestFrame;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.render.IFont;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.TaffyDimension;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CssColoringPreviewTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CssColoringPreviewTest.class);

    private static final String CSS_SAMPLE = """
            :root {
              --brand: #4f46e5;
              --accent: rgb(80, 227, 194);
            }

            .app-shell, #mainPane[data-theme^=dark] {
              color: var(--brand);
              background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
              border: 1px solid rgba(148, 163, 184, 0.35);
              margin: 12px 20px;
            }

            .card:hover::before {
              content: "状态中";
              padding: calc(4px + 0.6rem);
              transform: translateX(-50%);
              animation: fade-in 0.25s ease-in-out;
            }

            @media screen and (min-width: 960px) {
              .grid > .item:nth-child(2n + 1) {
                width: clamp(240px, 40vw, 560px);
              }
            }
            
            div {
                width: 1;
                height: 100%;
                size: 20 20;
            }
            """;

    public static void main(String[] ignored) {
        try (var frame = new WidgetTestFrame(CssColoringPreviewTest::createWindow)) {
            frame.run();
        } catch (Exception e) {
            LOGGER.error("css coloring preview failed", e);
            throw new RuntimeException(e);
        }
    }

    private static ContainerWidget createWindow() {
        var root = new ContainerWidget();
        root.asTreeRoot();
        root.addDecoration(new Background(0xFF111827));
        root.setStyle(s -> s.size = TaffySize.all(TaffyDimension.percent(1f)));

        var preview = new CssColoringWidget(CSS_SAMPLE);
        preview.setStyle(s -> s.size = TaffySize.all(TaffyDimension.percent(1f)));
        root.addChild(preview);
        return root;
    }

    private static final class CssColoringWidget extends Widget {

        private final List<Token> tokens;
        private final Int2ObjectMap<ColorMapping> colorMap;

        private CssColoringWidget(String css) {
            var lexer = new css3Lexer(CharStreams.fromString(css));
            var tokenStream = new CommonTokenStream(lexer);
            tokenStream.fill();
            this.tokens = tokenStream.getTokens();
            this.colorMap = CssParser.coloring(css);
            var style = CssParser.parse(
"""
div {
    width: 1;
    height: 100%;
    size: 20 20;
}
"""
            );
            style.sheets();
        }

        @Override
        public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            super.doRender(graphics, mouseX, mouseY, a);
            graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF0B1020);

            IFont font = graphics.defaultFont();
            float startX = this.x + 8f;
            float maxX = this.x + this.width - 8f;
            float maxY = this.y + this.height - font.lineHeight() - 2f;
            float cursorX = startX;
            float cursorY = this.y + 8f;
            int lineHeight = font.lineHeight() + 2;

            for (Token token : tokens) {
                if (token.getType() == Token.EOF) {
                    continue;
                }
                String text = token.getText();
                if (text == null || text.isEmpty()) {
                    continue;
                }
                int color = resolveColor(token.getTokenIndex());
                for (int i = 0; i < text.length(); ) {
                    int codePoint = text.codePointAt(i);
                    i += Character.charCount(codePoint);
                    if (codePoint == '\r') {
                        continue;
                    }
                    if (codePoint == '\n') {
                        cursorX = startX;
                        cursorY += lineHeight;
                        if (cursorY > maxY) {
                            return;
                        }
                        continue;
                    }
                    String chr = new String(Character.toChars(codePoint));
                    float charWidth = font.width(chr);
                    if (cursorX + charWidth > maxX) {
                        cursorX = startX;
                        cursorY += lineHeight;
                        if (cursorY > maxY) {
                            return;
                        }
                    }
                    graphics.drawString(font, chr, cursorX, cursorY, color);
                    cursorX += charWidth;
                }
            }
        }

        private int resolveColor(int tokenIndex) {
            ColorMapping mapping = colorMap.get(tokenIndex);
            if (mapping == null) {
                return 0xFFD4D4D4;
            }
            int color = mapping.color;
            if ((color & 0xFF000000) == 0) {
                return color | 0xFF000000;
            }
            return color;
        }
    }
}

