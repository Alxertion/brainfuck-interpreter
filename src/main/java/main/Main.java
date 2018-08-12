package main;

import interpreter.FileInterpreter;
import memory.Memory;
import memory.Memory32;
import memory.Memory16;
import memory.Memory8;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        ClassLoader classLoader = Main.class.getClassLoader();
        File file = new File(classLoader.getResource("testprograms/pi.bf").getFile());

        Memory memory = new Memory16();
        FileInterpreter fileInterpreter = new FileInterpreter(file, memory, "");
        fileInterpreter.run();

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        System.out.println();
        System.out.println("Finished in " + fileInterpreter.getTimeInMilliseconds() + "ms (" + fileInterpreter.getTimeInSeconds() + "s)");
        System.out.println("Number of executed instructions: " + formatter.format(fileInterpreter.getInstructionCount()));
    }
}
