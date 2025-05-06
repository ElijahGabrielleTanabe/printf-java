import com.github.ElijahGabrielleTanabe.Printf.Printf;

public class Driver
{
    public static void main(String[] args) 
    {
        Float f = 0.002F;
        String s = "Brrrrr";
        Printf print = new Printf();
        print.print("Hello %+013d World %-1.20f Hello %0s", 10, f, s);
    }
}
