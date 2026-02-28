import com.xkball.xklib.ui.backend.window.WidgetTestFrame;
import com.xkball.xklib.ui.layout.ScreenAxis;
import com.xkball.xklib.ui.layout.SizeParam;
import com.xkball.xklib.ui.layout.SplitParam;
import com.xkball.xklib.ui.widget.layout.SplitLayout;
import com.xkball.xklib.ui.widget.widgets.LabelField;

public class SplitLayoutTest {

    private static final String ARTICLE_LEFT = """
            The quick brown fox jumps over the lazy dog. \
            Pack my box with five dozen liquor jugs. \
            How vainly men themselves amaze to win the palm, the oak, or bays. \
            And their uncessant labours see crowned from some single herb or tree, \
            whose short and narrow verged shade does prudently their toils upbraid. \
            While all flowers and all trees do close to weave the garlands of repose. \
            Fair Quiet, have I found thee here, and Innocence, thy sister dear? \
            Mistaken long, I sought you then in busy companies of men. \
            Your sacred plants, if here below, only among the plants will grow. \
            Society is all but rude, to this delicious solitude. \
            How well the skilful gard'ner drew of flowers and herbs this dial new. \
            Where from above the milder sun does through a fragrant zodiac run. \
            And, as it works, the industrious bee computes its time as well as we. \
            How could such sweet and wholesome hours be reckoned but with herbs and flowers.""";

    private static final String ARTICLE_RIGHT = """
            In the beginning God created the heavens and the earth. \
            Now the earth was formless and empty, darkness was over the surface of the deep, \
            and the Spirit of God was hovering over the waters. \
            And God said, let there be light, and there was light. \
            God saw that the light was good, and he separated the light from the darkness. \
            God called the light day, and the darkness he called night. \
            And there was evening, and there was morning, the first day. \
            And God said, let there be a vault between the waters to separate water from water. \
            So God made the vault and separated the water under the vault from the water above it. \
            And it was so. God called the vault sky. \
            And there was evening, and there was morning, the second day. \
            And God said, let the water under the sky be gathered to one place, \
            and let dry ground appear. And it was so. \
            God called the dry ground land, and the gathered waters he called seas. \
            And God saw that it was good.""";

    public static void main(String[] args) {
        horizontalSplitDemo();
    }

    public static void horizontalSplitDemo() {
        new WidgetTestFrame(() -> {
            var split = new SplitLayout(new SplitParam.Builder()
                    .axis(ScreenAxis.HORIZONTAL)
                    .firstSize(SizeParam.parse("1"))
                    .secondSize(SizeParam.parse("1"))
                    .build());

            var left = new LabelField(ARTICLE_LEFT, 20, 0xFF000000);
            var right = new LabelField(ARTICLE_RIGHT, 20, 0xFF000000);

            split.setFirst(left);
            split.setSecond(right);
            return split;
        }).run();
    }
}
