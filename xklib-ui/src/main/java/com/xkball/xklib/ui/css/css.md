# 要求实现的功能:

## 解析器, 从String解析到CascadingStyleSheets对象, 
提供antlr实现的css3Lexer和css3Parser和css3ParserBaseListener, 
实现在com.xkball.xklib.antlr.css包下,
需要支持多线程调用

## 选择器, 实现在com.xkball.xklib.ui.css.selector包下:
- `*`
- 按id选择 `IGuiWidget#getCSSId` `#foo`
- 按class选择 `IGuiWidget#getCSSClassName` `.foo`
- 按元素类型选择 `IGuiWidget#getCSSType` `foo`
- 后代选择器 `IGuiWidget#getChildren` `foo bar`
- 子元素选择器 `foo>bar`
- 相邻元素选择器 `foo+bar`
- 后续元素选择器 `foo~bar`
- 组合选择器 `foo,bar`
- 伪类/元素, 仅支持 `:hover` `:focus`(对应`IGuiWidget#isPrimaryFocused`!) `:first-child` `:last-child` `:before` `:after`

## 属性, 实现在com.xkball.xklib.ui.css.property包下:
- TaffyStyle类中元素对应的属性
- background-color
- background-image 
- color 文本颜色
- text-align
- line-height 
- text-shadow
- border-style 和其上下左右分别指定版本 只允许 none 和 solid
- border-width 和其上下左右分别指定版本
- border-color 和其上下左右分别指定版本

## 属性值
- rl(), 用于获得资源时, 将其值转为ResourceLocation再通过ResourceManager或者TextureManager获得.
- 颜色: 16色颜色名, #或者0x表示的16进制(自动判断是否包含A通道), rgb(), rgba(), 其他的不支持
- 长度单位: px, %, em(总是等效于16px), 其他的不支持
- 函数 min() max()
- 对于grid布局: repeat() minmax()

# 不实现的功能:

渐变
字体
过渡
动画
媒体选择
属性选择器

## 属性值
- !important
- url() 离线程序,不使用网络,获取文件使用rl()

遇到不实现的功能不应该爆炸, 在日志里warn.