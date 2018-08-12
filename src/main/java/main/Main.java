package main;

import interpreter.FileInterpreter;
import memory.Memory;
import memory.Memory8;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        Memory memory = new Memory8();
        ClassLoader classLoader = Main.class.getClassLoader();
        File file = new File(classLoader.getResource("testprograms/helloworld.bf").getFile());
        FileInterpreter fileInterpreter = new FileInterpreter(file, memory);
        fileInterpreter.run();
        System.out.println(fileInterpreter.getTimeInMilliseconds() + "ms");
        System.out.println(fileInterpreter.getTimeInSeconds() + "s");
    }
}
