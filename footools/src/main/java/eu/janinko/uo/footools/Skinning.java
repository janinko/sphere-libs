package eu.janinko.uo.footools;

import java.util.HashMap;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class Skinning {

    public static final HashMap<String, Integer> skinning = new HashMap<>();
    private static final int GAIN_RADIUS = 300;
    private static final int ADV_RATE_0 = 250;
    private static final int ADV_RATE_50 = 1500;
    private static final int ADV_RATE_100 = 6000;

    static {
        skinning.put("i_ribs_raw", 0);
        skinning.put("i_bird_raw", 0);
        skinning.put("i_feather", 10);
        skinning.put("i_pickaxe", 10);
        skinning.put("i_kindling", 20);
        skinning.put("i_banana_unpeeled", 30);
        skinning.put("i_log", 30);
        skinning.put("i_reag_bone", 50);
        skinning.put("i_pigs_head", 50);
        skinning.put("i_hide", 80);
        skinning.put("i_pigs_feet", 80);
        skinning.put("i_chicken_leg_raw", 100);
        skinning.put("i_lamb_leg_raw", 120);
        skinning.put("i_cheese_wedge", 170);
        skinning.put("i_reag_ginseng", 200);
        skinning.put("i_lard", 220);
        skinning.put("i_intestinum", 220);
        skinning.put("i_reag_esence_fire", 250);
        skinning.put("i_reag_esence", 250);
        skinning.put("i_reag_esence_water", 250);
        skinning.put("i_reag_esence_air", 250);
        skinning.put("i_reag_esence_eart", 250);
        skinning.put("i_fur_light", 280);
        skinning.put("i_fur_dark", 280);
        skinning.put("i_hide_wolf", 300);
        skinning.put("i_bandage_bloody", 300);
        skinning.put("i_reag_spider_silk", 300);
        skinning.put("i_trophy_obri_pavouk", 300);
        skinning.put("i_reag_batwing", 320);
        skinning.put("i_trophy_desivy_pavouk", 330);
        skinning.put("i_trophy_zmije", 350);
        skinning.put("i_frog_leg_raw", 350);
        skinning.put("i_trophy_sklipkan", 360);
        skinning.put("i_trophy_vrrk", 360);
        skinning.put("i_bandage", 370);
        skinning.put("i_plice_hada", 370);
        skinning.put("i_trophy_upiriho_pavouka", 390);
        skinning.put("i_trophy_kobra", 400);
        skinning.put("i_trophy_skret", 400);
        skinning.put("i_trophy_vtackar", 420);
        skinning.put("i_hide_bear", 430);
        skinning.put("i_trophy_aligator", 430);
        skinning.put("i_ledovka_srdce_medvidete", 430);
        skinning.put("i_feather_raven", 450);
        skinning.put("i_trophy_snovac", 450);
        skinning.put("i_trophy_jesterak", 500);
        skinning.put("i_feather_harpy", 500);
        skinning.put("i_trophy_cerna_vdova", 500);
        skinning.put("i_trophy_mamba", 500);
        skinning.put("i_reag_nox_crystal", 530);
        skinning.put("i_trophy_gorila", 550);
        skinning.put("i_feather_eagle", 550);
        skinning.put("i_trophy_tarantule", 550);
        skinning.put("i_trophy_anakonda", 550);
        skinning.put("i_hide_bear_ice", 570);
        skinning.put("i_reag_dragon_blood", 580);
        skinning.put("i_reag_daemon_bone", 600);
        skinning.put("i_reag_daemon_blood", 620);
        skinning.put("i_reag_eye_of_newt", 630);
        skinning.put("i_reag_worm_heart", 680);
        skinning.put("i_hide_dragon", 680);
        skinning.put("i_trophy_mephitis", 700);
        skinning.put("i_potion_Poison_Deadly", 720);
        skinning.put("i_hide_daemon", 730);
        skinning.put("i_thread_acra", 880);
    }

    public static int difficulty(String resource) {
        return skinning.getOrDefault(resource, 0);
    }

    public static double getChance(int skill, String resource) {
        int difficulty = skinning.getOrDefault(resource, 0);
        if (difficulty > skill) {
            return 0;
        }
        int sance = 500 + (skill - difficulty) * 2;
        if (sance > 1000) {
            sance = 1000;
        }
        return sance / 1000.0;
    }

    public static double gainChance(int skill, String resource) {
        int difficulty = skinning.getOrDefault(resource, 0);
        if (skill > difficulty + GAIN_RADIUS) {
            return 0;
        }

        int skillAdj = skill + skill - difficulty;
        final int linear = getLinear(skillAdj);
        if (linear <= 0) {
            return 0;
        }
        int chance = 100000 / linear;
        if (chance < 0) {
            return 0;
        }

        return chance / 1000.0;
    }

    private static int getLinear(int skill) {
        int lo, hi, mod;
        if (skill < 500) {
            lo = ADV_RATE_0;
            hi = ADV_RATE_50;
            mod = 0;
        } else {
            lo = ADV_RATE_50;
            hi = ADV_RATE_100;
            mod = 500;
        }
        int a = lo + (hi - lo) * (skill - mod) / 500;
        return a;
    }

}
