/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assembler;

import com.sun.xml.internal.ws.util.StringUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pass2
{

    private String Header;
    private String records = "";
    private symboltable is;
    private HashMap<String, String> opTab;
    private HashMap<String, String> symTab;
    BufferedReader reader, obTablReader, symbtablereader;
    DecimalFormat byteFormat;
    DecimalFormat wordFormat;
    File Symbols, Intermediate;
    File ObjectCode;
    PrintWriter objectCode;

    public Pass2(File obTable, File Symbols, File Intermediate) throws FileNotFoundException, IOException
    {

        FileWriter ObjectCode = new FileWriter("ObjectCode.txt");
        objectCode = new PrintWriter(ObjectCode);
        this.opTab = new HashMap();
        this.symTab = new HashMap();
        reader = new BufferedReader(new FileReader(Intermediate));
        obTablReader = new BufferedReader(new FileReader(obTable));
        symbtablereader = new BufferedReader(new FileReader(Symbols));
        is = new symboltable();
        byteFormat = new DecimalFormat("00");
        wordFormat = new DecimalFormat("000000");
        String line;
        Pattern p = Pattern.compile("\\s*(\\w+)\\s+(\\w+)\\s*");
        Matcher m;

        while ((line = obTablReader.readLine()) != null)
        {
            m = p.matcher(line);
            if (m.matches())
            {

                opTab.put(m.group(1), m.group(2));
            } else
            {
                System.err.println("error");
            }
        }
        while ((line = symbtablereader.readLine()) != null)
        {
            m = p.matcher(line);
            if (m.matches())
            {
                symTab.put(m.group(1), m.group(2));
            } else
            {
                System.err.println(line);

                System.err.println("error SYMTABLE");
            }
        }

    }

    public void getObjectCode(String[] lenght) throws IOException
    {
        int recordLength;
        String currentLine;
        String Line;
        String startingAddress, Startgingaddress;
        String opcode, operand, address;
        int counter = 0;
        while ((currentLine = reader.readLine()) == null)
        {
            currentLine = reader.readLine();
            System.err.println("Reading null");
        }

        is.setLine(currentLine);
        int i = 0;
        String opcodec;
        String Starting = null;

        if (is.getOpcode().equals("START"))
        {

            Header = "H" + rightpadding(lenght[1], "      ") + leftpadding(is.getOperand(), "000000") + " " + leftpadding(lenght[0], "000000");
            startingAddress = is.getOperand();
            Starting = startingAddress;
            objectCode.println(Header);
            System.err.println(currentLine);

        } else
        {
            //System.err.println(is.getOpcode());
        }

        currentLine = reader.readLine();
        while (currentLine.replaceAll("\\s", "").equals(""))
        {
            currentLine = reader.readLine();
        }

        //System.err.println(currentLine);
        //RECORDS
        while (currentLine != null)
        {
            is.setLine(currentLine);
            startingAddress = is.getAddress();

            for (int j = 0; j < 10; j++)
            {

                opcode = is.getOpcode();
                operand = is.getOperand();
                address = is.getAddress();

                if (opTab.containsKey(opcode))
                {

                    if (symTab.containsKey(operand))
                    {
                        // System.err.println(operand);
                        if (is.indexing)
                        {
                            is.setIndex(false);
                            opcodec = Integer.toHexString(Integer.parseInt(symTab.get(operand), 16) + 32768);
                            System.err.println("Indexing");
                            records = records + " " + opTab.get(is.getOpcode()) + leftpadding(opcodec, "0000");
                             counter+=3;
                            // System.err.println(operand);
                        } else 
                        {
                            records = (records + " " + opTab.get(opcode) + leftpadding(symTab.get(operand),"0000"));
                            counter+=3;
                        }
                        

                    } else if(!"".equals(operand.replaceAll("\\s", "")))
                    { 
                    System.err.println("ERROR: Operand not found " + operand);
                    System.err.println("Program Terminating");
                    return;
                    } else {
                        records = (records + " " + opTab.get(opcode) + "0000");
                        counter+=3;
                    }
                        
                }
                 else if (opcode.equals("BYTE"))
                {
                    System.err.println(operand);
                    records = (records + operand);
                    if(is.asci)
                    {counter+=3;
                    is.asci = false;
                    }
                    else
                        counter +=1;
                } else if (opcode.equals("WORD"))
                {
                    records = (records + " " + leftpadding(Integer.toHexString(Integer.parseInt(operand)), "000000"));
                     counter+=3;
                } else if (opcode.equals("RESW") || opcode.equals("RESB"))

                {
                    while (is.getOpcode().equals("RESW") || is.getOpcode().equals("RESB"))
                    {
                        currentLine = reader.readLine();
                        while (currentLine != null && currentLine.replaceAll("\\s", "").equals("") && currentLine != null)
                        {
                            currentLine = reader.readLine();
                        }
                        if (currentLine == null)
                        {
                            break;
                        } else
                        {
                            is.setLine(currentLine);
                        }

                    }
                    break;
                } else if(!opcode.equals("END"))
                {
                    System.err.println("ERROR: Opcode not found " + opcode);
                    System.err.println("Program Terminating");
                    return;
                }
                currentLine = reader.readLine();
                while (currentLine != null && currentLine.replaceAll("\\s", "").equals(""))
                {
                    currentLine = reader.readLine();
                }
                //  System.err.println("Line:   " + currentLine);
                if (currentLine == null)
                {
                    break;
                } else
                {
                    is.setLine(currentLine);
                }

            }

            recordLength =  counter;
            counter = 0;
            //    System.err.println(recordLength);

            //    System.err.println(leftpadding(startingAddress, "000000" )+ "HOLW");
            // records = ("T" + leftpadding(startingAddress,"000000") + leftpadding(Integer.toHexString(recordLength),"00") + records);
            //   System.err.println(records);
            // System.err.println(i);
            objectCode.println(("T" + leftpadding(startingAddress, "000000")+ leftpadding(Integer.toHexString(recordLength), "00")+ records).replaceAll("\\s",""));
            records = "";

            // System.err.println(is.getOpcode());
        }
        objectCode.println("E" + leftpadding(Starting, "000000"));
        objectCode.close();
    }

    public String leftpadding(String s, String z)
    {

        return z.substring(s.length()) + s;

    }

    public String rightpadding(String s, String z)
    {

        return s + z.substring(s.length());

    }
}
