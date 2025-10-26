import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;

/**
 * Simulate the passage of time in a global static context.
 */
public class BankTime {
    private static LocalDateTime time = LocalDateTime.now();

    /** Get the current simulated bank time. */
    public static LocalDateTime now() {
        return time;
    }

    /** Reset the bank time to a specific value. */
    public static void reset(LocalDateTime newTime) {
        time = newTime;
    }

    /** Advance the bank time by some amount of time. */
    public static LocalDateTime advance(long amount, ChronoUnit unit) {
        time = time.plus(amount, unit);
        return time;
    }

    /** Advance the bank time by some amount of time. */
    public static LocalDateTime advance(TemporalAmount amount) {
        time = time.plus(amount);
        return time;
    }
}