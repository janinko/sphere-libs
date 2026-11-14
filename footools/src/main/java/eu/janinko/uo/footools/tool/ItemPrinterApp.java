/*
 * Copyright (C) 2020 Honza Brázdil &lt;jbrazdil@redhat.com&gt;
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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */
public class ItemPrinterApp {

    private static final Path SCP_PATH = Paths.get("/home/janinko/Ultima/andaria/sphere/");

    public static void main(String[] args) throws IOException {
        ItemPrinter ip = new ItemPrinter();

        ip.parse(SCP_PATH);
        //ip.printWeapon(Paths.get("/tmp/weapons"));


        Set<String> restrictedTypes = new HashSet<>();
        restrictedTypes.add("t_multi");
        restrictedTypes.add("t_eq_script");
        restrictedTypes.add("t_eq_horse");
        restrictedTypes.add("t_light_lit");
        ip.addFilters(i -> i.getType() != null, i -> !restrictedTypes.contains(i.getType().toLowerCase()));
        ip.addFilters(i -> !i.getDefname().startsWith("i_trip"),
                i -> !i.getDefname().startsWith("i_zbojnickeruiny"),
                i -> !i.getDefname().startsWith("i_turnaj"),
                i -> !i.getDefname().startsWith("i_key_fleder"),
                i -> !i.getDefname().startsWith("i_haunted"),
                i -> !i.getDefname().startsWith("i_biatlon"),
                i -> !i.getDefname().startsWith("i_fx"),
                i -> !i.getDefname().contains("_vanoc"));

        Set<String> restrictedItems = new HashSet<>();
        restrictedItems.add("millin_krystal");
        restrictedItems.add("magdalena_krystal");
        restrictedItems.add("johnyho_krystal");
        restrictedItems.add("i_rabit_hole");
        restrictedItems.add("i_rock_zaval");
        restrictedItems.add("i_quest_bag");
        restrictedItems.add("i_podlahar");
        ip.addFilters(i -> i.getType() != null, i -> !restrictedItems.contains(i.getDefname().toLowerCase()));

        ip.print(ItemPrinter.ITEM_IMG, Paths.get("/tmp/itemy-all"));
    }
}
