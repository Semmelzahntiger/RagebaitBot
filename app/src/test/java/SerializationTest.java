import com.google.gson.JsonObject;
import com.tree3.util.GSONSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SerializationTest {

    @Test
    void testLaTeXInterpreted() {
        JsonObject json = new JsonObject();
        json.addProperty("interpreted", "\\int_{-3}^{2}-5 \\cdot e^{-5x-5}dx");
        String testOutput = json.toString();

        String output = GSONSerializer.getInterpreted(testOutput).interpreted();
        Assertions.assertEquals("\\int_{-3}^{2}-5 \\cdot e^{-5x-5}dx", output);
    }
}
