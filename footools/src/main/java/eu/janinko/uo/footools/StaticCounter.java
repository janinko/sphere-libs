package eu.janinko.uo.footools;

import eu.janinko.andaria.ultimasdk.UOFiles;
import eu.janinko.andaria.ultimasdk.files.Statics;
import eu.janinko.andaria.ultimasdk.files.statics.Static;
import eu.janinko.andaria.ultimasdk.files.statics.StaticsBlock;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class StaticCounter {

    public static void main(String[] args) throws IOException {

        Statics statics = UOFiles.loadStaticsFromDir(Path.of("/home/janinko/Ultima/cuo/files/"));
        Map<Integer, Integer> allItems = new TreeMap<>();
        for (int i = 0; i <= 0x60000; i++) {
            StaticsBlock staticsBlock = statics.get(i);
            for (Static aStatic : staticsBlock.getStatics()) {

                if(aStatic.getId() >=2622 && aStatic.getId() <= 2627){
                    System.out.println(aStatic.getId() + ": " + aStatic.getX() + "," + aStatic.getY() + "," + aStatic.getZ());
                }
            }
            //staticsBlock.getStatics().forEach(a -> allItems.merge(a.getId(), 1, Integer::sum));
        }
        //allItems.forEach((k, v) -> System.out.println(k + ": " + v));
    }
}
