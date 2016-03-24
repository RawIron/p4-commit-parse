package parser;

import junit.framework.TestCase;
import parser.Exec;
import parser.P4Version;
import parser.SystemCommand;


public class ExecTest extends TestCase {

    public final void test_runP4Version() {
        SystemCommand p4Version = new P4Version();
        Exec exec = new Exec();
        exec.execute(p4Version);
        assertTrue(exec.stdout().startsWith("Perforce"));
    }
}
