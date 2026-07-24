import com.tree3.audio.InputStreamAudioPlayerSendHandler;
import com.tree3.util.LogUtil;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioTest {

    @Test
    void readsAudioFile() throws IOException {
        InputStream in = getClass().getResourceAsStream("tiki.mp3");
        Assertions.assertNotNull(in);
        byte[] buf = in.readAllBytes();
        Assertions.assertTrue(buf.length > 0);
    }

    @Test
    void senderProvidesData() {
        InputStream in = getClass().getResourceAsStream("tiki.mp3");
        Assertions.assertNotNull(in);

        InputStreamAudioPlayerSendHandler handler = new InputStreamAudioPlayerSendHandler();
        Assertions.assertDoesNotThrow(() -> handler.load(in));

//        boolean loaded = handler.load(in);
//        Assertions.assertTrue(loaded);
//        LogUtil.getLogger().debug(handler.getAudioStream().getFormat().toString());
    }
}
