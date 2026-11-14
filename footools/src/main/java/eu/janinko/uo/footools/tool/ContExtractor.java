/*
 * Copyright (C) 2020 janinko
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
package eu.janinko.uo.footools.tool;

import eu.janinko.uo.saves.Item;
import eu.janinko.uo.saves.SaveParser;
import eu.janinko.uo.saves.SaveFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 * @author janinko
 */
public class ContExtractor {
    private static Map<String, Integer> allItems = new HashMap<>();
    private static final Path SAVES_PATH = Paths.get("/mnt/data/Ultima/savy/200525");
    public static void main(String[] args) throws IOException {

        parse("spherechars.scp");

        Map<String, Integer> poItems = allItems;
        allItems = new HashMap<>();
        parse("sphereb01c.scp");
        Map<String, Integer> predItems = allItems;

        diff(predItems, poItems);
    }

    private static void parse(String filename) throws IOException {
        SaveParser parser = new SaveParser();
        SaveFile savefile = parser.parse(SAVES_PATH.resolve(filename));
        Map<Long, List<Item>> itemy = savefile.getItems().values().stream()
                .filter(i -> i.getCont() != null)
                .collect(Collectors.groupingBy(Item::getCont));
        //printItems(itemy, 447154, "");
        sumItems(itemy, 447154);
        sumItems(itemy, 269402);
        sumItems(itemy, 620546);
        sumItems(itemy, 410409);
        sumItems(itemy, 452694);
    }

    private static void sumItems(Map<Long, List<Item>> contMap, long i) {
        List<Item> itemy = contMap.get(i);
        if(itemy != null){
            for (Item item : itemy) {
                allItems.merge(item.getDef(), (int) item.getAmount(), Integer::sum);
                sumItems(contMap, item.getSerial());
            }
        }
    }

    private static void printItems(Map<Long, List<Item>> contMap, long i, String padd) {
        List<Item> itemy = contMap.get(i);
        if(itemy != null){
            for (Item item : itemy) {
                System.out.println(padd + item.getDef() + "\t" + item.getAmount());
                printItems(contMap, item.getSerial(), padd+" ");
            }
        }
    }

    private static void diff(Map<String, Integer> predItems, Map<String, Integer> poItems) {
        Set<String> names = new TreeSet<>();
        names.addAll(predItems.keySet());
        names.addAll(poItems.keySet());
        for (String name : names) {
            int predCount = predItems.getOrDefault(name, 0);
            int poCount = poItems.getOrDefault(name, 0);
            int diff = poCount - predCount;
            if(diff != 0){
                System.out.println(name + "\t" + diff);
            }
        }
    }
}
