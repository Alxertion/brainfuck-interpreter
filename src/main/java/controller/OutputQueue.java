package controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

public class OutputQueue extends OutputStream {
    private final BlockingQueue<Character> blockingQueue;

    public OutputQueue(BlockingQueue<Character> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void write(int i) throws IOException {
        try {
            blockingQueue.put((char) i);
        } catch (InterruptedException ignored) {
        }
    }
}
