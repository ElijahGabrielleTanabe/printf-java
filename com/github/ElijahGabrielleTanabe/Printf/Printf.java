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

        //System.out.println(message);
  
        //# Concatenate formated argument to message
            // Replace from format parameters position
        for (int i = 0; i < formatParameters.size(); i++)
        {
            String fp = formatParameters.get(i);
            System.out.println(fp);
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
        //# Verify format parameters syntax
            // Structure: %[flags][width][.precision]type
                // Chaining flags is valid syntax
                // No duplicate flags
        if (!format.matches("%([+-0]+|)(\\d+|)(\\.[1-9](\\d+|)|)([dfsc])")) { throw new IllegalArgumentException("Exceptioning'd"); }

        //# Apply format to associated argument
            // Split into four parts (flags, width, precision, type)
        String[] s = {
            regexMatch("(?:%)([+-[0]]+)(?:\\.|\\d|[dfsc])?", format), //Flags
            regexMatch("(?:[+-[0]%])(?<!\\.)([1-9]+)(?:\\.\\d+)?(?:[.dfsc])", format), //Width
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
        String flags = format[0];
        String width = format[1];
        String precision = format[2];
        String type = format[3];

        System.out.println("Parameter: " + Arrays.deepToString(format));
        System.out.println("Argument: " + o + " " + types.get(type));

        // For floats & doubles
            // Round to original precision if no precision is given
        if (precision.isEmpty() && type.equals("f"))
        {
            precision = Integer.toString(o.split("\\.")[1].length());
            System.out.println(precision);
        }
        

        //# Apply each individual part to the associated argument (if present)
            // Width overrides precision

            // Precision for Integers is minimum number of digits
                // Pad with leadings 0's if number of digits is less than precision

            // Precision for floats 

            // Precision for Strings is the maximum number of characters
                // Truncate at position of precision if string length is greater than precision

            // Precision for characters does nothing
        System.out.println();

        return "test";
    }

    public String regexMatch(String regex, String s)
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

    //!!VERIFY ACCURACY!!//
    public static Double round(Double value, int scale) 
    {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

    public String getLastPrint() { return this.lastPrint; }
}
