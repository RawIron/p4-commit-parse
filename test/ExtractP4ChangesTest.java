import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

public class ExtractP4ChangesTest {
    private ExtractP4Changes extractor() {
    	Settings settings = new Settings();
    	String[] ignoreNames = {"ignoreMe", "meToo"};
    	settings.ignoreNames(ignoreNames);
        ExtractP4Changes extractor = new ExtractP4Changes(settings);
        return extractor;
    }
    private ArrayList<P4Change> topic(String contents) {
        ArrayList<P4Change> changes = null;
        try {
            changes = extractor().parse(contents);
        } catch (MaxPatternMismatchReachedException e) {}
        return changes;
    }

    @Test
    public void test_emptyChanges() {
        String changeList = "";
        assertEquals(topic(changeList).size(), 0);
    }

    @Test
    public void test_oneChangeWithJustDeveloperName() {
        String changeList = "Change 1 ?? 2013/03/14 ?? tester";
        ArrayList<P4Change> changes = topic(changeList);

        assertEquals(changes.size(), 1);
        assertEquals(changes.get(0).id(), 1);
        assertEquals(changes.get(0).developer(), "tester");
        assertEquals(changes.get(0).dateWhenChangeSubmitted(), "2013/03/14");
    }

    @Test
    public void test_oneChangeWithBrokenDeveloperEmail() {
        String changeList = "Change 1 ?? 2013/03/14 ?? tester@thelongdomain@bademail.broken";
        ArrayList<P4Change> changes = topic(changeList);

        assertEquals(changes.size(), 1);
        assertEquals(changes.get(0).id(), 1);
        assertEquals(changes.get(0).developer(), "tester");
        assertEquals(changes.get(0).dateWhenChangeSubmitted(), "2013/03/14");
    }

    @Test
    public void test_oneChangeWithIgnoreName() {
        String changeList = "Change 11 ?? 2013/03/14 ?? THENTDOMAIN\\ignoreMe@thelongdomain@bademail.broken";
        ArrayList<P4Change> changes = topic(changeList);

        assertEquals(changes.size(), 1);
        assertEquals(changes.get(0).id(), 11);
        assertEquals(changes.get(0).developer(), "THENTDOMAIN\\ignoreMe");
        assertEquals(changes.get(0).ignoreDeveloper(), true);
    }

    @Test
    public void test_oneChangeWithDifferentDateformat() {
        String changeList = "Change 1 ?? 03/14/2013 ?? tester@thelongdomain.ok";
        ArrayList<P4Change> changes = topic(changeList);
        assertEquals(changes.size(), 1);
        assertEquals(changes.get(0).id(), 1);
        assertEquals(changes.get(0).developer(), "tester");
        assertEquals(changes.get(0).dateWhenChangeSubmitted(), "03/14/2013");
    }

    @Test
    public void test_oneChangeWithNotNumericId() {
        String changeList = "Change 1A23 ?? 03/14/2013 ?? tester@thelongdomain.ok";
        ArrayList<P4Change> changes = topic(changeList);
        assertEquals(0, changes.size());
    }

    // currently this is not matched
    public void test_oneChangeWithBrokenPositionsOfWords() {
        String changeList = "Change 1 2013/03/14 ?? tester@thelongdomain@bademail.broken";
        ArrayList<P4Change> changes = topic(changeList);
        assertEquals(changes.size(), 1);
        assertEquals(changes.get(0).id(), 1);
        assertEquals(changes.get(0).developer(), "");
        assertEquals(changes.get(0).dateWhenChangeSubmitted(), "");
    }

    @Test
    public void test_twoChanges() {
        String change1 = "Change 1 ?? 03/14/2013 ?? oneTester@thelongdomain.ok" + System.getProperty("line.separator");
        String change2 = "Change 123 ?? 03/15/2013 ?? anotherTester@thelongdomain.ok";
        ArrayList<P4Change> changes = topic(change1 + change2);

        assertEquals(changes.size(), 2);

        assertEquals(changes.get(0).id(), 1);
        assertEquals(changes.get(0).developer(), "oneTester");
        assertEquals(changes.get(0).dateWhenChangeSubmitted(), "03/14/2013");

        assertEquals(changes.get(1).id(), 123);
        assertEquals(changes.get(1).developer(), "anotherTester");
        assertEquals(changes.get(1).dateWhenChangeSubmitted(), "03/15/2013");
    }
}
