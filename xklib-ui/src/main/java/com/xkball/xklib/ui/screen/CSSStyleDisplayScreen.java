package com.xkball.xklib.ui.screen;

import com.xkball.xklib.ap.annotation.GuiWidgetClass;
import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.antlr.css.CssStyles;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.TextEdit;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

public class CSSStyleDisplayScreen extends SplitContainer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CSSStyleDisplayScreen.class);
    private static final String RESOURCE_PATH = "META-INF/services/" + GuiWidgetClass.class.getName();

    protected interface SearchTextListener {
        void onSearchTextChanged(String text);
    }

    protected interface SearchInput {
        Widget widget();
        String text();
    }
    
    private final List<String> allWidgetClassNames;
    
    private String selectedClassName;
    
    private final SearchInput searchBox;
    private final ContainerWidget widgetListContainer;

    private final SearchInput styleSearchBox;
    private final ContainerWidget styleListContainer;
    private final List<String> allStyleNames;
    private String selectedStyleName;

    private final Label selectedClassLabel;
    private final Label styleNameLabel;
    private final Label styleTypeLabel;
    private final Label styleDefaultValueLabel;
    private final Label styleEnumValuesLabel;
    
    public CSSStyleDisplayScreen() {
        super(false, 3);
        this.allWidgetClassNames = loadRegisteredWidgetClasses();
        this.allStyleNames = loadAllStyleNames();

        this.widgetListContainer = new ContainerWidget()
                .setCSSClassName("list")
                .addDynamicContent(this::buildWidgetListContent);

        this.searchBox = this.createSearchInput("searchBox", _ -> this.refreshWidgetList());

        this.styleSearchBox = this.createSearchInput("styleSearchBox", _ -> this.refreshStyleList());
        this.styleListContainer = new ContainerWidget()
                .setCSSClassName("list")
                .addDynamicContent(this::buildStyleListContent);

        this.selectedClassLabel = new Label(Objects.toString(this.selectedClassName, ""));
        this.styleNameLabel = new Label("").setCSSClassName("detailLabel");
        this.styleTypeLabel = new Label("").setCSSClassName("detailLabel");
        this.styleDefaultValueLabel = new Label("").setCSSClassName("detailLabel");
        this.styleEnumValuesLabel = new Label("").setCSSClassName("detailLabel");

        this.inlineStyle("size: 100% 100%;").asRootStyle("""
                .searchBox {
                    border-color: 0xFF334155;
                    border: 1rpx 1rpx;
                    size: 100% 50rpx;
                }
                .styleSearchBox {
                    border-color: 0xFF334155;
                    border: 1rpx 1rpx;
                    size: 100% 50rpx;
                }
                .list {
                    display: flex;
                    flex-direction: column;
                    overflow-x: scroll;
                    overflow-y: scroll;
                    scrollbar-width: 8;
                }
                .detailLabel {
                    text-color: -1;
                    text-align: left;
                    text-drop-shadow: false;
                    size: 100% 18rpx;
                    text-height: 10rpx;
                }
                .listButtonNormal {
                    size: 100% 18rpx;
                    text-align: left;
                    flex-shrink: 0;
                    text-drop-shadow: false;
                    text-color: -1;
                    text-height: 10rpx;
                }
                .listButtonSelected {
                    size: 100% 18rpx;
                    text-align: left;
                    background-color: 0xAA334155;
                    flex-shrink: 0;
                    text-drop-shadow: false;
                    text-color: -1;
                    text-height: 10rpx;
                }
                """);
        this.setRatio(0, 0.25f)
            .setRatio(1, 0.5f)
            .setPanel(0, new ContainerWidget()
                    .inlineStyle("flex-direction: column;")
                    .addChild(this.searchBox.widget())
                    .addChild(this.widgetListContainer))
            .setPanel(1, new ContainerWidget()
                    .inlineStyle("flex-direction: column;")
                    .addChild(this.selectedClassLabel)
                    .addChild(this.styleSearchBox.widget())
                    .addChild(this.styleListContainer))
            .setPanel(2, new ContainerWidget()
                    .inlineStyle("flex-direction: column;")
                    .addChild(this.styleNameLabel)
                    .addChild(this.styleTypeLabel)
                    .addChild(this.styleDefaultValueLabel)
                    .addChild(this.styleEnumValuesLabel));

        this.selectStyle("");
    }

    protected SearchInput createSearchInput(String cssClassName, SearchTextListener listener) {
        return new SearchTextEdit(cssClassName, listener);
    }
    
    private void refreshWidgetList() {
        this.widgetListContainer.updateDynamicContent();
    }

    private void refreshStyleList() {
        this.styleListContainer.updateDynamicContent();
    }
    
    private void select(String className) {
        this.selectedClassName = className;
        this.selectedClassLabel.setText(simpleName(Objects.toString(this.selectedClassName, "")));
        this.refreshWidgetList();
        this.refreshStyleList();
        this.refreshStyleDetails();
    }
    
    private void buildWidgetListContent(ContainerWidget container) {
        var query = normalizeQuery(this.searchBox.text());
        var items = this.allWidgetClassNames.stream()
                .filter(n -> matches(n, query))
                .toList();
        
        for (var className : items) {
            var item = new Button(simpleName(className), () -> this.select(className));
            item.setCSSClassName(Objects.equals(this.selectedClassName, className) ? "listButtonSelected" : "listButtonNormal");
            container.addChild(item);
        }
    }

    private void buildStyleListContent(ContainerWidget container) {
        var query = normalizeQuery(this.styleSearchBox.text());
        var selectedWidgetClass = tryLoadSelectedWidgetClass();

        var items = this.allStyleNames.stream()
                .filter(n -> matches(n, query))
                .filter(n -> isStyleApplicableToSelected(n, selectedWidgetClass))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        for (var styleName : items) {
            var btn = new Button(styleName, () -> this.selectStyle(styleName));
            btn.setCSSClassName(Objects.equals(this.selectedStyleName, styleName) ? "listButtonSelected" : "listButtonNormal");
            container.addChild(btn);
        }
    }

    private void selectStyle(String styleName) {
        this.selectedStyleName = styleName == null ? "" : styleName;
        this.refreshStyleList();
        this.refreshStyleDetails();
    }

    private void refreshStyleDetails() {
        var styleName = Objects.toString(this.selectedStyleName, "");
        if (styleName.isBlank()) {
            this.styleNameLabel.setText("");
            this.styleTypeLabel.setText("");
            this.styleDefaultValueLabel.setText("");
            this.styleEnumValuesLabel.setText("");
            return;
        }

        var data = CssStyles.INSTANCE.styleMap.get(styleName);
        if (data == null) {
            this.styleNameLabel.setText("name: " + styleName);
            this.styleTypeLabel.setText("type: ");
            this.styleDefaultValueLabel.setText("default: ");
            this.styleEnumValuesLabel.setText("values: ");
            return;
        }

        var typeName = data.clazz() == null ? "" : data.clazz().getSimpleName();
        var defaultValue = data.defaultValue() == null ? "" : String.valueOf(data.defaultValue());
        var enumValues = data.enumValues() == null ? "" : data.enumValues().stream().sorted(String.CASE_INSENSITIVE_ORDER).toList().toString();

        this.styleNameLabel.setText("name: " + styleName);
        this.styleTypeLabel.setText("type: " + typeName);
        this.styleDefaultValueLabel.setText("default: " + defaultValue);
        this.styleEnumValuesLabel.setText("values: " + enumValues);
    }

    private Class<?> tryLoadSelectedWidgetClass() {
        if (this.selectedClassName == null || this.selectedClassName.isBlank()) return null;
        try {
            return Class.forName(this.selectedClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Cannot load selected widget class.", e);
            return null;
        }
    }

    private static List<String> loadAllStyleNames() {
        return CssStyles.INSTANCE.styleMap.keySet().stream()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private static boolean isStyleApplicableToSelected(String styleName, Class<?> selectedWidgetClass) {
        if (selectedWidgetClass == null) return true;
        var data = CssStyles.INSTANCE.styleMap.get(styleName);
        if (data == null) return true;
        Predicate<?> filter = data.widgetFilter();
        if (filter == CssStyles.ANY_WIDGET) return true;
        var filterClass = tryExtractCapturedClass(filter);
        if (filterClass == null) return true;
        return filterClass.isAssignableFrom(selectedWidgetClass);
    }

    private static Class<?> tryExtractCapturedClass(Object lambda) {
        try {
            for (var f : lambda.getClass().getDeclaredFields()) {
                if (f.getType() == Class.class) {
                    f.setAccessible(true);
                    return (Class<?>) f.get(lambda);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private static String simpleName(String className) {
        int idx = className.lastIndexOf('.');
        if (idx < 0 || idx + 1 >= className.length()) return className;
        return className.substring(idx + 1);
    }
    
    private static boolean matches(String className, String query) {
        if (query == null || query.isBlank()) return true;
        return className.toLowerCase(Locale.ROOT).contains(query);
    }
    
    private static String normalizeQuery(String text) {
        if (text == null) return "";
        return text.trim().toLowerCase(Locale.ROOT);
    }
    
    private static List<String> loadRegisteredWidgetClasses() {
        var input = CSSStyleDisplayScreen.class.getClassLoader().getResourceAsStream(RESOURCE_PATH);
        if (input == null) return List.of();
        
        try (input) {
            var content = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            var lines = content.split("\n");
            
            var list = new ArrayList<String>();
            for (var line : lines) {
                var name = line.strip();
                if (name.isEmpty()) continue;
                list.add(name);
            }
            
            list.sort(Comparator.naturalOrder());
            return list;
        } catch (IOException e) {
            LOGGER.error("Failed to read registered widget classes.", e);
            return List.of();
        }
    }
    
    private static final class SearchTextEdit extends TextEdit implements SearchInput {
        
        private final SearchTextListener listener;
        
        private SearchTextEdit(String cssClassName, SearchTextListener listener) {
            super("");
            this.listener = listener;
            this.setAllowEdit(true);
            this.setMultiLine(false);
            this.setWrapLine(false);
            this.setCSSClassName(cssClassName);
        }
        
        @Override
        protected boolean onKeyPressed(IKeyEvent event) {
            var handled = super.onKeyPressed(event);
            if (handled) {
                this.listener.onSearchTextChanged(this.getText());
            }
            return handled;
        }
        
        @Override
        protected boolean onCharTyped(ICharEvent event) {
            var handled = super.onCharTyped(event);
            if (handled) {
                this.listener.onSearchTextChanged(this.getText());
            }
            return handled;
        }

        @Override
        public Widget widget() {
            return this;
        }

        @Override
        public String text() {
            return this.getText();
        }
    }
    
}
