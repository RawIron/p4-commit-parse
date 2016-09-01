package parser;

import junit.framework.TestCase;
import parser.Exec;
import parser.SystemCommand;


public class ExecTest extends TestCase {

    public final void test_runEcho() {
        Echo echo = new Echo();
        Exec exec = new Exec();
        exec.execute(echo);
        assertTrue(exec.stdout().startsWith("does"));
    }
}
