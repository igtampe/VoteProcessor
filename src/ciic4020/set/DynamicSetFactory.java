package ciic4020.set;

public class DynamicSetFactory<E> implements SetFactory<E> {

	@Override
	public Set<E> newInstance(int capacity) {
		return new DynamicSet<E>(capacity);
	}

}