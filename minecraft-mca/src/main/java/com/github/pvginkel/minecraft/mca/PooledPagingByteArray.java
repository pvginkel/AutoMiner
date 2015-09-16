package com.github.pvginkel.minecraft.mca;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PooledPagingByteArray {
	public static class Pool {
		private File f;
		private RandomAccessFile raf;
		private long offset = 0;

		public Pool() throws IOException {
			f = File.createTempFile("bpp", null);
			f.deleteOnExit();
//			System.out.println(f.getAbsolutePath());
			raf = new RandomAccessFile(f, "rw");
		}
		
		public PooledPagingByteArray getArray(int size) { return new PooledPagingByteArray(size, this); }
		
		public void close() throws IOException {
			raf.close();
			f.delete();
			f = null;
			raf = null;
		}
	}
	
	private final Pool pool;
	private byte[] array;
	private final int size;
	private long offset = -1;
	
	private PooledPagingByteArray(int size, Pool pool) { this.size = size; this.pool = pool; }
	
	public byte[] get() throws IOException {
		if (array == null) {
			pageIn();
		}
		return array;
	}
	
	public void reset() {
		array = null;
		offset = -1;
	}
	
	public void pageIn() throws IOException {
		array = new byte[size];
		if (offset != -1) {
			pool.raf.seek(offset);
			pool.raf.read(array);
		}
	}
	
	public void pageOut() throws IOException {
		if (array == null) { return; }
		if (offset == -1) {
			offset = pool.offset;
			pool.offset += size;
		}
		pool.raf.seek(offset);
		pool.raf.write(array);
		array = null;
	}
	
	public static void main(String[] args) throws IOException {
		Pool p = new Pool();
		long ms = System.currentTimeMillis();
		PooledPagingByteArray[] pbas = new PooledPagingByteArray[10000];
		for (int i = 0; i < 10000; i++) { pbas[i] = p.getArray(4096); }
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10000; j++) {
				pbas[j].get();
			}
			for (int j = 0; j < 10000; j++) {
				pbas[j].pageOut();
			}
		}
		System.out.println(System.currentTimeMillis() - ms);
	}
}
