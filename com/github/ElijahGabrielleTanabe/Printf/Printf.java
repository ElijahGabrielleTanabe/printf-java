package com.github.ElijahGabrielleTanabe.Printf;

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
        // Message to be transformed and printed
        StringBuilder message = new StringBuilder(s);
        //# Find all format parameters inside message, store inside a list
        String[] formatParameters = regexMatch("(%[\\d-+.dfcs]+)", s).split("(?=%)");

        //# Verify number of format parameters are the same as args
        if (formatParameters.length != args.length) { throw new IllegalArgumentException(); }

        //# Verify format parameters syntax
        // Structure: %[flags][width][.precision]type
        for (String format : formatParameters)
        {
            if (!format.matches("%([+-[0]]+)?(\\d+)?(\\.[1-9](\\d+|)|\\.[0])?([dfsc])")) 
            {
                throw new IllegalArgumentException("Improper Format: " + format);
            }
        }
  
        //# Construct argument
        for (int i = 0; i < formatParameters.length; i++)
        {
            String fp = formatParameters[i];

            // Position of format argument
            int pos = message.indexOf(fp);

            // Parse parts of format argument (flags, width, precision, type)
            String[] fpl = parseParameters(fp);
            String type = fpl[3];

            // Verify class type of argument matches parameter and transform into String
            String arg = transformArgument(type, args[i]);

            // Build argument with format argument parameters
            String formatedArgument = buildFormatedArg(fpl, arg);

            // Replace from format parameters position
            message.replace(pos, pos + fp.length(), formatedArgument);
        }

        // Print out the finished product!!
        System.out.print(message);

        // Store for usage
        this.lastPrint = message.toString();
    }

    private String[] parseParameters(String format)
    {
        //# Parse format into parts (flags, width, precision, type)
        return new String[]{
            regexMatch("(?:%)([+-[0]]+)(?:\\.|\\d|[dfsc])?", format), //Flags
            regexMatch("(?:[+-[0]%]+)(?<!\\.)([1-9][\\d]+)(?:\\.\\d+)?(?:[.dfsc])", format), //Width
            regexMatch("(?:\\.)(\\d+)(?:[dfsc])", format), //Precision
            regexMatch("(?:\\d|[+-[0]]|%)([dfsc])", format) //Type
        };
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

            if (o != null) { return o.toString(); }
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

        //# Apply precision to each respective type (int, double, string, char)
        switch(type)
        {
            // Precision for Integers is minimum number of digits
            case "d" -> {
                // Pad with leadings zero's if number of digits is less than precision    
                if (!precision.isEmpty() && o.length() < Integer.parseInt(precision))
                {
                    result = new StringBuilder(o);
                    
                    int decimalPlace = Integer.parseInt(precision) - o.length();

                    for (int i = 0; i < decimalPlace; i++) 
                    {
                        result.insert(0, '0');
                    }
                }
                // Supress output when precision is zero
                else if (!precision.isEmpty() && Integer.parseInt(precision) == 0)
                {
                    result = new StringBuilder("");
                    break;
                }
                // No change when precision is equal to or 
                // greater than number of digits or doesnt exist
                else { result = new StringBuilder(o); }

                // Move "-" if present to the beginning
                if (o.contains("-"))
                {
                    result.deleteCharAt(result.indexOf("-"));
                    result.insert(0, "-");
                }
            }
            // Precision for floats and doubles is the number of decimal digits
            case "f" -> {
                int p;

                // If no precision present, 6 decimal places
                if (precision.isEmpty()) { p = 6; }
                else { p = Integer.parseInt(precision); }
                
                result = new StringBuilder(o);

                int decimalPlace = o.split("\\.")[1].length();
                
                // Append zero's to reach precision point
                if (decimalPlace < p)
                {
                    for (int i = 0; i < p - decimalPlace; i++)
                    {
                        result.append("0");
                    }
                }
                // Delete digits to reach precision point
                else if (decimalPlace > p)
                {
                    if (p == 0) { p++; }

                    result.delete(result.indexOf(".") + 1 + p, result.length());
                }
            }
            // Precision for Strings is the maximum number of characters
            case "s" -> {
                // Truncate at position of precision if string length is greater than precision
                if (!precision.isEmpty() && o.length() > Integer.parseInt(precision)) 
                {
                    result = new StringBuilder(o.substring(0, Integer.parseInt(precision))); 
                }
                // Truncate to first character if precision is equal to zero
                else if (!precision.isEmpty() && Integer.parseInt(precision) == 0)
                {
                    result = new StringBuilder(o.substring(0, 1));
                }
                else { result = new StringBuilder(o); }
            }
            // Precision for characters does nothing
            case "c" -> {
                result = new StringBuilder(o);
            }
            default -> throw new IllegalArgumentException("Unknown Type: " + type);
        }

        // If "+" flag present, add "+" at the start of positive numbers
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

        //# Apply width
        if (!width.isEmpty() && Integer.parseInt(width) > result.length())
        {
            int spacingAmount = Integer.parseInt(width) - result.length();
            
            // If "-" flag present, left align argument
            if (flags.contains("-"))
            {
                // If "0" flag present, insert zero's
                if (flags.contains("0")) { insertWidthEnd(spacingAmount, result.length(), '0', result); }
                // Otherwise, insert space's
                else { insertWidthEnd(spacingAmount, result.length(), ' ', result); }
            }
            // Otherwise, right align argument
            else
            {
                // If "0" flag present, insert zero's
                if (flags.contains("0")) { insertWidthStart(spacingAmount, 0, '0', result); }
                // Otherwise, insert space's
                else { insertWidthStart(spacingAmount, 0, ' ', result); }
            }
        }

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

    public String getLastPrint() { return this.lastPrint; }
}
