/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ager;

import com.github.pvginkel.minecraft.mca.*;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class App {
    public static void main(String[] args) throws Exception {
        MCMap map = new MCMap(new File(args[0]));

        map.resetLevelData();
        map.clearPartOfBlob();

        Map<Block, Integer> types = new HashMap<>();
        Set<Block> blocks = new HashSet<>();
        blocks.add(Blocks.COAL_ORE);
        blocks.add(Blocks.DIAMOND_ORE);
        blocks.add(Blocks.EMERALD_ORE);
        blocks.add(Blocks.GOLD_ORE);
        blocks.add(Blocks.IRON_ORE);
        blocks.add(Blocks.LAPIS_ORE);
        blocks.add(Blocks.REDSTONE_ORE);

        int fi = 0;
        for (MCAFile f : map.files) {
            for (int zBlock = 0; zBlock < 32; zBlock++) {
                for (int xBlock = 0; xBlock < 32; xBlock++) {
                    if (f.chunks[zBlock][xBlock] == null) {
                        continue;
                    }
                    for (int dz = -1; dz < 2; dz++) {
                        for (int dx = -1; dx < 2; dx++) {
                            Chunk ch = map.getChunk(xBlock + f.xOffset * 32 + dx, zBlock + f.zOffset * 32 + dz);
                            if (ch != null) {
                                ch.prepare();
                            }
                        }
                    }
                    //f.chunks[zBlock][xBlock].prepare();
                    for (int ySection = 0; ySection < 16; ySection++) {
                        if (f.chunks[zBlock][xBlock].sections()[ySection] == null) {
                            continue;
                        }
                        SectionData sectionData = f.chunks[zBlock][xBlock].getSectionData(ySection);
                        for (int ly = 0; ly < 16; ly++) {
                            for (int lz = 0; lz < 16; lz++) {
                                for (int lx = 0; lx < 16; lx++) {
                                    int x = f.xOffset * 512 + xBlock * 16 + lx;
                                    int y = ySection * 16 + ly;
                                    int z = f.zOffset * 512 + zBlock * 16 + lz;
                                    Block block = sectionData.getBlock(lx, ly, lz);
                                    if (blocks.contains(block)) {
                                        if (types.get(block) == null) {
                                            types.put(block, 1);
                                        } else {
                                            types.put(block, types.get(block) + 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            System.out.println(++fi + "/" + map.files.size());

            for (Map.Entry<Block, Integer> entry : types.entrySet()) {
                System.out.println(entry.getKey().getName() + ": " + entry.getValue());
            }
        }
    }
}
