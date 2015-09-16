package com.github.pvginkel.autominer;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private final TObjectFloatMap<Key> values = new TObjectFloatHashMap<>();
    private final List<String> columns = new ArrayList<>();
    private final List<String> rows = new ArrayList<>();

    public void set(String column, String row, float value) {
        values.put(getKey(column, row), value);
    }

    public void add(String column, String row, float value) {
        Key key = getKey(column, row);
        values.put(key, values.get(key) + value);
    }

    public void write(OutputWriter writer) throws IOException {
        writer.write("");
        for (String column : columns) {
            writer.write(column);
        }
        writer.nl();
        for (String row : rows) {
            writer.write(row);
            for (String column : columns) {
                Key key = new Key(column, row);
                if (values.containsKey(key)) {
                    writer.write(String.format("%.1f", values.get(key)));
                } else {
                    writer.write("");
                }
            }
            writer.nl();
        }
    }

    private Key getKey(String column, String row) {
        if (!columns.contains(column)) {
            columns.add(column);
        }
        if (!rows.contains(row)) {
            rows.add(row);
        }
        return new Key(column, row);
    }

    private static class Key {
        final String row;
        final String column;

        public Key(String column, String row) {
            this.row = row;
            this.column = column;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Key)) {
                return false;
            }

            Key key = (Key)o;

            return row.equals(key.row) && column.equals(key.column);

        }

        @Override
        public int hashCode() {
            int result = row.hashCode();
            result = 31 * result + column.hashCode();
            return result;
        }
    }
}
