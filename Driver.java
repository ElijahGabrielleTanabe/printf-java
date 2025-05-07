import com.github.ElijahGabrielleTanabe.Printf.Printf;

public class Driver
{
    public static void main(String[] args) 
    {
        Integer d = -10;
        Float f = -0.002F;
        Double dd = 12.094;
        String s = "Aha";
        Character c = 'r';
        Printf print = new Printf();
        print.print("Hello %+0-13.5d World %-+4.5f Hello %015.20s World %7.2c Hello %+.6f", d, f, s, c, dd);
    }
}
