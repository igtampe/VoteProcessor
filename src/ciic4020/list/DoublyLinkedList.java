package ciic4020.list;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ciic4020.list.List;

public class DoublyLinkedList<E> implements List<E> {

	private class Node {
		private E value;
		private Node next, prev;
		
		/**
		 * Creates a new node
		 * @param value Value of the node
		 * @param next NEXT one in the sequence
		 * @param prev PREVIOUS one in the sequence
		 */
		public Node(E value, Node next, Node prev) {
			this.value = value;
			this.next = next;
			this.prev = prev;
		}
		
		//This constructor goes unused and I wonder why. Still though it would be nice to keep it.
		@SuppressWarnings("unused")
		public Node(E value) {this(value, null, null);} // Delegate to other constructor
		public Node() {this(null, null, null);} // Delegate to other constructor
		
		public E getValue() {return value;}
		public Node getNext() {return next;}
		public void setNext(Node next) {this.next = next;}
		public Node getPrev() {return prev;}
		public void setPrev(Node prev) {this.prev = prev;}
		
		public void clear() {
			value = null;
			next = prev = null;
		}				
	} // End of Node class

	
	private class ListIterator implements Iterator<E> {

		private Node nextNode;
		
		public ListIterator() {nextNode = header.getNext();}
	
		@Override
		public boolean hasNext() {return nextNode != trailer;}

		@Override
		public E next() {
			if (hasNext()) {
				E val = nextNode.getValue();
				nextNode = nextNode.getNext();
				return val;
			}
			else
				throw new NoSuchElementException();				
		}
		
	} // End of ListIterator class

	
	/* private fields */
	private Node header, trailer; // "dummy" nodes
	private int currentSize;

	
	public DoublyLinkedList() {
		header = new Node();
		trailer = new Node();
		header.setNext(trailer); 
		trailer.setPrev(header);
		currentSize = 0;
	}

	@Override
	public Iterator<E> iterator() {
		return new ListIterator();
	}

	@Override
	public void add(E obj) {
		//Create a new node with the one next being the trailer, and the one before being the one before the trailer
		//OSEA PONERLO JUSTO EN EL MEDIO DE AMBOS
		Node newNode = new Node(obj,trailer,trailer.getPrev());
		
		//Get the previous node
		Node PrevNode = trailer.getPrev();
		
		//Make sure the previous node links to the new node
		PrevNode.setNext(newNode);
		
		//Make sure the trailer node links back to the new node.
		trailer.setPrev(newNode);
		
		//Increment current size and then lets get the H E C C out
		currentSize++;
	}

	
	@Override
	public boolean remove(E obj) {
		Node curNode = header;
		Node nextNode = curNode.getNext();
		
		// Traverse the list until we find the element or we reach the end
		while (nextNode != trailer && !nextNode.getValue().equals(obj)) {
			curNode = nextNode;
			nextNode = nextNode.getNext();
		}
		
		// Need to check if we found it
		if (nextNode != trailer) { // Found it!
			// If we have A <-> B <-> C, need to get to A <-> C
			curNode.setNext(nextNode.getNext());
			
			//nextnode is the one we want to delete
			//Cur node is the one before it
			//So Get the node after the one we want to delete, and link it to curnode (which is the one before the one we want to delete)
			nextNode.getNext().setPrev(curNode);
			
			nextNode.clear(); // free up resources
			currentSize--;
			return true;
			
			//Oye unlike in a singly linked list where we need the node before the current one to delete it, here we only need the node itself and we can delete
			//the node using only its data. It may be w i s e to add a DeleteNode internal method which takes a node and does this all in one place.
			//In a singly linked one, it could also be done, but you'd just need the node before and the one u want to delete.
			
		}
		else
			return false;
	}
	
	private boolean remove(int index) {
		Node rmNode;
	
		// First confirm index is a valid position
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		// If we have A <-> B <-> C, need to get to A <-> C
		rmNode = get_node(index); // Get the node that is to be removed
		
		//link the one before rmNode to the one next to it
		rmNode.getPrev().setNext(rmNode.getNext());
		
		//Link the one next to rmNode to the one before it
		rmNode.getNext().setPrev(rmNode.getPrev());
		
		rmNode.clear(); //clear up resources
		currentSize--;		
		
		return true;
	}
	
	/* Private method to return the node at position index */
	private Node get_node(int index) {
		Node curNode;
		
		/* First confirm index is a valid position
		   Allow -1 so that header node may be returned */
		if (index < -1 || index >= size())
			throw new IndexOutOfBoundsException();
		curNode = header;
		// Since first node is pos 0, let header be position -1
		for (int curPos = -1; curPos < index; curPos++)
			curNode = curNode.getNext();
		// Perhaps we could traverse backwards instead if index > size/2...
		//Yeah probably do eso.
		return curNode;
	}

	public E get(int index) {
		// get_node allows for index to be -1, but we don't want get to allow that
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		return get_node(index).getValue();
	}

	@Override
	public E first() {return get(0);}

	public int firstIndex(E obj) {
		Node curNode = header.getNext();
		int curPos = 0;
		// Traverse the list until we find the element or we reach the end
		while (curNode != trailer && !curNode.getValue().equals(obj)) {
			curPos++;
			curNode = curNode.getNext();
		}
		if (curNode != trailer)
			return curPos;
		else
			return -1;
	}

	@Override
	public int size() {return currentSize;}

	@Override
	public boolean contains(E obj) {return firstIndex(obj) != -1;}

	@Override
	public void clear() {
		// Avoid throwing an exception if the list is already empty
		while (size() > 0)
			remove(0);
	}
}