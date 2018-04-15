package me.codetalk.param.type;

/**
 * 浮点型参数校验
 * 
 * @author Administrator
 *
 */
public class DoubleParam extends AbstractParam {

	private Double minVal;

	public DoubleParam(String name, boolean required) {
		this(name, required, null);
	}

	public DoubleParam(String name, boolean required, Double minVal) {
		super(name, required);

		this.minVal = minVal;
	}

	public boolean isValid(Object obj) {
		if (required && obj == null)
			return false;

		if (!required && obj == null)
			return true;

		if (!(obj instanceof Double) && !(obj instanceof Float) && !(obj instanceof Long) && !(obj instanceof Integer))
			return false;

		Double tmp = Double.parseDouble(obj.toString());
		if (minVal != null && tmp < minVal)
			return false;

		return true;
	}

}
