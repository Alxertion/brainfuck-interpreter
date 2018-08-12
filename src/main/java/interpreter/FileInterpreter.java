package interpreter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import memory.Memory;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class FileInterpreter {
    private final File file;
    private BufferedReader bufferedReader;
    private long elapsedTimeMilliseconds;
    private double elapsedTimeSeconds;
    private final Memory memory;
    private final String input;
    private String programCode;
    private int instructionPointer;
    private int inputPointer;
    private final BiMap<Integer, Integer> loopMap;

    public FileInterpreter(File file, Memory memory) {
        this(file, memory, "");
    }

    public FileInterpreter(File file, Memory memory, String input) {
        this.file = file;
        this.memory = memory;
        this.input = input;
        this.inputPointer = 0;
        loopMap = HashBiMap.create();
        readProgram();
        mapLoops();
    }

    private void readProgram() {
        // open the file and prepare for reading
        initializeStream();

        // execute the program
        try {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            programCode = stringBuilder.toString();
        } catch (IOException exception) {
            throw new InterpreterError("File '" + file.getName() + "' could not be closed!");
        }

        // close the file
        closeStream();
    }

    private void mapLoops() {
        Deque<Integer> beginningBracketStack = new ArrayDeque<>();
        int index = 0;
        for (char character : programCode.toCharArray()) {
            if (character == '[') {
                beginningBracketStack.push(index);
            } else if (character == ']') {
                if (beginningBracketStack.isEmpty()) {
                    throw new InterpreterError("Brackets are not matched properly (too many ']')!");
                }
                loopMap.put(beginningBracketStack.pop(), index);
            }
            index++;
        }
        if (!beginningBracketStack.isEmpty()) {
            throw new InterpreterError("Brackets are not matched properly (too many '[')!");
        }
    }

    public void run() {
        // reinitialize the program, if necessary
        if (memory.isDirty()) {
            memory.reinitialize();
        }
        instructionPointer = 0;

        // start the time measurement
        long startTime = System.currentTimeMillis();

        // execute the code
        while (instructionPointer < programCode.length()) {
            executeCurrentInstruction();
            instructionPointer++;
        }

        // end the time measurement
        long stopTime = System.currentTimeMillis();
        elapsedTimeMilliseconds = stopTime - startTime;
        elapsedTimeSeconds = elapsedTimeMilliseconds / 1000.0d;
    }

    private void executeCurrentInstruction() {
        switch (programCode.charAt(instructionPointer)) {
            case '<':
                memory.movePointerLeft();
                break;
            case '>':
                memory.movePointerRight();
                break;
            case '+':
                memory.incrementAtPointer();
                break;
            case '-':
                memory.decrementAtPointer();
                break;
            case '.':
                System.out.print(memory.getCharAtPointer());
                break;
            case ',':
                if (inputPointer < input.length()) {
                    memory.setCharAtPointer(input.charAt(inputPointer));
                    inputPointer++;
                }
                break;
            case '[':
                if (memory.getValueAtPointer() == 0) {
                    instructionPointer = loopMap.get(instructionPointer);
                }
                break;
            case ']':
                if (memory.getValueAtPointer() != 0) {
                    instructionPointer = loopMap.inverse().get(instructionPointer);
                }
                break;
        }
    }

    private void initializeStream() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException exception) {
            throw new InterpreterError("File " + file.getName() + " was not found!");
        }
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    private void closeStream() {
        try {
            bufferedReader.close();
        } catch (IOException exception) {
            throw new InterpreterError("File " + file.getName() + " could not be closed!");
        }
    }

    public long getTimeInMilliseconds() {
        return elapsedTimeMilliseconds;
    }

    public double getTimeInSeconds() {
        return elapsedTimeSeconds;
    }
}
