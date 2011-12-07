package org.softwareFm.collections.explorer;

import java.util.Map;

public interface IAddCardCallback{

	Runnable ok();

	Runnable cancel();

	boolean canOk(Map<String,Object> data);
}
