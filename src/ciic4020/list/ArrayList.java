package ciic4020.list;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayList<E> implements List<E> {

	//Hey I was here and cleaned up a few things we didn't use. Hope u don't mind
	//-IGT
	
	// private fields
	private E elements[];
	private int currentSize;
	
	private class ListIterator implements Iterator<E> {
		private int currentPosition;
		
		public ListIterator() {this.currentPosition = 0;}

		@Override
		public boolean hasNext() {return this.currentPosition < size();}

		@Override
		public E next() {
			if (this.hasNext()) {return (E) elements[this.currentPosition++];}
			else {throw new NoSuchElementException();}
		}
	}

	
	@SuppressWarnings("unchecked")
	public ArrayList(int initialCapacity) {
		if (initialCapacity < 1)
			throw new IllegalArgumentException("Capacity must be at least 1.");
		this.currentSize = 0;
		this.elements = (E[]) new Object[initialCapacity];
	}

	@Override
	public void add(E obj) {
		if (obj == null) {throw new IllegalArgumentException("Object cannot be null.");}
		else {
			if (this.size() == this.elements.length) {reAllocate();}
			this.elements[this.currentSize++] = obj;
		}		
	}
	
	@SuppressWarnings("unchecked")
	private void reAllocate() {
		// create a new array with twice the size
		E newElements[] = (E[]) new Object[2*this.elements.length];
		for (int i = 0; i < this.size(); i++)
			newElements[i] = this.elements[i];
		// replace old elements with newElements
		int oldcurrentsize=this.currentSize; 		//haha it is my fix que lindo
		this.clear();
		this.currentSize=oldcurrentsize;
		this.elements = newElements;
	}

	@Override
	public boolean remove(E obj) {
		if (obj == null) {throw new IllegalArgumentException("Object cannot be null.");}
		// first find obj in the array
		int position = this.firstIndex(obj);
		if (position >= 0) {return this.remove(position);} // found it
		else {return false;}
	}

	private boolean remove(int index) {
		if (index >= 0 && index < this.currentSize) {
			// move everybody one spot to the front
			for (int i = index; i < this.currentSize - 1; i++)
				this.elements[i] = this.elements[i + 1];
			this.elements[--this.currentSize] = null;
			return true;
		}
		else {return false;}
	}

	@Override
	public E first() {
		if (this.isEmpty()) {return null;}
		return this.elements[0];
	}

	public int firstIndex(E obj) {
		for (int i = 0; i < this.size(); i++)
			if (this.elements[i].equals(obj))
				return i;
		return -1;
	}

	@Override
	public int size() {return this.currentSize;}

	private boolean isEmpty() {return this.size() == 0;}

	@Override
	public boolean contains(E obj) {return this.firstIndex(obj) >= 0;}

	@Override
	public void clear() {
		for (int i = 0; i < this.currentSize; i++) {this.elements[i] = null;}
		this.currentSize = 0;
	}

	@Override
	public Iterator<E> iterator() {return new ListIterator();}
}