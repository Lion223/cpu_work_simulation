package App;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CPUQueue {
    private Queue<Process> queue;
    private int maxSize;

    public CPUQueue(int capacity) {
        this.queue = new ConcurrentLinkedQueue<>();
        maxSize = capacity;
    }

    public synchronized int getSize() {
        return queue.size();
    }

    public synchronized void setMaxSize(int capacity) {
        maxSize = capacity;
    }

    public synchronized int getMaxSize() {
        return maxSize;
    }

    public synchronized void add(Process process) {
        if (queue == null || process == null) {
            throw new IllegalArgumentException();
        }
        queue.add(process);
        if (queue.size() > maxSize) {

        }
    }

    public synchronized Process remove() {
        if (queue == null || queue.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return queue.remove();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public String toString() {
        return queue.toString();
    }
}