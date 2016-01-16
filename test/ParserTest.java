import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Arrays;


public class ParserTest {
    @Test
    public void test_arraySlicing() {
        int[] a = {1,3,4};
        int[] expected = {3,4};
        a = Arrays.copyOfRange(a, 1, a.length);
        assertArrayEquals(a, expected);
        assertEquals(a[0],3);
        assertEquals(a.length,2);
    }
}
