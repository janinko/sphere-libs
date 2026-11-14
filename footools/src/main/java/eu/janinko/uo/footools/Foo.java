/*
 * Copyright (C) 2021 janinko
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.janinko.uo.footools;

import eu.janinko.andaria.ultimasdk.UOFiles;
import eu.janinko.andaria.ultimasdk.files.Hues;
import eu.janinko.andaria.ultimasdk.files.hues.Hue;
import eu.janinko.andaria.ultimasdk.graphics.Color;
import eu.janinko.andaria.spherescript.sphere.objects.Itemdef;
import eu.janinko.andaria.spherescript.sphere.parsers.SphereParser;
import eu.janinko.uo.saves.Character;
import eu.janinko.uo.saves.SaveFile;
import eu.janinko.uo.saves.SaveParser;
import eu.janinko.uo.util.ValueParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author janinko
 */
public class Foo {

    private static Random generator = new Random();
    public static final Path SAVES_FOLDER = Paths.get("/home/janinko/Ultima/savy");
    public static final Path UO_FOLDER = Paths.get("/home/janinko/Ultima/hra");
    public static final Path SCRIPTS_FOLDER = Paths.get("/home/janinko/Ultima/andaria/sphere");

    public static void main(String[] args) throws IOException {
        //UOFiles ¨files = new UOFiles(UO_FOLDER);
//        Hues hues = files.getHues();
//        transformAll(hues);
//        UOFiles.saveToDir(hues, Paths.get("/tmp"));

        Map<String, Long> db = new HashMap<>();
        fixCreateMakeDB(Path.of("/home/janinko/Ultima/savy/171013/spherechars.scp"), db);
        fixCreateUpdateDB(Path.of("/home/janinko/Ultima/savy/191223/spherechars.scp"), db);
        fixCreateUpdateDB(Path.of("/home/janinko/Ultima/savy/210324/sphereb16c.scp"), db);
        fixCreateUpdateDB(Path.of("/home/janinko/Ultima/savy/251228/spherechars.scp"), db);
        fixCreateUpdateDB(Path.of("/home/janinko/Ultima/savy/260105/spherechars.scp"), db);
        Map<String, Long> updateDB = fixCreateApplyDB(Path.of("/home/janinko/Ultima/savy/260105/spherechars.scp"), db);
        fixCreateUpdateFile(Path.of("/home/janinko/Ultima/savy/260105/spherechars.scp"), Path.of("/tmp/spherechars.scp"), updateDB);

    }

    private static void fixCreateMakeDB(Path path, Map<String, Long> db) throws IOException {
        SaveFile save;
        try (SaveParser parser = new SaveParser()) {
            save = parser.parse(path);
        }
        for (Character character : save.getCharacters().values()) {
            String account = character.getAccount();
            Long serial = character.getSerial();
            long age = character.getCreate();
            long created = save.getTime() - age * 10;

            db.put(serial + "-" + account, created);
        }
        System.out.println("Save: " + save.getTime());
    }


    private static long fixCreateUpdateDB(Path path, Map<String, Long> db) throws IOException {
        SaveFile save;
        try (SaveParser parser = new SaveParser()) {
            save = parser.parse(path);
        }
        long lowestDiff = Long.MAX_VALUE;
        for (Character character : save.getCharacters().values()) {
            String account = character.getAccount();
            Long serial = character.getSerial();
            String key = serial + "-" + account;

            long storedCreate = db.getOrDefault(key, 0L);
            if (storedCreate == 0) {
                continue;
            }

            long createValue = character.getCreate();
            if (createValue >= 0) {
                long guessedCreate = save.getTime() - createValue * 10;
                long diff = guessedCreate - storedCreate;
                if (lowestDiff > diff) {
                    lowestDiff = diff;
                }
                if (storedCreate > 1000) {
                    //System.out.printf("0%x: %d %n", serial, createValue);
                    //System.out.println(key + ": " + guessedCreate + " <- " + storedCreate + " +" + diff);
                }
            }

        }
        System.out.println("Lowest diff: " + lowestDiff);

        long max = 0, min = Long.MAX_VALUE;
        long mmax = 0, mmin = Long.MAX_VALUE;
        for (Character character : save.getCharacters().values()) {
            String account = character.getAccount();
            Long serial = character.getSerial();
            String key = serial + "-" + account;
            if (db.containsKey(key)) {
                continue;
            }

            long createValue = character.getCreate();

            long create;
            if (createValue >= 0) {
                create = save.getTime() - createValue * 10 - lowestDiff;
                db.put(key, create);
                if (create > mmax) {
                    mmax = create;
                }
                if (create < mmin) {
                    mmin = create;
                }
            } else {
                create = (save.getTime() - createValue * 10) & 0xffffffffL;
                System.err.printf("0%x: %d %n", serial, create);
                if (create > max) {
                    max = create;
                }
                if (create < min) {
                    min = create;
                }
            }
        }
        System.out.println("Save: " + save.getTime());
        System.out.println("Good? Min: " + mmin + "  max: " + mmax);
        System.out.println("Bad   Min: " + min + "  max: " + max);
        return lowestDiff;
    }

    private static Map<String, Long> fixCreateApplyDB(Path path, Map<String, Long> db) throws IOException {
        SaveFile save;
        try (SaveParser parser = new SaveParser()) {
            save = parser.parse(path);
        }


        long lowestDiff = Long.MAX_VALUE;
        for (Character character : save.getCharacters().values()) {
            String account = character.getAccount();
            Long serial = character.getSerial();
            String key = serial + "-" + account;

            long storedCreate = db.getOrDefault(key, 0L);
            if (storedCreate == 0) {
                continue;
            }

            long createValue = character.getCreate();
            if (createValue >= 0) {
                long guessedCreate = save.getTime() - createValue * 10;
                long diff = guessedCreate - storedCreate;
                if (lowestDiff > diff) {
                    lowestDiff = diff;
                }
            }
        }
        System.out.println("Lowest diff: " + lowestDiff);

        Map<String, Long> updateMap = new HashMap<>();
        for (Character character : save.getCharacters().values()) {
            String account = character.getAccount();
            Long serial = character.getSerial();
            String key = serial + "-" + account;

            long storedCreate = db.getOrDefault(key, 0L);
            String stringserial = String.format("0%x", serial);
            long createValue = (save.getTime() - (storedCreate + lowestDiff))/10;
            updateMap.put(stringserial, createValue);
        }
        return updateMap;
    }

    public static void fixCreateUpdateFile(Path inputFile, Path outputFile, Map<String, Long> serialToTime) throws IOException {

        try (InputStream is = Files.newInputStream(inputFile); OutputStream os = Files.newOutputStream(outputFile)) {

            ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream();
            byte[] lastCreateLine = null;
            int b;

            while ((b = is.read()) != -1) {
                lineBuffer.write(b);

                if (b == '\n') {
                    byte[] currentLine = lineBuffer.toByteArray();
                    String lineStr = new String(currentLine).trim();
                    //System.err.println(">>> " + lineStr);
                    if (lineStr.startsWith("CREATE=")) {
                        // Store this line; we don't know the serial yet
                        lastCreateLine = currentLine;
                    } else if (lineStr.startsWith("SERIAL=") && lastCreateLine != null) {
                        String serial = lineStr.substring(7); // Extract serial
                        Long newTime = serialToTime.get(serial);

                        if (newTime != null) {
                            // Write the UPDATED create line
                            os.write(("CREATE=" + newTime + System.lineSeparator()).getBytes());
                        } else {
                            // Serial not found, write the original create line
                            os.write(lastCreateLine);
                            System.err.println("????!");
                        }
                        // Write the serial line as-is
                        os.write(currentLine);
                        lastCreateLine = null; // Reset
                    } else {
                        // If we had a pending CREATE line that wasn't followed by a SERIAL
                        if (lastCreateLine != null) {
                            os.write(lastCreateLine);
                            lastCreateLine = null;
                            System.err.println("????");
                        }
                        os.write(currentLine);
                    }
                    lineBuffer.reset();
                }
            }

            // Handle any trailing data without a newline
            if (lineBuffer.size() > 0) {
                os.write(lineBuffer.toByteArray());
            }
        }
    }


    private static void transformAll(Hues hues) {
        for (Hue hue : hues.getHues()) {
            pinkize(hue);
        }
    }

    private static void pinkize(Hue h) {
        Color[] colors = h.getColors();
        for (int i = 0; i < colors.length; i++) {
            colors[i] = pinkize(colors[i]);
        }
    }

    private static Color pinkize(Color c) {
        byte max = c.get5Blue();
        int sum = c.get5Green() + c.get5Red();
        if (c.get5Green() > max) {
            max = c.get5Green();
            sum = c.get5Blue() + c.get5Red();
        }
        if (c.get5Red() > max) {
            max = c.get5Red();
            sum = c.get5Blue() + c.get5Green();
        }
        if (sum % 2 == 1) {
            sum += generator.nextInt(2);
        }
        short avg = (short) (sum / 2);
        return Color.getInstance(max, avg, avg);
    }
}
