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

import java.util.Vector;

public final class HierarchyModelNode<ItemType> {

	private Vector<HierarchyModelNode<ItemType>> parentsNodes = new Vector<>();

	private Vector<HierarchyModelNode<ItemType>> childrenNodes = new Vector<>();

	private ItemType item;

	public HierarchyModelNode(ItemType item) {
		this.item = item;
	}

	public final void addChild(HierarchyModelNode<ItemType> child) {
		childrenNodes.add(child);
	}

	public final void addParent(HierarchyModelNode<ItemType> parent) {
		parentsNodes.add(parent);
	}

	public final Vector<HierarchyModelNode<ItemType>> getParentsNodes() {
		return parentsNodes;
	}

	public final Vector<HierarchyModelNode<ItemType>> getChildrenNodes() {
		return childrenNodes;
	}

	public final Vector<ItemType> getChildren() {
		Vector<ItemType> children = new Vector<>();
		for (HierarchyModelNode<ItemType> model : this.childrenNodes) {
			children.add(model.getItem());
		}
		return children;
	}

	public final Vector<ItemType> getParents() {
		Vector<ItemType> parents = new Vector<>();
		for (HierarchyModelNode<ItemType> model : this.parentsNodes) {
			parents.add(model.getItem());
		}
		return parents;
	}

	public final ItemType getItem() {
		return item;
	}
}
