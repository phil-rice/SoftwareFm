package org.arc4eclipse.utilities.pooling;

public class ExampleForPool implements IExampleForPool {

	int value;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	
	public String toString() {
		return "ExampleForPool [value=" + value + "]";
	}
}
