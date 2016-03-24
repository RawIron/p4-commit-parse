package parser;

public class P4Version extends SystemCommand {
	// p4 -V
    private static final String COMMAND_NAME = "p4";
    private static final String COMMAND_OPTARGS = "-V";
    private static final String COMMAND_ARGS = "";

    public P4Version() {
    	super(COMMAND_NAME, COMMAND_OPTARGS, COMMAND_ARGS);
    }
}
