package eu.janinko.uo.footools;

import eu.janinko.uo.saves.SaveParser;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import eu.janinko.uo.saves.SaveFile;
import eu.janinko.uo.saves.Spawn;
import eu.janinko.andaria.spherescript.sphere.parsers.SphereParser;
import eu.janinko.andaria.spherescript.sphere.objects.Chardef;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class SkinMapper {

    private static final String SCRIPTS = "/home/janinko/Ultima/andaria/sphere";
    private static final int mySkill = 100;

    public static void main(String[] args) throws IOException {
        Path saveFile = Paths.get("/tmp/save");

        System.out.println("Save file: " + saveFile);
        SaveParser parser = new SaveParser();
        SaveFile save = parser.parse(saveFile);

        Map<String, Chardef> chardefs = parseChardefs().stream()
                .filter(c -> c.getDefname() != null)
                .collect(Collectors.toMap(c -> c.getDefname(), c -> c));

            KuchMap map = new KuchMap();
            save.getItems().values().stream()
                    .filter(i -> i.getDef().contains("i_worldgem_bit"))
                    .filter(i -> i.get("COLOR", "00").equals("020"))
                    .filter(i -> i.get("CONT") == null)
                    .map(i -> new Spawn(i))
                    .filter(s -> s.getX() < 3000)
                    .filter(s -> chardefs.containsKey(s.getNpc()))
                    .map(s -> new KuchaniSpawn(s, Kuchani.getTotalChance(chardefs.get(s.getNpc()).getResources(), mySkill)))
                    .filter(k -> k.chance > 0)
                    .forEach(k -> {
                        System.out.printf("%d %dx%d %d %f %s\n",k.s.getAmount(), k.s.getX(), k.s.getY(), k.s.getDist(), k.chance, k.s.getNpc());
                        map.add(k);
                            });

            BufferedImage image = map.paint();

            try(OutputStream os = Files.newOutputStream(Paths.get("/tmp/mapa"+mySkill+".png"))){
                ImageIO.write(image, "png", os);
            }
    }

    private static Set<Chardef> parseChardefs() throws IOException {
        SphereParser parser = new SphereParser();
        Files.walk(Paths.get(SCRIPTS))
                .filter(f -> f.getFileName().toString().endsWith("scp"))
                .forEach(p -> {
                    try {
                        parser.parse(p);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
        Set<Chardef> chardefs = parser.getChardefs();
        return chardefs;
    }
    
    static class KuchaniSpawn {
        private final Spawn s;
        private final double chance;

        public KuchaniSpawn(Spawn s, double chance) {
            this.s = s;
            this.chance = chance;
        }

        public Spawn getS() {
            return s;
        }

        public double getChance() {
            return chance;
        }
        
    }
}
