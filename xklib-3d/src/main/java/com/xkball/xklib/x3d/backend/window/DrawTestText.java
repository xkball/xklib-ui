package com.xkball.xklib.x3d.backend.window;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IFont;
import com.xkball.xklib.ui.render.IGUIGraphics;

import java.util.ArrayList;
import java.util.List;

public class DrawTestText extends WindowAppBase{
    private static final String VISIBLE_ASCII = visibleAscii();
    private static final String COMMON_1000 = "一丁七万丈三上下不与专且世丘丙业丛东丝丞两严丧个中丰串临丸丹主丽举久么义之乌乎乏乐乔乖乘乙九也习乡书买乱乳了予争事二于云互五井亚些亡交亦产亩享京亭亮亲人亿什仁今介从仓仔他付仙代令以仪们仰仲件价任份仿企伊伍休众优会伟传伤伦伯估伴伸似但位低住佐体何余佛作你佩佳使依侠侦侧侨便促俄俊俗俘保信俩修俯俱倍倒候借倡倦债值假偏健偶偷停偿像儿允元兄充兆先光克免党入全八公六兰共关兴兵其具典养兼内冈册再写军农冠冬冰冲决况冷净准减凡凤出击函刀切刑划列刘则刚创初判别利到制刷券刺刻前剑剧剩副割力办功加务动助努劲劳势勇勉勤包匆北匠区医十千午升半华协南博占卡卢卫印危即却卷卸卵厂厅历压厌厕厚原去参又双反发叔取受变叙叛口古句另只叫召可台史右叶号司各合同名后吏吐向君否吧含听启吸吹吻吾告员呢周味呼命和咏咨咬咱咳哀品响哲哭哥哦哲唇唯唱商啊啦啥善喊喜喝喧喻嗓嗯嘛器四回因团园困围固国图圆场地址均坊坏坐块坚坛坡坦坪坤垂型垃垄";
    private static final String MULTI_ATLAS_TEXT = buildMultiAtlasText(9000);
    private static final IComponent RICH_TEXT = IComponent.sequence(
            IComponent.literal("[IComponent] ").withColor(0xFFCCCCCC),
            IComponent.literal("Red").withColor(0xFFFF5A5A),
            IComponent.literal("Green").withColor(0xFF64DD6A),
            IComponent.literal("Blue").withColor(0xFF66B2FF),
            IComponent.literal("Strike").withColor(0xFFFFD166).withStrikethrough(true),
            IComponent.literal("Baseline+g").withColor(0xFFB794F4).withBaseline(true)
    );
    private static final int PADDING = 20;
    private static final int LINE_GAP = 4;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int BG_COLOR = 0xFF101010;
    private static final String jsb =
            """
                    招牌文字提取
                    
                    - 顶部主招牌：
                    
                    - 中国传播家文化的主题餐厅
                    - 家是本
                    - 家是本心灵家港，幸福味道记忆处
                    - 葱花肉饼、酸辣粉、薯饼卷
                    - 一群人，一辈子，干好传播家是本文化这件事
                    - 吃家是本美味，唱家是本之歌，推家是本商机，传家是本文化
                    - 以原创歌曲《我爱四川》、《幸福的成都城》、《美丽的龙泉驿》宣传家乡
                    - 总店
                    - 下方红色横幅：
                    
                    - 家是本+传播共识+城市宣传+产品+店面+服务+可复制文化商业模式
                    - （具有社会标杆示范作用 自带两大永久生命力的大流量内容）
                    - 巨大历史机遇
                    - 新形势、新商业，新方向
                    - 新模式、新机遇，新选择，新人生
                    - 就业创业招商的智选 方向大于努力，平台比能力更重要
                    正在准备Windows
                                        请不要关闭你的计算机
                                        中国传播家文化的主题餐厅
                                        家是本  家是本心灵家港，幸福味道记忆处
                                        一群人，一辈子，干好传播家是本文化这件事
                                        The quick brown fox jumped over the lazy dog.
                                        隨手存取 AI，隨時使用最佳效能。善用 Windows 11 功能，進而保護並提升您的數碼生活。
                                        1234567890/*-+!@#$%^&*()_+{}:">?<[];\\,./'
                                        【】；‘，。、《超かぐや姫！》？、|
                    Windows PowerShell
                                        Copyright (C) Microsoft Corporation. All rights reserved.
                    
                                        Install the latest PowerShell for new features and improvements! https://aka.ms/PSWindows
                    
                                        Loading personal and system profiles took 1879ms.
                    
                                        Ageratum on  releases/1.21.1 [!+?] via 🅶 v8.8 via ☕ v21.0.5
                                        ❯
                    """;
    @Override
    public void render() {
        super.render();
        var guiGraphics = XKLib.RENDER_CONTEXT.get().getGUIGraphics();
        guiGraphics.getPose().pushMatrix();
        guiGraphics.getPose().scale(1f);
        guiGraphics.getPose().translate(0,0.5f);
        var font = guiGraphics.defaultFont();
        float x = PADDING;
        float maxWidth = this.window.getWidth() - PADDING * 2f;
        float y = PADDING;
        var list = new ArrayList<>(List.of(jsb.split("\n")));
        
        for(var str : list){
            guiGraphics.drawString(str, PADDING, y, 0xFF000000);
            y += font.lineHeight() + LINE_GAP;
        }
//        guiGraphics.fill(0, 0, this.window.getWidth(), this.window.getHeight(), BG_COLOR);
//        guiGraphics.drawString(font, "ASCII", x, y, 0xFF66B2FF);
//        y += font.lineHeight() + LINE_GAP;
//        y = drawWrapped(guiGraphics, font, VISIBLE_ASCII, x, y, maxWidth, TEXT_COLOR);
//
//        y += font.lineHeight() + LINE_GAP;
//        guiGraphics.drawString(font, "COMMON_1000", x, y, 0xFF66B2FF);
//        y += font.lineHeight() + LINE_GAP;
//        y = drawWrapped(guiGraphics, font, COMMON_1000, x, y, maxWidth, TEXT_COLOR);
//
//        y += font.lineHeight() + LINE_GAP;
//        guiGraphics.drawString(font, "RICH_TEXT", x, y, 0xFF66B2FF);
//        y += font.lineHeight() + LINE_GAP;
//        guiGraphics.drawString(font, RICH_TEXT, x, y, TEXT_COLOR);
//
//        y += font.lineHeight() + LINE_GAP;
//        guiGraphics.drawString(font, "MULTI_ATLAS", x, y, 0xFF66B2FF);
//        y += font.lineHeight() + LINE_GAP;
//        y = drawWrapped(guiGraphics, font, MULTI_ATLAS_TEXT, x, y, maxWidth, TEXT_COLOR);

        guiGraphics.getPose().popMatrix();
        guiGraphics.draw();
    }

    static String visibleAscii() {
        StringBuilder builder = new StringBuilder(95);
        for (int codePoint = 0x20; codePoint <= 0x7E; codePoint++) {
            builder.append((char) codePoint);
        }
        return builder.toString();
    }

    static String buildMultiAtlasText(int targetChars) {
        StringBuilder builder = new StringBuilder(targetChars);
        appendRange(builder, 0x4E00, 0x9FFF, targetChars);
        appendRange(builder, 0x3400, 0x4DBF, targetChars);
        appendRange(builder, 0x3041, 0x3096, targetChars);
        appendRange(builder, 0x30A1, 0x30FA, targetChars);
        appendRange(builder, 0xAC00, 0xD7A3, targetChars);
        if (builder.length() < targetChars) {
            while (builder.length() < targetChars) {
                builder.append(VISIBLE_ASCII);
            }
            builder.setLength(targetChars);
        }
        return builder.toString();
    }

    private static void appendRange(StringBuilder builder, int from, int to, int targetChars) {
        for (int codePoint = from; codePoint <= to && builder.length() < targetChars; codePoint++) {
            if (Character.isDefined(codePoint) && Character.isValidCodePoint(codePoint)) {
                builder.appendCodePoint(codePoint);
            }
        }
    }
    

    private static float drawWrapped(IGUIGraphics guiGraphics, IFont font, String text, float x, float y, float maxWidth, int color) {
        StringBuilder lineBuilder = new StringBuilder();
        float lineWidth = 0f;
        int lineHeight = font.lineHeight();

        for (int index = 0; index < text.length(); index++) {
            char currentChar = text.charAt(index);
            float charWidth = font.width(Character.toString(currentChar));
            if (lineWidth + charWidth > maxWidth && !lineBuilder.isEmpty()) {
                guiGraphics.drawString(font, lineBuilder.toString(), x, y, color);
                y += lineHeight + LINE_GAP;
                lineBuilder.setLength(0);
                lineWidth = 0f;
            }
            lineBuilder.append(currentChar);
            lineWidth += charWidth;
        }

        if (!lineBuilder.isEmpty()) {
            guiGraphics.drawString(font, lineBuilder.toString(), x, y, color);
            y += lineHeight + LINE_GAP;
        }
        return y;
    }
}
