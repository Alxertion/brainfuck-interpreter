package memory;

public class Memory8 implements Memory {
    private int initialSize;
    private byte[] memoryCells;
    private final OverflowBehaviour overflowBehaviour;
    private final boolean dynamicMemory;
    private int pointerIndex;
    private boolean dirty;

    public Memory8() {
        this(DEFAULT_MEMORY_SIZE, OverflowBehaviour.WRAP, true);
    }

    public Memory8(int size, OverflowBehaviour overflowBehaviour, boolean dynamicMemory) {
        this.initialSize = size;
        this.memoryCells = new byte[size];
        this.overflowBehaviour = overflowBehaviour;
        this.dynamicMemory = dynamicMemory;
        this.pointerIndex = 0;
        this.dirty = false;
    }

    public void reinitialize() {
        this.memoryCells = new byte[initialSize];
        this.pointerIndex = 0;
        this.dirty = false;
    }

    public int getCellValue(int index) {
        return memoryCells[index] & 0xFF;
    }

    public int getPointerIndex() {
        return pointerIndex;
    }

    public void movePointerLeft() {
        dirty = true;
        pointerIndex--;
        if (pointerIndex < 0) {
            if (overflowBehaviour == OverflowBehaviour.WRAP) {
                pointerIndex = memoryCells.length - 1;
            } else {
                throw new MemoryError("Memory underflow (went past index 0)!");
            }
        }
    }

    public void movePointerRight() {
        dirty = true;
        pointerIndex++;
        if (pointerIndex == memoryCells.length) {
            if (dynamicMemory) {
                resizeMemory();
            } else {
                throw new MemoryError("Memory overflow (went past index " + (memoryCells.length - 1) + ")!");
            }
        }
    }

    public void incrementAtPointer() {
        dirty = true;
        memoryCells[pointerIndex]++;
    }

    public void decrementAtPointer() {
        dirty = true;
        memoryCells[pointerIndex]--;
    }

    public char getCharAtPointer() {
        return (char) (memoryCells[pointerIndex] * 0xFF);
    }

    @Override
    public int getValueAtPointer() {
        return memoryCells[pointerIndex];
    }

    public void setCharAtPointer(char character) {
        dirty = true;
        memoryCells[pointerIndex] = (byte) character;
    }

    public boolean isDirty() {
        return dirty;
    }

    private void resizeMemory() {
        dirty = true;
        byte[] newMemory = new byte[memoryCells.length * 2];
        System.arraycopy(memoryCells, 0, newMemory, 0, memoryCells.length);
        memoryCells = newMemory;
    }
}
