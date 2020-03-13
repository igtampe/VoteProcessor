package ciic4020.set;

public interface SetFactory<E> {

	public Set<E> newInstance(int capacity);

}