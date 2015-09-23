package com.github.pvginkel.autominer.nbt;

import org.apache.commons.lang.Validate;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Stack;

public class NBTStreamReader implements Closeable {
    private static final NBTTag[] TAGS = NBTTag.values();

    private final DataInputStream is;
    private NBTTag nextTag;
    private final Stack<Tag> stack = new Stack<>();
    private Tag current;
    private String name;

    public NBTStreamReader(DataInputStream is) {
        Validate.notNull(is, "is");

        this.is = is;

        current = new Tag();
    }

    public NBTTag peek() throws IOException {
        if (current.length != -1) {
            if (current.index + 1 < current.length) {
                return current.type;
            } else {
                return NBTTag.END;
            }
        }

        if (nextTag == null) {
            nextTag = TAGS[is.readByte()];
        }

        return nextTag;
    }

    public NBTTag next() throws IOException {
        current.type = peek();

        if (current.length != -1) {
            current.index++;
        }

        nextTag = null;

        if (current.length == -1 && current.type != NBTTag.END) {
            name = is.readUTF();
        } else {
            name = null;
        }

        return current.type;
    }

    private void validateType(NBTTag type) {
        if (current.type != type) {
            throw new IllegalStateException(String.format("Cannot read %s; expected %s", type, current.type));
        }
    }

    public String name() {
        return name;
    }

    public int length() {
        return current.length;
    }

    public int index() {
        return current.index;
    }

    public void beginList() throws IOException {
        validateType(NBTTag.LIST);

        name = null;

        stack.push(current);

        current = new Tag();
        current.type = TAGS[is.readByte()];
        current.length = is.readInt();
        current.index = -1;
    }

    public void endList() throws IOException {
        if (current.length == -1) {
            throw new IllegalStateException(String.format("Expected the end of a list, had %s", current.type));
        }

        name = null;

        validateType(NBTTag.END);
        current = stack.pop();
    }

    public void beginCompound() {
        validateType(NBTTag.COMPOUND);

        name = null;

        stack.push(current);

        current = new Tag();
    }

    public void endCompound() {
        validateType(NBTTag.END);

        name = null;
        current = stack.pop();
    }

    public byte readByte() throws IOException {
        validateType(NBTTag.BYTE);
        return is.readByte();
    }

    public short readShort() throws IOException {
        validateType(NBTTag.SHORT);
        return is.readShort();
    }

    public int readInt() throws IOException {
        validateType(NBTTag.INT);
        return is.readInt();
    }

    public long readLong() throws IOException {
        validateType(NBTTag.LONG);
        return is.readLong();
    }

    public float readFloat() throws IOException {
        validateType(NBTTag.FLOAT);
        return is.readFloat();
    }

    public double readDouble() throws IOException {
        validateType(NBTTag.DOUBLE);
        return is.readDouble();
    }

    public String readString() throws IOException {
        validateType(NBTTag.STRING);
        return is.readUTF();
    }

    public byte[] readByteArray() throws IOException {
        validateType(NBTTag.BYTE_ARRAY);
        int length = is.readInt();
        byte[] result = new byte[length];
        is.readFully(result);
        return result;
    }

    public int[] readIntArray() throws IOException {
        validateType(NBTTag.INT_ARRAY);
        int length = is.readInt();
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = is.readInt();
        }
        return result;
    }

    private static class Tag {
        NBTTag type;
        int length = -1;
        int index = -1;
    }

    @Override
    public void close() throws IOException {
        is.close();
    }
}
