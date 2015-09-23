package com.github.pvginkel.autominer;

import com.github.pvginkel.autominer.support.Vector;

public class Chunk {
    private final Vector position;
    private final byte[] blocks;

    public Chunk(Vector position, byte[] blocks) {
        this.position = position;
        this.blocks = blocks;
    }

    public Vector getPosition() {
        return position;
    }

    public byte[] getBlocks() {
        return blocks;
    }
}
