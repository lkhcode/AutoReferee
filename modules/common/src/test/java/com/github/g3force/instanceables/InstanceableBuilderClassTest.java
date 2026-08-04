package com.github.g3force.instanceables;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.g3force.instanceables.InstanceableBuilderClass.ib;
import static org.assertj.core.api.Assertions.assertThat;


class InstanceableBuilderClassTest
{
	// --- fixture: a tiny immutable value + mutable builder ---
	record Point(int x, int y)
	{
	}

	static final class PointBuilder implements IInstanceableBuilder<Point>
	{
		private int x;
		private int y;


		PointBuilder setX(int x)
		{
			this.x = x;
			return this;
		}


		PointBuilder setY(int y)
		{
			this.y = y;
			return this;
		}


		@Override
		public Point build()
		{
			return new Point(x, y);
		}
	}


	private InstanceableBuilderClass<PointBuilder> pointInstanceable()
	{
		return ib(Point.class, PointBuilder::new)
				.setterParam(Integer.TYPE, "x", "1", PointBuilder::setX)
				.setterParam(Integer.TYPE, "y", "2", PointBuilder::setY);
	}


	@Test
	void newInstanceFromStringsBuildsValue()
	{
		Object result = pointInstanceable().newInstance(List.of("10", "20"));
		assertThat(result).isEqualTo(new Point(10, 20));
	}


	@Test
	void newDefaultInstanceUsesDeclaredDefaults()
	{
		Object result = pointInstanceable().newDefaultInstance();
		assertThat(result).isEqualTo(new Point(1, 2));
	}


	@Test
	void getImplReturnsFinalizedType()
	{
		assertThat(pointInstanceable().getImpl()).isEqualTo(Point.class);
	}


	@Test
	void getAllParamsExposesDeclaredParamsInOrder()
	{
		List<IInstanceableParameter> params = pointInstanceable().getAllParams();
		assertThat(params).extracting(IInstanceableParameter::getDescription).containsExactly("x", "y");
	}
}
