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

import eu.janinko.andaria.spherescript.sphere.objects.Itemdef;
import eu.janinko.andaria.spherescript.sphere.parsers.SphereParser;
import eu.janinko.uo.saves.Item;
import eu.janinko.uo.saves.SaveParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author janinko
 */
public class Cernohrad {
    public static final Path saveFolder = Paths.get("/home/janinko/Ultima/savy/201129");
    private static final Path SCP_PATH = Paths.get("/home/janinko/Ultima/andaria/sphere/");
    public static void main(String[] args) throws IOException {

        SphereParser parser = new SphereParser();
        try (Stream<Path> files = Files.walk(SCP_PATH)) {
            files.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".scp"))
                    .forEach(p -> {
                        try {
                            parser.parse(p);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
        }
        List<String> asList = Arrays.asList("i_arrow_acid","i_arrow_cold","i_arrow_energy","i_arrow_fire","i_arrow_full_gold","i_arrow_mytheril","i_sipka","i_xbolt_cold","i_xbolt_copper","i_xbolt_energy","i_xbolt_fire","i_xbolt_full_gold","i_xbolt_full_silver","i_xbolt_heavy","i_xbolt_mytheril","i_xbolt_wood");
        Set<String> foo = new HashSet<>(asList);
        //Thread.sleep(1000);
        for (Itemdef itemdef : parser.getItemdefs()) {
            String id = itemdef.getDefname();
            if(id == null){
                id = itemdef.getDefid();
            }
            if(foo.contains(id)){
                System.out.println(id + "\t" + itemdef.getNameSingular());
            }

        }
    }

    public static void mainCount(String[] args) throws IOException {

        SaveParser parser = new SaveParser();
        List<Item> predBitvou = Files.walk(saveFolder)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().contains("16"))
                .filter(p -> p.toString().endsWith("scp"))
                .map(f -> {
                    try {
                        return parser.parse(f);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .flatMap(s -> s.getItems().values().stream())
                .collect(Collectors.toList());
        List<Item> poBitve = Files.walk(saveFolder)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().contains("06"))
                .filter(p -> p.toString().endsWith("scp"))
                .map(f -> {
                    try {
                        return parser.parse(f);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .flatMap(s -> s.getItems().values().stream())
                .collect(Collectors.toList());

/*        Map<String, ItemCount> itemy = new TreeMap<>();
        for (Item item : predBitvou) {
            itemy.compute(item.getDef(), (k, v) ->{ if(v == null) return ItemCount.pred(item.getAmount()); else { v.pred += item.getAmount(); return v;}});
        }
        for (Item item : poBitve) {
            itemy.compute(item.getDef(), (k, v) ->{ if(v == null) return ItemCount.po(item.getAmount()); else { v.po += item.getAmount(); return v;}});
        }
        for (Map.Entry<String, ItemCount> e : itemy.entrySet()) {
            ItemCount itemCount = e.getValue();
            if(itemCount.pred != itemCount.po){
                System.out.println(e.getKey() + ": " + itemCount.pred + " -> " + itemCount.po);
            }
        }*/
    }

    private static class ItemCount{
       int pred = 0;
       int po = 0;

       private static ItemCount pred(int i){
           ItemCount r = new ItemCount();
           r.pred = i;
           return r;
       }

       private static ItemCount po(int i){
           ItemCount r = new ItemCount();
           r.pred = i;
           return r;
       }
    }
}
