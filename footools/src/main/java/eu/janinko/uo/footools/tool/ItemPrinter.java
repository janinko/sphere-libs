package eu.janinko.uo.footools.tool;

import eu.janinko.andaria.spherescript.sphere.data.Itemdefs;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import eu.janinko.andaria.spherescript.sphere.objects.Itemdef;
import eu.janinko.andaria.spherescript.sphere.objects.Resource;
import eu.janinko.andaria.spherescript.sphere.parsers.SphereParser;
import lombok.Getter;

/**
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */
public class ItemPrinter {

    public static final ItemFormat WEAPON = ItemPrinter::printWeapon;
    public static final ItemFormat ITEM = ItemPrinter::printItem;
    public static final ItemFormat ITEM_IMG = ItemPrinter::printItemImgInfo;

    private final SphereParser parser = new SphereParser();
    @Getter
    private Itemdefs items = new Itemdefs();
    private Map<String, String> defnames;
    private final List<Predicate<Itemdef>> filters = new ArrayList<>();

    public void addFilters(Predicate<Itemdef>... filters){
        for(Predicate<Itemdef> filter : filters){
            this.filters.add(filter);
        }
    }

    public void parse(Path scpPath) throws IOException{
        parser.parseDirectory(scpPath);

        items.addAll(parser.getItemdefs());
        items.resolveInheritance();

        defnames = parser.getDefnames();
    }

    public void print(ItemFormat format, Path outputPath) throws IOException {
        Stream<Itemdef> itemStream = items.values().stream();
        for (Predicate<Itemdef> filter : filters) {
            itemStream = itemStream.filter(filter);
        }
        Iterable<String> strings = itemStream.map(i -> format.format(this, i)).filter(Objects::nonNull)::iterator;

        Files.write(outputPath, strings);
    }

    private String printItemImgInfo(Itemdef i) {
        String defname = i.getDefname().toLowerCase();
        Long id = items.getId(i);
        if(id == null) return null;
        int color = getColor(i);
        return defname + '\t' + id + '\t' + color;
    }

    private String printItem(Itemdef i) {
        StringBuilder out = new StringBuilder();
        String id = i.getId() == null ? i.getDefid() : i.getId();
        if (!id.matches("[0-9]+|0[0-9a-f]+")) {
            return null;
        }
        if (i.getDupeitem() != null) {
            return null;
        }
        if (i.getType() == null) {
            return null;
        }
        if (i.getColor() != null) {
            return null;
        }
        out.append(i.getDefname()).append('\t');
        out.append(id).append('\t');
        out.append(i.getName()).append('\t');
        out.append(i.getCategory()).append('\t');
        out.append(i.getSubsection()).append('\t');
        out.append(i.getDescription()).append('\t');
        out.append(i.getType().toLowerCase()).append('\t');
        out.append(i.getTag("material")).append('\t');
        out.append(i.getFlip()).append('\t');
        out.append(i.getRepair()).append('\t');
        out.append(i.getDye()).append('\t');
        out.append(i.getDamageMin()).append('\t');
        out.append(i.getDamageMax()).append('\t');
        out.append(i.getHitMax()).append('\t');
        out.append(i.getHitMin()).append('\t');
        out.append(i.getSpeed()).append('\t');
        out.append(i.getSkill()).append('\t');
        out.append(i.getReqstr()).append('\t');
        out.append(i.getTwohands()).append('\t');
        out.append(i.getRangeMax() == null ? "1" : i.getRangeMax().toString()).append('\t');
        out.append(formatResources(i.getResources())).append('\t');
        out.append(formatResources(i.getSkillmake())).append('\t');
        out.append(i.getWeight()).append('\t');
        out.append(i.getDupelist() == null ? "null" : i.getDupelist().stream().collect(Collectors.joining(","))).append('\t');
        out.append(i.getTevents() == null ? "" : i.getTevents().stream().collect(Collectors.joining(","))).append('\t');
        out.append(i.getTdata1()).append('\t');
        out.append(i.getTdata2()).append('\t');
        out.append(i.getTdata3()).append('\t');
        out.append(i.getTdata4()).append('\t');
        TreeMap tags = new TreeMap(i.getTags());
        tags.remove("material");
        out.append(String.valueOf(tags)).append('\t');
        return out.toString();
    }

    public void printWeapon(Path output) throws IOException {
        try (BufferedWriter out = Files.newBufferedWriter(output)) {
            for(Itemdef i : items.values()){
                printWeapon(i, out);
            }
        }
    }

    private String printWeapon(Itemdef i) {
        Long dmgMin = i.getDamageMin();
        List<Resource> resources = i.getResources();
        List<Resource> skillmake = i.getSkillmake();
        if(dmgMin == null) return null;
        if(resources == null) return null;
        if(skillmake == null) return null;
        Long id = items.getId(i);
        if (id == null) return null;
        int color = getColor(i);
        String speed = i.getSpeedText();
        Long dmgMax = i.getDamageMax();
        Long hitMin = i.getHitMin();
        Long hitMax = i.getHitMax();
        Long weight = i.getWeight();
        Long reqstr = i.getReqstr();
        String type = i.getType();
        String skill = i.getSkill();
        Boolean twohands = i.getTwohands();
        return id + "\t" + color + "\t" + i.getNameSingular() + "\t" + dmgMin + "\t"
                + dmgMax + "\t" + hitMin + "\t" + hitMax + "\t" + speed + "\t" + twohands + "\t" + weight + "\t"
                + type + "\t" + skill + "\t" + reqstr + "\t"
                + formatResources(resources, items) + "\t" + formatSkillmake(skillmake);
    }


    private void printWeapon(Itemdef i, BufferedWriter out) throws IOException {
        String weapon = printWeapon(i);
        if(weapon == null) return;
        out.write(Itemdefs.getDefname(i) + "\t" + weapon + "\n");
    }

    private int getColor(Itemdef itemdef) {
        return getColor(itemdef, itemdef.getColor());
    }

    private int getColor(Itemdef itemdef, String color) {
        if (color == null) {
            return 0;
        }

        try {
            if (color.startsWith("0")) {
                return Integer.parseInt(color, 16);
            }
            return Integer.parseInt(color, 10);
        } catch (NumberFormatException ex) {
            color = color.toLowerCase();
            String ncolor = null;
            if (defnames.containsKey(color)) {
                ncolor = defnames.get(color);
            } else if(color.startsWith("<tdata")){
                switch(color.charAt(6)){
                    case '1': ncolor = itemdef.getTdata1(); break;
                    case '2': ncolor = itemdef.getTdata2(); break;
                    case '3': ncolor = itemdef.getTdata3(); break;
                    case '4': ncolor = itemdef.getTdata4(); break;
                }
            }
            if(ncolor == null){
                System.err.println("Unknown color " + color);
                return -1;
            }
            return getColor(itemdef, ncolor);
        }
    }

    private static String formatResources(List<Resource> res, Itemdefs items){
        if(res == null) return null;
        return res.stream()
                .map(r -> {
                        Itemdef i = items.get(r.getDefname());
                        String name = r.getDefname();
                        if(i != null){
                            String n = r.getCount() > 1? i.getNamePlural(): i.getNameSingular();
                            if (n != null) name = n;
                        }
                        return r.getCount()+ "x " + name;
                })
                .collect(Collectors.joining(", "));
    }

    private static String formatResources(List<Resource> res){
        if(res == null) return null;
        return res.stream()
                .map(r -> r.getCount()+ " " + r.getDefname())
                .collect(Collectors.joining(", "));
    }

    private static String formatSkillmake(List<Resource> res){
        if(res == null) return null;
        return res.stream()
                .filter(r -> r.getCount() > 1)
                .map(r -> {
                        String name = r.getDefname();
                        return r.getCount() / 10.0+ " % " + translateSkill(name);
                })
                .collect(Collectors.joining(", "));
    }

    private static String translateSkill(String skillName){
        switch(skillName){
            case "kovarstvi": return "kovářství";
            case "tesarina": return "tesařina";
            case "krejcovstvi": return "krejčovství";
            case "rezbarstvi": return "řezbářství";
            case "opisovani": return "opisování";
            case "leceni": return "léčení";
            case "hornictvi": return "hornictví";
            default: return skillName;
        }
    }

    private static void parseTemplate(String line) {
        if(false){

        }else{
            //it.unknown();
        }
    }

    @FunctionalInterface
    public interface ItemFormat{
        String format(ItemPrinter ip, Itemdef item);
    }

}
