/*
 * Copyright (C) 2024 janinko
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
package eu.janinko.uo.tools.save;

import eu.janinko.uo.saves.Item;
import eu.janinko.uo.saves.SaveFile;
import eu.janinko.uo.saves.SaveParser;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author janinko
 */
public class ContItems {

    public static final Path SAVES_FOLDER = Paths.get("/tmp/golem");
    public static final Path OUT_PATH = Paths.get("/tmp/golem/out.scp");
    public static PrintWriter OUT;

    public static void main(String[] args) throws IOException {
        OUT = new PrintWriter(Files.newBufferedWriter(OUT_PATH));
        SaveParser parser = new SaveParser();
        SaveFile save = parser.parse(SAVES_FOLDER.resolve("sphereb17c.scp"));
        Map<Long, Item> items = save.getItems().values().stream().collect(Collectors.toMap(Item::getSerial, Function.identity()));

        //long restoreUid = 0x100ebel;
        long restoreUid = 0x49f70;
        //Map<Long, Item> contItems = save.getItems().stream().filter(i -> getTop(i, items) == restoreUid).collect(Collectors.toMap(Item::getSerial, Function.identity()));
        Set<Item> contItems = save.getItems().values().stream().filter(i -> getTop(i, items) == restoreUid).collect(Collectors.toSet());

        Set<Long> allConts = contItems.stream().map(a -> a.getCont()).collect(Collectors.toSet());
        Set<Long> printedConts = new HashSet<>();

        OUT.println(PREAMBLE);
        OUT.println("[function f_restore_save_" + restoreUid + "]");
        OUT.println("serv.B ZACATEK LAGU");
        OUT.println("serv.newitem i_bag");
        OUT.println("src.bounce <new>");
        OUT.println("local.cnt_" + restoreUid + "=<new>");
        OUT.println("");

        printedConts.add(restoreUid);

        int pastSize = contItems.size();
        while (!contItems.isEmpty()) {
            for (Iterator<Item> it = contItems.iterator(); it.hasNext();) {
                Item item = it.next();
                if (printedConts.contains(item.getCont())) {
                    final boolean isCont = allConts.contains(item.getSerial());
                    printAsScript(item, isCont);
                    if (isCont) {
                        printedConts.add(item.getSerial());
                    }
                    it.remove();
                }
            }
            if (pastSize <= contItems.size()) {
                throw new RuntimeException("contItems didn't shrink!\n" + contItems);
            }
            pastSize = contItems.size();
        }

        OUT.println("serv.B KONEC LAGU");
        OUT.println();
        OUT.println("[EOF]");

        //contItems.forEach(ContItems::printAsScript);
        //String collect = contItems.stream().flatMap(a -> a.getFields().keySet().stream()).distinct().collect(Collectors.joining(", "));
        //OUT.println("Ommited: " + collect);
//        Map<String, Integer> itemcounts = save.getItems().stream().filter(i -> getTop(i, items) == 0x100ebel).collect(Collectors.toMap(Item::getDef, Item::getAmount, Integer::sum, TreeMap::new));
        //      itemcounts.forEach((a, b) -> print(a, b));
        OUT.close();
    }

    public static long getTop(Item i, Map<Long, Item> items) {
        if (i.getCont() == null) {
            return i.getSerial();
        }
        final Item parent = items.get(i.getCont());
        if (parent == null) {
            return i.getCont();
        }
        return getTop(parent, items);
    }

    private static void print(Item i) {
        OUT.println(i.getDef() + " " + i.getAmount() + "x");
    }

    private static void print(String def, Integer count) {
        OUT.println(def + " " + count + "x");
    }

    private static void printAsScript(Item i, boolean isCont) {
        OUT.println("serv.newitem " + i.getDef());
        setKey("nameU", escapeTag(i.getName()));
        setKey("dispid", i.getDispID());
        setKey("type", i.getType());
        setKey("attr", i.getAttr());
        setKey("more1", i.getMore1());
        setKey("more2", i.getMore2());
        setKey("moreX", i.getMoreX());
        setKey("moreY", i.getMoreY());
        setKey("moreZ", i.getMoreZ());
        setKey("moreM", i.getMoreM());
        setKey("color", i.getColor());
        setKey("timer", i.getTimer());
        setKey("layer", i.getLayer());

        /*
    private Long contgrid;
    private Long quality;
         */
        if (i.getAmount() > 1) {
            OUT.println("new." + "amount" + "=" + i.getAmount());
        }
        for (Map.Entry<String, String> e : i.getTags().entrySet()) {
            OUT.println("new.tag." + e.getKey() + "=" + escapeTag(e.getValue()));
        }
        for (String event : i.getEvents()) {
            OUT.println("new.events=+" + event);
        }
        for (String speech : i.getSpeech()) {
            OUT.println("new.events=+" + speech);
        }
        if (i.getLink() != null) {
            if (!i.getDef().equals("i_flesh_head")) {
                OUT.println("src.sysmessageVioletU U itemu <new.nameu> <new> neumim nastavit: link=" + i.getTimestamp());
            }
        }
        if (!i.getFields().isEmpty()) {
            OUT.println("src.sysmessageVioletU U itemu <new.nameu> <new> neumim nastavit: " + i.getFields());
        }
        if (i.getTimestamp() != null) {
            OUT.println("src.sysmessageVioletU U itemu <new.nameu> <new> neumim nastavit: timestamp=" + i.getTimestamp());
        }

        OUT.println("new.cont=<local.cnt_" + i.getCont() + ">");
        OUT.println("new.contp=" + i.getPx() + "," + i.getPy());
        if (isCont) {
            OUT.println("local.cnt_" + i.getSerial() + "=<new>");
        }

        OUT.println("");
    }

    private static void setKey(final String key, final Object value) {
        if (value != null) {
            OUT.println("new." + key + "=" + value);
        }
    }

    private static String escapeTag(String string) {
        if (string == null) {
            return null;
        }
        return string.replace("<br>", "<def.BR>")
                .replace("<basefont color black>", "<def.contrestore_color_black>")
                .replace("<basefontcolor red>", "<def.contrestore_color_red>")
                .replace("<basefontcolor purple>", "<def.contrestore_color_purple>")
                .replace("<basefontcolor green>", "<def.contrestore_color_green>")
                .replace("<basefont size 5>", "<def.bfont_book_5>")
                .replace("<i>", "<def.contrestore_i>")
                .replace("<u>", "<def.contrestore_u>");
    }

    private static final String PREAMBLE = "[defname d_temp_def_contrestore]\n"
            + "contrestore_i <i>\n"
            + "contrestore_u <u>\n"
            + "contrestore_color_black <basefont color black>\n"
            + "contrestore_color_purple <basefontcolor purple>\n"
            + "contrestore_color_green <basefontcolor green>\n"
            + "contrestore_color_red <basefontcolor red>\n";
}
