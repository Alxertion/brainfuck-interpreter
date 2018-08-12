package interpreter;

import memory.Memory;
import memory.Memory8;

import java.io.*;

public class FileInterpreter {
    private final String filename;
    private BufferedReader bufferedReader;
    private long elapsedTimeMilliseconds;
    private double elapsedTimeSeconds;
    private final Memory memory;

    public FileInterpreter(String filename) {
        this.filename = filename;
        this.memory = new Memory8();
    }

    public FileInterpreter(String filename, Memory memory) {
        this.filename = filename;
        this.memory = memory;
    }

    public void run() {
        // open the file and prepare for reading
        initializeStream();

        // start the time measurement
        long startTime = System.currentTimeMillis();

        // execute the program
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                for (int i = 0; i <= line.length(); i++) {
                    char currentInstruction = line.charAt(i);
                    executeInstruction(currentInstruction);
                }
            }
        } catch (IOException exception) {
            throw new InterpreterError("File " + filename + " could not be closed!");
        }

        // end the time measurement
        long stopTime = System.currentTimeMillis();
        elapsedTimeMilliseconds = stopTime - startTime;
        elapsedTimeSeconds = elapsedTimeMilliseconds / 1000.0d;

        // close the file
        closeStream();
    }

    private void executeInstruction(char currentInstruction) {
        
    }

    private void initializeStream() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filename);
        } catch (FileNotFoundException exception) {
            throw new InterpreterError("File " + filename + " was not found!");
        }
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    private void closeStream() {
        try {
            bufferedReader.close();
        } catch (IOException exception) {
            throw new InterpreterError("File " + filename + " could not be closed!");
        }
    }

    public long getTimeInMilliseconds() {
        return elapsedTimeMilliseconds;
    }

    public double getTimeInSeconds() {
        return elapsedTimeSeconds;
    }
}
