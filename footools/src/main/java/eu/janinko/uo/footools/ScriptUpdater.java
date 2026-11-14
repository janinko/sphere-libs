package eu.janinko.uo.footools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class ScriptUpdater {


    private enum ScriptState {
        ITEMDEF_HEADER,
        ITEMDEF_TRIGGER,
        CHARDEF_HEADER,
        CHARDEF_TRIGGER,
        OTHER
    }


    private final Matcher chardefMatcher = Pattern.compile("\\[chardef ([a-z0-9_]+)]").matcher("");

    private final Matcher itemdefdefMatcher = Pattern.compile("\\[itemdef ([a-z0-9_]+)]").matcher("");
    private final Matcher triggerMatcher = Pattern.compile("on=([@a-z0-9_,]+)").matcher("");
    private final Matcher armorMatcher = Pattern.compile("armor=([0-9]+)").matcher("");
    private final Matcher defnameMatcher = Pattern.compile("defname=([a-z0-9_]+)").matcher("");

    private ScriptState state = ScriptState.OTHER;
    private ScriptState previousState = ScriptState.OTHER;
    private String defname = "";
    private final Map<String, Integer> defsToAR;

    private boolean charARUpdated = false;

    private final InputStream in;
    private final PrintStream out;

    public ScriptUpdater(InputStream in, PrintStream out) throws IOException {
        this.in = in;
        this.out = out;

        defsToAR = Files.lines(Path.of("/tmp/ardefs")).map(s -> s.split(";"))
                .collect(Collectors.toMap(s -> s[0], s -> Integer.valueOf(s[1])));
    }

    public static void main(String[] args) throws IOException {
        ScriptUpdater sup = new ScriptUpdater(System.in, System.out);
        sup.parse();
    }

    private void parse() throws IOException {
        int byteRead;
        byte[] buff = new byte[16384];
        int b = 0;
        while ((byteRead = in.read()) != -1) {
            buff[b++] = (byte) byteRead;
            if (byteRead == '\n') {
                // Process this line's bytes
                processSingleLineBytes(buff, b);

                // Clear for the next line
                b = 0;
            }
        }
        if(b > 0) {
            processSingleLineBytes(buff, b);
        }
    }

    private void processSingleLineBytes(byte[] lineBytes, int length) throws IOException {
        String line = new String(lineBytes, 0, length -1, StandardCharsets.UTF_8);
        workLine(line, lineBytes, length);
    }

    private void workLine(String line, byte[] lineBytes, int length) throws IOException {
        String trimLine = line.trim().toLowerCase().replaceFirst("//.*", "");
        //System.err.println("WORK: " + trimLine  );
        if (checkTransition(trimLine)) {
            //System.err.println("MATCHED: " + trimLine  + " byteS: " + Arrays.toString(lineBytes) );
            if(state == ScriptState.CHARDEF_HEADER){
                charARUpdated = false;
            }
            if(previousState == ScriptState.CHARDEF_HEADER && !charARUpdated){
                if (forceCharAR()) {
                    charARUpdated = true;
                    out.println();
                }
            }
            out.write(lineBytes, 0, length);
            return;
        }
        if (defnameMatcher.reset(trimLine).matches()) {
            //System.err.println("Updating defname " + defname + " to " + defnameMatcher.group(1));
            defname = defnameMatcher.group(1);
        }

        if (state == ScriptState.ITEMDEF_HEADER) {
            if (armorMatcher.reset(trimLine).matches()) {
                //updateItemAR();
                out.write(lineBytes, 0, length);
            }else {
                out.write(lineBytes, 0, length);
            }
        } else if (state == ScriptState.CHARDEF_HEADER) {
            if (armorMatcher.reset(trimLine).matches()) {
                if (updateCharAR()) {
                    charARUpdated = true;
                } else {
                    out.write(lineBytes, 0, length);
                }
                //out.write(lineBytes, 0, length);
            }else {
                out.write(lineBytes, 0, length);
            }

        } else {
            out.write(lineBytes, 0, length);
        }
    }

    private boolean updateCharAR() {
        Integer i = defsToAR.get(defname);
        if (i == null) {
            System.err.println("Unknown defname!! " + defname);
            return false;
        }
        String s = armorMatcher.replaceFirst("ARMOR=" + i);
        out.println(s);
        return true;
    }

    private boolean forceCharAR() {
        Integer i = defsToAR.get(defname);
        if (i == null) {
            System.err.println("Unknown defname!! " + defname);
            return false;
        }
        String s = "ARMOR=" + i;
        out.println(s);
        return true;
    }

    private void updateItemAR() {
        int oldAR = Integer.parseInt(armorMatcher.group(1));
        double ar = 60.0 / Math.exp(-0.022 * oldAR) - 60.0;
        String newAR = Long.toString(Math.round(ar));
        String s = armorMatcher.replaceFirst("ARMOR=" + newAR);
        out.println(s);
    }


    private boolean checkTransition(String line) {
        //System.err.println("CHECKING LINE: " + line);
        if (chardefMatcher.reset(line).matches()) {
            defname = chardefMatcher.group(1);
            previousState = state;
            state = ScriptState.CHARDEF_HEADER;
            return true;
        }
        if (itemdefdefMatcher.reset(line).matches()) {
            defname = itemdefdefMatcher.group(1);
            previousState = state;
            state = ScriptState.ITEMDEF_HEADER;
            return true;
        }
        if (triggerMatcher.reset(line).matches()) {
            switch (state) {
                case CHARDEF_HEADER:
                case CHARDEF_TRIGGER:
                    previousState = state;
                    state = ScriptState.CHARDEF_TRIGGER;
                    break;
                case ITEMDEF_HEADER:
                case ITEMDEF_TRIGGER:
                    previousState = state;
                    state = ScriptState.ITEMDEF_TRIGGER;
                    break;
                default:
                    previousState = state;
                    state = ScriptState.OTHER;
            }
            return true;
        }
        return false;
    }

}
