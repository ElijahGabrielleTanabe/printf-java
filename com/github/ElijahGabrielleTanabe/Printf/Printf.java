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
        "d", Integer.valueOf(0),
        "f", Double.valueOf(0), //Promote Floats to Double
        "s", String.valueOf(""),
        "c", Character.valueOf(' ')
    );

    // Create a printf object for std.out
    public Printf()
    {
        this.lastPrint = "";
    }

    public void print(String s, Object ... args)
    {
        //# find all format parameters inside message, store in a list
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

        System.out.println(message);
  
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
        System.out.println(message);
        this.lastPrint = message.toString();
    }   

    public <T> String buildFormatedArg(String format, T o)
    {
        System.out.println(format);
        System.out.println(o + " " + o.getClass().getName());

        //# Verify format parameters syntax
            // Structure: %[flags][width][.precision]type
                // Chaining flags is valid syntax
                // No duplicate flags
        if (!format.matches("%([+-0]+|)(\\d+|)(.[1-9](\\d+|)|)([dfsc])")) { throw new IllegalArgumentException("Exceptioning'd"); }

        //# Apply format to associated argument
            // Split into four parts (flags, width, precision, type)
                // Flag Regex:
                // Width Regex:
                // Precision Regex:
                // Type Regex:
            // Apply each individual part to the associated argument (if present)

        return "poop";
    }

    public String getLastPrint() { return this.lastPrint; }
}
