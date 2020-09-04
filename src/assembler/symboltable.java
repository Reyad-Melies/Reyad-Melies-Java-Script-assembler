/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assembler;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Omar
 */
public class symboltable
{

    String line;
    String opCode;
    String operand;
    String address;
    String label;
    boolean comment;
    boolean indexing=false;
    boolean asci =false;
    boolean hexa=false;

    public void setLine(String line)
    {
        this.line = line;
        Pattern p = Pattern.compile("\\s*(\\w+)\\s+(.*)\\s+(\\w+)\\s*");//G1 = label , G2 = Opcode , G3 = Operand
        Matcher m = p.matcher(line);
        boolean b = m.matches();
        if (b)
        {

            opCode = m.group(1).replaceAll("\\s", "");
            operand = m.group(2).replaceAll("\\s", "");
            address = m.group(3).replaceAll("\\s", "");

            if (operand.matches("(\\w+),X"))
            {
                indexing = true;
                
            }
            if (operand.matches("X'(\\w+)'"))
            { 
                hexa = true;
                System.err.println("Hexa: Operand  "+ operand);
                m =   Pattern.compile("X'(\\w+)'").matcher(operand);
                m.matches();
                operand = m.group(1);
               
            }
            if (operand.matches("C'(\\w+)'"))
            {
               asci  = true;
               System.err.println("asci Found : " + operand);
            }
        } else System.err.println("Error matching : " + line);
        {
            comment = false;
        }
    }

    public String getLabel()
    {

        return label;
    }

    public String getOpcode()
    {

        return opCode;
    }

    public String getOperand() throws UnsupportedEncodingException
    {
        if (indexing)
        {  
           // System.err.println(operand.split(",")[0]);
           return operand.split(",")[0];
        } else if(asci)
        { 
            
            
            
            String hexa=operand.substring(2, operand.length()-1);
            String text="";
            
            byte[] bytes = hexa.getBytes("US-ASCII");
            for(int i=0;i<hexa.length();i++)
            text = text +  Integer.toHexString(Integer.parseInt((String.valueOf(bytes[i]))));
          
            
            System.err.println("ASCI: " + text);
            return text;
        }else
        if(hexa)
        {
           
            return operand;
                    
         }
        
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
    
    private static String hexToASCII(String hexValue)
{
    StringBuilder output = new StringBuilder("");
    for (int i = 0; i < hexValue.length(); i += 2)
    {
        String str = hexValue.substring(i, i + 2);
        output.append((char) Integer.parseInt(str, 16));
    }
    return output.toString();
}
public void setIndex(boolean index)
{
    indexing = index;
}
}
