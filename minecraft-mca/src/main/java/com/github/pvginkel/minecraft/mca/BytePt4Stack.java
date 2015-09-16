package com.github.pvginkel.minecraft.mca;

public final class BytePt4Stack {
	byte[] data;
	int headPtr = -4;
	public int x, y, z, l;

	public BytePt4Stack(final int capacity) {
		data = new byte[4 * capacity];
	}
	
	public int size() { return headPtr / 4 + 1; }
	public int capacity() { return data.length / 4; }
	public boolean isEmpty() { return headPtr == -4; }
	public void clear() { headPtr = -4; }
	public void push(int x, int y, int z, int l) {
		headPtr += 4;
		if (headPtr == data.length) {
			byte[] newData = new byte[data.length * 2];
			System.arraycopy(data, 0, newData, 0, data.length);
			data = newData;
		}
		data[headPtr    ] = (byte) x;
		data[headPtr + 1] = (byte) y;
		data[headPtr + 2] = (byte) z;
		data[headPtr + 3] = (byte) l;
	}
	public void pop() {
		x = data[headPtr    ];
		y = data[headPtr + 1];
		z = data[headPtr + 2];
		l = data[headPtr + 3];
		headPtr -= 4;
	}
	public void get(final int index) {
		x = data[index * 4    ];
		y = data[index * 4 + 1];
		z = data[index * 4 + 2];
		l = data[index * 4 + 3];
	}
	public void compact() {
		byte[] newData = new byte[headPtr + 4];
		System.arraycopy(data, 0, newData, 0, newData.length);
		data = newData;
	}
	public void compactTo(final int cap) {
		byte[] newData = new byte[Math.max(headPtr + 4, cap * 4)];
		System.arraycopy(data, 0, newData, 0, newData.length);
		data = newData;
	}
}
