package com.github.pvginkel.minecraft.mca;

import com.google.common.primitives.UnsignedBytes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Block {
    private static final List<Block> TYPES = buildTypes();
    private static final Map<String, Block> TYPES_BY_NAME = buildNamedTypes();
    private final byte type;
    private final String name;

    private Block(int type, String name) {
        this.type = UnsignedBytes.checkedCast(type);
        this.name = name;
    }

    public byte getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    private static Map<String, Block> buildNamedTypes() {
        Map<String, Block> types = new HashMap<String, Block>();

        for (Block block : TYPES) {
            if (block != null) {
                types.put(block.name, block);
            }
        }

        return types;
    }

    private static List<Block> buildTypes() {
        List<Block> types = new ArrayList<>();

        register(types, 0, "air");
        register(types, 1, "stone");
        register(types, 2, "grass");
        register(types, 3, "dirt");
        register(types, 4, "cobblestone");
        register(types, 5, "planks");
        register(types, 6, "sapling");
        register(types, 7, "bedrock");
        register(types, 8, "flowing_water");
        register(types, 9, "water");
        register(types, 10, "flowing_lava");
        register(types, 11, "lava");
        register(types, 12, "sand");
        register(types, 13, "gravel");
        register(types, 14, "gold_ore");
        register(types, 15, "iron_ore");
        register(types, 16, "coal_ore");
        register(types, 17, "log");
        register(types, 18, "leaves");
        register(types, 19, "sponge");
        register(types, 20, "glass");
        register(types, 21, "lapis_ore");
        register(types, 22, "lapis_block");
        register(types, 23, "dispenser");
        register(types, 24, "sandstone");
        register(types, 25, "noteblock");
        register(types, 26, "bed");
        register(types, 27, "golden_rail");
        register(types, 28, "detector_rail");
        register(types, 29, "sticky_piston");
        register(types, 30, "web");
        register(types, 31, "tallgrass");
        register(types, 32, "deadbush");
        register(types, 33, "piston");
        register(types, 34, "piston_head");
        register(types, 35, "wool");
        register(types, 36, "piston_extension");
        register(types, 37, "yellow_flower");
        register(types, 38, "red_flower");
        register(types, 39, "brown_mushroom");
        register(types, 40, "red_mushroom");
        register(types, 41, "gold_block");
        register(types, 42, "iron_block");
        register(types, 43, "double_stone_slab");
        register(types, 44, "stone_slab");
        register(types, 45, "brick_block");
        register(types, 46, "tnt");
        register(types, 47, "bookshelf");
        register(types, 48, "mossy_cobblestone");
        register(types, 49, "obsidian");
        register(types, 50, "torch");
        register(types, 51, "fire");
        register(types, 52, "mob_spawner");
        register(types, 53, "oak_stairs");
        register(types, 54, "chest");
        register(types, 55, "redstone_wire");
        register(types, 56, "diamond_ore");
        register(types, 57, "diamond_block");
        register(types, 58, "crafting_table");
        register(types, 59, "wheat");
        register(types, 60, "farmland");
        register(types, 61, "furnace");
        register(types, 62, "lit_furnace");
        register(types, 63, "standing_sign");
        register(types, 64, "wooden_door");
        register(types, 65, "ladder");
        register(types, 66, "rail");
        register(types, 67, "stone_stairs");
        register(types, 68, "wall_sign");
        register(types, 69, "lever");
        register(types, 70, "stone_pressure_plate");
        register(types, 71, "iron_door");
        register(types, 72, "wooden_pressure_plate");
        register(types, 73, "redstone_ore");
        register(types, 74, "lit_redstone_ore");
        register(types, 75, "unlit_redstone_torch");
        register(types, 76, "redstone_torch");
        register(types, 77, "stone_button");
        register(types, 78, "snow_layer");
        register(types, 79, "ice");
        register(types, 80, "snow");
        register(types, 81, "cactus");
        register(types, 82, "clay");
        register(types, 83, "reeds");
        register(types, 84, "jukebox");
        register(types, 85, "fence");
        register(types, 86, "pumpkin");
        register(types, 87, "netherrack");
        register(types, 88, "soul_sand");
        register(types, 89, "glowstone");
        register(types, 90, "portal");
        register(types, 91, "lit_pumpkin");
        register(types, 92, "cake");
        register(types, 93, "unpowered_repeater");
        register(types, 94, "powered_repeater");
        register(types, 95, "stained_glass");
        register(types, 96, "trapdoor");
        register(types, 97, "monster_egg");
        register(types, 98, "stonebrick");
        register(types, 99, "brown_mushroom_block");
        register(types, 100, "red_mushroom_block");
        register(types, 101, "iron_bars");
        register(types, 102, "glass_pane");
        register(types, 103, "melon_block");
        register(types, 104, "pumpkin_stem");
        register(types, 105, "melon_stem");
        register(types, 106, "vine");
        register(types, 107, "fence_gate");
        register(types, 108, "brick_stairs");
        register(types, 109, "stone_brick_stairs");
        register(types, 110, "mycelium");
        register(types, 111, "waterlily");
        register(types, 112, "nether_brick");
        register(types, 113, "nether_brick_fence");
        register(types, 114, "nether_brick_stairs");
        register(types, 115, "nether_wart");
        register(types, 116, "enchanting_table");
        register(types, 117, "brewing_stand");
        register(types, 118, "cauldron");
        register(types, 119, "end_portal");
        register(types, 120, "end_portal_frame");
        register(types, 121, "end_stone");
        register(types, 122, "dragon_egg");
        register(types, 123, "redstone_lamp");
        register(types, 124, "lit_redstone_lamp");
        register(types, 125, "double_wooden_slab");
        register(types, 126, "wooden_slab");
        register(types, 127, "cocoa");
        register(types, 128, "sandstone_stairs");
        register(types, 129, "emerald_ore");
        register(types, 130, "ender_chest");
        register(types, 131, "tripwire_hook");
        register(types, 132, "tripwire");
        register(types, 133, "emerald_block");
        register(types, 134, "spruce_stairs");
        register(types, 135, "birch_stairs");
        register(types, 136, "jungle_stairs");
        register(types, 137, "command_block");
        register(types, 138, "beacon");
        register(types, 139, "cobblestone_wall");
        register(types, 140, "flower_pot");
        register(types, 141, "carrots");
        register(types, 142, "potatoes");
        register(types, 143, "wooden_button");
        register(types, 144, "skull");
        register(types, 145, "anvil");
        register(types, 146, "trapped_chest");
        register(types, 147, "light_weighted_pressure_plate");
        register(types, 148, "heavy_weighted_pressure_plate");
        register(types, 149, "unpowered_comparator");
        register(types, 150, "powered_comparator");
        register(types, 151, "daylight_detector");
        register(types, 152, "redstone_block");
        register(types, 153, "quartz_ore");
        register(types, 154, "hopper");
        register(types, 155, "quartz_block");
        register(types, 156, "quartz_stairs");
        register(types, 157, "activator_rail");
        register(types, 158, "dropper");
        register(types, 159, "stained_hardened_clay");
        register(types, 160, "stained_glass_pane");
        register(types, 161, "leaves2");
        register(types, 162, "log2");
        register(types, 163, "acacia_stairs");
        register(types, 164, "dark_oak_stairs");
        register(types, 170, "hay_block");
        register(types, 171, "carpet");
        register(types, 172, "hardened_clay");
        register(types, 173, "coal_block");
        register(types, 174, "packed_ice");
        register(types, 175, "double_plant");
        register(types, 255, "unknown");

        return types;
    }

    private static void register(List<Block> types, int type, String name) {
        while (types.size() <= type) {
            types.add(null);
        }
        types.set(type, new Block(type, name));
    }

    public static Block get(byte type) {
        int intType = UnsignedBytes.toInt(type);
        Block block = null;
        if (intType < TYPES.size()) {
            block = TYPES.get(intType);
        }
        if (block == null) {
            return Blocks.UNKNOWN;
        }
        return block;
    }

    public static Block get(String name) {
        Block block = TYPES_BY_NAME.get(name);
        if (block == null) {
            return Blocks.UNKNOWN;
        }
        return block;
    }
}
