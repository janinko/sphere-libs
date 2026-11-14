package eu.janinko.uo.footools;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Random;

public class MarketSim {
    private static final double RATE = 0.01;
    private static final Random generator = new Random();

    public static void main(String[] args) {
        //testBuy(22);
        CommodityScriptable commodityScriptable = new CommodityScriptable(22, 3);
        int sellPrice = commodityScriptable.sell(50) - 1;
        System.out.println("S: " + sellPrice);
        int buyPrice = commodityScriptable.buy(50) + 1;
        System.out.println("B: " + buyPrice);

        //exploit2(new CommodityScriptableNoBigJump(2222, 3));


        //exploit(new CommodityScriptable(220, 3));
        //sell(new CommodityScriptableNoBigJump(2200, 3), 10000);
        //buy(new CommodityScriptableNoBigJump(22, 3), 10000);
        //simulate(new CommodityScriptable(302, 1), 22.0);
        //simulate(new CommodityScriptable(302, 1), generator.nextDouble()*(400.0-225.0)+225.0);
        //buyAndSell(new CommodityScriptable(22, 1));
        //buyThenSell(new CommodityScriptable(22, 1));
        //sellThenBuy(new CommodityScriptable(22, 1));
    }

    private static void testBuy(int price) {
        CommodityScriptable commodityScriptable = new CommodityScriptable(price, 3);
        CommodityScriptable commodityScriptablePredict = new CommodityScriptable(price, 3);
        CommodityScriptable commodityScriptable1 = new CommodityScriptable(price, 3);
        CommodityScriptable commodityScriptable10 = new CommodityScriptable(price, 3);

        int totalPrice = 0;
        int totalPrice1 = 0;
        int totalPrice10 = 0;
        for (int i=1; i <= 1000; i++){
            totalPrice += commodityScriptable.getPriceGP();
            commodityScriptable.buy();

            totalPrice1 += commodityScriptable1.buy(1);

            int predict = commodityScriptablePredict.getBuyPrice(i);

            CommodityScriptable commodityScriptableAllAtOnce = new CommodityScriptable(price, 3);
            int allAtOnce = commodityScriptableAllAtOnce.buy(i);

            if (i % 10 == 0) {
                totalPrice10 += commodityScriptable10.buy(10);
                System.out.println(i+ ": "+ totalPrice + " : " + predict + " : " + allAtOnce  + " : " + totalPrice1 + " : " + totalPrice10);
            }else {
                System.out.println(i+ ": "+ totalPrice + " : " + predict + " : " + allAtOnce + " : " + totalPrice1);
            }
        }
    }


    private static void testSell(int price) {
        CommodityScriptableNoBigJump commodityScriptable = new CommodityScriptableNoBigJump(price, 3);
        CommodityScriptableNoBigJump commodityScriptablePredict = new CommodityScriptableNoBigJump(price, 3);
        CommodityScriptableNoBigJump commodityScriptable1 = new CommodityScriptableNoBigJump(price, 3);
        CommodityScriptableNoBigJump commodityScriptable10 = new CommodityScriptableNoBigJump(price, 3);

        int totalPrice = 0;
        int totalPrice1 = 0;
        int totalPrice10 = 0;
        for (int i=1; i <= 1000; i++){
            totalPrice += commodityScriptable.getPriceGP();
            commodityScriptable.sell();

            totalPrice1 += commodityScriptable1.sell(1);

            int predict = commodityScriptablePredict.getSellPrice(i);

            CommodityScriptableNoBigJump commodityScriptableAllAtOnce = new CommodityScriptableNoBigJump(price, 3);
            int allAtOnce = commodityScriptableAllAtOnce.sell(i);

            if (i % 10 == 0) {
                totalPrice10 += commodityScriptable10.sell(10);
                System.out.println(i+": " + totalPrice + " : " + predict + " : " + allAtOnce  + " : " + totalPrice1 + " : " + totalPrice10);
            }else {
                System.out.println(i+": " + totalPrice + " : " + predict + " : " + allAtOnce + " : " + totalPrice1);
            }
        }
    }

    private static void exploit2(CommodityScriptable commodityScriptable) {
        int basePrice = commodityScriptable.getPriceGP();
        int comodityAmount = 0;
        int gpAmount = 0;
        for (int i=0; i < 1000; i++){

            int nextPriceChange = commodityScriptable.nextPriceChange;
            if(commodityScriptable.getPriceGP() <= basePrice) {
                int volume = 1;
                double best = 9999999.0;
                int worsened = 0;
                int volumeToBuy = 0;
                while (worsened < 3) {
                    int buyPrice = commodityScriptable.getBuyPrice(volume) + nextPriceChange;
                    double pricePer = ((double) buyPrice) / volume;
                    //System.out.println("B " + volume + " " + buyPrice + " (" + pricePer + ")");
                    if (pricePer <= best) {
                        best = pricePer;
                        volumeToBuy = volume;
                    } else {
                        worsened++;
                    }
                    volume++;
                }
                if (volumeToBuy == 0) {
                    throw new IllegalStateException();
                }

                int buyingFor = commodityScriptable.buy(volumeToBuy) + nextPriceChange;
                System.out.println("Buying " + volumeToBuy + " for " + buyingFor + " (" + (((double) buyingFor) / volumeToBuy) + ")" );
                gpAmount -= buyingFor;
                comodityAmount += volumeToBuy;
            }else{
                int volume = 1;
                double best = 0;
                int worsened = 0;
                int volumeToSell = 0;
                while (worsened < 3) {
                    int sellPrice = commodityScriptable.getSellPrice(volume) - nextPriceChange;
                    double pricePer = ((double) sellPrice) / volume;
                    //System.out.println("S " + volume + " " + sellPrice + " (" + pricePer + ")");
                    if (pricePer >= best) {
                        best = pricePer;
                        volumeToSell = volume;
                    } else {
                        worsened++;
                    }
                    volume++;
                }
                if (volumeToSell == 0) {
                    throw new IllegalStateException();
                }

                int sellingFor = commodityScriptable.sell(volumeToSell) - nextPriceChange;
                System.out.println("Selling " + volumeToSell + " for " + sellingFor + " (" + (((double) sellingFor) / volumeToSell) + ")" );
                gpAmount += sellingFor;
                comodityAmount -= volumeToSell;
            }

            double per = ((double) -gpAmount ) / comodityAmount;
            double diff;
            if(comodityAmount < 0){
                diff = basePrice - per;
            }else {
                diff = per - basePrice;
            }
            System.out.println(comodityAmount + " / " + gpAmount + " ( " +diff + ")");
        }
    }

    private static void exploit(Commodity commodity){
        int basePrice = commodity.getPriceGP();
        int commodityChange = 0;
        int gpPayed = 0;
        for (int i=0; i< 1000; i++){
            if(commodity.getPriceGP() <= basePrice){
                gpPayed += commodity.getPriceGP();
                commodityChange ++;
                commodity.buy();
            }else {
                gpPayed -= commodity.getPriceGP();
                commodityChange --;
                commodity.sell();
            }
            gpPayed ++;
            double avg = ((double) gpPayed) / commodityChange;
            System.out.println("C: " + commodityChange + "\tP: " + gpPayed + "\tA: " + (avg));
        }

    }

    private static void simulate(Commodity comodity, double realPrice){
        for(int i=0; i< 10000; i++){
            if(i % 100 == 0) {
                System.out.println(comodity.getTurnoverVolume() + ": " + comodity.getPrice());
            }
            if(simulateStep(comodity, realPrice)){
                return;
            }
        }
    }

    private static boolean simulateStep(Commodity comodity, double realPrice){
        boolean buy = comodity.getPrice() < (realPrice - 0.5);// wantToBuy(realPrice, comodity.getPrice());
        boolean sell = comodity.getPrice() > (realPrice + 0.5);// wantToSell(realPrice, comodity.getPrice());
        if (!buy && !sell) {
            System.out.println("Price stabilized at " + comodity.getPrice() + " at volume " + comodity.getTurnoverVolume());
            return true;
        }
        if(buy && sell){
            if(generator.nextBoolean()){
                comodity.buy();
            }else{
                comodity.sell();
            }
        }
        if(buy){
            comodity.buy();
        }else{
            comodity.sell();
        }
        return false;
    }

    private static boolean wantToBuy(double realPrice, double price){
        if( price >= realPrice){
            return false;
        }
        double opt = realPrice / 3;
        if( price <= opt){
            return true;
        }
        double chance = (realPrice - price) / (realPrice - opt);
        return generator.nextDouble() < chance;
    }

    private static boolean wantToSell(double realPrice, double price){
        if( price <= realPrice){
            return false;
        }
        double opt = realPrice + realPrice / 3;
        if( price >= opt){
            return true;
        }
        double chance = (price - realPrice) / (opt - realPrice);
        return generator.nextDouble() < chance;
    }

    private static void buyAndSell(Commodity commodity) {
        for(int i=0; i< 2000; i++){
            System.out.println(commodity.getPrice());
            if(i % 2 == 0){
                commodity.buy();
            }else {
                commodity.sell();
            }
        }
        System.out.println(commodity.getPrice());
    }

    public static void sell(Commodity commodity, int volume) {
        long totalPrice = 0;
        for(int i = 0; i< volume; i++){
            //System.out.println(commodity.getPrice());
            totalPrice += commodity.getPriceGP();
            commodity.sell();
        }
        System.out.println(commodity.getPrice());
        System.out.println("Total price: " + totalPrice);
    }

    public static void buy(Commodity commodity, int volume) {
        for(int i = 0; i< volume; i++){
            System.out.println(commodity.getPrice());
            commodity.buy();
        }
        System.out.println(commodity.getPrice());
    }

    public static void sellThenBuy(Commodity commodity) {
        for(int i=0; i< 1000; i++){
            System.out.println(commodity.getPrice());
            commodity.sell();
        }
        for(int i=0; i< 1000; i++){
            System.out.println(commodity.getPrice());
            commodity.buy();
        }
        System.out.println(commodity.getPrice());
    }

    public static void buyThenSell(Commodity commodity) {
        for(int i=0; i< 1000; i++){
            System.out.println(commodity.getPrice());
            commodity.buy();
        }
        for(int i=0; i< 1000; i++){
            System.out.println(commodity.getPrice());
            commodity.sell();
        }
        System.out.println(commodity.getPrice());
    }

    public interface Commodity {
        void buy();

        void sell();

        double getPrice();
        int getPriceGP();

        int getTurnoverVolume();
    }


    @Data
    @AllArgsConstructor
    private static class CommodityScriptableNoBigJump implements Commodity {
        int price;
        int turnoverVolume;
        int volumeChangeBoundary;
        int volumeChange;

        public CommodityScriptableNoBigJump(int price, int turnoverVolume) {
            this.price = price;
            this.turnoverVolume = turnoverVolume;

            computeNextChange(false);
            this.volumeChange /= 2;
        }

        protected int volumeChangeBoundary(int volume, int price) {
            double volumeChange = Math.log(volume) / (price * RATE);
            if (volumeChange > 1) {
                return (int) Math.round(volumeChange);
            } else {
                return 1;
            }
        }

        private void computeNextChange(boolean buy) {
            this.volumeChangeBoundary = volumeChangeBoundary(turnoverVolume, price);
            if (buy) {
                volumeChange = 0;
            } else {
                volumeChange = volumeChangeBoundary;
            }
            System.out.println("Next change after " + volumeChangeBoundary +  " (now " + price + ")");
        }

        public int getBuyPrice(int wantedVolume){
            int vc = volumeChange;
            int vcb = volumeChangeBoundary;
            int wv = wantedVolume;
            int p = price;
            int v = turnoverVolume;

            int resPrice = 0;

            int i = vcb - vc;
            if(i < 1){
                i = 1;
            }
            while (i <= wv){
                resPrice += i * p;
                wv -= i;

                p ++;
                v += i;

                vcb = volumeChangeBoundary(v, p);
                vc = 0;

                i = vcb;
            }
            resPrice += wv * p;

            return resPrice;
        }

        public int buy(int wantedVolume) {
            int wv = wantedVolume;          // local.wanted = <argv[0]>
            int vc = volumeChange;          // local.volumeChange = <var.iron_market_volumeChange>
            int vcb = volumeChangeBoundary; // local.volumeChangeBoundary = <var.iron_market_volumeChangeBoundary>
            int p = price;                  // local.price = <var.iron_market_price>
            int tv = turnoverVolume;        // local.turnoverVolume = <var.iron_market_turnoverVolume>

            int resPrice = 0;               // local.resultPrice = 0

            int i = vcb - vc;               // local.increment = <eval <local.volumeChangeBoundary> - <local.volumeChange>>
            if (i < 1) {                    // if (<local.increment> < 1)
                i = 1;                      //     local.increment = 1
            }                               // endif
            while (i <= wv) {               // while (<local.increment> <= <local.wanted>)
                resPrice += i * p;          //     local.resultPrice += <eval <local.increment> * <local.price>
                wv -= i;                    //     local.wanted -= <local.increment>

                p += 1;                     //     local.price ++
                tv += i;                    //     local.turnoverVolume += <local.increment>

                vcb = volumeChangeBoundary(tv, p);
                                            //     local.volumeChangeBoundary = <floatval (10 * (logarithm(<local.turnoverVolume>, e) / <local.price>)) + 0.5>
                                            //     if (<local.volumeChangeBoundary> < 1)
                                            //         local.volumeChangeBoundary = 1
                                            //     endif
                vc = 0;                     //     local.volumeChange = 0;
                i = vcb;                    //     local.increment = <local.volumeChangeBoundary>
            }                               // endWhile
            resPrice += wv * p;             // local.resultPrice += <eval <local.wanted> * <local.price>

            this.volumeChange = vc + wv;    // var.iron_market_volumeChange = <eval <local.volumeChange> + <local.wanted>>
            this.volumeChangeBoundary = vcb;// var.iron_market_volumeChangeBoundary = <local.volumeChangeBoundary>
            this.price = p;                 // var.price = <local.price>
            this.turnoverVolume = tv + wv;  // var.iron_market_turnoverVolume = <eval <local.turnoverVolume> + <local.wanted>>

            return resPrice;
        }


        public int getSellPrice(int wantedVolume) {
            int wv = wantedVolume;
            int vc = volumeChange;
            int vcb = volumeChangeBoundary;
            int p = price;
            int tv = turnoverVolume;

            int resPrice = 0;

            int i = vc;
            if (i < 1) {
                i = 1;
            }
            while (i < wv) {
                resPrice += i * p;
                wv -= i;

                p--;
                if (p <= 0) {
                    p = 1;
                }
                tv += i;

                vcb = volumeChangeBoundary(tv, p);
                vc = vcb;

                i = vc;
            }
            resPrice += wv * p;

            return resPrice;
        }

        public int sell(int wantedVolume) {
            int wv = wantedVolume;          // local.wanted = <argv[0]>
            int vc = volumeChange;          // local.volumeChange = <var.iron_market_volumeChange>
            int vcb = volumeChangeBoundary; // local.volumeChangeBoundary = <var.iron_market_volumeChangeBoundary>
            int p = price;                  // local.price = <var.iron_market_price>
            int tv = turnoverVolume;        // local.turnoverVolume = <var.iron_market_turnoverVolume>

            int resPrice = 0;               // local.resultPrice = 0

            int i = vc;                     // local.increment = <local.volumeChange>
            if (i < 1) {                    // if (<local.increment> < 1)
                i = 1;                      //     local.increment = 1
            }                               // endif
            while (i <= wv) {               // while (<local.increment> <= <local.wanted>)
                resPrice += i * p;          //     local.resultPrice += <eval <local.increment> * <local.price>
                wv -= i;                    //     local.wanted -= <local.increment>

                p--;                        //     local.price --
                if (p <= 0) {               //     if (<local.price> <= 0)
                    p = 1;                  //         local.price = 1
                }                           //     endif

                tv += i;                    //     local.turnoverVolume += <local.increment>

                vcb = volumeChangeBoundary(tv, p);
                                            //     local.volumeChangeBoundary = <floatval (10 * (logarithm(<local.turnoverVolume>, e) / <local.price>)) + 0.5>
                                            //     if (<local.volumeChangeBoundary> < 1)
                                            //         local.volumeChangeBoundary = 1
                                            //     endif
                vc = vcb;                   //     local.volumeChange = <local.volumeChangeBoundary>
                i = vcb;                    //     local.increment = <local.volumeChangeBoundary>
            }                               // endWhile
            resPrice += wv * p;             // local.resultPrice += <eval <local.wanted> * <local.price>

            this.volumeChange = vc - wv;    // var.iron_market_volumeChange = <eval <local.volumeChange> - <local.wanted>>
            this.volumeChangeBoundary = vcb;// var.iron_market_volumeChangeBoundary = <local.volumeChangeBoundary>
            this.price = p;                 // var.price = <local.price>
            this.turnoverVolume = tv + wv;  // var.iron_market_turnoverVolume = <eval <local.turnoverVolume> + <local.wanted>>

            return resPrice;
        }

        @Override
        public void buy() {
            volumeChange ++;
            turnoverVolume++;

            if(volumeChange >= volumeChangeBoundary){
                this.price += 1;
                computeNextChange(true);
            }
        }

        @Override
        public void sell() {
            volumeChange --;
            turnoverVolume++;

            if(price > 1) {
                if (volumeChange <= 0) {
                    this.price -= 1;
                    computeNextChange(false);
                }
            }
        }

        public double getPrice() {
            return price;
        }

        @Override
        public int getPriceGP() {
            return price;
        }
    }

    @Data
    @AllArgsConstructor
    private static class CommodityScriptable implements Commodity {
        int price;
        int turnoverVolume;
        int nextPriceChange;
        int changeAfterVolumeChange;
        int volumeChange;

        public CommodityScriptable(int price, int volume) {
            this.price = price;
            this.turnoverVolume = volume;

            computeNextChange(false);
            this.volumeChange /= 2;
        }

        protected int nextPriceChange(double priceChange){
            if (priceChange < 1) {
                return 1;
            } else {
                return (int) Math.round(priceChange);
            }
        }

        protected int changeAfterVolumeChange(double priceChange) {
            if (priceChange < 1) {
                return (int) Math.round(1 / priceChange);
            } else {
                return 1;
            }
        }

        private void computeNextChange(boolean buy) {
            double rate = RATE / Math.log(turnoverVolume);
            double priceChange = price * rate;

            this.nextPriceChange = nextPriceChange(priceChange);
            this.changeAfterVolumeChange = changeAfterVolumeChange(priceChange);
            if (buy) {
                volumeChange = 0;
            } else {
                volumeChange = changeAfterVolumeChange;
            }
            System.out.println("Next change after " + changeAfterVolumeChange + " +- " + nextPriceChange + "(now " + price + ")");
        }

        public int getBuyPrice(int wantedVolume){
            int vc = volumeChange;
            int cavc = changeAfterVolumeChange;
            int wv = wantedVolume;
            int p = price;
            int v = turnoverVolume;
            int npc = nextPriceChange;

            int resPrice = 0;

            int i;
            if(vc >= cavc){
                i = 1;
            }else {
                i = cavc - vc;
            }
            while (i < wv){
                resPrice += i * p;
                wv -= i;

                p += npc;
                v += i;


                double rate = RATE / Math.log(v);
                double priceChange = p * rate;
                npc = nextPriceChange(priceChange);
                cavc = changeAfterVolumeChange(priceChange);
                vc = 0;

                i = cavc - vc;
            }
            resPrice += wv * p;
            wv -= wv;

            return resPrice;
        }

        public int buy(int wantedVolume){
            int vc = volumeChange;
            int cavc = changeAfterVolumeChange;
            int wv = wantedVolume;
            int p = price;
            int v = turnoverVolume;
            int npc = nextPriceChange;

            int resPrice = 0;

            int i;
            if(vc >= cavc){
                i = 1;
            }else {
                i = cavc - vc;
            }
            while (i <= wv){
                resPrice += i * p;
                wv -= i;

                p += npc;
                v += i;


                double rate = RATE / Math.log(v);
                double priceChange = p * rate;
                npc = nextPriceChange(priceChange);
                cavc = changeAfterVolumeChange(priceChange);
                vc = 0;

                i = cavc - vc;
            }
            resPrice += wv * p;
            this.volumeChange = vc + wv;
            this.changeAfterVolumeChange = cavc;
            this.price = p;
            this.turnoverVolume = v + wv;
            this.nextPriceChange = npc;



            return resPrice;
        }


        public int getSellPrice(int wantedVolume){
            int vc = volumeChange;
            int cavc = changeAfterVolumeChange;
            int wv = wantedVolume;
            int p = price;
            int v = turnoverVolume;
            int npc = nextPriceChange;

            int resPrice = 0;

            int i;
            if(vc <= 0){
                i = 1;
            }else {
                i = vc;
            }
            while (i < wv){
                resPrice += i * p;
                wv -= i;

                p -= npc;
                if(p <= 0){
                    resPrice += wv;
                    wv = 0;
                    break;
                }
                v += i;


                double rate = RATE / Math.log(v);
                double priceChange = p * rate;
                npc = nextPriceChange(priceChange);
                cavc = changeAfterVolumeChange(priceChange);
                vc = cavc;

                i = vc;
            }
            resPrice += wv * p;

            return resPrice;
        }

        public int sell(int wantedVolume){
            int vc = volumeChange;
            int cavc = changeAfterVolumeChange;
            int wv = wantedVolume;
            int p = price;
            int v = turnoverVolume;
            int npc = nextPriceChange;

            int resPrice = 0;

            int i;
            if(vc <= 0){
                i = 1;
            }else {
                i = vc;
            }
            while (i <= wv){
                resPrice += i * p;
                wv -= i;

                if(p - npc <= 0){
                    p = 1;
                    resPrice += wv;
                    wv = 0;
                    break;
                }
                p -= npc;
                v += i;


                double rate = RATE / Math.log(v);
                double priceChange = p * rate;
                npc = nextPriceChange(priceChange);
                cavc = changeAfterVolumeChange(priceChange);
                vc = cavc;

                i = vc;
            }
            resPrice += wv * p;
            this.volumeChange = vc - wv;
            this.changeAfterVolumeChange = cavc;
            this.price = p;
            this.turnoverVolume = v + wv;
            this.nextPriceChange = npc;

            return resPrice;
        }

        @Override
        public void buy() {
            volumeChange ++;
            turnoverVolume++;

            if(volumeChange >= changeAfterVolumeChange){
                this.price += nextPriceChange;
                computeNextChange(true);
            }
        }

        @Override
        public void sell() {
            volumeChange --;
            turnoverVolume++;

            if(price > 1) {
                if (volumeChange <= 0) {
                    this.price -= nextPriceChange;
                    computeNextChange(false);
                }
            }
        }

        public double getPrice() {
            return price;
        }

        @Override
        public int getPriceGP() {
            return price;
        }
    }

    @Data
    @AllArgsConstructor
    private static class CommodityLogaritmic implements Commodity {
        double price;
        int turnoverVolume;

        public void buy() {
            price += priceModif() * price;
            turnoverVolume++;
        }
        public void sell() {
            price -= priceModif() * price;
            turnoverVolume++;
        }
        private double priceModif(){
            return RATE / Math.log(price * turnoverVolume);
        }


        @Override
        public int getPriceGP() {
            return (int) Math.round(price);
        }
    }
}
