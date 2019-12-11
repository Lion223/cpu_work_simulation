package App;

public class App {
    private static final int NUMBER_OF_CPU = 2;
    private static final int NUMBER_OF_FLOWS = 2;
    private static final int NUMBER_OF_QUEUE = 2;
    private static final int NUMBER_OF_PROCESS = 4;
    private static final int NUMBER_OF_CAPACITY = 2;

    private final CPU[] cpus;
    private final ProcessFlow[] flows;
    private final CPUQueue[] queues;
    private final Thread[] flowThreads;
    private final Thread[] cpuThreads;
    private int processes;

    public App(int nCPU, int nFlow, int nQueue, int nCapacity, int nProcesses) {
        if (nCPU <= 0 || nFlow <= 0 || nQueue <= 0 || nProcesses <= 0 || nCapacity <= 0) {
            throw new IllegalArgumentException();
        }

        cpus = new CPU[nCPU];
        cpuThreads = new Thread[nCPU];
        for (int i = 0; i < nCPU; i++) {
            cpus[i] = new CPU();
            cpuThreads[i] = new Thread(cpus[i]);
        }

        flows = new ProcessFlow[nFlow];
        flowThreads = new Thread[nFlow];
        for (int i = 0; i < nFlow; i++) {
            flows[i] = new ProcessFlow(nProcesses);
            flowThreads[i] = new Thread(flows[i]);
        }

        this.queues = new CPUQueue[nQueue];
        for (int i = 0; i < nQueue; i++) {
            queues[i] = new CPUQueue(nCapacity);
        }
        processes = nProcesses * 2;
    }

    private boolean isAlive() {
        for (int i = 0; i < flowThreads.length; i++) {
            if (flowThreads[i].isAlive()) {
                return true;
            }
        }
        return false;
    }

    public App(int nProcesses) {
        this(NUMBER_OF_CPU, NUMBER_OF_FLOWS, NUMBER_OF_QUEUE, NUMBER_OF_CAPACITY, nProcesses);
    }

    private static boolean isAnyFlowAlive(App ss) {
        for (int i = 0; i < ss.flowThreads.length; i++) {
            if (ss.flowThreads[i].isAlive()) {
                return true;
            }
        }
        return false;
    }
    private static boolean isAnyCPUAlive(App ss) {
        for (int i = 0; i < ss.cpuThreads.length; i++) {
            if (ss.cpuThreads[i].isAlive()) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        App ss = new App(NUMBER_OF_PROCESS);

        for (int i = 0, k = 1; i < ss.flows.length && k < ss.flows.length; i++, k++) {
            ss.flows[i].setQueue(ss.queues[0]);
            ss.flows[k].setQueue(ss.queues[1]);
        }
        for (int i = 0; i < ss.cpuThreads.length; i++) {
            ss.cpuThreads[i].start();
        }

        for (int i = 0; i < ss.flowThreads.length; i++) {
            ss.flowThreads[i].start();
        }

        Process p = null;
        CPU cpu0 = ss.cpus[0];
        CPU cpu1 = ss.cpus[1];
        CPUQueue queue1 = ss.queues[0];
        CPUQueue queue2 = ss.queues[1];

        while(ss.isAlive() || (!queue1.isEmpty() || !queue2.isEmpty())){
            if(!queue1.isEmpty() || !queue2.isEmpty()){
                if(!queue1.isEmpty())
                    p = queue1.remove();
                else if(!queue2.isEmpty())
                    p = queue2.remove();


                if(queue1.getMaxSize() == queue1.getSize() || queue2.getMaxSize() == queue2.getSize()){
                    putWorkToCPU(cpu0, queue1, queue2, p);
                }
                else{
                    putWorkToCPU(cpu1, queue1, queue2, p);
                }
            }
        }

        System.out.println("Work is done");
        while(isAnyCPUAlive(ss)) {
            for (int i = 0; i < ss.cpuThreads.length; i++) {
                if(!ss.cpus[0].isBusy()) {
                    ss.cpuThreads[0].interrupt();
                }
                if(!ss.cpus[1].isBusy()) {
                    ss.cpuThreads[1].interrupt();
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
        }

        System.out.println("\nResults:");
        System.out.println("Process count: "+ss.processes);
        System.out.println("CPU " + cpu0 + " has worked out " + cpu0.getProcessCount() + " processes "
                + String.format("(%.1f%%)",100*(double)cpu0.getProcessCount()/ss.processes));
        System.out.println("CPU " + cpu1 + " has worked out " + cpu1.getProcessCount() + " processes "
                + String.format("(%.1f%%)",100*(double)cpu1.getProcessCount()/ss.processes));

    }

    private static void putWorkToCPU(CPU cpu, CPUQueue queue1, CPUQueue queue2, Process p){
        if(!cpu.isBusy()){
            cpu.setTask(p);
            System.out.println(p + " was taken for processing " + cpu);
        }
        else{
            if(queue1.getMaxSize() != queue1.getSize())
                queue1.add(p);
            else if(queue2.getMaxSize() != queue2.getSize()){
                queue2.add(p);
            }
        }
    }
}