package memory;

public class Memory16 implements Memory {
    private int initialSize;
    private short[] memoryCells;
    private OverflowBehaviour overflowBehaviour;
    private boolean dynamicMemory;
    private int pointerIndex;
    private boolean dirty;

    public Memory16() {
        this(DEFAULT_MEMORY_SIZE, OverflowBehaviour.WRAP, true);
    }

    public Memory16(int size, OverflowBehaviour overflowBehaviour, boolean dynamicMemory) {
        this.initialSize = size;
        this.memoryCells = new short[size];
        this.overflowBehaviour = overflowBehaviour;
        this.dynamicMemory = dynamicMemory;
        this.pointerIndex = 0;
        this.dirty = false;
    }

    public void reinitialize() {
        this.memoryCells = new short[initialSize];
        this.pointerIndex = 0;
        this.dirty = false;
    }

    public int getCellValue(int index) {
        return memoryCells[index] & 0xFFFF;
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
        return (char) (memoryCells[pointerIndex] & 0xFFFF);
    }

    public int getValueAtPointer() {
        return memoryCells[pointerIndex];
    }

    public void setCharAtPointer(char character) {
        dirty = true;
        memoryCells[pointerIndex] = (short) character;
    }

    public boolean isDirty() {
        return dirty;
    }

    private void resizeMemory() {
        dirty = true;
        short[] newMemory = new short[memoryCells.length * 2];
        System.arraycopy(memoryCells, 0, newMemory, 0, memoryCells.length);
        memoryCells = newMemory;
    }
}
