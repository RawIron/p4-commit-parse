package parser;

import static org.junit.Assert.*;

import org.junit.Test;
import parser.Echo;
import parser.P4SubmittedChanges;
import parser.SystemCommand;


public class SystemCommandTest {
	@Test
	public void test_wordsInEcho() {
		SystemCommand echo = new Echo();
		Assert.assertEquals("echo", echo.name());
		Assert.assertEquals("-n", echo.optargs());
		Assert.assertEquals("does it work", echo.args());
	}

	@Test
	public void test_echo() {
		SystemCommand echo = new Echo();
		Assert.assertEquals("echo -n does it work", echo.command().trim());
	}

	@Test
	public void test_p4SubmittedChanges() {
		SystemCommand p4 = new P4SubmittedChanges();
		p4.optargs("-p p4host.example.com");
		p4.args("//depot/project/");
		Assert.assertEquals("p4.exe -p p4host.example.com changes -l -s submitted //depot/project/", p4.command().trim());
	}
}
