package com.utils;

import java.util.Collection;
import java.util.function.Function;

import com.utils.exceptions.OutOfBoundsException;

/**
 * A generic {@code Range} class that represents a continuous interval between two comparable elements: 
 * a lower bound ({@code start}) and an upper bound ({@code end}).
 * <p>
 * The {@code Range} class ensures that {@code start} is always less than or equal to {@code end}. 
 * If the constructor or setter methods receive inputs that would violate this order, 
 * the bounds are automatically swapped to preserve validity.
 * <p>
 * This class can be used to model numeric ranges, date intervals, character bounds, or any 
 * other data type that implements {@link Comparable}.
 * 
 * <p><strong>Examples:</strong></p>
 * <pre>{@code
 * Range<Integer> intRange = new Range<>(5, 10);
 * System.out.println(intRange.contains(7)); // true
 * 
 * Range<String> alphaRange = new Range<>("a", "m");
 * System.out.println(alphaRange.contains("z")); // false
 * }</pre>
 * 
 * @param <T> the type of the bounds, which must implement {@link Comparable}
 * 
 * @author Luna
 * @version 1.0.0
 */
public class Range<T extends Comparable<T>> {
	
	/** Lower bound of the Range */
	private T start;
	/** Upper bound of the Range */
	private T end;
	
	/**
	 * Constructs a {@code Range} with the given bounds.
	 * If {@code start} > {@code end}, they are swapped to maintain validity.
	 *
	 * @param 	start	The lower bound
	 * @param 	end		The upper bound
	 * @throws	NullPointerException if either bound is {@code null}
	 * @author	Luna
	 * @version	1.0.0
	 */
	public Range(T start, T end) throws NullPointerException {
		if (start == null || end == null)
			throw new NullPointerException("Range bounds cannot be null");

		if(start.compareTo(end) > 0) {
			this.start = end;
			this.end = start;
		} else {
			this.start = start;
			this.end = end;
		}
	}

	/**
	 * Constructs a singleton {@code Range} where {@code start} and {@code end} are the same value.
	 *
	 * @param 	value	The single bound value
	 * @throws 	NullPointerException if {@code value} is {@code null}
	 * @author	Luna
	 * @version	1.0.0
	 */
	public Range(T value) throws NullPointerException {
		if (value == null)
			throw new NullPointerException("Range bounds cannot be null");
		this.start = value;
		this.end = value;
	}

	/**
	 * Factory method to create a {@code Range} with two bounds.
	 *
	 * @param <T>	type of the elements (must be comparable)
	 * @param start	lower bound
	 * @param end	upper bound
	 * @return a new {@code Range} instance
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static <T extends Comparable<T>> Range<T> of(T start, T end) {
		return new Range<T>(start,end);
	}

	/**
	 * Factory method to create a singleton {@code Range} (start == end).
	 *
	 * @param <T>   type of the element
	 * @param value the single bound value
	 * @return a new {@code Range} instance with identical start and end
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static <T extends Comparable<T>> Range<T> singleton(T value) {
		return new Range<>(value);
	}

	/**	
	 * Retrieves the element of the lower bound {@code start}.
	 * @return	T	The element used for the lower bound {@code start} of the {@code Range} instance
	 * @author	Luna
	 * @version	1.0.0
	 */
	public T getStart() {return this.start;}
	
	/**	
	 * Allows to modify the lower bound of the {@code Range} instance.
	 * If the given value is higher than the {@code end} of the {@code Range} instance,
	 * then the lower bound {@code start} of the {@code Range} instance will inherit the upper bound {@code end} value
	 * and the given value will be used for the upper bound {@code end} of the {@code Range}.
	 * @param 	value	The value to be used to modify the bounds of the {@code Range} (lower)
	 * @author	Luna
	 * @version	1.0.0
	 */
	public void setStart(T value) {
		if (value.compareTo(this.end) > 0) {
			this.start = this.end;
			this.end = value;
		} else this.start = value;
	}
	
	/**	
	 * Retrieves the element of the upper bound {@code end}.
	 * @return	T	The element used for the upper bound {@code end} of the {@code Range} instance
	 * @author	Luna
	 * @version	1.0.0
	 */
	public T getEnd() {return this.end;}
	
	/**	
	 * Allows to modify the upper bound of the {@code Range} instance.
	 * If the given value is lower than the {@code start} of the {@code Range} instance,
	 * then the upper bound {@code end} of the {@code Range} instance will inherit the lower bound {@code start} value
	 * and the given value will be used for the lower bound {@code start} of the {@code Range}.
	 * @param 	value	The value to be used to modify the bounds of the {@code Range} (upper)
	 * @author	Luna
	 * @version	1.0.0
	 */
	public void setEnd(T value) {
		if (value.compareTo(this.start) < 0) {
			this.end = this.start;
			this.start = value;
		} else this.end = value;
	}
	
	/**	
	 * This method checks whether or not the given element {@code value} is contained within the {@code Range} Object (this).
	 * @param 	value		The element <T> to check
	 * @return 	(boolean)	Returns {@code true} if the given element is within the {@code Range} Object bounds, {@code false} otherwise
	 * @author	Luna
	 * @version	1.0.0
	 */
	public boolean contains(T value) {
		return (value.compareTo(this.start) >= 0) && (value.compareTo(this.end) <= 0);
	}
	
	/**	
	 * This method checks whether or not every element of the given collection ('values') is contained within the Range Object (this).
	 * If null, returns false.
	 * @param values		A collection of <T> elements to check
	 * @return (boolean)	Returns true only if every element in the collection is in the given Range, false otherwise
	 * @author	Luna
	 * @version	1.0.0
	 */
	public boolean containsAll(Collection<T> values) {
		boolean result = true;
		if (values==null) return false;
		for (T value : values) {
			if (!this.contains(value)) {result = false; break;}
		}
		return result;
	}

	/**
	 * Checks if this {@code Range} represents a single point (start == end).
	 *
	 * @return {@code true} if the range is empty, {@code false} otherwise
	 * @author	Luna
	 * @version	1.0.0
	 */
	public boolean isEmpty() {
		return this.start.equals(this.end);
	}

	/**
	 * Returns the size (distance) between {@code start} and {@code end} if both are {@link Number}.
	 * Uses double precision and floors the result.
	 *
	 * @return the numeric distance, or 0 if not numeric
	 * @author	Luna
	 * @version	1.0.0
	 */
	public int size() {
		if (this.start instanceof Number && this.end instanceof Number) {
			return (int) Math.floor(((Number) this.end).doubleValue() - ((Number) this.start).doubleValue());
		}
		return 0;
	}

	/**
	 * Checks if this {@code Range} and another {@code Range} have bounds of the same runtime type.
	 *
	 * @param that another {@code Range}
	 * @param <R>  type of the other range's elements
	 * @return {@code true} if both ranges are of the same element class
	 * @author	Luna
	 * @version	1.0.0
	 */
	public <R extends Comparable<R>> boolean isInstanceOf(Range<R> that) {
		return that.instance().equals(this.instance());
	}

	/**
	 * @return the runtime {@link Class} of the start element
	 * @author	Luna
	 * @version	1.0.0
	 */
	public Class<?> instance() {
		return this.start.getClass();
	}

	/**
	 * Checks if this {@code Range} overlaps another.
	 *
	 * @param that another {@code Range}
	 * @return {@code true} if there is any overlap, including enclosure
	 * @author	Luna
	 * @version	1.0.0
	 */
	public boolean overlaps(Range<T> that) {
		return (this.start.compareTo(that.end) <= 0) && (that.start.compareTo(this.end) <= 0);
	}

	/**
	 * Checks if this {@code Range} is entirely before a given value.
	 *
	 * @param value the value to compare
	 * @return {@code true} if this.end < value
	 * @author	Luna
	 * @version	1.0.0
	 */
	public boolean isBefore(T value) {
		return value.compareTo(this.end) > 0;
	}

	/**
	 * Checks if this {@code Range} is entirely after a given value.
	 *
	 * @param value the value to compare
	 * @return {@code true} if this.start > value
	 * @author	Luna
	 * @version	1.0.0
	 */
	public boolean isAfter(T value) {
		return value.compareTo(this.start) < 0;
	}

	/**
	 * Checks if this {@code Range} completely contains another {@code Range}.
	 *
	 * @param that another {@code Range}
	 * @return {@code true} if this.start <= that.start and this.end >= that.end
	 * @author	Luna
	 * @version	1.0.0
	 */
	public boolean encloses(Range<T> that) {
		return this.contains(that.start) && this.contains(that.end);
	}

	/**
	 * Computes the intersection of this {@code Range} with another.
	 *
	 * @param that another {@code Range}
	 * @return a new {@code Range} representing the overlap
	 * @throws OutOfBoundsException if the ranges do not overlap
	 * @author	Luna
	 * @version	1.0.0
	 */
	public Range<T> intersection(Range<T> that) throws OutOfBoundsException {
		if (!this.overlaps(that)) throw new OutOfBoundsException("No intersection found between these two Range");
		T start = this.contains(that.start) ? that.start : this.start;
		T end = this.contains(that.end) ? that.end : this.end;
		return new Range<>(start,end);
	}

	/**
	 * Computes the span (union bounds) of this {@code Range} and another.
	 *
	 * @param that another {@code Range}
	 * @return a new {@code Range} covering both ranges
	 * @author	Luna
	 * @version	1.0.0
	 */
	public Range<T> span(Range<T> that) {
		return new Range<T>(
			(this.start.compareTo(that.start) <= 0 ? this.start : that.start),
			(this.end.compareTo(that.end) <= 0 ? this.end : that.end)
		);
	}

	/**
	 * Transforms this {@code Range} into another {@code Range} of type {@code R},
	 * by applying a mapping function to both bounds.
	 * If the mapped values are reversed, they are swapped to maintain order.
	 *
	 * @param mapper mapping function from T to R
	 * @param <R>    target type (must be comparable)
	 * @return a new {@code Range<R>} with transformed bounds
	 * @author	Luna
	 * @version	1.0.0
	 */
	public <R extends Comparable<R>> Range<R> map(Function<? super T, ? extends R> mapper) {
        return new Range<>(mapper.apply(start), mapper.apply(end));
    }
	/**
	 * Checks equality between two {@code Range} instances of possibly different types.
	 * Ranges are equal if they share the same element type and identical bounds.
	 *
	 * @param that another {@code Range}
	 * @param <R>  type of the other range
	 * @return {@code true} if both ranges are equal
	 * @author	Luna
	 * @version	1.0.0
	 */
	@SuppressWarnings("unlikely-arg-type")
	public <R extends Comparable<R>> boolean equals(Range<R> that) {
		return isInstanceOf(that) && this.start.equals(that.start) && this.end.equals(that.end);
	}

	/**
	 * @return a string representation of the {@code Range} in format {@code [start,end]}
	 * @author	Luna
	 * @version	1.0.0
	 */
	@Override
	public String toString() {
		return "[%s,%s]".formatted(this.start.toString(),this.end.toString());
	}
	
}
