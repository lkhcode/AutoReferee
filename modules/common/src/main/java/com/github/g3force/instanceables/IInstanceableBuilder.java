package com.github.g3force.instanceables;

/**
 * A mutable builder that finalizes into an immutable instance of type {@code T}.
 * Used by {@link InstanceableBuilderClass} to produce an instance after all declared
 * setters have been applied.
 *
 * @param <T> the produced (finalized) type
 */
public interface IInstanceableBuilder<T>
{
	/**
	 * @return the finalized, immutable instance
	 */
	T build();
}
