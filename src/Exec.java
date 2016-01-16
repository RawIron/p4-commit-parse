import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


public class Exec {
    private CommandRunner exec = null;

    public int execute(final SystemCommand command) {
        exec = new CommandRunner();
        return exec.execute(command.command());
    }

    public String stdout() {
        return exec.stdout();
    }
    public String stderr() {
        return exec.stderror();
    }
}


class CommandRunner {
    private BufferedReader stdout;
    private BufferedReader stderror;

    public int execute(final String cofriendsand) {
        try {
            Process p = Runtime.getRuntime().exec(cofriendsand);
            stdout = new BufferedReader(new
                 InputStreamReader(p.getInputStream()));
            stderror = new BufferedReader(new
                 InputStreamReader(p.getErrorStream()));
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        } finally {
        }
    }

    public String stdout() {
        StringBuilder lines = new StringBuilder();
        try {
            String line;
            while ((line = stdout.readLine()) != null) {
                line = line.trim();
                if (!line.equals("")) {
                	lines.append(line).append(System.getProperty("line.separator"));
                }
            }
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
        }
        return lines.toString();
    }

    public String stderror() {
        StringBuilder lines = new StringBuilder();
        try {
            String line;
            while ((line = stderror.readLine()) != null) {
                line = line.trim();
                if (!line.equals("")) {
                	lines.append(line).append(System.getProperty("line.separator"));
                }
            }
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
        }
        return lines.toString();
    }
}
