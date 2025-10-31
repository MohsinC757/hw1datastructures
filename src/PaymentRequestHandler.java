import java.time.LocalDateTime;
import java.time.Duration;

public class PaymentRequestHandler {

    private double[] accounts;
    private double overDraftFee;
    private int processRate;

    // arrays used for heap
    private int[] requestAccount;
    private double[] requestValue;
    private int requestCount;


    private LocalDateTime latestTime;
    // private method implementations for heap


    private void insertRequest(int account, double value) {

        if (requestCount >= requestValue.length) {
            int newSize = requestValue.length * 2;
            double[] newValues = new double[newSize];
            int[] newAccounts = new int[newSize];

            for (int i = 0; i < requestCount; i++) {

                newValues[i] = requestValue[i];
                newAccounts[i] = requestAccount[i];
            }
            requestValue = newValues;
            requestAccount = newAccounts;}
        // inserts new element through heap
        requestValue[requestCount] = value;
        requestAccount[requestCount] = account;
        heapInsertion(requestCount);
        requestCount++;
    }


    private void pop() {
        if (requestCount == 0)
        {return;}
        requestValue[0] = requestValue[requestCount - 1];
        requestAccount[0] = requestAccount[requestCount - 1];
        requestCount--;
        heapRemover(0);
    }

    /*after inserting a new account along with its request, the insertion method checks up through the parents and
    swaps the parent and the given inserted element if the element is greater
    */
    private void heapInsertion(int i) {
        while (i > 0) {int parent = (i - 1) / 2;
            if (requestValue[i] <= requestValue[parent]) break;
            swap(i, parent);
            i = parent;
        }
    }

    /* This heaps sorts and checks the arrays through its children after removing the max element(which is
    requestvalue(0). It make sure that the children are smaller than its parent in this specific heap(since the
    bank wants it to be sorted from greatest to least for more overdrafts). Looking back in hindsight about the exam
    I didn't have good practice in terms of heaps other than how it works conceptually
     and this wouldve really helped on the last question especially with this array implementation.
         */
    private void heapRemover(int i) {
        while (true) {int left = 2 * i + 1;
            int right = 2 * i + 2;
            int max = i;
            if (left < requestCount)
            {
                if(requestValue[left] > requestValue[max]){
                    max = left;}
            }
            if (right < requestCount ) {
                if(requestValue[right] > requestValue[max]){
                    max = right;}
            }

            if (max == i) break;
            swap(i, max);
            i = max;
        }
    }
    //basic swap method using temp calues
    private void swap(int i, int j)
    {
        double tempVal = requestValue[i];
        requestValue[i] = requestValue[j];
        requestValue[j] = tempVal;
        int temporaryAcc = requestAccount[i];
        requestAccount[i] = requestAccount[j];
        requestAccount[j] = temporaryAcc;
    }


    //request handler constructor and public methods

    public PaymentRequestHandler(int processRate, double overdraftFee, int numAccounts) {
        accounts = new double[numAccounts];
        this.overDraftFee = overdraftFee;
        this.processRate = processRate;
        this.requestAccount = new int[100];
        this.requestValue = new double[100];
        this.requestCount = 0;
        this.latestTime = BankTime.now();
    }

    public double deposit(int account, double amount) {
        accounts[account] += amount;
        return accounts[account];
    }

    public double getBalance(int account) {
        return accounts[account];
    }

    public boolean submitRequest(int account, double value) {
        processAllRequests();


        if (accounts[account] < 0) return false;

        insertRequest(account, value); // insert into heap
        return true;
    }

    private void processAllRequests() {
        LocalDateTime now = BankTime.now();
        Duration elapsed = Duration.between(latestTime, now);
        int canProcess = (int) (elapsed.toMillis() / 1000.0 * processRate);

        if (canProcess > 0) {
            processRequests(canProcess);
            latestTime = now;
        }
    }


    private void processRequests(int count) {
        int toProcess = Math.min(count, requestCount);
        for (int i = 0; i < toProcess; i++) {
            int acc = requestAccount[0];
            double val = requestValue[0];
            pop();
            accounts[acc] -= val;
            if (accounts[acc] < 0) {
                accounts[acc] -= overDraftFee;
            }
        }
    }


    public Duration processRemaining() {
        LocalDateTime now = BankTime.now();

        int remaining = requestCount;
        Duration totalTime = Duration.ofSeconds(remaining / processRate);
        processRequests(remaining);
        latestTime = now.plus(totalTime);
        return totalTime;
    }


}