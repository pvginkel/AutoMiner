package com.github.pvginkel.minecraft.mca;

import java.util.ArrayList;
import java.util.Iterator;

public class CoordMap<T> implements Iterable<T> {
	private Object[][] map = new Object[1][1];
	private ArrayList<T> list = new ArrayList<T>();
	private int xOffset, yOffset;
	
	public T get(int x, int y) {
		int nx = x + xOffset;
		int ny = y + yOffset;
		if (ny < 0 || ny >= map.length || nx < 0 || nx >= map[ny].length) {
			return null;
		}
		return (T) map[ny][nx];
	}
	
	public void put(int x, int y, T o) {
		int nx = x + xOffset;
		int ny = y + yOffset;
		if (ny < 0) {
			Object[][] newMap = new Object[map.length - ny][map[0].length];
			System.arraycopy(map, 0, newMap, -ny, map.length);
			map = newMap;
			yOffset -= ny;
			put(x, y, o);
			return;
		}
		if (ny >= map.length) {
			Object[][] newMap = new Object[ny + 1][map[0].length];
			System.arraycopy(map, 0, newMap, 0, map.length);
			map = newMap;
			put(x, y, o);
			return;
		}
		if (nx < 0) {
			Object[][] newMap = new Object[map.length][map[0].length - nx];
			for (int i = 0; i < map.length; i++) {
				System.arraycopy(map[i], 0, newMap[i], -nx, map[i].length);
			}
			map = newMap;
			xOffset -= nx;
			put(x, y, o);
			return;
		}
		if (nx >= map[0].length) {
			Object[][] newMap = new Object[map.length][nx + 1];
			for (int i = 0; i < map.length; i++) {
				System.arraycopy(map[i], 0, newMap[i], 0, map[i].length);
			}
			map = newMap;
			put(x, y, o);
			return;
		}
		list.remove((T) map[ny][nx]);
		list.add(o);
		map[ny][nx] = o;
	}

	@Override
	public Iterator<T> iterator() { return list.iterator(); }
	
	public int size() { return list.size(); }
	
	// Quick testing code.
	public static void main(String[] args) {
		CoordMap<String> cm = new CoordMap<String>();
		System.out.println(cm.get(0, 0) == null);
		System.out.println(cm.get(0, 1) == null);
		System.out.println(cm.get(-1, -1) == null);
		cm.put(0, 0, "a");
		System.out.println("a".equals(cm.get(0, 0)));
		cm.put(1, 0, "b");
		System.out.println("b".equals(cm.get(1, 0)));
		cm.put(-1, 0, "c");
		System.out.println("c".equals(cm.get(-1, 0)));
		cm.put(-9, 0, "d");
		System.out.println("d".equals(cm.get(-9, 0)));
		cm.put(0, 9, "e");
		System.out.println("e".equals(cm.get(0, 9)));
		cm.put(20, 20, "f");
		System.out.println("f".equals(cm.get(20, 20)));
		cm.put(-20, -20, "g");
		System.out.println("g".equals(cm.get(-20, -20)));
	}
}
