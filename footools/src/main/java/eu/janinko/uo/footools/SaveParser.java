/*
 * Copyright (C) 2018 Honza Brázdil &lt;jbrazdil@redhat.com&gt;
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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.janinko.uo.saves.SaveFile;

/**
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */
public class SaveParser {

    private static final Path SAVES_PATH = Paths.get("/mnt/data/tmp/material");////home/janinko/Ultima/andaria/sphere/");

    public static void main(String[] args) throws IOException {

        //printSpawns(save);
        List<String> defList = getTrophyDeflist();

        Map<String, String> defMapping = new HashMap();
        defMapping.put("i_ingot_copper", "i_ore_copper");
        defMapping.put("i_ingot_gold", "i_ore_gold");
        defMapping.put("i_ingot_iron", "i_ore_iron");
        defMapping.put("i_ingot_mytheril", "i_ore_mytheril");
        defMapping.put("i_ingot_silver", "i_ore_silver");

        Set<String> defs = new HashSet<>();
        defs.addAll(defList);
        //defs.addAll(defMapping.keySet());

        eu.janinko.uo.saves.SaveParser parser = new eu.janinko.uo.saves.SaveParser();
        SaveFile save;

        List<String> dates = new ArrayList<>();
        dates.add("2019-01-14");
        dates.add("2019-04-10");
        dates.add("2019-05-01");
        dates.add("2019-07-02");
        dates.add("2019-08-05");
        dates.add("2019-09-29");
        dates.add("2019-12-23");
        dates.add("2020-01-25");
        dates.add("2020-02-04");
        dates.add("2020-02-15");
        dates.add("2020-03-08");
        dates.add("2020-03-19");
        dates.add("2020-03-30");
        dates.add("2020-04-04");
        dates.add("2020-04-13");
        dates.add("2020-04-24");

        StringBuilder sb = new StringBuilder();
        sb.append("cas");
        for (String def : defList) {
            sb.append('\t').append(def);
        }
        sb.append('\n');
        for (String date : dates) {
            System.out.println("Doing " + date);
            save = null;
            System.gc();
            Map<String, Integer> pocty = new HashMap<>();
            save = parser.parse(SAVES_PATH.resolve(date + "c.scp"));
            save.getItems().values().stream()
                    .filter(i -> defs.contains(i.getDef()))
                    .forEach(i -> pocty.merge(mapDef(i.getDef(), defMapping), intOrOne(i.getFields().get("AMOUNT")), Integer::sum));

            save = parser.parse(SAVES_PATH.resolve(date + "w.scp"));
            save.getItems().values().stream()
                    .filter(i -> defs.contains(i.getDef()))
                    .forEach(i -> pocty.merge(mapDef(i.getDef(), defMapping), intOrOne(i.getFields().get("AMOUNT")), Integer::sum));

            sb.append(date);
            for (String def : defList) {
                sb.append('\t').append(pocty.getOrDefault(def, 0));
            }
            sb.append('\n');
        }
        System.out.println(sb);
    }

    private static List<String> getMiningDeflist() {
        List<String> defList = new ArrayList<>();
        defList.add("i_clay");
        defList.add("i_coal");
        defList.add("i_gem_amber");
        defList.add("i_gem_amethyst_4");
        defList.add("i_gem_citrine");
        defList.add("i_gem_diamond");
        defList.add("i_gem_emerald");
        defList.add("i_gem_ruby");
        defList.add("i_gem_sapphire");
        defList.add("i_gem_star_sapphire_3");
        defList.add("i_gem_tourmaline");
        defList.add("i_ore_copper");
        defList.add("i_ore_gold");
        defList.add("i_ore_iron");
        defList.add("i_ore_mytheril");
        defList.add("i_ore_silver");
        defList.add("i_reag_obsidian");
        defList.add("i_reag_pig_iron");
        defList.add("i_reag_sulfur_ash");
        defList.add("i_stone_build");
        defList.add("i_stone_marble");
        defList.add("i_stone_sand");
        defList.add("i_tinderbox");
        return defList;
    }

    private static List<String> getTrophyDeflist() {
        List<String> defList = new ArrayList<>();
        defList.add("i_trophy_zmije");
        defList.add("i_trophy_kobra");
        defList.add("i_trophy_mamba");
        defList.add("i_trophy_anakonda");
        defList.add("i_trophy_obri_pavouk");
        defList.add("i_trophy_desivy_pavouk");
        defList.add("i_trophy_sklipkan");
        defList.add("i_trophy_upiriho_pavouka");
        defList.add("i_trophy_vtackar");
        defList.add("i_trophy_snovac");
        defList.add("i_trophy_cerna_vdova");
        defList.add("i_trophy_tarantule");
        defList.add("i_trophy_mephitis");
        defList.add("i_trophy_vrrk");
        defList.add("i_trophy_skret");
        defList.add("i_trophy_aligator");
        defList.add("i_trophy_jesterak");
        defList.add("i_trophy_gorila");
        defList.add("i_trophy_krava");
        defList.add("i_trophy_katakan");
        defList.add("i_trophy_parasekt");
        defList.add("i_trophy_zabi_matka");
        defList.add("i_trophy_druid");
        defList.add("i_trophy_dabel");
        defList.add("i_trophy_strazny_golem");
        defList.add("i_trophy_golem_strazce");
        defList.add("i_trophy_kuresekt");
        defList.add("i_trophy_kurekral");
        defList.add("i_trophy_minotaur");
        defList.add("i_trophy_vudce_lidozroutu");
        defList.add("i_trophy_pavouci_kral");
        defList.add("i_trophy_pavouci_kralovna");
        defList.add("i_trophy_alfa_vrrk");
        defList.add("i_trophy_alfa_bachyne");
        defList.add("i_trophy_alfa_dvanacterak");
        defList.add("i_trophy_alfa_lisak");
        defList.add("i_trophy_alfa_kralik");
        return defList;
    }

    public static String mapDef(String key, Map<String, String> map) {
        String ret = map.get(key);
        return ret == null ? key : ret;
    }

    public static Integer intOrOne(String s) {
        System.out.println("Pocitam " + s);
        if (s == null) {
            return 1;
        }
        return Integer.valueOf(s);
    }

    public static void printSpawns(SaveFile save) {
        Map<String, Spawn> spawny = new HashMap<>();
        save.getItems().values().stream()
                .filter(i -> i.getDef().contains("i_worldgem_bit]"))
                .filter(i -> !"mr_nothing".equals(i.get("MORE1")))
                //.filter(i -> Integer.parseInt(i.get("P").split(",")[0]) < 5000)
                .filter(i -> i.get("TYPE") == null)
                .forEach(i -> {
                    String[] morep = i.get("MOREP", "1,1").split(",");
                    String p = i.get("P");
                    int min = Integer.parseInt(morep[0]);
                    int minf = min <= 0 ? 1 : min;
                    int max = Integer.parseInt(morep[1]);
                    int maxf = max <= 0 ? 1 : max;
                    int amount = Integer.parseInt(i.get("AMOUNT", "1"));
                    System.out.println(i.get("MORE1") + "\t" + p + "\t" + amount + "\t" + minf + "\t" + maxf);
                    spawny.compute(i.get("MORE1"), (t, u) -> {
                        if (u == null) {
                            return new Spawn(t, minf, maxf, amount);
                        } else {
                            u.add(minf, maxf, amount);
                            return u;
                        }
                    });
                });
        //spawny.values().forEach(System.out::println);
    }

    private static class Spawn {

        private String id;
        private double min;
        private double max;
        private int amount;

        Spawn(String id, int min, int max, int amount) {
            this.id = id;
            this.min = 1440.0 / min;
            this.max = 1440.0 / max;
            this.amount = amount;
        }

        void add(int min, int max, int amount) {
            this.min += 1440.0 / min;
            this.max += 1440.0 / max;
            this.amount += amount;
        }

        @Override
        public String toString() {
            return String.format("%s\t%.2f\t%.2f\t%d", id, min, max, amount);
        }
    }

    public static void main2(String[] args) throws IOException {
        Path saveFile = Paths.get("/tmp/save/sphere.scp");

        eu.janinko.uo.saves.SaveParser parser = new eu.janinko.uo.saves.SaveParser();

        SaveFile save = parser.parse(saveFile);

        String[] lekvary = {"i_potion_refresh_great", "i_potion_heal_great", "i_potion_mana_great",
            "i_potion_strength_great", "i_potion_poison_deadly", "i_potion_explosion_great",
            "i_potion_restore_great", "i_potion_cure_great", "i_potion_bless_great",
            "i_potion_dispel_great", "i_potion_agility_great", "i_potion_clever_great",
            "i_potion_cleansing_great", "i_potion_trance_great"};
        Set<String> lektvary = new HashSet<>();
        for (String s : lekvary) {
            lektvary.add("[WI " + s + "]");
        }

        save.getItems().values().stream()
                .filter(i -> "Sam Noe".equals(i.getTag("vyrobce")))
                .filter(i -> i.getDef().contains("i_sword_long_mytheril"))
                .forEach(System.out::println);

    }
}
