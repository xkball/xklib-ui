import com.xkball.xklib.utils.TickHelper;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TickHelperTest {

    @Test
    void tickEnforcesTpsRoughly() {
        TickHelper helper = new TickHelper(10.0);
        helper.tick();
        long start = System.nanoTime();
        helper.tick();
        helper.tick();
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000L;
        assertTrue(elapsedMillis >= 150L, "elapsedMillis=" + elapsedMillis);
    }

    @Test
    void invalidTpsThrows() {
        assertThrows(IllegalArgumentException.class, () -> new TickHelper(0.0));
    }

    @Test
    void tickDoesNotWaitWhenBehind() {
        TickHelper helper = new TickHelper(20.0);
        helper.tick();
        LockSupport.parkNanos(150_000_000L);

        long start = System.nanoTime();
        helper.tick();
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000L;

        assertTrue(elapsedMillis < 100L, "elapsedMillis=" + elapsedMillis);
    }
}
