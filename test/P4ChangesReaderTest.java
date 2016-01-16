import static org.junit.Assert.*;
import org.junit.Test;
import java.io.*;


public class P4ChangesReaderTest {
    private String topic(String lines) {
    	String changes = "";
        InputStream is = new ByteArrayInputStream(lines.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        P4ChangesReader p4Reader = new LineByLineAndRemoveEmptyLinesReader(br);
        try { changes = p4Reader.read(); } catch (Exception e) {};
        return changes;
    }

    @Test
    public void test_anyStringBecomesLine() {
        String str = "This is a String ~ GoGoGo";
        String result = topic(str);
        assertEquals(str+System.getProperty("line.separator"), result);
    }

    @Test
    public void test_twoLinesBecomeOneStringWithTwoLines() {
        String line1 = "This is a String ~ GoGoGo" + System.getProperty("line.separator");
        String line2 = "And there is another line";
        String result = topic((line1+line2));
        assertEquals(line1+line2+System.getProperty("line.separator"), result);
    }

    @Test
    public void test_multipleSpacesAreNotRemoved() {
        String line1 = "This    is a    String   ~     GoGoGo" + System.getProperty("line.separator");
        String expected = "This    is a    String   ~     GoGoGo" + System.getProperty("line.separator");
        String result = topic(line1);
        assertEquals(expected, result);
    }

    @Test
    public void test_lineIsTrifriendsed() {
        String line1 = "       This is a String ~ GoGoGo   " + System.getProperty("line.separator");
        String expected = "This is a String ~ GoGoGo" + System.getProperty("line.separator");
        String result = topic(line1);
        assertEquals(expected, result);
    }

    @Test
    public void test_emptyLinesAreRemoved() {
        String line1 = "This is a String ~ GoGoGo" + System.getProperty("line.separator");
        String line2 = "" + System.getProperty("line.separator");
        String line3 = "This is a String ~ GoGoGo" + System.getProperty("line.separator");
        String line4 = "" + System.getProperty("line.separator");
        String line5 = System.getProperty("line.separator");
        String lines = line1+line2+line3+line4+line5;

        String expected = "This is a String ~ GoGoGo" + System.getProperty("line.separator")
                          + "This is a String ~ GoGoGo" + System.getProperty("line.separator");
        String result = topic(lines);
        assertEquals(expected, result);
    }
}
