package com.github.pvginkel.autominer;

import com.github.pvginkel.autominer.nbt.Block;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;

import java.util.*;

public class MinedCounter {
    private final TObjectFloatMap<Block> counts = new TObjectFloatHashMap<>();

    public void inc(Block block) {
        counts.put(block, counts.get(block) + 1);
    }

    public void add(Block block, float count) {
        counts.put(block, counts.get(block) + count);
    }

    public void addAll(MinedCounter counter) {
        for (Object key : counter.counts.keys()) {
            add((Block)key, counter.counts.get(key));
        }
    }

    public void average(int total) {
        for (Object key : counts.keys()) {
            counts.put((Block)key, counts.get(key) / total);
        }
    }

    public Set<Entry> entrySet() {
        Set<Entry> entries = new HashSet<>();

        for (Object key : counts.keys()) {
            entries.add(new Entry((Block)key, counts.get(key)));
        }

        return entries;
    }

    @Override
    public String toString() {
        List<Entry> entries = new ArrayList<>(entrySet());

        Collections.sort(entries, (lhs, rhs) -> lhs.block.getName().compareTo(rhs.block.getName()));

        StringBuilder sb = new StringBuilder();

        for (Entry entry : entries) {
            sb.append(entry.block.getName()).append(": ").append(entry.value).append('\n');
        }

        return sb.toString();
    }

    public float get(Block block) {
        return counts.get(block);
    }

    public static class Entry {
        private final Block block;
        private final float value;

        private Entry(Block block, float value) {
            this.block = block;
            this.value = value;
        }

        public Block getBlock() {
            return block;
        }

        public float getValue() {
            return value;
        }
    }
}
