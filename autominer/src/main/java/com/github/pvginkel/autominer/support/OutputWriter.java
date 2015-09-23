package com.github.pvginkel.autominer.support;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class OutputWriter {
    private final OutputStreamWriter writer;
    private boolean hadOne = false;

    public OutputWriter(OutputStreamWriter writer) {
        this.writer = writer;
    }

    public void write(String value) throws IOException {
        if (hadOne) {
            writer.write('\t');
        } else {
            hadOne = true;
        }
        writer.write(value);
    }

    public void nl() throws IOException {
        writer.write('\n');
        hadOne = false;
    }
}
