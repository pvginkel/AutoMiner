package com.github.pvginkel.autominer;

import com.github.pvginkel.autominer.nbt.Block;
import com.github.pvginkel.autominer.nbt.Blocks;
import com.github.pvginkel.autominer.support.Point;
import com.github.pvginkel.autominer.support.Vector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Miner {
    private static final Set<Block> ORES = new HashSet<>(Arrays.asList(
        Blocks.COAL_ORE,
        Blocks.DIAMOND_ORE,
        Blocks.EMERALD_ORE,
        Blocks.GOLD_ORE,
        Blocks.IRON_ORE,
        Blocks.LAPIS_ORE,
        Blocks.REDSTONE_ORE
    ));

    private final BlockMap map;
    private final MinedCounter mined = new MinedCounter();

    public Miner(BlockMap map) {
        this.map = map;
    }

    public MinedCounter getMined() {
        return mined;
    }

    public void run(Vector offset, Point direction, int length) {
        int x = offset.getX();
        int y = offset.getY();
        int z = offset.getZ();

        for (int i = 0; i < length; i++) {
            mine(x, y, z, 10);
            mine(x, y + 1, z, 10);

            x += direction.getX();
            z += direction.getY();
        }
    }

    private void mine(int x, int y, int z, int depth) {
        if (depth <= 0) {
            return;
        }

        if (remove(x, y, z) == Blocks.UNKNOWN) {
            return;
        }

        recurse(depth, x - 1, y, z);
        recurse(depth, x + 1, y, z);
        recurse(depth, x, y - 1, z);
        recurse(depth, x, y + 1, z);
        recurse(depth, x, y, z - 1);
        recurse(depth, x, y, z + 1);
    }

    private void recurse(int depth, int ix, int iy, int iz) {
        Block block = map.get(ix, iy, iz);
        if (ORES.contains(block) || block == Blocks.AIR) {
            mine(ix, iy, iz, depth - 1);
        }
    }

    private Block remove(int x, int y, int z) {
        Block block = map.get(x, y, z);
        if (block != Blocks.UNKNOWN) {
            map.set(x, y, z, Blocks.UNKNOWN);
            if (block != Blocks.AIR) {
                mined.inc(block);
            }
        }
        return block;
    }
}
