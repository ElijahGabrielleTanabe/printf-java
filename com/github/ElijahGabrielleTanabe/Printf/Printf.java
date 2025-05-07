package com.github.ElijahGabrielleTanabe.Printf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Printf 
{
    private String lastPrint;

    //List of types (key:value pair list)
    //!!MEGA JANK!!
    private final Map<String, String> types = Map.of(
        "d", "java.lang.Integer",
        "f", "java.lang.Double",
        "s", "java.lang.String",
        "c", "java.lang.Character"
    );

    public Printf()
    {
        this.lastPrint = "";
    }

    public void print(String s, Object ... args)
    {
        //# Find all format parameters inside message, store in a list
            // Regex: %[\d-+.dfcs]+
        StringBuilder message = new StringBuilder(s);
        ArrayList<String> formatParameters = new ArrayList<>();

        Pattern pattern = Pattern.compile("%[\\d-+.dfcs]+");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find())
        {
            formatParameters.add(matcher.group().trim());
        }

        //# Verify number of format parameters are the same as args
        if (formatParameters.size() != args.length) { throw new IllegalArgumentException(); }

        //# Verify format parameters syntax
            // Structure: %[flags][width][.precision]type
                // Chaining flags is valid syntax
                //!!NO DUPLICATE FLAGS!!
        for (String format : formatParameters)
        {
            if (!format.matches("%([+-[0]]+)?(\\d+)?(\\.[1-9](\\d+|)|\\.[0])?([dfsc])")) 
            {
                throw new IllegalArgumentException("Improper Format: " + format);
            }
        }
        
        //System.out.println(message);
  
        //# Concatenate formated argument to message
            // Replace from format parameters position
        for (int i = 0; i < formatParameters.size(); i++)
        {
            String fp = formatParameters.get(i);
            System.out.println("Original F-Parameter: " + fp);
            int pos = message.indexOf(fp);
            String[] fp1 = parseParameters(fp);
            String type = fp1[3];

            String arg = transformArgument(type, args[i]);

            message.replace(pos, pos + fp.length(), buildFormatedArg(fp1, arg));
        }

        //Print out the finished product!!
        //OR
        //Return a String for usage
        System.out.print(message);
        this.lastPrint = message.toString();
    }

    private String[] parseParameters(String format)
    {
        //# Parse format into parts
            // Split into four parts (flags, width, precision, type)
        String[] s = {
            regexMatch("(?:%)([+-[0]]+)(?:\\.|\\d|[dfsc])?", format), //Flags
            regexMatch("(?:[+-[0]%]+)(?<!\\.)([1-9][\\d]+)(?:\\.\\d+)?(?:[.dfsc])", format), //Width
            regexMatch("(?:\\.)(\\d+)(?:[dfsc])", format), //Precision
            regexMatch("(?:\\d|[+-[0]]|%)([dfsc])", format) //Type
        };

        return s;
    }

    private <T> String transformArgument(String type, T o)
    {
        //# Verify type is instanceof argument
        try 
        {
            // Promote Floats to Double (C printf functionality)
            if (o instanceof Float aFloat)
            {
                Double d = aFloat.doubleValue();

                if (!Class.forName(types.get(type)).isInstance(d))
                {
                    throw new IllegalArgumentException("Illegal type");
                }

                return d.toString();
            }
            else if (!Class.forName(types.get(type)).isInstance(o))
            {
                throw new IllegalArgumentException("Illegal type");
            }

            if (o != null)
            {
                return o.toString();
            }
        } 
        catch (ClassNotFoundException e) 
        {
            e.printStackTrace();
        }
        
        throw new IllegalArgumentException("Illegal type");
    }

    public String buildFormatedArg(String[] format, String o)
    {
        StringBuilder result = null;
        String flags = format[0];
        String width = format[1];
        String precision = format[2];
        String type = format[3];

        System.out.println("F-Parameters: " + Arrays.deepToString(format));
        System.out.println("Argument: " + o);
        System.out.println("Type: " + types.get(type));

        //# Apply each individual part to the associated argument (if present)
            // Apply precision
        switch(type)
        {
            case "d" -> {
                // Precision for Integers is minimum number of digits
                    // Pad with leadings 0's if number of digits is less than precision
                    // Supress output when precision is 0
                if (!precision.isEmpty() && o.length() < Integer.parseInt(precision))
                {
                    result = new StringBuilder(o);
                    
                    int remainder = Integer.parseInt(precision) - o.length();

                    for (int i = 0; i < remainder; i++)
                    {
                        result.insert(0, '0');
                    }
                }
                else if (!precision.isEmpty() && Integer.parseInt(precision) == 0)
                {
                    result = new StringBuilder("");
                    break;
                }
                else
                {
                    result = new StringBuilder(o);
                }

                if (o.contains("-"))
                {
                    result.deleteCharAt(result.indexOf("-"));
                    result.insert(0, "-");
                }
            }
            case "f" -> {
                // Precision for floats and doubles is the number of decimal digits
                // Minimum of 1, not truncated by width
                int p;

                // Round to original precision if no precision is given
                if (precision.isEmpty())
                {
                    p = o.split("\\.")[1].length();
                }
                else
                {
                    p = Integer.parseInt(precision);
                }
                
                result = new StringBuilder(round(Double.valueOf(o), p).toString());

                int remainder = result.toString().split("\\.")[1].length();

                if (remainder < p)
                {
                    for (int i = 0; i < p - remainder; i++)
                    {
                        result.append("0");
                    }
                }
            }
            case "s" -> {
                // Precision for Strings is the maximum number of characters
                // Truncate at position of precision if string length is greater than precision
                if (!precision.isEmpty() && o.length() > Integer.parseInt(precision)) 
                {
                    result = new StringBuilder(o.substring(0, Integer.parseInt(precision))); 
                }
                else if (!precision.isEmpty() && Integer.parseInt(precision) == 0)
                {
                    result = new StringBuilder(o.substring(0, 1));
                }
                else
                {
                    result = new StringBuilder(o);
                }
            }
            case "c" -> {
                // Precision for characters does nothing
                result = new StringBuilder(o);
            }
            default -> throw new IllegalArgumentException("Unknown Type: " + type);
        }
            
        // Argument is not truncated even if it is greater than width
            // Apply flags
        if (flags.contains("+"))
        {
            if (type.equals("d") || type.equals("f"))
            {
                if (result != null && result.indexOf("-") == -1)
                {
                    result.insert(0, "+");
                }
            }
        }

        // Apply width
        if (!width.isEmpty() && Integer.parseInt(width) > result.length())
        {
            int spacingAmount = Integer.parseInt(width) - result.length();
            
            if (flags.contains("-"))
            {
                if (flags.contains("0")) { insertWidthEnd(spacingAmount, result.length() - 1, '0', result); }
                else { insertWidthEnd(spacingAmount, result.length() - 1, ' ', result); }
            }
            else
            {
                if (flags.contains("0")) { insertWidthStart(spacingAmount, 0, '0', result); }
                else { insertWidthStart(spacingAmount, 0, ' ', result); }
            }
        }

        System.out.println(result + "\n");

        return result.toString();
    }

    private String regexMatch(String regex, String s)
    {
        StringBuilder comp = new StringBuilder();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);

        while (matcher.find()) 
        {
            for (int i = 1; i <= matcher.groupCount(); i++) 
            {
                String group = matcher.group(i);

                if (group != null) 
                {
                    comp.append(group);
                }
            }
        }

        return comp.toString();
    }

    private void insertWidthStart(int amount, int index, char c, StringBuilder b)
    {
        for (int i = 0; i < amount; i++)
        {
            b.insert(index, c);
        }
    }

    private void insertWidthEnd(int amount, int index, char c, StringBuilder b)
    {
        for (int i = 0; i < amount; i++)
        {
            b.insert(index, c);
            index++;
        }
    }

    //!!VERIFY ACCURACY!!//
    public static Double round(Double value, int scale) 
    {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

    public String getLastPrint() { return this.lastPrint; }
}
