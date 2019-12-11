package App;

public class ProcessFlow implements Runnable {
    private static int counter = 0;
    private final int id = counter++;
    private static final int MIN_TIME_TO_NEXT = 10;
    private static final int MAX_TIME_TO_NEXT = 500;
    private final int n;
    private CPUQueue queue;
    private boolean finished;

    public ProcessFlow(int n) {
        if(n <= 0) {
            throw new IllegalArgumentException();
        }
        this.n = n;

    }

    public void setQueue(CPUQueue queue) {
        if(queue == null ) {
            throw new IllegalArgumentException();
        }
        this.queue = queue;
    }

    public Process genProcess() {
        int timeToNext = (int) (Math.random() * (MAX_TIME_TO_NEXT - MIN_TIME_TO_NEXT) + MIN_TIME_TO_NEXT);
        return new Process(timeToNext, id);
    }

    @Override
    public void run() {
        System.out.println(this + " has started");
        if(queue == null) {
            throw new IllegalArgumentException();
        }
        try {
            for (int i = 0; i < n; i++) {
                Process p = genProcess();
                queue.add(p);
                System.out.println(String.format("%s, %s tact: %4d",p,this, p.getTime()));

                Thread.sleep(p.getTime());
            }


        } catch (InterruptedException e) {
            System.out.println(this + " has stopped");
        }
        System.out.println(this + " has ended");
    }

    public synchronized int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Flow: %2d",id);
    }
}
