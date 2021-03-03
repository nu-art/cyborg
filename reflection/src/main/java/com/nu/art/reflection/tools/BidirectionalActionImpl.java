/*
 * The reflection project, is collection of reflection tools I've picked up
 * along the way, use it wisely!
 *
 * Copyright (C) 2018  Adam van der Kruk aka TacB0sS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nu.art.reflection.tools;

import java.io.Serializable;
import java.util.Vector;

public class BidirectionalActionImpl<Type>
	extends BidirectionalAction<Type>
	implements Serializable {

	public enum BidirectionalState {
		Before,
		After;

		private BidirectionalState() { }
	}

	private static final long serialVersionUID = 957952667991035128L;

	private Vector<Type> items = new Vector<>();

	private BidirectionalState state = BidirectionalState.Before;

	private boolean reverseOnFailure = true;

	private String actionDescription;

	public BidirectionalActionImpl(Class<Type> type,
	                               String actionDescription,
	                               String redoMethodName,
	                               Object[] redoParameters,
	                               String undoMethodName,
	                               Object[] undoParameters)
		throws Exception {
		super(type, redoMethodName, redoParameters, undoMethodName, undoParameters);
		this.actionDescription = actionDescription;
	}

	public void setReverseOnFailure(boolean reverseOnFailure) {
		this.reverseOnFailure = reverseOnFailure;
	}

	public BidirectionalActionImpl(Class<Type> type, String actionDescription, String forwardMethod, String backwardMethod)
		throws Exception {
		super(type, forwardMethod, backwardMethod);
		this.actionDescription = actionDescription;
	}

	public void addItem(Type item)
		throws Exception {
		if (item == null) {
			return;
		}
		items.add(item);
		if (state == BidirectionalState.After) {
			try {
				forwardInvocation.invokeMethod(item);
			} catch (Exception e) {
				backwardInvocation.invokeMethod(item);
			}
		}
	}

	private void backward(int from, int until, Object[] parameters)
		throws Exception {
		for (int i = from; i >= until; i--) {
			backwardInvocation.invokeMethod(items.get(i), parameters);
		}
	}

	public final void backward(Object... parameters)
		throws Exception {
		if (state == BidirectionalState.Before) {
			return;
		}
		backward(items.size() - 1, 0, parameters);
		state = BidirectionalState.Before;
	}

	public final void forward(Object... parameters)
		throws Exception {
		if (state == BidirectionalState.After) {
			return;
		}
		int i = 0;
		try {
			for (; i < items.size(); i++) {
				forwardInvocation.invokeMethod(items.get(i), parameters);
			}
			state = BidirectionalState.After;
		} catch (Exception e) {
			if (reverseOnFailure) {
				backward(i, 0);
			}
			throw e;
		}
	}

	public BidirectionalState getState() {
		return state;
	}

	public void removeItem(Type installable)
		throws Exception {
		if (state == BidirectionalState.After) {
			int index = items.indexOf(installable);
			try {
				backward(items.size() - 1, index);
			} catch (Exception e) {
				items.remove(installable);
				throw e;
			}
		}
		items.remove(installable);
	}
}
