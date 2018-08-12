package memory;

public interface Memory {
    int DEFAULT_MEMORY_SIZE = 30000;
    void reinitialize();
    int getCellValue(int index);
    int getPointerIndex();
    void movePointerLeft();
    void movePointerRight();
    void incrementAtPointer();
    void decrementAtPointer();
    char getCharAtPointer();
    int getValueAtPointer();
    void setCharAtPointer(char character);
    boolean isDirty();
}
