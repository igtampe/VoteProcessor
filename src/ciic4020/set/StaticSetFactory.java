package ciic4020.set;

public class StaticSetFactory<E> implements SetFactory<E> {

	@Override
	public Set<E> newInstance(int capacity) {
		return new StaticSet<E>(capacity);
	}

}