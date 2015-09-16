package com.github.pvginkel.minecraft.mca;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PagingByteArray {
	private byte[] array;
	private int size;
	private File pageFile;
	
	public PagingByteArray(int size) { this.size = size; }
	
	public byte[] get() throws IOException {
		if (array == null) {
			pageIn();
		}
		return array;
	}
	
	public void pageIn() throws IOException {
		if (pageFile == null) {
			array = new byte[size];
		} else {
			RandomAccessFile raf = new RandomAccessFile(pageFile, "r");
			raf.read(array);
			raf.close();
		}
	}
	
	public void pageOut() throws IOException {
		if (pageFile == null) {
			pageFile = File.createTempFile("pba", null);
		}
		RandomAccessFile raf = new RandomAccessFile(pageFile, "rw");
		raf.write(array);
		raf.close();
	}
	
	public static void main(String[] args) throws IOException {
		long ms = System.currentTimeMillis();
		PagingByteArray[] pbas = new PagingByteArray[100];
		for (int i = 0; i < 100; i++) { pbas[i] = new PagingByteArray(4096); }
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
				pbas[j].get();
			}
			for (int j = 0; j < 100; j++) {
				pbas[j].pageOut();
			}
		}
		System.out.println(System.currentTimeMillis() - ms);
	}
}
