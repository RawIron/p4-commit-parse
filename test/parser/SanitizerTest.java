package parser;

import org.junit.Test;
import static org.junit.Assert.*;


public class SanitizerTest {

    @Test
    public void shouldRemoveSingleSpacesBetweenWords() throws Exception {
        String actual = "some string with single spaces, no leading and no trailing";
        String expected = "somestringwithsinglespaces,noleadingandnotrailing";
        assertEquals(expected, Sanitizer.RemoveSpaces(actual));
    }

    @Test
    public void shouldRemoveMultiSpacesBetweenWords() throws Exception {
        String actual = "some string with    multi spaces, no   leading and no    trailing";
        String expected = "somestringwithmultispaces,noleadingandnotrailing";
        assertEquals(expected, Sanitizer.RemoveSpaces(actual));
    }

    @Test
    public void shouldRemoveLeadingAndTrailingSpaces() throws Exception {
        String actual = "     somestringwithspaces,leadingandtrailing ";
        String expected = "somestringwithspaces,leadingandtrailing";
        assertEquals(expected, Sanitizer.RemoveSpaces(actual));
    }

    @Test
    public void shouldRemoveTabFormfeedAndReturn() throws Exception {
        String actual = "one\t\ttwo\tthree\rfour\ffive";
        String expected = "onetwothreefourfive";
        assertEquals(expected, Sanitizer.RemoveSpaces(actual));
    }

    @Test
    public void shouldTranslateTabFormfeedAndReturnIntoSpaces() throws Exception {
        String actual = "one\t\ttwo\tthree\rfour\ffive";
        String expected = "one  two three four five";
        assertEquals(expected, Sanitizer.TranslateMultipleSpacesIntoOne(actual));
    }

    @Test
    public void shouldTranslateTabFormfeedAndReturnIntoPunctations() throws Exception {
        String actual = "one\n\ntwo three\rfour\ffive";
        String expected = "one; ; two three; four; five";
        assertEquals(expected, Sanitizer.TranslateNewlineIntoPunctation(actual));
    }

}