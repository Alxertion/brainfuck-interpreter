package memory;

public class Memory8 implements Memory {
    byte[] memoryCells;

    private Memory8() {
        this(DEFAULT_MEMORY_SIZE);
    }

    private Memory8(int size) {
        memoryCells = new byte[size];
    }
}
