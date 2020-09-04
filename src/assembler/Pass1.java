/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assembler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Omar
 */
public class Pass1
{

    private String currentLine = null;
    public String LocCounter;
    private String characters;
    private int address = 0;
    int startAddress = 0;
    String opcode = "";
    Instructionassembler instruction;
    int programLength;
    boolean LabelDuplicate = false;
    boolean InvalidOpCode = false;
    HashMap<String, String> list = new HashMap<>();
    HashMap<String, String> labels = new HashMap();
    HashMap<String, String> opTable = new HashMap();
    File code;
    File optable;
    File symTab;
    File intermediate;
     String length;

    public Pass1(File code, File optab,File symTab,File intermediate) throws FileNotFoundException, IOException
    {   
       
        this.code = code;
        this.optable = optab;
        this.intermediate = intermediate;
        this.symTab = symTab;
        BufferedReader reader = new BufferedReader(new FileReader(optab));
        String line;
        Pattern p = Pattern.compile("\\s*(\\w+)\\s*(\\w+)");
        Matcher m;

        while ((line = reader.readLine()) != null)
        {
            m = p.matcher(line);
            if (m.matches())
            {
                opTable.put(m.group(1), m.group(2));
            } else
            {
                System.err.println("error");
            }
        }
    }

    public String[] PassOne() throws IOException
    {

        FileWriter intermediatee = new FileWriter(intermediate);
        FileWriter symTablee = new FileWriter(symTab);
        PrintWriter intermediate = new PrintWriter (intermediatee);
        PrintWriter symTable = new PrintWriter(symTablee);
        String title="";
        try
        {

            BufferedReader BufferedReader = new BufferedReader(new FileReader(code));

            if ((currentLine = BufferedReader.readLine()) != null)
            {
                 
                instruction = new Instructionassembler(currentLine);
                
               
                if (instruction.getOpcode().equals("START"))
                {

                    LocCounter = instruction.getOperand();
                    address = Integer.parseInt(LocCounter, 16);
                    startAddress = address;
                    intermediate.println(instruction.getOpcode() + "    " + instruction.getOperand() + "    " + LocCounter);
                    title = instruction.getLabel();
                } else
                {
                    address = 0;
                    LocCounter = Integer.toString(address);

                }
            }
            while ((currentLine = BufferedReader.readLine()) != null && !(instruction.getOpcode().replaceAll(" ","").equals("END")))
            {
                while(currentLine.contains("."))
                        currentLine = BufferedReader.readLine();
                 while(currentLine.replaceAll(" ","").equals(""))
                        currentLine = BufferedReader.readLine();
                //list.put(currentLine, LocCounter);
                instruction = new Instructionassembler(currentLine);
               // System.err.println(currentLine);
              //  System.err.println("Label: "+instruction.getLabel() + "  OPCODE: " + instruction.getOpcode() + "  Operand: " + instruction.getOperand());
                 // System.err.println(instruction.getOpcode());
                if (!(instruction.getOpcode().replaceAll(" ","").equals("END")))
                {
                    if (currentLine.startsWith("."))
                    //do whatever with the line read
                    {
                        continue;
                    }
                    if (!(instruction.getLabel().replaceAll("\\s", "").equals("")) && !(instruction.getLabel().equals("\t"))&& !(instruction.getLabel().replaceAll("\\s", "").equals("Label")))

                    {
                        if (labels.containsKey(instruction.getLabel()) )
                        {
                           // System.out.println("Error: " + instruction.getLabel() + " Label Already Exists");
                            LabelDuplicate = true;
                        } else
                        {
                          //  labels.put(instruction.getLabel(), LocCounter);
                            symTable.println(instruction.getLabel() +"   "+ LocCounter);
                            
                        }
                    }
                    if (opTable.containsKey(instruction.getOpcode()))
                    {
                        address = address + 3;
                       // LocCounter = Integer.toHexString(address);
                    } else if (instruction.getOpcode().equals("WORD"))
                    {
                        address = address + 3;
                       // LocCounter = Integer.toHexString(address);
                    } else if (instruction.getOpcode().equals("RESW"))
                    {
                        address = address + 3 * Integer.parseInt(instruction.getOperand());
                        //LocCounter = Integer.toHexString(address);
                    } else if (instruction.getOpcode().equals("RESB"))
                    {
                        address = address + Integer.parseInt(instruction.getOperand());
                        //LocCounter = Integer.toHexString(address);
                    } else if (instruction.getOpcode().equals("BYTE"))
                    {
                      if (instruction.asci)
                      {   instruction.asci = false;
                          characters = instruction.getOperand().replaceAll("C","").replaceAll("'", "");
                          System.err.println("ASCI FOUND  "+instruction.getOperand() + " " +characters.length());
                          address = address + (characters.length() );
                       // LocCounter = Integer.toHexString(address);
                      }
                      else
                          address = address +1;

                    } else
                    {
                        InvalidOpCode = true;
                        System.out.println("OpCode not found");
                        System.err.println("CODE: " + instruction.getOpcode());
                        
                    }
                    intermediate.println(instruction.getOpcode() + "    " + instruction.getOperand() + "    " + LocCounter+ "\n");
                    LocCounter = Integer.toHexString(address);

                }
                
            }
             length = Integer.toHexString(Integer.parseInt(LocCounter, 16) - startAddress);
        } catch (IOException e)
        {
            System.err.println(e.getCause());
        }
        intermediate.println("END  "+ startAddress);
        intermediate.close();
        symTable.close();
        System.out.println("End of Pass 1");
        String [] program = {length, title};
        
return program;
 }
}
