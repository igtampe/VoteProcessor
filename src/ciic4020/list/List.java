package ciic4020.list;

/**
 * 	This is a slightly stripped down version of the list interface. We only use what we need. Let's save some space!
 * @author igtampe
 *
 * @param <E>
 */
public interface List<E> extends Iterable<E> {
	
	public void add(E obj);
	public boolean remove(E obj);
	public E first();
	public int size();
	public boolean contains(E obj);
	public void clear();
}