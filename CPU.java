package App;

public class CPU implements Runnable {
    private static final int MIN_DURATION = 50;
    private static final int MAX_DURATION = 250;
    private static final int TIME_SLOT = 1000;

    private static int counter = 0;
    private int processes = 0;

    private final int id = counter++;
    private final int time;
    private boolean busy;
    private Process process;
    private Process lost;

    public CPU() {
        this.time = (int) (Math.random() * (MAX_DURATION - MIN_DURATION) + MIN_DURATION);
        busy = false;
    }

    public synchronized void setTask(Process p) {
        setProcess(p);
        setBusy(true);
        processes++;
    }

    public synchronized void setProcess(Process process) {
        this.process = process;
    }

    public synchronized void setBusy(boolean busy) {
        this.busy = busy;
    }

    public synchronized boolean isBusy() {
        return busy;
    }

    public synchronized int getProcessCount() {
        return processes;
    }

    public synchronized void setLost(Process p) {
        lost = p;
    }

    public synchronized Process getLost() {
        return lost;
    }

    public synchronized Process getProcess() {
        if (busy && process != null) {
            return process;
        }
        return null;
    }

    @Override
    public void run() {
        System.out.println(this + " has started");
        while (!Thread.interrupted()) {
            try {
                if (busy) {
                    if (process == null) {
                        throw new IllegalArgumentException("CPU wrong work:" + this);
                    }
                    System.out.println(this + " has started work on the process:" + process);
					// simulates processing
                    Thread.sleep(time);	
                    System.out.println(this + " has ended work on the process:" + process);
					// remove process
                    setProcess(null);	
                    busy = false;
                }
                tick();
                System.out.println(this + " tact");
            } catch (InterruptedException e) {
                if (process == null) { 
                    System.out.println(this + " request to exit");
                    break;
                }
                setLost(process);
            }
        }
        System.out.println(this + " has ended");
    }

    private void tick() throws InterruptedException {
        Thread.sleep(TIME_SLOT);
    }

    @Override
    public String toString() {
        return "CPU: " + id +
                " tact: " + time;
    }
}
