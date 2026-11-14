package eu.janinko.uo.footools.save;

import eu.janinko.uo.saves.Character;
import eu.janinko.uo.saves.Item;
import eu.janinko.uo.saves.SaveFile;
import eu.janinko.uo.saves.SaveParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SaveComparator {

    private static final Path SAVES_PATH = Paths.get("/tmp/savy/");

    private static HashMap<Long, ContainerItem> containers = new HashMap<>();

    public static void main(String[] args) throws IOException {
        //itemHeatMap(SAVES_PATH.resolve("new"));
        countItemsInContainers();
    }

    private static void findSameItemsInSave() throws IOException {
        SaveParser parser = new SaveParser();

        Map<Long, Item> itemyOld = new HashMap<>();
        itemyOld.putAll(parser.parse(SAVES_PATH.resolve("old").resolve("sphereworld.scp")).getItems());
        itemyOld.putAll(parser.parse(SAVES_PATH.resolve("old").resolve("spherechars.scp")).getItems());

        Map<Long, Item> itemyNew = new HashMap<>();
        itemyNew.putAll(parser.parse(SAVES_PATH.resolve("new").resolve("sphereworld.scp")).getItems());
        itemyNew.putAll(parser.parse(SAVES_PATH.resolve("new").resolve("spherechars.scp")).getItems());

        Map<Long, Item> itemyChanged = new HashMap<>(itemyNew);
        Map<Long, Item> itemyAll = new HashMap<>(itemyNew);

        itemyOld.keySet().retainAll(itemyNew.keySet());
        itemyNew.keySet().retainAll(itemyOld.keySet());


        Map<Long, Item> itemySame = new HashMap<>();

        for (Map.Entry<Long, Item> e : itemyOld.entrySet()) {
            Long id = e.getKey();
            Item itemOld = e.getValue();
            Item itemNew = itemyNew.get(id);
            if(itemOld.equals(itemNew)){
                itemySame.put(id, itemOld);
            }
        }


        itemyChanged.keySet().removeAll(itemySame.keySet());
        for (Map.Entry<Long, Item> e : itemyChanged.entrySet()) {
            Item item = e.getValue();
            Long cont = item.getCont();
            while (cont != null) {
                itemySame.remove(cont);
                Item contItem = itemyAll.get(cont);

                cont = contItem == null ? null : contItem.getCont();
            }
        }


        int size;
        int prevSize;
        do {
            prevSize = itemySame.size();
            System.out.println(itemySame.size());

            Iterator<Map.Entry<Long, Item>> iterator = itemySame.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, Item> e = iterator.next();

                Item item = e.getValue();
                if (item.getCont() != null) {
                    if (!itemySame.containsKey(item.getCont())) {
                        iterator.remove();
                    }
                }
            }
            size = itemySame.size();
        }while (prevSize != size);


        System.out.println(itemySame.size());

        try(BufferedWriter out = Files.newBufferedWriter(SAVES_PATH.resolve("same-items"))) {
            for (Item item : itemySame.values()) {
                out.write(String.format("0%x\t%s", item.getSerial(), item.getDef()));
                out.newLine();
            }
        }


        itemySame.values().forEach(i -> {
            if(i.getCont() != null) {
                container(i.getCont()).add(container(i.getSerial()));
            }
        });

        for(ContainerItem item: containers.values()){
            if(item.getCount() > 0) {
                System.out.println(String.format("0%x", item.id) + "\t" + item.getCount());
            }
        }
    }


    private static void countItemsInContainers() throws IOException {
        SaveParser parser = new SaveParser();
        Map<Long, Item> itemy = new HashMap<>();
        itemy.putAll(parser.parse(SAVES_PATH.resolve("new").resolve("sphereworld.scp")).getItems());
        itemy.putAll(parser.parse(SAVES_PATH.resolve("new").resolve("spherechars.scp")).getItems());

        itemy.values().forEach(i -> {
            if(i.getCont() != null) {
                ContainerItem container = container(i.getSerial());
                container.defname = i.getDef();
                container(i.getCont()).add(container);
            }
        });

        Map<String, List<Integer>> conts = new HashMap<>();
        for (ContainerItem ci : containers.values()) {
            if (ci.defname != null && !ci.items.isEmpty()) {
                List<Integer> integers = conts.computeIfAbsent(ci.defname, (x) -> new ArrayList<>());
                integers.add(ci.items.size());
            }
        }

        for (Map.Entry<String, List<Integer>> e : conts.entrySet()) {
            System.out.print(e.getKey());
            for (Integer count : e.getValue()) {
                System.out.print("\t" + count);
            }
            System.out.println();
        }
    }

    private static void countItemsAtGMOstrov(Path savedir) throws IOException {
        SaveParser parser = new SaveParser();
        Map<Long, Item> itemy = new HashMap<>(parser.parse(savedir.resolve("sphereworld.scp")).getItems());

        int count = 0;
        Iterator<Map.Entry<Long, Item>> iterator = itemy.entrySet().iterator();
        OUTER: while (iterator.hasNext()){
            Map.Entry<Long, Item> e = iterator.next();
            Item item = e.getValue();
            Long cont = item.getCont();
            while (cont != null) {
                item = itemy.get(cont);
                if(item == null){
                    iterator.remove();
                    continue OUTER;
                }
                cont = item.getCont();
            }

            if(item.getPy() > 3500 && item.getPx() < 710){
                count++;
            }
        }

        System.out.println("Počet itemů ve světě (ne u postav): " + itemy.size());
        System.out.println("Počet itemů na GM ostrově: " + count);
    }

    private static void itemHeatMap(Path savedir) throws IOException {
        SaveParser parser = new SaveParser();
        Map<Long, Item> itemy = new HashMap<>();
        Map<Long, Character> chars = new HashMap<>();
        SaveFile saveFile = parser.parse(savedir.resolve("sphereworld.scp"));
        itemy.putAll(saveFile.getItems());
        chars.putAll(saveFile.getCharacters());

        saveFile = parser.parse(savedir.resolve("spherechars.scp"));
        itemy.putAll(saveFile.getItems());
        chars.putAll(saveFile.getCharacters());


        int max = 0;
        int[][] counts = new int[896][512];
        for (Item item : itemy.values()) {
            int x = item.getPx() == null ? 0 :item.getPx();
            int y = item.getPy() == null ? 0 :item.getPy();
            Long cont = item.getCont();
            while(cont != null){
                Item containerItem = itemy.get(cont);
                if (containerItem == null) {
                    Character character = chars.get(cont);
                    x = character.getPx();
                    y = character.getPy();
                    cont = null;
                } else {
                    if(containerItem.getPx() != null) {
                        x = containerItem.getPx();
                        y = containerItem.getPy();
                    }
                    cont = containerItem.getCont();
                }
            }
            if(++counts[x/8][y/8] > max){
                max = counts[x/8][y/8];
            }
        }


        BufferedImage image = paint(counts, max);

        try(OutputStream os = Files.newOutputStream(Paths.get("/tmp/mapa-itemy.png"))){
            ImageIO.write(image, "png", os);
        }
    }

    public static BufferedImage paint(int[][] countmap, int max){
        int width = countmap.length;
        int height = countmap[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int strength = (255 * countmap[x][y] + 127) / max;
                int color =+ (Math.round(255*strength) << 16);
                image.setRGB(x, y, color);
            }
        }
        return image;
    }
    private static ContainerItem container(Long id){
        return containers.computeIfAbsent(id, ContainerItem::new );
    }

    private static class ContainerItem {
        private Long id;

        private String defname;
        private int cont;

        private Set<ContainerItem> items = new HashSet<>();

        public ContainerItem(Long id) {
            this.id = id;
        }

        public void add(ContainerItem i) {
            items.add(i);
        }

        public long getCount(){
            return items.stream().mapToLong(ContainerItem::getCount).sum() + items.size();
        }
    }
}
