import java.io.File;
import java.io.IOException;

public class Driver {

    public static void main(String [] args) throws IOException {
        double[] c1 = {5, -7, 6, 9};
        int[] e1 = {15, 11, 12, 13};

        double[] c2 = {1, -2, 4, -3, -5};
        int[] e2 = {3, 2, 4, 7, 13};

        Polynomial p1 = new Polynomial(c1, e1);
        Polynomial p2 = new Polynomial(c2, e2);
        Polynomial p3 = p1.multiply(p2);
        System.out.println("p3 = p1 * p2 (multiply):");
        p3.printInfo();
        File testFile = new File("testing.txt");
        Polynomial imported = new Polynomial(testFile);
        System.out.println("Importing from testing.txt (create one yourself):");
        imported.printInfo();

        p3.saveToFile("output");

        System.out.println("Saved p3 to output.txt");
        System.out.println("Importing p3 from output.txt:");
        Polynomial p4 = new Polynomial(new File("output.txt"));
        p4.printInfo();

        System.out.println("p1 + p2 (add):");
        Polynomial p5 = p1.add(p2);
        p5.printInfo();
    }

}
