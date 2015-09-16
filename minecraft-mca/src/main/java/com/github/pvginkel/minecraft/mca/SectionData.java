package com.github.pvginkel.minecraft.mca;

public class SectionData {
    private final byte[] sectionData;

    public SectionData(byte[] sectionData) {
        this.sectionData = sectionData;
    }

    public Block getBlock(int lx, int ly, int lz) {
        int type = sectionData[((ly * 16 + lz) * 16 + lx)];
        return Block.get(type);
    }
}
