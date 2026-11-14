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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import eu.janinko.andaria.spherescript.sphere.objects.Itemdef;
import eu.janinko.andaria.spherescript.sphere.objects.Resource;
import lombok.Data;
import lombok.Getter;

import static java.util.function.Function.identity;

/**
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */
public class SimulatorOprav {

    private static final Path SCP_PATH = Paths.get("/home/janinko/Ultima/andaria/sphere/");
    private static final Integer ONE = 1;
    private static final DeleteMeClass DeleteMe = null;

    public static void main(String[] args) throws IOException {
        Map<String, Itemdef> itemdefs = getItems(SCP_PATH);
        Map<String, Itemdef> nameMap = nameMap(itemdefs.values());
        itemdefs.clear();
        //System.exit(0);
        //Map<String, Itemdef> mapping = readMappingLine(Paths.get("/tmp/idmapping"), items);
        Collection<ItemRepairs> items = readItemRepairs(Paths.get("/mnt/data/tmp/opravy"), nameMap);
        nameMap.clear();

        List<ResourceX> collect = items.stream()
                //.filter(i -> !i.getRepairResourcesNew(50).isEmpty())
                //.peek(i -> System.out.println(i.getItem().getItem().getDefname() + " (" + i.getItem().getUid() + "): " + i.getRepairResourcesNew(50)))
                .map(i -> i.getRepairResourcesNewMod(50))
                //.map(i -> i.getRepairResourcesCurrent())
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(ResourceX::getDefname, Collectors.summingDouble(ResourceX::getCount)))
                .entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> new ResourceX(e.getValue(), e.getKey()))
                .collect(Collectors.toList());
        System.out.println("Suma sumarum: " + collect);
        System.exit(0);
        DecimalFormat f = new DecimalFormat("0.0");
        TreeMap<Integer, Integer> histogram = items.stream()
                .map(i -> i.initialMax())
                //.map(f::format)
                .collect(Collectors.toMap(identity(), x -> ONE, Integer::sum, TreeMap::new));

        for (int d = 1; d <= 200; d++) {
            Integer val = histogram.getOrDefault(d, 0);
            System.out.println(d + "\t" + val);
        }

        /*entries.forEach(SimulatorOprav::printResources);
        printResources(entries);*/
    }

    private static void printResources(Collection<Entry> entries) {
        String lastMonth = "";
        Map<String, Double> foo = null;
        for (Entry e : entries) {
            String month = e.datum.substring(0, 2);
            if (!lastMonth.equals(month)) {
                System.out.println(lastMonth + ": " + foo);
                foo = new HashMap<>();
                lastMonth = month;
            }
            for (ResourceX res : getResources(e)) {
                foo.merge(res.getDefname(), res.getCount(), Double::sum);
            }
        }
        System.out.println(lastMonth + ": " + foo.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).map(e -> e.getValue() + "x " + e.getKey()).collect(Collectors.joining("\n")));
    }

    private static Map<String, Itemdef> nameMap(Collection<Itemdef> itemy) {
        return itemy.stream()
                .filter(i -> i.getName() != null)
                .filter(i -> i.getResources() != null)
                .filter(i -> i.getTag("material") != null)
                //.filter(i -> i.getTevents().contains("t_repairable"))
                .collect(Collectors.toMap(i -> normalize(i.getNameSingular()), i -> i, (i, j) -> {
                    System.err.println("dup: " + i.getName() + " - " + i.getDefid() + " " + j.getDefid());
                    return i;
                }));
    }

    public static String normalize(String s) {
        s = Normalizer.normalize(s.toLowerCase(), Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    private static Map<String, Itemdef> getItems(Path path) throws IOException {
        ItemPrinter ip = new ItemPrinter();

        ip.parse(path);
        throw new UnsupportedOperationException("TODO");
        //return null; // TODO: smaz null ip.getItems();
    }

    private static List<Entry> readEntries(Path file, Map<String, Itemdef> map) throws IOException {
        try (Stream<String> lines = Files.lines(file)) {
            return lines
                    .map(l -> new Entry(l, map))
                    .filter(e -> e.item != null)
                    .filter(e -> e.item.getTag("material").equals("material_leather_daemon"))
                    .collect(Collectors.toList());
        }
    }

    private static Collection<ItemRepairs> readItemRepairs(Path file, Map<String, Itemdef> map) throws IOException {
        try (Stream<String> lines = Files.lines(file)) {
            Map<ItemInstance, ItemRepairs> ret = new HashMap<>();
            lines
                    .map(l -> new Entry(l, map))
                    .filter(e -> e.item != null)
                    .filter(Objects::nonNull)
                    //.filter(e -> "04009673d".equals(e.uid))
                    .filter(e -> e.item.getTag("material").equals("material_full_gold"))
                    //.filter(e -> e.item.getDefname().startsWith("i_ringmail"))
                    .forEach(e -> {
                        ItemRepairs opravy = ret.computeIfAbsent(new ItemInstance(e), ItemRepairs::new);
                        opravy.add(new Oprava(e));
                    });
            return ret.values();
        }
    }

    public static void printResources(Entry e) {
        int procento = (1000 * e.zDur) / e.maxDur;

        List<Resource> resources = new ArrayList<>();
        for (Resource resource : e.item.getResources()) {
            long kolik = ((resource.getCount() - 2) * ((1000 - procento) / 2)) / 1000;
            int min = 0;
            if (procento < 500) {
                min = 1;
            }
            if (kolik < min) {
                kolik = min;
            }
            if (kolik > 0) {
                resources.add(new Resource(kolik, resource.getDefname()));
            }
        }
        System.out.println(e.datum + " " + e.cas + " " + e.uid + " opravuje " + e.zDur + "/" + e.maxDur + " za: " + resources.stream().map(Resource::toString).collect(Collectors.joining(", ")));
    }

    public static List<ResourceX> getResources(Entry e) {
        int procento = (1000 * e.zDur) / e.maxDur;

        List<ResourceX> resources = new ArrayList<>();
        for (Resource resource : e.item.getResources()) {
            //long kolik = ((resource.getCount() - 2) * ((1000 - procento) / 2)) / 1000;
            int koef = 100;
            double kolik = Math.round((resource.getCount() * (1000 - procento) * koef) / 100000.0);

            int min = 0;
            //if(procento < 500)
            //    min = 1;
            if (kolik < min) {
                kolik = min;
            }
            if (kolik > 0) {
                resources.add(new ResourceX(kolik, resource.getDefname()));
            }
        }

        return resources;
    }

    private static Map<String, Itemdef> readMappingLine(Path file, Map<String, Itemdef> items) throws IOException {
        try (Stream<String> lines = Files.lines(file)) {
            return lines
                    .peek(System.out::println)
                    .map(l -> l.split("\t"))
                    .collect(Collectors.toMap(s -> s[1], s -> items.get(s[0])));
        }
    }

    @Getter
    private static class ResourceX {

        private final double count;
        private final String defname;

        public ResourceX(double count, String defname) {
            this.count = count;
            this.defname = defname;
        }

        @Override
        public String toString() {
            return count + "x " + defname;
        }

    }

    @Getter
    private static class ItemRepairs {

        private final ItemInstance item;
        private final TreeSet<Oprava> opravy = new TreeSet<>();

        public ItemRepairs(ItemInstance item) {
            this.item = item;
        }

        public void add(Oprava o) {
            opravy.add(o);
        }

        public int totalDurRepaired() {
            return opravy.stream()
                    .mapToInt(Oprava::onlyRepaired)
                    .sum();
        }

        public int initialMax() {
            return opravy.first().getMaxDur();
        }

        public List<ResourceX> getRepairResourcesCurrent() {
            return getRepairResources(o -> o.getRepairResourcesCurrent(item.getItem().getResources()));
        }

        public List<ResourceX> getRepairResourcesNew(int k) {
            int celkemOpraveno = 0;
            Map<String, Double> resourceSum = new HashMap<>();
            for (Oprava oprava : opravy) {
                //System.out.println("Repairing " + item.getItem().getDefname() + " (" + item.getUid() + "): " + oprava.getZDur() + "->" + oprava.getNaDur() + "(=" + (oprava.getMaxDur()-oprava.getZDur()) + " / " + oprava.getMaxDur());
                int koef = 500 * celkemOpraveno / (k * oprava.getMaxDur()) + 50;
                //int koef = 500 * celkemOpraveno / (k) + 50;
                if (koef > 200) {
                    koef = 200;
                }
                final List<ResourceX> repairResources = oprava.getRepairResourcesNew(item.getItem().getResources(), koef);
                //System.out.println("  celkem uz opraveno: " + celkemOpraveno + " = " + koef + " - " + repairResources);
                repairResources
                        .forEach(r -> resourceSum.merge(r.getDefname(), r.getCount(), Double::sum));
                celkemOpraveno += oprava.onlyRepaired();
            }
            return resourceSum.entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .map(e -> new ResourceX(e.getValue(), e.getKey()))
                    .collect(Collectors.toList());
        }

        public List<ResourceX> getRepairResourcesNewMod(int k) {
            double celkemOpraveno = 0;
            Map<String, Double> resourceSum = new HashMap<>();
            for (Oprava oprava : opravy) {
                System.out.println("Repairing " + item.getItem().getDefname() + " (" + item.getUid() + "): " + oprava.getZDur() + "->" + oprava.getNaDur() + "(=" + (oprava.getMaxDur() - oprava.getZDur()) + " / " + oprava.getMaxDur());
                int koef = (int) Math.round(500 * celkemOpraveno / (k * DeleteMe.newMax(oprava.getMaxDur())) + 50);
                //int koef = (int) Math.round(500 * celkemOpraveno / (k) + 50);
                if (koef > 200) {
                    koef = 200;
                }
                final List<ResourceX> repairResources = oprava.getRepairResourcesNew(item.getItem().getResources(), koef);
                System.out.println("  celkem uz opraveno: " + celkemOpraveno + " = " + koef + " - " + repairResources);
                repairResources
                        .forEach(r -> resourceSum.merge(r.getDefname(), r.getCount(), Double::sum));
                celkemOpraveno += DeleteMe.newDamag(oprava.getMaxDur() - oprava.onlyRepaired(), oprava.getMaxDur());
            }
            return resourceSum.entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .map(e -> new ResourceX(e.getValue(), e.getKey()))
                    .collect(Collectors.toList());
        }

        private List<ResourceX> getRepairResources(Function<Oprava, List<ResourceX>> repairResourceGetter) {
            return opravy.stream()
                    .map(repairResourceGetter)
                    .flatMap(List::stream)
                    .collect(Collectors.groupingBy(ResourceX::getDefname, Collectors.summingDouble(ResourceX::getCount)))
                    .entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .map(e -> new ResourceX(e.getValue(), e.getKey()))
                    .collect(Collectors.toList());
        }
    }

    @Data
    private static class ItemInstance {

        private final String uid;
        private final Itemdef item;

        public ItemInstance(Entry e) {
            this.uid = Objects.requireNonNull(e.uid);
            this.item = Objects.requireNonNull(e.item);
        }

    }

    @Getter
    private static class Oprava implements Comparable<Oprava> {

        private final LocalDateTime cas;
        private final int zDur;
        private final int naDur;
        private final int maxDur;

        public Oprava(Entry e) {
            this(e.datum, e.cas, e.zDur, e.naDur, e.maxDur);
        }

        public Oprava(String datum, String cas, int zDur, int naDur, int maxDur) {
            this.cas = LocalDateTime.parse(datum + "T" + cas);
            this.zDur = zDur;
            this.naDur = naDur;
            this.maxDur = maxDur;
        }

        public int repaired() {
            return naDur - zDur;
        }

        public int onlyRepaired() {
            if (naDur <= zDur) {
                return 0;
            }
            return naDur - zDur;
        }

        public List<ResourceX> getRepairResourcesCurrent(List<Resource> resources) {
            int procento = (1000 * zDur) / maxDur;

            List<ResourceX> ret = new ArrayList<>();
            for (Resource resource : resources) {
                long kolik = ((resource.getCount() - 2) * ((1000 - procento) / 2)) / 1000;

                int min = 0;
                if (procento < 500) {
                    min = 1;
                }
                if (kolik < min) {
                    kolik = min;
                }
                if (kolik > 0) {
                    ret.add(new ResourceX(kolik, resource.getDefname()));
                }
            }
            return ret;
        }

        /**
         * @param koef 100 = 1.0
         */
        public List<ResourceX> getRepairResourcesNew(List<Resource> resources, int koef) {
            int procento = (10000 * zDur) / maxDur;

            List<ResourceX> ret = new ArrayList<>();
            for (Resource resource : resources) {
                long kolik = (resource.getCount() * (10000 - procento) * koef) / 1000_000;

                int min = 0;
                if (koef >= 100) {
                    min = 1;
                }
                if (kolik < min) {
                    kolik = min;
                }
                if (kolik > 0) {
                    ret.add(new ResourceX(kolik, resource.getDefname()));
                }
            }
            return ret;
        }

        /**
         * @param koef 100 = 1.0
         */
        public List<ResourceX> getRepairResourcesNewMod(List<Resource> resources, int koef) {
            double zdurm = DeleteMe.newDamag(zDur, maxDur);
            double maxm = DeleteMe.newMax(maxDur);
            long procento = Math.round((1000 * zdurm) / maxm);

            List<ResourceX> ret = new ArrayList<>();
            for (Resource resource : resources) {
                long kolik = (resource.getCount() * (1000 - procento) * koef) / 100_000;

                int min = 0;
                if (koef >= 100) {
                    min = 1;
                }
                if (kolik < min) {
                    kolik = min;
                }
                if (kolik > 0) {
                    ret.add(new ResourceX(kolik, resource.getDefname()));
                }
            }
            return ret;
        }

        @Override
        public int compareTo(Oprava o) {
            return cas.compareTo(o.cas);
        }

    }

    private static class Entry {

        private final String datum;
        private final String cas;
        private final String uid;
        private final int zDur;
        private final int naDur;
        private final int maxDur;
        private final Itemdef item;

        private Entry(String line, Map<String, Itemdef> map) {
            String[] split = line.split("\t");
            if (split.length < 7) {
                System.err.println("eee: " + line);
            }
            this.datum = split[0];
            this.cas = split[1];
            String name = split[2];
            this.uid = split[3];
            this.zDur = Integer.parseInt(split[4]);
            this.naDur = Integer.parseInt(split[5]);
            this.maxDur = Integer.parseInt(split[6]);
            this.item = map.get(normalize(name));
            if (this.item == null) {
                System.err.println("EE: " + line);
            }
        }

        @Override
        public String toString() {
            return "Entry{" + "datum=" + datum + ", cas=" + cas + ", uid=" + uid + ", zDur=" + zDur + ", naDur=" + naDur + ", maxDur=" + maxDur + ", item=" + item.getDefname() + '}';
        }
    }
    
    private static class DeleteMeClass{

        private int newMax(int maxDur) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        private double newDamag(int zDur, int maxDur) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
}
