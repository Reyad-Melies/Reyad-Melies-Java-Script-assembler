/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assembler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Omar
 */
public class Instructionassembler
{

    String line;
    String opCode;
    String operand;
    String address;
    String label;
    boolean comment;
    boolean indexing;
    boolean asci;

    Instructionassembler()
    {

    }

    public void setLine(String line)
    {
        this.line = line;
        Pattern p = Pattern.compile("(\\w*)\\s+(\\w+)\\s*(.*)\\s*");//G1 = label , G2 = Opcode , G3 = Operand
        Matcher m = p.matcher(line);
        boolean b = m.matches();
        if (b)
        {
            comment = true;
            label = m.group(1).replaceAll("\\s", "");
            opCode = m.group(2).replaceAll("\\s", "");
            operand = m.group(3).replaceAll("\\s", "");
           
            if(operand.matches("\\w+,X"))
                indexing = true;
            if(operand.matches("X'\\w*'"))
                asci = false;
            if(operand.matches("C'(\\w+)'"))
            {
                asci = true;
                
            }

        } else
        {
            comment = false;
        }
    }

    public String getLabel()
    {

        return label;
    }

    public Instructionassembler(String line)
    {
        setLine(line);
    }

    public String getOpcode()
    {

        return opCode;
    }

    public String getOperand()
    {
       
        return operand;

    }

    public String getAddress()
    {
        return address;
    }

    public boolean comment()
    {
        return comment;
    }

}
