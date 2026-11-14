package eu.janinko.uo.footools.tool;

import eu.janinko.andaria.spherescript.sphere.data.Itemdefs;
import eu.janinko.andaria.spherescript.sphere.objects.Itemdef;
import eu.janinko.andaria.spherescript.sphere.objects.Resource;
import eu.janinko.andaria.spherescript.sphere.parsers.SphereParser;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResoureValueChecker {
    private static final Path SCP_PATH = Paths.get("/home/janinko/Ultima/andaria/sphere/");

    private final SphereParser parser = new SphereParser();
    private Itemdefs items = new Itemdefs();

    public ResoureValueChecker() {
    }

    public static void main(String[] args) throws IOException {
        ResoureValueChecker resoureValueChecker = new ResoureValueChecker();
        resoureValueChecker.parse(SCP_PATH);
        Set<String> ids = Set.of("i_lantern", "i_candle2", "i_candleabra", "i_candleabra2", "i_brazier_smallest", "i_brazier_small", "i_brazier", "i_brazier_tall", "i_rune_marker", "i_rune_newbie", "i_comm_crystal_green", "i_crystal_ball", "i_koule_vedeni", "i_chainmail_gloves", "i_chainmail_tunic", "i_chainmail_leggings", "i_chainmail_coif", "i_chainmail_gloves_copper", "i_chainmail_tunic_copper", "i_chainmail_leggings_copper", "i_chainmail_coif_copper", "i_chainmail_gloves_silver", "i_chainmail_tunic_silver", "i_chainmail_leggings_silver", "i_chainmail_coif_silver", "i_chainmail_gloves_gold", "i_chainmail_tunic_gold", "i_chainmail_leggings_gold", "i_chainmail_coif_gold", "i_chainmail_gloves_mytheril", "i_chainmail_tunic_mytheril", "i_chainmail_leggings_mytheril", "i_chainmail_coif_mytheril", "i_chainmail_gloves_blackrock", "i_chainmail_tunic_blackrock", "i_chainmail_leggings_blackrock", "i_chainmail_coif_blackrock", "i_chainmail_gloves_steel", "i_chainmail_tunic_steel", "i_chainmail_leggings_steel", "i_chainmail_coif_steel", "i_box_brass", "i_basket", "i_clock", "i_sextant", "i_spyglass", "i_bottle_empty", "i_pitcher_glass", "i_jar_honey_empty", "i_glass", "i_bottle_ale_empty", "i_lockpick", "i_mortar_pestle", "i_saw", "i_scissors", "i_sewing_kit", "i_hammer", "i_tinker_tools", "i_scales", "i_palette", "i_rydlo", "i_pen_and_ink ", "i_dice_cup", "i_beads", "i_necklace_pearl_new", "i_necklace_copper", "i_earrings_copper", "i_necklace_copper_beads", "i_ring_copper", "i_bracelet_copper", "i_necklace_copper_star_sapphire", "i_necklace_copper_emerald", "i_necklace_copper_sapphire", "i_necklace_copper_ruby", "i_necklace_copper_citrine", "i_necklace_copper_tourmaline", "i_necklace_copper_amber", "i_necklace_copper_diamond", "i_necklace_copper_amethyst", "i_necklace_copper_pearl", "i_ring_copper_star_sapphire", "i_ring_copper_emerald", "i_ring_copper_sapphire", "i_ring_copper_ruby", "i_ring_copper_citrine", "i_ring_copper_tourmaline", "i_ring_copper_amber", "i_ring_copper_diamond", "i_ring_copper_amethyst", "i_ring_copper_pearl", "i_earrings_copper_star_sapphire", "i_earrings_copper_emerald", "i_earrings_copper_sapphire", "i_earrings_copper_ruby", "i_earrings_copper_citrine", "i_earrings_copper_tourmaline", "i_earrings_copper_amber", "i_earrings_copper_diamond", "i_earrings_copper_amethyst", "i_earrings_copper_pearl", "i_necklace_gold", "i_necklace_gold_beads", "i_earrings_gold", "i_ring_gold", "i_bracelet_gold", "i_necklace_gold_star_sapphire", "i_necklace_gold_emerald", "i_necklace_gold_sapphire", "i_necklace_gold_ruby", "i_necklace_gold_citrine", "i_necklace_gold_tourmaline", "i_necklace_gold_amber", "i_necklace_gold_diamond", "i_necklace_gold_amethyst", "i_necklace_gold_pearl", "i_ring_gold_star_sapphire", "i_ring_gold_emerald", "i_ring_gold_sapphire", "i_ring_gold_ruby", "i_ring_gold_citrine", "i_ring_gold_tourmaline", "i_ring_gold_amber", "i_ring_gold_diamond", "i_ring_gold_amethyst", "i_ring_gold_pearl", "i_earrings_gold_star_sapphire", "i_earrings_gold_emerald", "i_earrings_gold_sapphire", "i_earrings_gold_ruby", "i_earrings_gold_citrine", "i_earrings_gold_tourmaline", "i_earrings_gold_amber", "i_earrings_gold_diamond", "i_earrings_gold_amethyst", "i_earrings_gold_pearl", "i_necklace_silver", "i_necklace_silver_beads", "i_earrings_silver", "i_ring_silver", "i_bracelet_silver", "i_necklace_silver_star_sapphire", "i_necklace_silver_emerald", "i_necklace_silver_sapphire", "i_necklace_silver_ruby", "i_necklace_silver_citrine", "i_necklace_silver_tourmaline", "i_necklace_silver_amber", "i_necklace_silver_diamond", "i_necklace_silver_amethyst", "i_necklace_silver_pearl", "i_ring_silver_star_sapphire", "i_ring_silver_emerald", "i_ring_silver_sapphire", "i_ring_silver_ruby", "i_ring_silver_citrine", "i_ring_silver_tourmaline", "i_ring_silver_amber", "i_ring_silver_diamond", "i_ring_silver_amethyst", "i_ring_silver_pearl", "i_earrings_silver_star_sapphire", "i_earrings_silver_emerald", "i_earrings_silver_sapphire", "i_earrings_silver_ruby", "i_earrings_silver_citrine", "i_earrings_silver_tourmaline", "i_earrings_silver_amber", "i_earrings_silver_diamond", "i_earrings_silver_amethyst", "i_earrings_silver_pearl", "i_necklace_mytheril_new", "i_necklace_mytheril_beads_new", "i_earrings_mytheril_new", "i_ring_mytheril_new", "i_bracelet_mytheril_new", "i_necklace_mytheril_star_sapphire", "i_necklace_mytheril_emerald", "i_necklace_mytheril_sapphire", "i_necklace_mytheril_ruby", "i_necklace_mytheril_citrine", "i_necklace_mytheril_tourmaline", "i_necklace_mytheril_amber", "i_necklace_mytheril_diamond", "i_necklace_mytheril_amethyst", "i_necklace_mytheril_pearl", "i_ring_mytheril_star_sapphire", "i_ring_mytheril_emerald", "i_ring_mytheril_sapphire", "i_ring_mytheril_ruby", "i_ring_mytheril_citrine", "i_ring_mytheril_tourmaline", "i_ring_mytheril_amber", "i_ring_mytheril_diamond", "i_ring_mytheril_amethyst", "i_ring_mytheril_pearl", "i_earrings_mytheril_star_sapphire", "i_earrings_mytheril_emerald", "i_earrings_mytheril_sapphire", "i_earrings_mytheril_ruby", "i_earrings_mytheril_citrine", "i_earrings_mytheril_tourmaline", "i_earrings_mytheril_amber", "i_earrings_mytheril_diamond", "i_earrings_mytheril_amethyst", "i_earrings_mytheril_pearl", "i_necklace_blackrock", "i_necklace_blackrock_beads", "i_earrings_blackrock", "i_ring_blackrock", "i_bracelet_blackrock", "i_necklace_blackrock_star_sapphire", "i_necklace_blackrock_emerald", "i_necklace_blackrock_sapphire", "i_necklace_blackrock_ruby", "i_necklace_blackrock_citrine", "i_necklace_blackrock_tourmaline", "i_necklace_blackrock_amber", "i_necklace_blackrock_diamond", "i_necklace_blackrock_amethyst", "i_necklace_blackrock_pearl", "i_ring_blackrock_star_sapphire", "i_ring_blackrock_emerald", "i_ring_blackrock_sapphire", "i_ring_blackrock_ruby", "i_ring_blackrock_citrine", "i_ring_blackrock_tourmaline", "i_ring_blackrock_amber", "i_ring_blackrock_diamond", "i_ring_blackrock_amethyst", "i_ring_blackrock_pearl", "i_earrings_blackrock_star_sapphire", "i_earrings_blackrock_emerald", "i_earrings_blackrock_sapphire", "i_earrings_blackrock_ruby", "i_earrings_blackrock_citrine", "i_earrings_blackrock_tourmaline", "i_earrings_blackrock_amber", "i_earrings_blackrock_diamond", "i_earrings_blackrock_amethyst", "i_earrings_blackrock_pearl", "i_nails", "i_clock_parts", "i_sextant_parts", "i_wire_iron", "i_wire_steel", "i_wire_copper", "i_wire_silver", "i_wire_gold", "i_wire_mytheril", "i_wire_blackrock", "i_axel_and_gears", "i_gears", "i_hinge", "i_firm_hinge", "i_springs", "i_axel", "i_barrel_tap", "i_container_pass_set", "i_lock_set", "i_key_ring", "i_key_copper", "i_key_gold", "i_key_iron", "i_key_magic", "i_key_silver", "i_trap_set_1", "i_trap_set_2", "i_trap_set_3", "i_trap_set_4", "i_trap_set_5", "i_trap_set_6", "i_trap_set_7", "i_trap_set_8", "i_trap_set_9", "i_trap_set_10", "i_trap_set_11", "i_trap_set_12", "i_trap_set_13", "i_trap_set_14", "i_trap_set_15", "i_trap_set_16", "i_trap_set_17", "i_mirror");

        resoureValueChecker.printLeafItemsWithoutValue(ids);
        resoureValueChecker.printItemsWithResourceAndValue(ids);


    }

    private void printItemsWithResourceAndValue(Set<String> ids) {
        for (Itemdef item : items.values()) {
            if(!ids.contains(item.getDefname())){
                continue;
            }
            if (item.getResources() != null) {
                int resources = 0;
                for (Resource resource : item.getResources()) {

                    String defname = resource.getDefname();
                    if(defname.startsWith("t_")){
                        continue;
                    }
                    resources++;
                }
                if(resources > 0){
                    if(item.getValue() != null) {
                        System.out.println(item.getDefname() + " has resources AND value");
                    }
                }
            }
        }
    }

    private void printLeafItemsWithoutValue(Set<String> ids) {
        Set<Itemdef> leaves = new HashSet<>();
        for (Itemdef item : items.values()) {
            if (item.getResources() != null) {
                for (Resource resource : item.getResources()) {

                    String defname = resource.getDefname();
                    if(defname.startsWith("t_")){
                        continue;
                    }
                    Itemdef res = items.get(defname);
                    if(res == null){
                        //System.err.println("Resource " + defname + " does not exist??");
                        continue;
                    }
                    if (res.getResources() == null) {
                        leaves.add(res);
                    }
                }
            }
        }

        for (Itemdef leaf : leaves) {
            if(!ids.contains(leaf.getDefname())){
                continue;
            }
            if(leaf.getValue() == null){
                System.out.println(leaf.getDefname() + " is missing value!");
            }
        }
    }

    public void parse(Path scpPath) throws IOException {
        parser.parseDirectory(scpPath);

        items.addAll(parser.getItemdefs());
        items.resolveInheritance();

    }
}
