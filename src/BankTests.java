import test.Test;
import static test.BkTest.assertEquals;
import static test.BkTest.assertFalse;
import static test.BkTest.runSuites;

/**
 * Run a handful of tests to verify basic functionality of your class.
 * Feel free to add your own tests to ensure your project matches all
 * requirements.
 */
public class BankTests {
    private final PaymentRequestHandler handler = new PaymentRequestHandler(1, 35.00, 1);

    /**
     * Advance the simulated clock by msLater milliseconds and then submit a
     * payment request to handler.
     */
    private static boolean submitRequest(PaymentRequestHandler handler, int msLater, int account, double value) {
        BankTime.advance(msLater, java.time.temporal.ChronoUnit.MILLIS);
        return handler.submitRequest(account, value);
    }

    /**
     * Process all remaining requests and advance the simulated clock by
     * the time required for processing.
     */
    private static void processRemaining(PaymentRequestHandler handler) {
        BankTime.advance(handler.processRemaining());
    }

    /** There should be no fee if the balance stays non-negative. */
    @Test
    public void WithoutOverdraft() {
        handler.deposit(0, 100);
        submitRequest(handler, 1000, 0, 25);
        submitRequest(handler, 1000, 0, 25);
        submitRequest(handler, 1000, 0, 25);
        submitRequest(handler, 1000, 0, 25);
        processRemaining(handler);
        assertEquals(0, handler.getBalance(0));
    }

    /** If the overdrafting request is made after all other requests are
     * processed, expect only one fee. */
    @Test
    public void WithOneOverdraft() {
        handler.deposit(0, 75);
        submitRequest(handler, 1000, 0, 25);
        submitRequest(handler, 1000, 0, 25);
        submitRequest(handler, 1000, 0, 25);
        submitRequest(handler, 1000, 0, 75);
        processRemaining(handler);
        assertEquals(-110, handler.getBalance(0));
    }

    /** If multiple unpayable requests are made before any unpayable
     * request has been processed, expect a fee for each. */
    @Test
    public void WithMultipleOverdraft() {
        handler.deposit(0, 25);
        submitRequest(handler, 300, 0, 25); // This will take 1 second to
        // process
        submitRequest(handler, 300, 0, 25); // This request will be accepted
        // and incur overdraft
        submitRequest(handler, 300, 0, 25); // First request should still be
        // processing, so accept this
        // request as well and incur
        // overdraft
        processRemaining(handler);
        assertEquals(-120, handler.getBalance(0));
    }

    /** If a large request is made before smaller requests have been
     * processed, process the large request first and then charge
     * overdraft fees on all remaining small requests. */
    @Test
    public void WithUnfairOverdraft() {
        handler.deposit(0, 75);
        submitRequest(handler, 250, 0, 25); // Process for 1 second.
        submitRequest(handler, 250, 0, 25); // Large request should be
        // processed first
        submitRequest(handler, 250, 0, 25); // Large request should be
        // processed first
        submitRequest(handler, 250, 0, 75); // Process this request before
        // 2nd,3rd requests, causing
        // a total of 3 overdraft fees
        processRemaining(handler);
        assertEquals(-180, handler.getBalance(0));
    }

    /** If the balance is negative when a request is made, decline the
     * payment. */
    @Test
    public void WithRejectedPayment() {
        handler.deposit(0, 24);
        submitRequest(handler, 1000, 0, 25); // Finish processing before
        // next charge
        assertFalse(submitRequest(handler, 1000, 0, 25));
        processRemaining(handler);
        assertEquals(-36.0, handler.getBalance(0));
    }

    public static void main(String[] args) {
        runSuites("BankTests");
    }
}