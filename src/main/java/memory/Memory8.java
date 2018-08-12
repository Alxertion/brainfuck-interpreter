package memory;

public class Memory8 implements Memory {
    private byte[] memoryCells;
    private OverflowBehaviour overflowBehaviour;
    private boolean dynamicMemory;
    private int pointerIndex;

    public Memory8() {
        this(DEFAULT_MEMORY_SIZE, OverflowBehaviour.WRAP, true);
    }

    public Memory8(int size, OverflowBehaviour overflowBehaviour, boolean dynamicMemory) {
        this.memoryCells = new byte[size];
        this.overflowBehaviour = overflowBehaviour;
        this.dynamicMemory = dynamicMemory;
        this.pointerIndex = 0;
    }

    public int getCellValue(int index) {
        return memoryCells[index] & 0xFF;
    }

    public int getPointerIndex() {
        return pointerIndex;
    }

    public void movePointerLeft() {
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
        pointerIndex++;
        if (pointerIndex == memoryCells.length) {
            if (dynamicMemory) {
                resizeMemory();
            } else {
                throw new MemoryError("Memory overflow (went past index " + (memoryCells.length - 1) + ")!");
            }
        }
    }

    private void resizeMemory() {
        byte[] newMemory = new byte[memoryCells.length * 2];
        System.arraycopy(memoryCells, 0, newMemory, 0, memoryCells.length);
        memoryCells = newMemory;
    }

    public void incrementAtPointer() {
        memoryCells[pointerIndex]++;
    }

    public void decrementAtPointer() {
        memoryCells[pointerIndex]--;
    }

    public char getCharAtPointer() {
        return (char) (memoryCells[pointerIndex] * 0xFF);
    }

    public void setCharAtPointer(char character) {
        memoryCells[pointerIndex] = (byte) character;
    }
}
