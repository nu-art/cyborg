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

package com.nu.art.reflection.hierarchy;

import java.util.Hashtable;
import java.util.Vector;

public final class HierarchyModel<ItemType> {

	private final Hashtable<ItemType, HierarchyModelNode<ItemType>> hierarchyModels = new Hashtable<>();

	private final TypeHierarchy<ItemType> itemHierarchy;

	public HierarchyModel(TypeHierarchy<ItemType> itemHeirarchy) {
		this.itemHierarchy = itemHeirarchy;
	}

	public final HierarchyModelNode<ItemType> mapAncestors(ItemType item) {
		HierarchyModelNode<ItemType> hierarchyModel;
		if ((hierarchyModel = hierarchyModels.get(item)) != null) {
			return hierarchyModel;
		}
		hierarchyModel = new HierarchyModelNode<>(item);
		hierarchyModels.put(item, hierarchyModel);
		Vector<ItemType> parents = itemHierarchy.getParents(item);
		for (ItemType parent : parents) {
			HierarchyModelNode<ItemType> parentHierarchyModel;
			if ((parentHierarchyModel = hierarchyModels.get(parent)) == null) {
				parentHierarchyModel = mapAncestors(parent);
			}
			parentHierarchyModel.addChild(hierarchyModel);
			hierarchyModel.addParent(parentHierarchyModel);
		}
		return hierarchyModel;
	}

	public final HierarchyModelNode<ItemType> mapChildren(ItemType item) {
		HierarchyModelNode<ItemType> hierarchyModel;
		if ((hierarchyModel = hierarchyModels.get(item)) != null) {
			return hierarchyModel;
		}
		hierarchyModel = new HierarchyModelNode<>(item);
		hierarchyModels.put(item, hierarchyModel);
		Vector<ItemType> children = itemHierarchy.getChildren(item);
		for (ItemType child : children) {
			HierarchyModelNode<ItemType> childHierarchyModel;
			if ((childHierarchyModel = hierarchyModels.get(child)) == null) {
				childHierarchyModel = mapAncestors(child);
			}
			childHierarchyModel.addParent(hierarchyModel);
			hierarchyModel.addChild(childHierarchyModel);
		}
		return hierarchyModel;
	}

	public final HierarchyModelNode<ItemType> getModelFor(ItemType child) {
		return hierarchyModels.get(child);
	}

	public final void removeAll() {
		hierarchyModels.clear();
	}
}
