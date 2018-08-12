package main;

import interpreter.FileInterpreter;
import memory.Memory;
import memory.Memory32;
import memory.Memory16;
import memory.Memory8;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        Memory memory = new Memory16();
        ClassLoader classLoader = Main.class.getClassLoader();
        File file = new File(classLoader.getResource("testprograms/pi.bf").getFile());
        FileInterpreter fileInterpreter = new FileInterpreter(file, memory, "");
        fileInterpreter.run();
        System.out.println();
        System.out.println("Finished in " + fileInterpreter.getTimeInMilliseconds() + "ms");
        System.out.println("Finished in " + fileInterpreter.getTimeInSeconds() + "s");
        System.out.println("Number of executed instructions: " + fileInterpreter.getInstructionCount());
    }
}
