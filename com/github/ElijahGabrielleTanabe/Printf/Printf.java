package com.github.ElijahGabrielleTanabe.Printf;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Printf 
{
    private String lastPrint;

    //List of types (key:value pair list)
    //!!MEGA JANK!!
    private Map<String, Object> types = Map.of(
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
            int pos = message.indexOf(fp);
            message.replace(pos, pos + fp.length(), buildFormatedArg(fp, args[i]));
        }

        //Print out the finished product!!
        //OR
        //Return a String for usage
        System.out.print(message);
        this.lastPrint = message.toString();
    }   

    public <T> String buildFormatedArg(String format, T o)
    {
        System.out.println("\nParameter: " + format);
        System.out.println("Argument: " + o + " " + o.getClass().getName() + "\n");

        //# Verify format parameters syntax
            // Structure: %[flags][width][.precision]type
                // Chaining flags is valid syntax
                // No duplicate flags
        if (!format.matches("%([+-0]+|)(\\d+|)(\\.[1-9](\\d+|)|)([dfsc])")) { throw new IllegalArgumentException("Exceptioning'd"); }

        //# Apply format to associated argument
            // Split into four parts (flags, width, precision, type)
        String flags = regexMatch("(?:%)([+-0]+)(?:[\\d\\.dfsc])", format);
        String width = regexMatch("(?:[+-0%])([1-9]+)(?:\\.\\d+)?(?:[.dfsc])", format);
        String precision = regexMatch("(?:\\.)(\\d+)(?:[dfsc])", format);
        String type = regexMatch("(?:\\d|[+-0]|%)([dfsc])", format);

        // For floats & doubles
            // Round to original precision if no precision is given
        if (precision.isEmpty() && type.equals("f"))
        {
            precision = Integer.toString(o.toString().split("\\.")[1].length());
            System.out.println(precision);
        }
        
        //# Verify type is instanceof argument
            // Promote Floats to Double
        System.out.println(o.getClass().getName());

        //# Apply each individual part to the associated argument (if present)
            // Width overrides precision
            // Precision for integers is minimum number of digits
                // Pad with leadings 0's if number of digits is less than precision

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

        System.out.println("group: " + comp);

        return comp.toString();
    }

    public String getLastPrint() { return this.lastPrint; }

    //!!VERIFY ACCURACY!!//
    public static Double round(Double value, int scale) 
    {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }
}
