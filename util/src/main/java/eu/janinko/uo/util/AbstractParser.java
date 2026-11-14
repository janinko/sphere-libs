package eu.janinko.uo.util;

/**
 *
 * @author janinko
 */
public class AbstractParser {

    protected ScriptReader it;

    public void reset(ScriptReader it) {
        this.it = it;
    }

    protected long parseNum(String line) {
        line = line.trim();
        try {
            return ValueParser.parseNum(line);
        } catch (NumberFormatException ex) {
            throw it.error("Failed to parse number '" + line + "'", ex);
        }
    }

}
