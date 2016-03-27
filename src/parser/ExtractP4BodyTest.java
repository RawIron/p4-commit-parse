package parser;

import static org.junit.Assert.*;

import org.apache.xmlbeans.SystemProperties;
import org.junit.Test;

import parser.ExtractP4Body;
import parser.MaxPatternMismatchReachedException;
import parser.P4Body;
import parser.Sanitizer;


public class ExtractP4BodyTest {
    private ExtractP4Body extractor() {
        ExtractP4Body extractor = new ExtractP4Body();
        return extractor;
    }

    private P4Body topic(String rawBody) {
        P4Body body = null;
        try {
            body = extractor().parse(rawBody);
        } catch (MaxPatternMismatchReachedException e) {}
        return body;
    }

    @Test
    public void test_lineSeparator() {
        String actual = "word" + System.getProperty("line.separator");
        String expected = "word\n";
        assertEquals(expected, actual);
    }

    @Test
    public void test_emptyString() {
        String rawBody = "";
        assertEquals(topic(rawBody).tfc(), "");
    }

    @Test
    public void test_oneTask_id() {
        String rawBody = "TASK ID: some task" + System.getProperty("line.separator");
        String expected = "some task";
        assertEquals(expected.trim(), topic(rawBody).taskId().trim());
    }

    @Test
    public void test_oneTaskid() {
        String rawBody = "TASKID: some task" + System.getProperty("line.separator");
        String expected = "some task";
        assertEquals(expected.trim(), topic(rawBody).taskId().trim());
    }

    @Test
    public void test_emptyTaskid() {
        String rawBody = "TASK ID: " + System.getProperty("line.separator");
        String expected = "";
        assertEquals(expected.trim(), topic(rawBody).taskId().trim());
    }

    @Test
    public void test_twoEmptyTaskid() {
        String rawBody = "TASK ID: " + System.getProperty("line.separator");
        rawBody += rawBody;
        String expected = "";
        assertEquals(expected.trim(), topic(rawBody).taskId().trim());
    }

    @Test
    public void test_twoTaskid() {
        String firstTask = "TASK ID: first task" + System.getProperty("line.separator");
        String secondTask = "TASK ID: second task" + System.getProperty("line.separator");
        String expected = "first task" + " second task";
        P4Body body = topic(firstTask + secondTask);
        assertEquals(expected.trim(), body.taskId().trim());
    }

    @Test
    public void test_twoTaskidWithTfcBetween() {
        String firstTask = "TASK ID: first task" + System.getProperty("line.separator");
        String firstTfc = "TFC: some tfc" + System.getProperty("line.separator");
        String secondTask = "TASK ID: second task" + System.getProperty("line.separator");
        String expected = "first task" + " second task";
        P4Body body = topic(firstTask + firstTfc + secondTask);
        assertEquals(expected.trim(), body.taskId().trim());
    }

    @Test
    public void test_twoTaskidOnSameLine() {
        String firstTask = "TASK ID: first task";
        String secondTask = "TASK ID: second task" + System.getProperty("line.separator");
        String expected = "first task" + "TASK ID: second task";
        P4Body body = topic(firstTask + secondTask);
        assertEquals(expected.trim(), body.taskId().trim());
    }

    @Test
    public void test_oneTaskidFollowedByNewSignature() {
        String firstTask = "TASK ID: first task" + System.getProperty("line.separator");
        String firstNew = "VERY NEW: this was just added" + System.getProperty("line.separator");
        String expected = "first task" + "VERY NEW: this was just added";
        P4Body body = topic(firstTask + firstNew);
        assertEquals(expected.trim(), body.taskId().trim());
    }

    @Test
    public void test_dbChangeChoiceAnswerY() {
        String rawBody = "DB CHANGE:Y" + System.getProperty("line.separator");
        boolean expected = true;
        assertEquals(expected, topic(rawBody).dbChange());
    }

    @Test
    public void test_dbChoiceAnswery() {
        String rawBody = "DB:y" + System.getProperty("line.separator");
        boolean expected = true;
        assertEquals(expected, topic(rawBody).dbChange());
    }

    @Test
    public void test_dbChoiceAnswerYes() {
        String rawBody = "DB:Yes" + System.getProperty("line.separator");
        boolean expected = true;
        assertEquals(expected, topic(rawBody).dbChange());
    }

    @Test
    public void test_notifyChoiceAnswerY() {
        String rawBody = "NOTIFY QA (Y/N): Y" + System.getProperty("line.separator");
        boolean expected = true;
        assertEquals(expected, topic(rawBody).notifyChoice());
    }

    @Test
    public void test_notifyTeam() {
        String rawBody = "NOTIFY:QA" + System.getProperty("line.separator");
        String expected = "QA";
        assertEquals(expected, topic(rawBody).notifyTeam());
    }

    @Test
    public void test_reason() {
        String rawBody = "REASON: fixed bug" + System.getProperty("line.separator");
        String expected = "fixed bug";
        assertEquals(expected, topic(rawBody).reasonForChange().trim());
    }

    @Test
    public void test_reasonForChange() {
        String rawBody = "REASON FOR CHANGE: fixed bug" + System.getProperty("line.separator");
        String expected = "fixed bug";
        assertEquals(expected, topic(rawBody).reasonForChange().trim());
    }
}
