package com.github.pvginkel.autominer.nbt;

public class SectionData {
    private final byte[] sectionData;

    public SectionData(byte[] sectionData) {
        this.sectionData = sectionData;
    }

    public byte getType(int lx, int ly, int lz) {
        return sectionData[((ly * 16 + lz) * 16 + lx)];
    }

    public Block getBlock(int lx, int ly, int lz) {
        return Block.get(getType(lx, ly, lz));
    }
}
