package com.github.pvginkel.minecraft.mca;

import com.jcraft.jzlib.GZIPInputStream;

import java.io.*;
import java.util.ArrayList;

public class MCMap {
    private final CoordMap<MCAFile> files = new CoordMap<>();
    private final Tag levelDat;
    private final Tag levelDatOld;
    private int maxChunksLoaded;
    private final ArrayList<Chunk> loadedChunks = new ArrayList<>();

    public MCMap(File worldF) throws FileNotFoundException, IOException {
        this(worldF, (int)(Math.min(Runtime.getRuntime().maxMemory() / 500000, 100000000)));
    }

    public MCMap(File worldF, int maxChunksLoaded) throws FileNotFoundException, IOException {
        this.maxChunksLoaded = maxChunksLoaded;

        InputStream fis = null;
        try {
            fis = new GZIPInputStream(new FileInputStream(new File(worldF, "level.dat")));
            levelDat = Tag.readFrom(fis);
        } finally {
            fis.close();
        }

        fis = null;
        try {
            if (new File(worldF, "level.dat_old").exists()) {
                fis = new GZIPInputStream(new FileInputStream(new File(worldF, "level.dat_old")));
                levelDatOld = Tag.readFrom(fis);
            } else {
                levelDatOld = null;
            }
        } finally {
            fis.close();
        }

        for (File f : new File(worldF, "region").listFiles()) {
            if (f.getName().endsWith(".mca")) {
                String[] bits = f.getName().split("[.]");
                int x = Integer.parseInt(bits[1]);
                int z = Integer.parseInt(bits[2]);
                files.put(x, z, new MCAFile(new File(worldF, "region"), x, z, loadedChunks, maxChunksLoaded));
            }
        }
        for (MCAFile f : files) {
            for (int z = 0; z < 32; z++) {
                for (int x = 0; x < 32; x++) {
                    int chunkX = f.getXOffset() * 32 + x;
                    int chunkZ = f.getZOffset() * 32 + z;
                    Chunk ch = getChunk(chunkX, chunkZ);
                    if (ch == null) {
                        continue;
                    }
                    for (int dz = -1; dz < 2; dz++) {
                        for (int dx = -1; dx < 2; dx++) {
                            Chunk ch2 = getChunk(chunkX + dx, chunkZ + dz);
                            if (ch2 != null) {
                                ch.setChunk(dx + 1, dz + 1, ch2);
                            }
                        }
                    }
                }
            }
        }
    }

    public CoordMap<MCAFile> getFiles() {
        return files;
    }

    public void resetLevelData() {
        levelDat.findTagByName("raining").setValue((byte)0);
        levelDat.findTagByName("thundering").setValue((byte)0);
        levelDat.findTagByName("Time").setValue((long)1776562l);
        if (levelDatOld != null) {
            levelDatOld.findTagByName("raining").setValue((byte)0);
            levelDatOld.findTagByName("thundering").setValue((byte)0);
            levelDatOld.findTagByName("Time").setValue((long)1776562l);
        }
    }

    static int fileC(int c) {
        return c < 0 ? ((c + 1) / 512 - 1) : c / 512;
    }

    static int rem(int c) {
        return c - fileC(c) * 512;
    }

    static int blockChunkRem(int c) {
        return (c - fileC(c) * 512) / 16;
    }

    static int chunkFileC(int c) {
        return c < 0 ? ((c + 1) / 32 - 1) : c / 32;
    }

    static int chunkRem(int c) {
        return c - chunkFileC(c) * 32;
    }

    public final Chunk getChunkForBlock(int blockX, int blockZ) {
        MCAFile f = files.get(fileC(blockX), fileC(blockZ));
        if (f == null) {
            return null;
        }
        return f.getChunk(blockChunkRem(blockX), blockChunkRem(blockZ));
    }

    public final Chunk getChunk(int chunkX, int chunkZ) {
        MCAFile f = files.get(chunkFileC(chunkX), chunkFileC(chunkZ));
        if (f == null) {
            return null;
        }
        return f.getChunk(chunkRem(chunkX), chunkRem(chunkZ));
    }

    public boolean getPartOfBlob(int x, int y, int z) {
        MCAFile f = files.get(fileC(x), fileC(z));
        if (f == null) {
            return false;
        }
        return f.getPartOfBlob(rem(x), y, rem(z));
    }

    public void setPartOfBlob(int x, int y, int z, boolean value) {
        MCAFile f = files.get(fileC(x), fileC(z));
        if (f == null) {
            return;
        }
        f.setPartOfBlob(rem(x), y, rem(z), value);
    }

    public void clearPartOfBlob() {
        for (MCAFile f : files) {
            f.clearPartOfBlob();
        }
    }

    public int getBlockType(int x, int y, int z) {
        MCAFile f = files.get(fileC(x), fileC(z));
        if (f == null) {
            return -1;
        }
        return f.getBlockType(rem(x), y, rem(z));
    }

    public void setBlockType(byte type, int x, int y, int z) {
        MCAFile f = files.get(fileC(x), fileC(z));
        if (f == null) {
            return;
        }
        f.setBlockType(type, rem(x), y, rem(z));
    }

    public boolean isDataMaskSet(int mask, int x, int y, int z) {
        int d = getData(x, y, z);
        return (d & mask) == mask;
    }

    public int getData(int x, int y, int z) {
        MCAFile f = files.get(fileC(x), fileC(z));
        if (f == null) {
            return -1;
        }
        return f.getData(rem(x), y, rem(z));
    }

    public void setData(byte data, int x, int y, int z) {
        MCAFile f = files.get(fileC(x), fileC(z));
        if (f == null) {
            return;
        }
        f.setData(data, rem(x), y, rem(z));
    }
}
