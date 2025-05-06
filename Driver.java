import com.github.ElijahGabrielleTanabe.Printf.Printf;

public class Driver
{
    public static void main(String[] args) 
    {
        Integer d = 10;
        Float f = 0.002F;
        Double dd = 12.094;
        String s = "Brrrrr";
        Character c = 'r';
        Printf print = new Printf();
        print.print("Hello %+013d World %-1.20f Hello %0s World %7.2c Hello %+.6f", d, f, s, c, dd);
    }
}
