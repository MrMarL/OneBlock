package oneblock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generic weighted random picker with O(log n) pick via prefix-sum + binary search.
 * Entries are accumulated through {@link #add(Object, int)}; {@link #pick(Random)}
 * will call {@link #build()} lazily if the prefix sums are not yet built.
 *
 * pick() on an empty / zero-weight pool returns null.
 */
public class WeightedPool<T> {
	private final List<T> values = new ArrayList<>();
	private final List<Integer> weights = new ArrayList<>();
	private int[] cumulative = new int[0];
	private int total = 0;
	private boolean built = false;

	public void add(T value, int weight) {
		if (weight <= 0) return;
		values.add(value);
		weights.add(weight);
		built = false;
	}

	public void build() {
		cumulative = new int[weights.size()];
		total = 0;
		for (int i = 0; i < weights.size(); i++) {
			total += weights.get(i);
			cumulative[i] = total;
		}
		built = true;
	}

	public int size() { return values.size(); }

	public int totalWeight() {
		if (!built) build();
		return total;
	}

	public T pick(Random rnd) {
		if (!built) build();
		if (total <= 0) return null;
		int r = rnd.nextInt(total);
		int lo = 0, hi = cumulative.length - 1;
		while (lo < hi) {
			int mid = (lo + hi) >>> 1;
			if (cumulative[mid] <= r) lo = mid + 1;
			else hi = mid;
		}
		return values.get(lo);
	}

	public List<Entry<T>> entries() {
		List<Entry<T>> list = new ArrayList<>(values.size());
		for (int i = 0; i < values.size(); i++)
			list.add(new Entry<>(values.get(i), weights.get(i)));
		return Collections.unmodifiableList(list);
	}

	public static final class Entry<V> {
		public final V value;
		public final int weight;
		public Entry(V value, int weight) {
			this.value = value;
			this.weight = weight;
		}
	}
}
