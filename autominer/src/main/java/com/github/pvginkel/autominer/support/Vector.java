package com.github.pvginkel.autominer.support;

public class Vector {
    private final int x;
    private final int y;
    private final int z;

    public Vector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vector)) {
            return false;
        }

        Vector vector = (Vector)o;

        return x == vector.x && y == vector.y && z == vector.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return x + "x" + y + "x" + z;
    }
}
