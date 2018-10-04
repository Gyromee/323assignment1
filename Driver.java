import java.io.FileNotFoundException;
import java.io.IOException;

public class Driver{
    public static void main(String[] args) throws FileNotFoundException, IOException {
    
       LexicalAnalyzer lexical = new LexicalAnalyzer("text.txt");
       lexical.start();
    }
}
