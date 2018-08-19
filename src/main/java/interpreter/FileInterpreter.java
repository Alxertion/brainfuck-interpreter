package interpreter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import memory.Memory;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

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
    private long instructionCount;
    private OutputStream outputStream;
    private volatile boolean running = false;

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

    public void run(OutputStream outputStream) {
        // starting the interpreter
        running = true;

        // reinitialize the program, if necessary
        reinitialize();
        this.outputStream = outputStream;

        // start the time measurement
        long startTime = System.currentTimeMillis();

        // execute the code
        while (instructionPointer < programCode.length()) {
            executeCurrentInstruction();
            instructionPointer++;
            instructionCount++;
        }

        // end the time measurement
        long stopTime = System.currentTimeMillis();
        elapsedTimeMilliseconds = stopTime - startTime;
        elapsedTimeSeconds = elapsedTimeMilliseconds / 1000.0d;
        running = false;
    }

    public long getTimeInMilliseconds() {
        return elapsedTimeMilliseconds;
    }

    public double getTimeInSeconds() {
        return elapsedTimeSeconds;
    }

    public long getInstructionCount() {
        return instructionCount;
    }

    public String getFormattedInstructionCount() {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        return formatter.format(instructionCount);
    }

    private void reinitialize() {
        if (memory.isDirty()) {
            memory.reinitialize();
        }
        instructionPointer = 0;
        instructionCount = 0;
    }

    private void readProgram() {
        // open the file and prepare for reading
        initializeStream();

        // execute the program
        try {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                line = removeComments(line);
                stringBuilder.append(line);
            }
            programCode = stringBuilder.toString();
        } catch (IOException exception) {
            throw new InterpreterError("File '" + file.getName() + "' could not be closed!");
        }

        // close the file
        closeStream();
    }

    private String removeComments(String line) {
        return line.replaceAll("[^<>+\\-.,\\[\\]]","");
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
                try {
                    outputStream.write(memory.getCharAtPointer());
                }
                catch (IOException exception) {
                    throw new InterpreterError("Can not write to output stream!");
                }
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
        InputStream inputStream;
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

    public boolean isRunning() {
        return running;
    }
}
