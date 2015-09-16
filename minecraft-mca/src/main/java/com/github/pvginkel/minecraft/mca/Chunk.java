package com.github.pvginkel.minecraft.mca;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Chunk {
    private Tag t;
    private Tag[] sections = new Tag[16];
    private byte[][] sectionBlocks = new byte[16][0];
    private int globalChunkX;
    private int globalChunkZ;
    private Chunk[][] chunks = new Chunk[3][3];
    private PooledPagingByteArray partOfBlob;
    private byte[][] sectionSkyLights = new byte[16][0];
    private PooledPagingByteArray supported;
    private RegionFile rf;
    private int rfX;
    private int rfZ;
    private int maxChunksLoaded;
    private static long accessCounter = 0;
    private long lastAccess = 0;
    private ArrayList<Chunk> loadedChunks;

    public Chunk(RegionFile rf, int rfX, int rfZ, int globalChunkX, int globalChunkZ, ArrayList<Chunk> loadedChunks, int maxChunksLoaded, PooledPagingByteArray.Pool pool) throws IOException {
        this.rf = rf;
        this.rfX = rfX;
        this.rfZ = rfZ;
        this.globalChunkX = globalChunkX;
        this.globalChunkZ = globalChunkZ;
        this.maxChunksLoaded = maxChunksLoaded;
        this.loadedChunks = loadedChunks;
        supported = pool.getArray(256 * 16 * 16);
        partOfBlob = pool.getArray(256 * 16 * 16);
    }

    public boolean loaded() {
        return t != null;
    }

    public Chunk prepare() {
        lastAccess = accessCounter++;
        if (t == null) { // !loaded()
            if (loadedChunks.size() >= maxChunksLoaded) {
                Collections.sort(loadedChunks, new Comparator<Chunk>() {
                    @Override
                    public int compare(Chunk t, Chunk t1) {
                        return t.lastAccess > t1.lastAccess ? 1 : t.lastAccess < t1.lastAccess ? -1 : 0;
                    }
                });
                int pops = Math.max(1, Math.min(40, loadedChunks.size() / 8));
                for (Chunk ch : loadedChunks.subList(0, pops)) {
                    ch.save();
                }
                loadedChunks.subList(0, pops).clear();
            }
            load();
            loadedChunks.add(this);
            /*if (loadedChunkCache.size() >= maxChunksLoaded) {
				// Should prolly just take X.
				int pops = Math.max(1, Math.min(40, loadedChunkCache.size() / 8));
				System.out.println("Popping " + pops);
				for (int i = 0; i < pops; i++) {
					loadedChunkCache.pop().save();
				}
			}
			load();*/
        }
        return this;
    }

    private void load() {
        if (loaded()) {
            return;
        }
        try {
            t = Tag.readFrom(rf.getChunkDataInputStream(rfX, rfZ));
            Tag[] sArray = (Tag[])t.findTagByName("Level").findTagByName("Sections").getValue();
            for (Tag section : sArray) {
                this.sections[(Byte)section.findTagByName("Y").getValue()] = section;
                this.sectionBlocks[(Byte)section.findTagByName("Y").getValue()] = (byte[])section.findTagByName("Blocks").getValue();
                this.sectionSkyLights[(Byte)section.findTagByName("Y").getValue()] = (byte[])section.findTagByName("SkyLight").getValue();
            }
            supported.pageIn();
            partOfBlob.pageIn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        if (!loaded()) {
            return;
        }
        try {
            OutputStream os = rf.getChunkDataOutputStream(rfX, rfZ);
            t.writeTo(os);
            os.close();
            t = null;
            sections = new Tag[16];
            sectionBlocks = new byte[16][0];
            sectionSkyLights = new byte[16][0];
            supported.pageOut();
            partOfBlob.pageOut();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Tag[] sections() {
        prepare();
        return sections;
    }

    public SectionData getSectionData(int ySection) {
        return new SectionData((byte[])sections()[ySection].findTagByName("Blocks").getValue());
    }

    static boolean printPOB;

    public boolean getPartOfBlob(int x, int y, int z) {
        if (printPOB) {
            System.err.println(globalChunkX + "/" + globalChunkZ);
        }
        //prepare();
        try {
            return partOfBlob.get()[y * 256 + z * 16 + x] > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //return partOfBlob.get(y * 256 + z * 16 + x);
    }

    public void setPartOfBlob(int x, int y, int z, boolean value) {
        //prepare();
        try {
            partOfBlob.get()[y * 256 + z * 16 + x] = (byte)(value ? 1 : 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //partOfBlob.set(y * 256 + z * 16 + x, value);
    }

    public void clearPartOfBlob() {
        partOfBlob.reset();
    }

    public int getBlockType(int x, int y, int z) {
        if (y < 0 || y > 255) {
            return -1;
        }
        //prepare();
        int section = y / 16;
        int remY = y % 16;
        if (sections[section] == null) {
            return -1;
        }
        try {
            //return ((byte[]) sections[section].findTagByName("Blocks").getValue())[((remY * 16 + z) * 16 + x)];
            return sectionBlocks[section][((remY * 16 + z) * 16 + x)];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(x + "/" + y + "/" + z);
            throw new RuntimeException(e);
        }
    }

    public void setBlockType(byte type, int x, int y, int z) {
        if (y < 0 || y > 255) {
            return;
        }
        //prepare();
        int section = y / 16;
        int remY = y % 16;
        if (sections[section] == null) {
            return;
        }
        //((byte[]) sections[section].findTagByName("Blocks").getValue())[((remY * 16 + z) * 16 + x)] = type;
        sectionBlocks[section][((remY * 16 + z) * 16 + x)] = type;
    }

    public static int getNybble(byte b, int offset) {
        int val = b < 0 ? b + 256 : b;
        return offset == 0 ? (val % 16) : (val / 16);
    }

    public static byte setNybble(byte b, int offset, int value) {
        int val = b < 0 ? b + 256 : b;
        int result = offset == 0 ? ((val / 16) * 16 + value) : (val % 16 + value * 16);
        if (result > 127) {
            result -= 256;
        }
        return (byte)result;
    }

    public int getData(int x, int y, int z) {
        if (y < 0 || y > 255) {
            return -1;
        }
        //prepare();
        int section = y / 16;
        int remY = y % 16;
        if (sections[section] == null) {
            return -1;
        }
        int addr = ((remY * 16 + z) * 16 + x);
        byte b = ((byte[])sections[section].findTagByName("Data").getValue())[addr / 2];
        return getNybble(b, addr % 2);
    }

    public void setData(byte data, int x, int y, int z) {
        if (y < 0 || y > 255) {
            return;
        }
        //prepare();
        int section = y / 16;
        int remY = y % 16;
        if (sections[section] == null) {
            return;
        }
        int addr = ((remY * 16 + z) * 16 + x);
        byte b = ((byte[])sections[section].findTagByName("Data").getValue())[addr / 2];
        ((byte[])sections[section].findTagByName("Data").getValue())[addr / 2] = setNybble(b, addr % 2, data);
    }

    public int getSkyLight(int x, int y, int z) {
        if (y < 0 || y > 255) {
            return -1;
        }
        //prepare();
        int section = y / 16;
        int remY = y % 16;
        if (sections[section] == null) {
            return -1;
        }
        int addr = ((remY * 16 + z) * 16 + x);
        //byte b = ((byte[]) sections[section].findTagByName("SkyLight").getValue())[addr / 2];
        byte b = sectionSkyLights[section][addr / 2];
        return getNybble(b, addr % 2);
    }

    public void setSkyLight(byte light, int x, int y, int z) {
        if (y < 0 || y > 255) {
            return;
        }
        //prepare();
        int section = y / 16;
        int remY = y % 16;
        if (sections[section] == null) {
            return;
        }
        int addr = ((remY * 16 + z) * 16 + x);
        //byte b = ((byte[]) sections[section].findTagByName("SkyLight").getValue())[addr / 2];
        byte b = sectionSkyLights[section][addr / 2];
        //((byte[]) sections[section].findTagByName("SkyLight").getValue())[addr / 2] = setNybble(b, addr % 2, light);
        sectionSkyLights[section][addr / 2] = setNybble(b, addr % 2, light);
    }

    public int getBlockLight(int x, int y, int z) {
        if (y < 0 || y > 255) {
            return -1;
        }
        //prepare();
        int section = y / 16;
        int remY = y % 16;
        if (sections[section] == null) {
            return -1;
        }
        int addr = ((remY * 16 + z) * 16 + x);
        byte b = ((byte[])sections[section].findTagByName("BlockLight").getValue())[addr / 2];
        return getNybble(b, addr % 2);
    }

    public void setBlockLight(byte light, int x, int y, int z) {
        if (y < 0 || y > 255) {
            return;
        }
        //prepare();
        int section = y / 16;
        int remY = y % 16;
        if (sections[section] == null) {
            return;
        }
        int addr = ((remY * 16 + z) * 16 + x);
        byte b = ((byte[])sections[section].findTagByName("BlockLight").getValue())[addr / 2];
        ((byte[])sections[section].findTagByName("BlockLight").getValue())[addr / 2] = setNybble(b, addr % 2, light);
    }

    static final int[] NS_X = {-1, 1, 0, 0, 0, 0};
    static final int[] NS_Y = {0, 0, -1, 1, 0, 0};
    static final int[] NS_Z = {0, 0, 0, 0, -1, 1};

    public void clearTileEntity(int x, int y, int z, int globX, int globY, int globZ) {
        if (y < 0 || y > 255) {
            return;
        }
        //prepare();
        Tag tesN = t.findTagByName("Level").findTagByName("TileEntities");
        Tag[] tes = (Tag[])tesN.getValue();
        //System.out.println(tes);
        for (int i = 0; i < tes.length; i++) {
			/*System.out.println(tes[i].findTagByName("x").getValue());
			System.out.println(tes[i].findTagByName("y").getValue());
			System.out.println(tes[i].findTagByName("z").getValue());
			System.out.println();*/
            if (((Integer)tes[i].findTagByName("x").getValue()).equals(globX) &&
                ((Integer)tes[i].findTagByName("y").getValue()).equals(globY) &&
                ((Integer)tes[i].findTagByName("z").getValue()).equals(globZ)) {
                tesN.removeTag(i);
                //System.out.println("FOUND AND REMOVED!");
                return;
            }
        }
    }

    public Tag getTileEntity(int x, int y, int z, int globX, int globY, int globZ) {
        if (y < 0 || y > 255) {
            return null;
        }
        //prepare();
        Tag tesN = t.findTagByName("Level").findTagByName("TileEntities");
        Tag[] tes = (Tag[])tesN.getValue();
        for (int i = 0; i < tes.length; i++) {
            if (((Integer)tes[i].findTagByName("x").getValue()).equals(globX) &&
                ((Integer)tes[i].findTagByName("y").getValue()).equals(globY) &&
                ((Integer)tes[i].findTagByName("z").getValue()).equals(globZ)) {
                return tes[i];
            }
        }

        return null;
    }

    public void setTileEntity(Tag te, int x, int y, int z, int globX, int globY, int globZ) {
        if (y < 0 || y > 255) {
            return;
        }
        //prepare();
        Tag tesN = t.findTagByName("Level").findTagByName("TileEntities");
        Tag[] tes = (Tag[])tesN.getValue();
        for (int i = 0; i < tes.length; i++) {
            if (((Integer)tes[i].findTagByName("x").getValue()).equals(globX) &&
                ((Integer)tes[i].findTagByName("y").getValue()).equals(globY) &&
                ((Integer)tes[i].findTagByName("z").getValue()).equals(globZ)) {
                tes[i] = te;
                return;
				/*tesN.removeTag(i);
				break;*/
            }
        }

        tesN.addTag(te);
    }

    public void clearAllEntities() {
        prepare();
        t.findTagByName("Level").findTagByName("Entities").clearList();//setValue(Tag.Type.TAG_Compound);
    }

    public Chunk getChunk(int dx, int dz) {
        return chunks[dz][dx];
    }

    public void setChunk(int dx, int dz, Chunk chunk) {
        chunks[dz][dx] = chunk;
    }
}
