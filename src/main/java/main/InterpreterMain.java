package main;

import interpreter.FileInterpreter;
import memory.Memory;
import memory.Memory8;

import java.io.File;

public class InterpreterMain {
    public static void main(String[] args) {
        ClassLoader classLoader = InterpreterMain.class.getClassLoader();
        File file = new File(classLoader.getResource("testprograms/sierpinski.bf").getFile());

        Memory memory = new Memory8();
        FileInterpreter fileInterpreter = new FileInterpreter(file, memory, "");
        fileInterpreter.run(System.out);

        System.out.println();
        System.out.println("Finished in " + fileInterpreter.getTimeInMilliseconds() + "ms (" + fileInterpreter.getTimeInSeconds() + "s)");
        System.out.println("Number of executed instructions: " + fileInterpreter.getFormattedInstructionCount());
    }
}
