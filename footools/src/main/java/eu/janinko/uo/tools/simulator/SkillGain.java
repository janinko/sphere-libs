package eu.janinko.uo.tools.simulator;

import lombok.Data;
import lombok.Getter;
import lombok.Value;

import java.util.*;
import java.util.function.Function;

import static java.lang.Math.min;

public class SkillGain {
    private static Random generator = new Random();

    // 30: 300
    // 33: 330
    private static final List<SkillRange> mechanikaRange = List.of(
            new SkillRange(300, 30),
            new SkillRange(330, 33),
            new SkillRange(830, 35),
            new SkillRange(850, 48),
            new SkillRange(980, 53)
    );

    Function <TriggerArgs, Boolean> trigger;

    private static int getDiff(List<SkillRange> skillRange, int skill){
        int diff = 1;
        for (SkillRange range : skillRange) {
            if (range.minSkill <= skill) {
                diff = range.diff;
            }
        }
        return diff;
    }

    public static void mainMech(String[] args) {
        SkillGain skillGain = new SkillGain(a -> false);
        int[] histogram = new int[1000];
        for(int i = 0; i < 1_000_000; i++){
            Skill mechanika = new Skill(1000, 500, 100, 566, 2666, 4000);
            mechanika.setBase(300);
            while (mechanika.getBaseLevel() < mechanika.getMaxLevel()){
                histogram[mechanika.getBaseLevel()] ++;
                int diff = getDiff(mechanikaRange, mechanika.getBaseLevel());
                if(diff <= 0){
                    diff = 1;
                }
                skillGain.skillExperience(mechanika, diff);
            }
            if( i % 10_000 == 0){
                System.out.print(".");
            }
            if(i % 100_000 == 99_999){
                System.out.println();
            }
        }

        System.out.println();
        for(int i = 0; i<1000; i++){
            System.out.println(histogram[i]);
        }
    }

    public static void main(String[] args) {
        SkillGain skillGain = new SkillGain(SkillGain::skillBoostUnder30and50);
        int[] histogram = new int[1000];
        for(int i = 0; i < 1_000_000; i++){
            Skill secne = new Skill(1000, 0, 1500, 2500, 6000);
            //secne.setBase(1);
            while (secne.getBaseLevel() < secne.getMaxLevel()){
                histogram[secne.getBaseLevel()] ++;
                if(generator.nextInt(750) <= secne.getBaseLevel()){
                    int diff = secne.getBaseLevel() / 10 - 10;
                    skillGain.skillExperience(secne, diff);
                }else if(secne.getBaseLevel() <= 300){
                    skillGain.skillExperience(secne, generator.nextInt(30));
                }
                //System.out.println(secne.getBaseLevel());
            }
            if( i % 10_000 == 0){
                System.out.print(".");
            }
            if(i % 100_000 == 99_999){
                System.out.println();
            }
        }

        System.out.println();
        for(int i = 0; i<1000; i++){
            System.out.println(histogram[i]);
        }
    }

    public static void mainKuchani(String[] args) {
        SkillGain skillGain = new SkillGain(SkillGain::skillBoostUnder30and50);
        Map<Integer, Integer> histogram = new TreeMap<>();

        long vlku = 0;
        long goril = 0;
        long triesBossu = 0;
        for(int i = 0; i < 1_000_000; i++){
            Skill kuchani = new Skill(750, 250, 250, 1400, 4250);
            kuchani.setBase(300);
            int tries = 0;
            while (kuchani.getBaseLevel() < kuchani.getMaxLevel()){
                tries++;
                int diff;
                if(kuchani.getBaseLevel() < 550){
                    vlku ++;
                    diff = 30;
                }else if(kuchani.getBaseLevel() < 750){
                    goril ++;
                    diff = 55;
                }else {
                    triesBossu ++;
                    diff = 88;
                }
                int sance = 500 + (kuchani.getBaseLevel() - (diff * 10))*2;
                if(generator.nextInt(1000) >= sance){
                    continue;
                }
                skillGain.skillExperience(kuchani, diff);
            }

            histogram.merge(tries, 1, Integer::sum);
        }

        histogram.forEach((key, value) -> System.out.println(key + "\t" + value));
        System.out.println(vlku);
        System.out.println(goril);
        System.out.println(triesBossu);
    }

    public SkillGain(Function<TriggerArgs, Boolean> trigger) {
        this.trigger = trigger;
    }
    // Skill_Experience

    private void skillExperience(Skill skill, int difficulty){
        difficulty *= 10;
        int iSkillLevel = skill.getBaseLevel();

        if ( difficulty < 0 )
        {
                // failure. Give a little experience for failure at low levels.
                if ( iSkillLevel < 300 )
                {
                        difficulty = (( min( -difficulty, iSkillLevel )) / 2 ) - 8;

                        if ( difficulty <= 0 )
                                difficulty = 1;
                }
                else
                {
                        difficulty = 0;
                }
        }
        if (difficulty > 1000) {
            difficulty = 1000;
        }

        int iSkillMax = skill.getMaxLevel();  // max advance for this skill.

        // ex. ADV_RATE=2000,500,25 for ANATOMY (easy)
        // ex. ADV_RATE=8000,2000,100 for alchemy (hard)
        // assume 100 = a 1 for 1 gain.
        // ex: 8000 = we must use it 80 times to gain .1
        // Higher the number = the less probable to advance.
        // Extrapolate a place in the range.

        // give a bonus or a penalty if the task was too hard or too easy.
        int iSkillAdj = iSkillLevel + (iSkillLevel - difficulty);
        int iGainRadius = skill.getGainRadius();
        if ((iGainRadius > 0) && ((difficulty + iGainRadius) < iSkillLevel)) {
            return;
        }

        int iChance = skill.getAdvRate().getChancePercent( iSkillAdj );

        if ( iChance <= 0 )
                return; // less than no chance ?

        int iRoll = generator.nextInt(1000);

        if ( iSkillLevel < iSkillMax ) // Are we in position to gain skill ?
        {
            // slightly more chance of decay than gain
            if ( (iRoll * 3) <= (iChance * 4) ) {
                // Skill_Decay();
            }

            if ( difficulty > 0 )
            {

                if ( iRoll <= iChance )
                {
                    iSkillLevel++;
                        TriggerArgs args = new TriggerArgs();
                        args.in1 = skill;
                        args.in2 = iSkillLevel;

                        if(onTriggerSkillChange(args)){
                            return;
                        }

                        iSkillLevel = args.in2;
                    skill.setBase(iSkillLevel);
                }
            }
        }

        // stat gain

    }

    private boolean onTriggerSkillChange(TriggerArgs args) {
        return trigger.apply(args);
    }

    private static boolean skillBoostUnder30and50(TriggerArgs args) {
        if(args.in1.getBaseLevel() < 500){
            if(args.in2 - args.in1.getBaseLevel() == 1){
                if(args.in1.getBaseLevel() > 300){
                    args.in2 = args.in2 + generator.nextInt(5);
                }else {
                    args.in2 = args.in2 + generator.nextInt(10);
                }
                if(args.in2 > args.in1.getMaxLevel()){
                    args.in2 = args.in1.getMaxLevel();
                }
            }
        }
        return false;
    }

    private static class TriggerArgs{
        Skill in1;
        int in2;
    }

    @Getter
    private static class Skill {
        private int baseLevel = 0;

        private final int maxLevel;

        private final int gainRadius;

        private final ValueCurveDef advRate;

        public Skill(int maxLevel, int gainRadius, int ... advRate){
            this.maxLevel = maxLevel;
            this.gainRadius = gainRadius;
            this.advRate = new ValueCurveDef(advRate);
        }

        public void setBase(int iSkillLevel) {
            this.baseLevel = iSkillLevel;
        }
    }

    private static class ValueCurveDef {
        private int[] values;

        public ValueCurveDef(int ... values) {
            this.values = values;
        }
        int getChancePercent( int iSkillPercent ){
            // ARGS:
            //  iSkillPercent = 0 - 1000 = 0 to 100.0 percent
            //
            //  m_Rate[3] = the 3 advance rate control numbers, 0,50,100 skill levels
            //   (How many uses for a gain of .1 (div by 100))
            // RETURN:
            //  percent chance of success * 10 = 0 - 1000.

            // How many uses for a gain of .1 (div by 100)
            int iChance = getLinear( iSkillPercent );
            if ( iChance <= 0 )
                return 0; // less than no chance ?
            // Express uses as a percentage * 10.
            return( 100000 / iChance );

        }

        int getLinear( int iSkillPercent ) {
            //
            // ARGS:
            //  iSkillPercent = 0 - 1000 = 0 to 100.0 percent
            //  m_Rate[3] = the 3 advance rate control numbers, 100,50,0 skill levels
            //              Acts as line segments.
            // RETURN:
            //  raw chance value.

            int iSegSize;
            int iLoIdx;

            int iQty = values.length;
            switch (iQty)
            {
                case 0:
                    return( 0 );    // no values defined !
                case 1:
                    return( values[0] );
                case 2:
                    iLoIdx = 0;
                    iSegSize = 1000;
                    break;
                case 3:
                    // Do this the fastest.
                    if ( iSkillPercent >= 500 )
                    {
                        iLoIdx = 1;
                        iSkillPercent -= 500;
                    }
                    else
                    {
                        iLoIdx = 0;
                    }
                    iSegSize = 500;
                    break;
                default: // More
                    //iLoIdx = IMULDIV( iSkillPercent, iQty, 1000 );
                    iLoIdx = (int) ((((long)iSkillPercent*(long)iQty)+(1000 / 2))/(1000));
                    iQty--;
                    if ( iLoIdx < 0 )
                        iLoIdx = 0;
                    if ( iLoIdx >= iQty )
                        iLoIdx = iQty-1;
                    iSegSize = 1000 / iQty;
                    iSkillPercent -= ( iLoIdx * iSegSize );
                    break;
            }

            int iLoVal = values[iLoIdx];
            int iHiVal = values[iLoIdx+1];
            //int iChance = iLoVal + IMULDIV( iHiVal - iLoVal, iSkillPercent, iSegSize );
            long hiloDiff = (iHiVal - iLoVal);
            long mul = hiloDiff * (long) (iSkillPercent);
            int iChance = iLoVal + (int) ((mul +(iSegSize / 2))/(iSegSize));

            if ( iChance <= 0 )
                return 0; // less than no chance ?

            return( iChance );
        }
// #define IMULDIV(a,b,c) ((((LONGLONG)(a)*(LONGLONG)(b))+(c / 2))/(c))

    }


    @Value
    private static class SkillRange{
        int minSkill;
        int diff;
    }
}
