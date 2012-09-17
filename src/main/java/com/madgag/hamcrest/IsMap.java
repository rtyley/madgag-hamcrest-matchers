package com.madgag.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;

public class IsMap<K,V> extends TypeSafeMatcher<Map<K,V>> {
	private Map<Matcher<K>, Matcher<V>> keyValueConstraints;
	private final boolean shouldHaveOnlyTheseEntries;

	private IsMap(boolean shouldHaveOnlyTheseEntries) {
		this.shouldHaveOnlyTheseEntries = shouldHaveOnlyTheseEntries;
		this.keyValueConstraints = new HashMap<Matcher<K>, Matcher<V>>();
	}

	public static <K,V> IsMap<K,V> containing(Matcher<K> keyConstraint, Matcher<V> valueConstraint) {
		return new IsMap<K,V>(false).and(keyConstraint, valueConstraint);
	}

    public static <K,V> IsMap<K,V> containing(Map<K, V> allItems) {
        IsMap<K,V> matcher = new IsMap<K,V>(false);
        for (Map.Entry<K,V> e : allItems.entrySet()) {
            matcher.and(e.getKey(),e.getValue());
        }
        return matcher;
    }

	public static <K,V> IsMap<K,V> containing(K key, V value) {
		return new IsMap<K,V>(false).and(key, value);
	}

	public static <K,V> IsMap<K,V> containingOnly(K key, Matcher<V> valueConstraint) {
		return new IsMap<K,V>(true).and(key, valueConstraint);
	}

	public static <K,V> IsMap<K,V> containingOnly(Matcher<K> keyConstraint, Matcher<V> valueConstraint) {
		return new IsMap<K,V>(true).and(keyConstraint, valueConstraint);
	}

	public static <K,V> IsMap<K,V> containingOnly(K key, V value) {
		return new IsMap<K,V>(true).and(key, value);
	}

	public IsMap<K,V> and(Matcher<K> keyConstraint, Matcher<V> valueConstraint) {
		keyValueConstraints.put(keyConstraint,valueConstraint);
		return this;
	}

	public IsMap<K,V> and(K key, Matcher<V> valueConstraint) {
		return and(equalTo(key),valueConstraint);
	}

	public IsMap<K,V> and(K key, V value) {
		return and(key,equalTo(value));
	}

	@Override
	public boolean matchesSafely(Map<K, V> map) {
		Set<Entry<Matcher<K>, Matcher<V>>> keyValueConstraintsNotYetSatisfied = new HashSet<Entry<Matcher<K>, Matcher<V>>>(keyValueConstraints.entrySet());

		for (Entry<?, ?> mapEntry : map.entrySet()) {
			boolean matchedEntry = false;
			for (Entry<Matcher<K>, Matcher<V>> keyValueConstraintEntry : keyValueConstraintsNotYetSatisfied) {
				Matcher<K> keyConstraint = keyValueConstraintEntry.getKey();
				if (keyConstraint.matches(mapEntry.getKey())) {
					Matcher<V> valueConstraint = keyValueConstraintEntry.getValue();
					if (valueConstraint.matches(mapEntry.getValue())) {
						matchedEntry = true;
						keyValueConstraintsNotYetSatisfied.remove(keyValueConstraintEntry);
						break; // We have matched the key, and the value of the entry satisfies the constraint
					} else {
						return false; // We have matched the key, but the value of the entry does not satisfy the constraint
					}
				}
			}
			if (!matchedEntry && shouldHaveOnlyTheseEntries) {
				return false;
			}
		}
		return keyValueConstraintsNotYetSatisfied.isEmpty();
	}

	// @Override
	public void describeTo(Description description) {
		description.appendText("map containing");
		if (shouldHaveOnlyTheseEntries) {
			description.appendText(" only");
		}
		description.appendText(" [");
		boolean firstEntry = true;
		for (Entry<Matcher<K>, Matcher<V>> entry : keyValueConstraints.entrySet()) {
			if (firstEntry) {
				firstEntry = false;
			} else {
				description.appendText(", ");
			}
			entry.getKey().describeTo(description);
			description.appendText("->");
			entry.getValue().describeTo(description);
		}
		description.appendText("]");
	}
}
