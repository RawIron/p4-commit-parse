import java.util.List;
import java.util.ArrayList;


public abstract class SystemCommand {
    private String name;
    private String optargs;
    private String args;

    private List<String> command = null;

    public SystemCommand(final String commandName,
                         String commandOptargs,
                         String commandArgs)
    {
    	this.name = commandName;
    	this.optargs = commandOptargs;
    	this.args = commandArgs;
    }


    public String command() {
    	build();
        String commandLine = "";
        for (String word : command) {
            commandLine += " " + word;
        }
        return commandLine;
    }

    private void build() {
        command = new ArrayList<String>();
        command.add(name());
        command.add(optargs());
        command.add(args());
    }


    public String name() {
    	return name;
    }
    public String optargs() {
    	return optargs;
    }
    public String args() {
    	return args;
    }
    public void optargs(final String optargs) {
    	if (this.optargs.trim().isEmpty()) {
    		this.optargs = optargs.trim();
    	} else {
    		this.optargs = optargs + " " + this.optargs;
    	}
    }
    public void args(final String args) {
    	if (this.args.trim().isEmpty()) {
    		this.args = args.trim();
    	} else {
    		this.args = args + " " + this.args;
    	}
    }
}


class Echo extends SystemCommand {
	// echo -n does it work
    private static final String COMMAND_NAME = "echo";
    private static final String COMMAND_OPTARGS = "-n";
    private static final String COMMAND_ARGS = "does it work";

    public Echo() {
    	super(COMMAND_NAME, COMMAND_OPTARGS, COMMAND_ARGS);
    }
}

class P4Version extends SystemCommand {
	// p4 -V
    private static final String COMMAND_NAME = "p4";
    private static final String COMMAND_OPTARGS = "-V";
    private static final String COMMAND_ARGS = "";

    public P4Version() {
    	super(COMMAND_NAME, COMMAND_OPTARGS, COMMAND_ARGS);
    }
}

class P4SubmittedChanges extends SystemCommand {
	// P4.exe -p your.perforce.com:1555 changes -l -s submitted //depot/projects/services/@fromCL,toCL
    private static final String COMMAND_NAME = "p4.exe";
    private static final String COMMAND_OPTARGS = "changes -l -s submitted";
    private static final String COMMAND_ARGS = "";

    public P4SubmittedChanges() {
    	super(COMMAND_NAME, COMMAND_OPTARGS, COMMAND_ARGS);
    }
}
