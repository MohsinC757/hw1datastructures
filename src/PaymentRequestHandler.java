import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.Duration;

public class PaymentRequestHandler {

    /** Initialize a new request handler capable of processing processRate
     * requests per second. */
    private double[] accounts;
    private double overDraftFee;
    private int processRate;
    private int[] requestAccount;
    private double[] requestValue;
    private int requestCount;
    private LocalDateTime latestTime;

    public PaymentRequestHandler(int processRate, double overdraftFee, int numAccounts) {
        accounts = new double[numAccounts];
        this.overDraftFee= overdraftFee;
        this.processRate = processRate;

        this.requestAccount = new int[100];
        this.requestValue = new double[100];
        this.requestCount = 0;

        this.latestTime = BankTime.now();

    }

    /** Deposit some an amount of money into a single account. */
    public double deposit(int account, double amount) {
        accounts[account]+= amount;
        return 0.0;
    }

    /** Get balance of a single account. */
    public double getBalance(int account) {
        return accounts[account];
    }

    /** Submit a request for processing and process as many requests as
     * possible since the last time a request was submitted. Return false
     * if the user has a negative balance and the request is rejected.
     * Otherwise return true.
     *
     * NOTE:
     * Because we are simulating the passage of time, any amount of
     * simulated time might pass in between instructions. To create the
     * illusion of processing processRate requests per second, we must
     * check the time "elapsed" and process all requests that *would* have
     * been processed in that time.
     * */
    public boolean submitRequest(int account, double value) {

        processAllRequests();
        if (accounts[account] < 0) {
            return false;
        }
        requestAccount[requestCount] = account;
        requestValue[requestCount] = value;
        requestCount++;

        return true;
    }


    private void processAllRequests(){
        LocalDateTime now = BankTime.now();
        Duration elapsed = Duration.between(latestTime, now);


        int canProcess =  (int)(elapsed.toMillis() / 1000.0 * processRate);
        if (canProcess > 0) {
             processRequests(canProcess);
            latestTime = now;
        }
    }
      private void processRequests(int count){
        for(int i =0;i<requestCount;i++){
            int max = i;
            for(int j = i+1; j<requestCount;j++){
                if(requestValue[j] > requestValue[max]){
                    max = j;
                }
            }
           double temp = requestValue[i];
            requestValue[i] = requestValue[max];
            requestValue[max] = temp;

            int tempAcc = requestAccount[i];
            requestAccount[i] = requestAccount[max];
            requestAccount[max] = tempAcc;
        }

        int toProcess = Math.min(count, requestCount);
        for (int i = 0; i < toProcess; i++) {
            int acc = requestAccount[i];
            double val = requestValue[i];

            accounts[acc] -= val;

            if (accounts[acc] < 0) {
                accounts[acc] -= overDraftFee; // overdraft penalty
            }
        }

        // Remove processed requests from queue
        int remaining = requestCount - toProcess;
        for (int i = 0; i < remaining; i++) {
            requestAccount[i] = requestAccount[i + toProcess];
            requestValue[i] = requestValue[i + toProcess];
        }
        requestCount = remaining;

    }
    /** Process all remaining requests in the queue and return the time
     * required to process from when this method is called.
     */
    public Duration processRemaining() {
        LocalDateTime now = BankTime.now();
        processRequests(requestCount);
        Duration totalTime = Duration.ofSeconds((long) Math.ceil((double) requestCount / processRate));
        latestTime = now.plus(totalTime);
        return totalTime;
    }


}