package com.github.pvginkel.autominer.nbt;

/*
 ** 2011 January 5
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 **/

/*
 * 2011 February 16
 * 
 * This source code is based on the work of Scaevolus (see notice above).
 * It has been slightly modified by Mojang AB (constants instead of magic
 * numbers, a chunk timestamp header, and auto-formatted according to our
 * formatter template).
 * 
 */

// Interfaces with region files on the disk

/*

 Region File Format

 Concept: The minimum unit of storage on hard drives is 4KB. 90% of Minecraft
 chunks are smaller than 4KB. 99% are smaller than 8KB. Write a simple
 container to store chunks in single files in runs of 4KB sectors.

 Each region file represents a 32x32 group of chunks. The conversion from
 chunk number to region number is floor(coord / 32): a chunk at (30, -3)
 would be in region (0, -1), and one at (70, -30) would be at (3, -1).
 Region files are named "r.x.z.data", where x and z are the region coordinates.

 A region file begins with a 4KB header that describes where chunks are stored
 in the file. A 4-byte big-endian integer represents sector offsets and sector
 counts. The chunk offset for a chunk (x, z) begins at byte 4*(x+z*32) in the
 file. The bottom byte of the chunk offset indicates the number of sectors the
 chunk takes up, and the top 3 bytes represent the sector number of the chunk.
 Given a chunk offset o, the chunk data begins at byte 4096*(o/256) and takes up
 at most 4096*(o%256) bytes. A chunk cannot exceed 1MB in size. If a chunk
 offset is 0, the corresponding chunk is not stored in the region file.

 Chunk data begins with a 4-byte big-endian integer representing the chunk data
 length in bytes, not counting the length field. The length must be smaller than
 4096 times the number of sectors. The next byte is a version field, to allow
 backwards-compatible updates to how chunks are encoded.

 A version of 1 represents a gzipped NBT file. The gzipped data is the chunk
 length - 1.

 A version of 2 represents a deflated (zlib compressed) NBT file. The deflated
 data is the chunk length - 1.

 */

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

@SuppressWarnings("unused")
public class RegionFile implements Closeable {
    private static final int VERSION_GZIP = 1;
    private static final int VERSION_DEFLATE = 2;

    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = SECTOR_BYTES / 4;

    private final RandomAccessFile file;
    private final int offsets[];
    private final ArrayList<Boolean> sectorFree;
    private int sizeDelta;
    private long lastModified = 0;

    public RegionFile(File path) throws IOException {
        offsets = new int[SECTOR_INTS];

        sizeDelta = 0;

        if (path.exists()) {
            lastModified = path.lastModified();
        }

        file = new RandomAccessFile(path, "rw");

        if (file.length() < SECTOR_BYTES) {
            throw new IllegalStateException("Invalid region file");
        }

        if ((file.length() & 0xfff) != 0) {
            throw new IllegalStateException("Invalid region file");
        }

        /* set up the available sector map */
        int nSectors = (int)file.length() / SECTOR_BYTES;
        sectorFree = new ArrayList<>(nSectors);

        for (int i = 0; i < nSectors; ++i) {
            sectorFree.add(true);
        }

        sectorFree.set(0, false); // chunk offset table
        sectorFree.set(1, false); // for the last modified info

        file.seek(0);
        for (int i = 0; i < SECTOR_INTS; ++i) {
            int offset = file.readInt();
            offsets[i] = offset;
            if (offset != 0 && (offset >> 8) + (offset & 0xFF) <= sectorFree.size()) {
                for (int sectorNum = 0; sectorNum < (offset & 0xFF); ++sectorNum) {
                    sectorFree.set((offset >> 8) + sectorNum, false);
                }
            }
        }
    }

    /* the modification date of the region file when it was first opened */
    public long lastModified() {
        return lastModified;
    }

    /* gets how much the region file has grown since it was last checked */
    public int getSizeDelta() {
        int ret = sizeDelta;
        sizeDelta = 0;
        return ret;
    }

    /*
     * gets an (uncompressed) stream representing the chunk data returns null if
     * the chunk is not found or an error occurs
     */
    public NBTStreamReader getChunk(int x, int z) throws IOException {
        if (x < 0 || x >= 32 || z < 0 || z >= 32) {
            throw new IllegalStateException("Invalid chunk position");
        }

        int offset = getOffset(x, z);
        if (offset == 0) {
            throw new IllegalStateException("Chunk not present");
        }

        int sectorNumber = offset >> 8;
        int numSectors = offset & 0xFF;

        if (sectorNumber + numSectors > sectorFree.size()) {
            throw new IllegalStateException("Invalid sector number");
        }

        file.seek(sectorNumber * SECTOR_BYTES);
        int length = file.readInt();

        if (length > SECTOR_BYTES * numSectors) {
            throw new IllegalStateException("Invalid length");
        }

        byte version = file.readByte();

        byte[] data = new byte[length - 1];
        file.read(data);
        InputStream is = new ByteArrayInputStream(data);

        switch (version) {
            case VERSION_GZIP:
                is = new GZIPInputStream(is);
                break;

            case VERSION_DEFLATE:
                is = new InflaterInputStream(is);
                break;

            default:
                throw new IllegalStateException("Invalid chunk version " + version);
        }

        return new NBTStreamReader(new DataInputStream(new BufferedInputStream(is)));
    }

    private int getOffset(int x, int z) {
        return offsets[x + z * 32];
    }

    public boolean hasChunk(int x, int z) {
        return getOffset(x, z) != 0;
    }

    @Override
    public void close() throws IOException {
        file.close();
    }
}
