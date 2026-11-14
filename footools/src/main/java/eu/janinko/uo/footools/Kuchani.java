package eu.janinko.uo.footools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.janinko.andaria.spherescript.sphere.parsers.SphereParser;
import eu.janinko.andaria.spherescript.sphere.objects.Chardef;
import eu.janinko.andaria.spherescript.sphere.objects.Resource;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class Kuchani {
    private static final String SCRIPTS = "/home/janinko/Ultima/andaria/sphere";
    
    public static void main(String[] args) throws IOException, InterruptedException {

        SphereParser parser = new SphereParser();
        Files.walk(Paths.get(SCRIPTS))
                .peek( f -> System.out.println("visiting " + f))
                .filter(f -> f.getFileName().toString().endsWith("scp"))
                .peek( f -> System.out.println("Doing " + f))
                .forEach(p -> {
                    System.out.println("Script file: " + p);
                    try {
                        parser.parse(p);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

        Map<String, Double> chances = new HashMap<>();
        System.err.flush();
        Thread.sleep(1000);
        System.err.flush();
        int mySkill = 406;
        //for (mySkill = 300; mySkill < 1000; mySkill++) {
            for (Chardef chardef : parser.getChardefs()) {
                if(chardef.getDefname() == null){
                    continue;
                }
                double totalChance = getTotalChance(chardef.getResources(), mySkill);
                if(totalChance > 0)
                    System.out.println(totalChance+" " + chardef.getName() + " (" + chardef.getDefname() + ")");
                chances.put(chardef.getDefname(), totalChance);
            }
            //System.out.println();
            String out = chances.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    //.map(e -> e.getKey() + ": " + e.getValue())
                    //.findFirst().orElse("!!!");
                    .mapToDouble(e -> e.getValue())
                    .sum() +"";
            System.out.println(mySkill + "\t" + out);
        //}
    }

    public static double getTotalChance(List<Resource> resources, int mySkill) {
        resources.sort((a, b) -> Integer.compare(Skinning.difficulty(b.getDefname()), Skinning.difficulty(a.getDefname())));
        double totalChance = 0;
        double invChance = 1;
        for (Resource r : resources) {
            String def = r.getDefname();
            long count = r.getCount();
            
            double getChance = Skinning.getChance(mySkill, def);
            double wgetChance = 1 - Math.pow(1 - getChance, count);
            double cgetChance = invChance * wgetChance;
            
            double gainChance = Skinning.gainChance(mySkill, def);
            
            double chance = cgetChance * gainChance;
            
            //System.out.printf("%s i:%f c:%f g:%f %f, ",r, invChance, cgetChance, gainChance, chance);
            totalChance += chance;
            invChance = invChance * (1 - wgetChance);
        }
        return totalChance;
    }
    
    public static double cestnost(String defname) {
        double mod;
        switch (defname) {
            case "c_stag": mod = 0.112019914651494;
                break;
            case "c_doe": mod = 0.0608108108108108;
                break;
            case "c_eagle": mod = 0.0448079658605974;
                break;
            case "c_pig_big": mod = 0.0401849217638691;
                break;
            case "c_bird": mod = 0.0398293029871977;
                break;
            case "c_rabbit": mod = 0.0384068278805121;
                break;
            case "c_wolf": mod = 0.0359174964438122;
                break;
            case "c_snake": mod = 0.034850640113798;
                break;
            case "c_a_black_bear": mod = 0.0327169274537696;
                break;
            case "c_bird_jungle": mod = 0.0327169274537696;
                break;
            case "c_chicken_old": mod = 0.0320056899004267;
                break;
            case "c_bear_brown": mod = 0.0316500711237553;
                break;
            case "c_a_direwolf": mod = 0.0298719772403983;
                break;
            case "c_gorilla": mod = 0.0288051209103841;
                break;
            case "c_alligator": mod = 0.0259601706970128;
                break;
            case "c_dolphin": mod = 0.0259601706970128;
                break;
            case "c_a_jackrabbit": mod = 0.0256045519203414;
                break;
            case "c_orc": mod = 0.0245376955903272;
                break;
            case "c_orc_w_club": mod = 0.0241820768136558;
                break;
            case "c_bird_raven": mod = 0.0216927453769559;
                break;
            case "c_m_orc_mage": mod = 0.0216927453769559;
                break;
            case "c_bird_magpie": mod = 0.019203413940256;
                break;
            case "c_spider_giant": mod = 0.019203413940256;
                break;
            case "c_a_wolf_gray": mod = 0.0188477951635846;
                break;
            case "c_bird_crow": mod = 0.0188477951635846;
                break;
            case "c_rat": mod = 0.0188477951635846;
                break;
            case "c_bear_grizzly": mod = 0.0181365576102418;
                break;
            case "c_had_new": mod = 0.017425320056899;
                break;
            case "c_goat": mod = 0.0167140825035562;
                break;
            case "c_fox": mod = 0.0163584637268848;
                break;
            case "c_sheep_woolly": mod = 0.0163584637268848;
                break;
            case "c_rat_giant": mod = 0.0149359886201991;
                break;
            case "c_a_black_bear_evil": mod = 0.0138691322901849;
                break;
            case "c_lizardman_spear": mod = 0.0138691322901849;
                break;
            case "c_toad_giant": mod = 0.0131578947368421;
                break;
            default:
                mod = -1;
                break;
        }
        return mod;
    }
}
