package App;

public class Process implements Runnable {
    private static int counter = 0;
    private int id;
    private final int time;
    private final int flow;

    public Process(int time, int flow) {
        this.time = time;
        this.flow = flow;
        setId();
    }

	// set unique id to any process
    private synchronized void setId() {  
        id = counter++;
    }

    @Override
    public void run() {
    }

    public int getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

	// CPU generator flow id
    public int getFlow() {  
        return flow;
    }

    @Override
    public String toString() {
        return String.format("CPU: %2d flow: %2d tact: %4d",id,flow,time);

    }
}
