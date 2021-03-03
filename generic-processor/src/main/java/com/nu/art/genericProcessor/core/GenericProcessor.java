/*
 * The generic-processor module, is an infrastructure extending
 * and simplifying the command pattern.
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

package com.nu.art.genericProcessor.core;

import com.nu.art.genericProcessor.interfaces.IBeanBinder;
import com.nu.art.modular.core.Module;

import java.util.HashMap;

public class GenericProcessor
	extends Module {

	private final HashMap<Class<? extends BeanProcessor<?>>, BeanProcessor<?>> cachedProcessors = new HashMap<>();

	@Override
	protected void init() {
	}

	public final <Type extends Bean> void executeBeanAction(IBeanBinder<Type> beanBinder, Type bean) {
		BeanProcessor<? super Type> handler = getProcessor(beanBinder.getHandlerType());
		handler.execute(bean);
	}

	private <Type extends Bean> void executeAction(IBeanBinder<Type> beanBinder, String actionAsString, Type bean) {
		BeanProcessor<? super Type> handler = getProcessor(beanBinder.getHandlerType());
		if (handler instanceof NestedProcessor) {
			((NestedProcessor) handler).executeAction(actionAsString);
			return;
		}

		handler.execute(bean);
	}

	@SuppressWarnings("unchecked")
	private <Type extends Bean> BeanProcessor<? super Type> getProcessor(Class<? extends BeanProcessor<? super Type>> handlerType) {
		BeanProcessor<? super Type> handler = (BeanProcessor<? super Type>) cachedProcessors.get(handlerType);
		if (handler == null)
			cachedProcessors.put(handlerType, handler = createModuleItem(handlerType));
		return handler;
	}
}
