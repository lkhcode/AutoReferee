package com.github.g3force.instanceables;

import com.github.g3force.instanceables.InstanceableClass.NotCreateableException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;


/**
 * An {@link IInstanceable} that builds an immutable value object through a mutable builder:
 * a builder is created, each declared setter applies one parsed string value to it, and
 * {@link IInstanceableBuilder#build()} produces the instance. Used to construct skill inputs
 * from the generic instanceable GUI without reflecting a constructor.
 *
 * @param <B> the builder type
 */
public class InstanceableBuilderClass<B extends IInstanceableBuilder<?>> implements IInstanceable
{
	private final Class<?> impl;
	private final Supplier<B> builderFactory;
	private final List<InstanceableSetter<?, B>> setters = new ArrayList<>();


	public InstanceableBuilderClass(
			final Class<?> impl,
			final Supplier<B> builderFactory
	)
	{
		this.impl = impl;
		this.builderFactory = builderFactory;
	}


	public static <B extends IInstanceableBuilder<?>> InstanceableBuilderClass<B> ib(
			final Class<?> impl,
			final Supplier<B> builderFactory
	)
	{
		return new InstanceableBuilderClass<>(impl, builderFactory);
	}


	public <R> InstanceableBuilderClass<B> setterParam(
			final Class<R> impl,
			final String description,
			final String defaultValue,
			final BiConsumer<B, R> setter,
			final Class<?>... genericsImpls
	)
	{
		setters.add(new InstanceableSetter<>(impl, description, defaultValue, setter, genericsImpls));
		return this;
	}


	@Override
	public List<IInstanceableParameter> getAllParams()
	{
		return new ArrayList<>(setters);
	}


	@Override
	public Class<?> getImpl()
	{
		return impl;
	}


	@Override
	public Object newInstance(final List<String> values)
	{
		if (values.size() != setters.size())
		{
			throw new NotCreateableException("Wrong number of parameters: " + values);
		}
		B builder = builderFactory.get();
		int i = 0;
		for (InstanceableSetter<?, B> setter : setters)
		{
			setter.apply(builder, values.get(i++));
		}
		return builder.build();
	}


	@Override
	public Object newDefaultInstance()
	{
		B builder = builderFactory.get();
		for (InstanceableSetter<?, B> setter : setters)
		{
			setter.apply(builder, setter.getDefaultValue());
		}
		return builder.build();
	}
}
