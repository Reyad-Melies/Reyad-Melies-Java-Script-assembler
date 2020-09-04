/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assembler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Omar
 */
public class Assembler
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {
        File code = new File("code.txt");
        File opTable = new File("optable.txt");
        File SymTable = new File("Symboltable.txt");
        File Intermediate = new File("Intermediate.txt");
  
        long startTime = System.currentTimeMillis();
        Pass1 pass1 = new Pass1(code, opTable, SymTable, Intermediate);
       
        String[] length = pass1.PassOne();
     
        Pass2 pass2 = new Pass2(opTable, SymTable, Intermediate);
        pass2.getObjectCode(length);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.err.println("Elapsed Time:" + elapsedTime + " ms");
       

    }

}
