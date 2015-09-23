package com.github.pvginkel.autominer.nbt;

public class NBTWalker {
    private final NBTStreamReader sr;

    public NBTWalker(NBTStreamReader sr) throws Exception {
        this.sr = sr;

        NBTTag tag = sr.next();
        if (tag != NBTTag.END) {
            readValue(tag);
        }
    }

    public boolean value(String name, byte value) throws Exception {
        return true;
    }
    
    public boolean value(String name, short value) throws Exception {
        return true;
    }
    
    public boolean value(String name, int value) throws Exception {
        return true;
    }
    
    public boolean value(String name, long value) throws Exception {
        return true;
    }
    
    public boolean value(String name, float value) throws Exception {
        return true;
    }
    
    public boolean value(String name, double value) throws Exception {
        return true;
    }
    
    public boolean value(String name, String value) throws Exception {
        return true;
    }
    
    public boolean value(String name, byte[] value) throws Exception {
        return true;
    }
    
    public boolean value(String name, int[] value) throws Exception {
        return true;
    }
    
    public boolean list(String name, int length) throws Exception {
        return readNested();
    }

    public boolean compound(String name) throws Exception {
        return readNested();
    }

    private boolean readNested() throws Exception {
        NBTTag tag;
        while ((tag = sr.next()) != NBTTag.END) {
            if (!readValue(tag)) {
                return false;
            }
        }

        return true;
    }

    private boolean readValue(NBTTag tag) throws Exception {
        switch (tag) {
            case BYTE:
                return value(sr.name(), sr.readByte());
            case SHORT:
                return value(sr.name(), sr.readShort());
            case INT:
                return value(sr.name(), sr.readInt());
            case LONG:
                return value(sr.name(), sr.readLong());
            case FLOAT:
                return value(sr.name(), sr.readFloat());
            case DOUBLE:
                return value(sr.name(), sr.readDouble());
            case BYTE_ARRAY:
                return value(sr.name(), sr.readByteArray());
            case STRING:
                return value(sr.name(), sr.readString());
            case LIST:
                String name = sr.name();
                sr.beginList();
                if (!list(name, sr.length())) {
                    return false;
                }
                sr.endList();
                return true;
            case COMPOUND:
                name = sr.name();
                sr.beginCompound();
                if (!compound(name)) {
                    return false;
                }
                sr.endCompound();
                return true;
            case INT_ARRAY:
                return value(sr.name(), sr.readIntArray());
            default:
                throw new IllegalStateException();
        }
    }
}
