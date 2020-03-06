package ciic4020.list;

public interface List<E> extends Iterable<E> {

	//This is a slightly stripped down version of the list.
	
	public void add(E obj);
	public boolean remove(E obj);
	public E first();
	public int size();
	public boolean contains(E obj);
	public void clear();
}