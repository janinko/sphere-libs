/*
 * Copyright (C) 2022 janinko
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
package eu.janinko.uo.footools.save;

import eu.janinko.uo.saves.SaveParser;
import eu.janinko.uo.saves.SaveFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author janinko
 */
public class SaveDiffer {
    
    private static final Path SAVES_PATH = Paths.get("/tmp/and-save");
    
    public static void main(String[] args) throws IOException {
        

        SaveParser parser = new SaveParser();

        Map<String, Integer> poctyPred = getPoctySmrti(parser, SAVES_PATH.resolve("pred"));
        Map<String, Integer> poctyPo = getPoctySmrti(parser, SAVES_PATH.resolve("po"));
        
            for(String id: poctyPred.keySet()){
                Integer pred = poctyPred.get(id);
                Integer po = poctyPo.getOrDefault(id, 0);
                if(!pred.equals(po)){
                    System.out.println(id + "\t" + pred + "\t" + po + "\t" + (po - pred));
                }
            }
    }

    private static Map<String, Integer> getPoctyItemu(SaveParser parser, Path predPath) throws IOException {
        Map<String, Integer> poctyPred = new HashMap<>();
        SaveFile savePred = parser.parse(predPath.resolve("sphereworld.scp"));
        savePred.getItems().values().stream()
                .forEach(i -> poctyPred.merge(i.getDef(), i.getAmount(), Integer::sum));
        savePred = parser.parse(predPath.resolve("spherechars.scp"));
        savePred.getItems().values().stream()
                .forEach(i -> poctyPred.merge(i.getDef(), i.getAmount(), Integer::sum));
        return poctyPred;
    }

    private static Map<String, Integer> getPoctySmrti(SaveParser parser, Path predPath) throws IOException {
        Map<String, Integer> poctyPred = new HashMap<>();
        SaveFile savePred = parser.parse(predPath.resolve("spherechars.scp"));
        savePred.getCharacters().values().stream()
                .forEach(i -> poctyPred.put(i.getName(), (int) i.getDeaths()));
        return poctyPred;
    }
}
