package com.github.g3force.instanceables;

import java.util.List;


/**
 * Common contract for objects that describe how to build an instance from GUI-provided
 * string parameters. Implemented by {@link InstanceableClass} (reflective constructor path)
 * and {@link InstanceableBuilderClass} (builder path).
 */
public interface IInstanceable
{
	/**
	 * @return the type produced by {@link #newInstance(List)} / {@link #newDefaultInstance()}
	 */
	Class<?> getImpl();

	/**
	 * @return the parameters to render in a GUI, in application order
	 */
	List<IInstanceableParameter> getAllParams();

	/**
	 * Build an instance from the given string values (one per {@link #getAllParams()} entry, in order).
	 *
	 * @param values one string per parameter
	 * @return the built instance
	 */
	Object newInstance(List<String> values);

	/**
	 * Build an instance using each parameter's declared default value.
	 *
	 * @return the built instance
	 */
	Object newDefaultInstance();
}
