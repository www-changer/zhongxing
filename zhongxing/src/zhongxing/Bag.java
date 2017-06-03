package zhongxing;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Bag<Item> implements Iterable<Item> {
	private Node<Item> first;
	private int n;

	private static class Node<Item> {
		private Item item;
		private Node<Item> next;
	}

	public Bag() {
		first = null;
		n = 0;
	}

	public boolean isEmpty() {
		return first == null;
	}

	public int size() {
		return n;
	}

	public void add(Item item) {
		Node<Item> oldfirst = first;
		first = new Node<Item>();
		first.item = item;
		first.next = oldfirst;
		n++;
	}

	public void detele(Item item) {
		Node<Item> index = first;
		if (first.item.equals(item)) {
			first = first.next;
		} else {
			while (index.next.item != item) {
				index = index.next;
			}
			index.next = index.next.next;
		}
		n--;
	}
	
	public boolean contains(Item item){
		Node<Item> index = first;
		while(index!=null){
			//转换为Edge比较(equals返回false)
			if(((Edge) index.item).sameAS(item)) return true;
			index = index.next;
		}
		return false;
	}

	public Iterator<Item> iterator() {
		return new ListIterator<Item>(first);
	}

	private class ListIterator<Item> implements Iterator<Item> {
		private Node<Item> current;

		public ListIterator(Node<Item> first) {
			current = first;
		}

		public boolean hasNext() {
			return current != null;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public Item next() {
			if (!hasNext())
				throw new NoSuchElementException();
			Item item = current.item;
			current = current.next;
			return item;
		}
	}

	

}