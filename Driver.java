import com.github.ElijahGabrielleTanabe.Printf.*;

public class Driver
{
    public static void main(String[] args) 
    {
        Printf print = new Printf();
        print.print("Hello %3d World %-1.30f", 10, 0.002);
    }
}
