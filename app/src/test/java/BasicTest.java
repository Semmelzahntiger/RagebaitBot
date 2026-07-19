import com.tree3.util.LaTeXParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.UUID;

public class BasicTest {

    public static boolean inputStreamToPngFile(InputStream inputStream, String filePath) {
        try (InputStream in = inputStream;
             OutputStream out = new FileOutputStream(filePath)) {
            in.transferTo(out);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Test
    void test() {
        boolean test = true;
        Assertions.assertTrue(test);
    }
    @Test
    void testLatexParser() {
        Optional<InputStream> result = LaTeXParser.formulaToInputStream("\\frac{a}{b} + \\sqrt{x^2}");
        Assertions.assertTrue(result.isPresent());
        boolean written = inputStreamToPngFile(result.get(), UUID.randomUUID() + ".png");
        Assertions.assertTrue(written);
    }
}
