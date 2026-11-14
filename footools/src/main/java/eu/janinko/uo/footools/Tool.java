package eu.janinko.uo.footools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import eu.janinko.uo.saves.Entry;
import eu.janinko.uo.saves.Item;
import eu.janinko.uo.saves.SaveFile;
import eu.janinko.uo.saves.Sector;
import eu.janinko.andaria.spherescript.sphere.parsers.SphereParser;
import eu.janinko.andaria.spherescript.sphere.objects.Itemdef;
import eu.janinko.andaria.spherescript.sphere.objects.Resource;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class Tool {
    
    private static final Path SCP_PATH = Paths.get("/tmp/aaa");////home/janinko/Ultima/andaria/sphere/");

    
    public static void main2(String[] args) throws IOException {
        Path scriptFile = SCP_PATH.resolve("items/n_weapons.scp");
        
        System.out.println("Script file: " + scriptFile);
        
        SphereParser parser = new SphereParser();
        Files.walk(SCP_PATH)
                .filter(Files::isRegularFile)
                //.filter(p -> p.equals(scriptFile))
                .filter(p -> p.getFileName().toString().endsWith(".scp"))
                .forEach(p -> {
                    try {
                        parser.parse(p);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
        Map<String, Itemdef> items = parser.getItemdefs().stream()
                .collect(Collectors.toMap(i -> i.getDefname() == null? i.getDefid() : i.getDefname(),
                Function.identity(),
                (a, b) -> {System.out.println("Duplicate: " + a.getDefname() + " ("+a.getDefid()+") " + b.getDefname() + " ("+a.getDefid()+")"); return a;}));
        
        BufferedWriter oout = Files.newBufferedWriter(Paths.get("/tmp/itemy"));
        PrintStream out = System.out;
        for(Map.Entry<String, Itemdef> e : items.entrySet()){
            String d = e.getKey();
            Itemdef i = e.getValue();
            
            String id = i.getId() == null ? i.getDefid() : i.getId();
            if(!id.matches("[0-9]+|0[0-9a-f]+"))
                continue;
            if(i.getDupeitem() != null)
                continue;
           
            if(i.getType() == null)
                continue;
            
            if(i.getColor() != null)
                continue;
            
            
            oout.append(i.getDefname()).write('\t');
            oout.append(id).write('\t');
            oout.append(i.getName()).write('\t');
            oout.append(i.getCategory()).write('\t');
            oout.append(i.getSubsection()).write('\t');
            oout.append(i.getDescription()).write('\t');
            oout.append(i.getType().toLowerCase()).write('\t');
            oout.append(i.getTag("material")).write('\t');
            oout.append(String.valueOf(i.getFlip())).write('\t');
            oout.append(String.valueOf(i.getRepair())).write('\t');
            oout.append(String.valueOf(i.getDye())).write('\t');
            oout.append(String.valueOf(i.getDamageMin())).write('\t');
            oout.append(String.valueOf(i.getDamageMax())).write('\t');
            oout.append(String.valueOf(i.getHitMax())).write('\t');
            oout.append(String.valueOf(i.getHitMin())).write('\t');
            oout.append(String.valueOf(i.getSpeed())).write('\t');
            oout.append(i.getSkill()).write('\t');
            oout.append(String.valueOf(i.getReqstr())).write('\t');
            oout.append(String.valueOf(i.getTwohands())).write('\t');
            oout.append(i.getRangeMax() == null ? "1" : i.getRangeMax().toString()).write('\t');
            oout.append(formatResources(i.getResources())).write('\t');
            oout.append(formatResources(i.getSkillmake())).write('\t');
            oout.append(String.valueOf(i.getWeight())).write('\t');
            oout.append(i.getDupelist() == null ? "null" : i.getDupelist().stream().collect(Collectors.joining(","))).write('\t');
            oout.append(i.getTevents() == null ? "" : i.getTevents().stream().collect(Collectors.joining(","))).write('\t');
            oout.append(i.getTdata1()).write('\t');
            oout.append(i.getTdata2()).write('\t');
            oout.append(i.getTdata3()).write('\t');
            oout.append(i.getTdata4()).write('\t');
            i.getTags().remove("material");
            oout.append(String.valueOf(i.getTags())).write('\t');
            
            
            oout.write('\n');
        }
        oout.close();
    }
    
    private static <T> T resolveProperty(Function<Itemdef, T> key, Itemdef i, Map<String, Itemdef> items){
        T ret = key.apply(i);
        if(ret == null){
            String id = i.getId();
            Itemdef ni = items.get(id);
            if(ni != null){
                ret = resolveProperty(key, ni, items);
            }
        }
        return ret;
    }
    
    private static Long resolveId(Itemdef i, Map<String, Itemdef> items) {
        String id;
        if (!i.getDefid().equals(i.getDefname())) {
            id = i.getDefid();
        } else {
            id = i.getId();
        }
        if(id == null){
            return null;
        }
        try{
            if(id.startsWith("0")){
                return Long.parseLong(id,16);
            }
            return Long.parseLong(id,10);
        }catch(NumberFormatException ex){
            Itemdef i2 = items.get(id);
            return i2 == null ? null : resolveId(i2, items);
        }
    }
    
    private static String formatResources(List<Resource> res, Map<String, Itemdef> items){
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
                        return r.getCount() / 10.0+ " % " + name;
                })
                .collect(Collectors.joining(", "));
    }
    
    private static void parseTemplate(String line) {
        if(false){
            
        }else{
            //it.unknown();
        }
    }   
    

    private static SaveFile parseFile2(Iterator<String> it) {
        SaveFile save = new SaveFile();
        String line = null;
        try{
            while(it.hasNext()){
                line = it.next();

                if(line.startsWith("TITLE")){
                    save.setTitle(line.split("=")[1]);
                }else if(line.startsWith("VERSION")){
                    save.setVersion(line.split("=")[1]);
                }else if(line.startsWith("TIME")){
                    save.setTime(Integer.parseInt(line.split("=")[1]));
                }else if(line.startsWith("SAVECOUNT")){
                    save.setSavecount(Integer.parseInt(line.split("=")[1]));
                }else if(line.startsWith("[SECTOR")){
                    save.put(parseSector(line, it));
                }else if(line.startsWith("[WI")){
                    save.put(parseItem(line, it));
                }else if(line.startsWith("[WC")){
                    save.put(parseCharacter(line, it));
                }else if(line.isEmpty()){
                    ;//noop
                }else if("[EOF]".equals(line)){
                    return save;
                }else{
                    throw new IllegalStateException(line);
                }
            }
        }finally{
            System.err.println("Previous line: " + line);
        }
        return save;
    }

    private static Sector parseSector(String line, Iterator<String> it) {
        Sector s = new Sector(line);
        parseEntry(s, it);
        return s;
    }

    private static Item parseItem(String line, Iterator<String> it) {
        Item s = new Item(line);
        parseEntry(s, it);
        return s;
    }

    private static eu.janinko.uo.saves.Character parseCharacter(String line, Iterator<String> it) {
        eu.janinko.uo.saves.Character s = new eu.janinko.uo.saves.Character(line);
        parseEntry(s, it);
        return s;
    }

    private static void parseEntry(Entry s, Iterator<String> it) {
            String line = null;
        try{
            while(it.hasNext()){
                line = it.next();
                if(line.isEmpty()){
                    return;
                }
                String[] split = line.split("=",2);
                if(split.length == 1){
                    s.put(split[0],"");
                }else{
                    s.put(split[0],split[1]);
                }
            }
        }catch(RuntimeException ex){
            System.err.println("Previous line: " + line);
            throw ex;
        }
    }



}
