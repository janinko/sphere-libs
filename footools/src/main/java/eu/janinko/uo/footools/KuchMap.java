package eu.janinko.uo.footools;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import eu.janinko.uo.footools.SkinMapper.KuchaniSpawn;
import eu.janinko.uo.saves.Spawn;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class KuchMap {

    public MapField[][] map = new MapField[6144][4096];
    double maxChance = 0;

    public KuchMap() {
        for (int x = 0; x < 6144; x++) {
            for (int y = 0; y < 4096; y++) {
                map[x][y] = new MapField();
            }
        }
    }
    
    public void add(KuchaniSpawn ks){
        Spawn s = ks.getS();
        
        final int cx = s.getX();
        final int cy = s.getY();
        final int dist = s.getDist();
        int sx = cx - dist;
        int sy = cy - dist;
        int ex = cx + dist + 1;
        int ey = cy + dist + 1;
        if(sx < 0) sx = 0;
        if(sy < 0) sy = 0;
        if(ex >= 6144) ex = 6144;
        if(ey >= 4096) ey = 4096;
        
        double area = dist*dist * Math.PI;
        MapEntry e = new MapEntry(s.getAmount() / area, ks.getChance());
        if(ks.getChance() > maxChance)
            maxChance = ks.getChance();
        
        for(int x=sx; x<ex; x++){
            for(int y=sy; y<ey; y++){
                double d = Math.sqrt((x-cx)*(x-cx) + (y-cy)*(y-cy));
                if(d > dist) continue;
                map[x][y].add(e);
            }
        }
    }
    
    public BufferedImage paint(){
        BufferedImage image = new BufferedImage(6144, 4096, BufferedImage.TYPE_INT_ARGB);
        double maxAmount = getMax();
        double max = 0;
        for (int x = 0; x < 6144; x++) {
            for (int y = 0; y < 4096; y++) {
                if(!map[x][y].entries.isEmpty()){
                    image.setRGB(x, y, map[x][y].getColor(maxChance, maxAmount));
                }
            }
        }
        return image;
    }
    
    private double getMax(){
        double max = 0;
        for (int x = 0; x < 6144; x++) {
            for (int y = 0; y < 4096; y++) {
                if(map[x][y].sum > max)
                    max = map[x][y].sum;
            }
        }
        return max;
    }
  
    private static class MapField{
        List<MapEntry> entries = new ArrayList<>();
        double sum = 0;

        private void add(MapEntry e) {
            entries.add(e);
            sum += e.amount;
        }
        
        int getColor(double maxChance, double maxArea){
            float alpha = (float) -(0.2 + sum/maxArea * 0.6);
            double chance = entries.stream()
                    .mapToDouble(e -> e.chance)
                    .average().orElse(0.5);
            float red = (float) (chance / maxChance);
            float green = 1-red;
            
            int color = (Math.round(255*alpha) << 24)
                    + (Math.round(255*red) << 16)
                    + (Math.round(255*green) << 8);
            return color;
        }
        
    }
    private static class MapEntry{
        final double amount;
        final double chance;

        public MapEntry(double amount, double chance) {
            this.amount = amount;
            this.chance = chance;
        }
    }
}
