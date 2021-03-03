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

import java.util.List;
import java.util.Vector;

@SuppressWarnings("unchecked")
public abstract class TypeHierarchy<ItemType> {

	/**
	 * Just a dummy empty vector to return as an empty parent.
	 */
	protected final Vector<ItemType> emptyVector = new Vector<>();

	/**
	 * @param container The questioned container.
	 *
	 * @return A collection of the items of the supplied container.
	 */
	public abstract Vector<ItemType> getChildren(ItemType container);

	/**
	 * @param item The item to retrieve the container for.
	 *
	 * @return The container of the specified item.
	 */
	public abstract ItemType getParent(ItemType item);

	/**
	 * Sets the supplied parent as the supplied item's parent.
	 *
	 * @param item   The item to be set with the supplied parent.
	 * @param parent The parent to set to the item.
	 */
	protected abstract void setParent(ItemType item, ItemType parent);

	/**
	 * @param parent The container in question.
	 *
	 * @return Weather the supplied item is a container type.
	 */
	public abstract boolean isParent(ItemType parent);

	/**
	 * @param container The container to add the items to.
	 * @param items     A collection of items to add to the supplied container.
	 */
	public final void addChildren(ItemType container, ItemType... items) {
		insertChildren(container, getChildCount(container), items);
	}

	/**
	 * @param container The container to add the items to.
	 * @param items     A collection of items to add to the supplied container.
	 */
	public final void addChildren(ItemType container, List<ItemType> items) {
		insertChildren(container, getChildCount(container), items);
	}

	/**
	 * @param parent The container to add the items to.
	 * @param item   An item to insert into the supplied container, at the specified index.
	 * @param index  The insertion index.
	 */
	public final void insertChild(ItemType parent, int index, ItemType item) {
		Vector<ItemType> childrenVector = getChildren(parent);
		childrenVector.add(index, item);
		setParent(item, parent);
	}

	/**
	 * @param parent The container to add the items to.
	 * @param items  A collection of items to insert into the supplied container, at the specified index.
	 * @param index  The insertion index.
	 */
	public final void insertChildren(ItemType parent, int index, ItemType... items) {
		for (ItemType item : items) {
			insertChild(parent, index, item);
		}
	}

	/**
	 * @param parent     The container to add the items to.
	 * @param index      The insertion index.
	 * @param itemsToAdd A collection of items to insert into the supplied container, at the specified index.
	 */
	public final void insertChildren(ItemType parent, int index, List<ItemType> itemsToAdd) {
		for (ItemType itemToAdd : itemsToAdd) {
			insertChild(parent, index, itemToAdd);
		}
	}

	/**
	 * @param parent The container to remove the items from.
	 * @param items  A collection of items to remove from the supplied container.
	 */
	public final void removeChildren(ItemType parent, ItemType... items) {
		for (ItemType item : items) {
			removeChild(parent, item);
		}
	}

	/**
	 * @param parent The container to remove the item from.
	 * @param item   The item to be removed.
	 */
	public final void removeChild(ItemType parent, ItemType item) {
		Vector<ItemType> childrenVector = getChildren(parent);
		childrenVector.remove(item);
		setParent(item, null);
	}

	/**
	 * @param parent The container to remove the item from.
	 * @param index  The index to remove an element from.
	 */
	public final void removeChild(ItemType parent, int index) {
		Vector<ItemType> childrenVector = getChildren(parent);
		ItemType item = childrenVector.remove(index);
		setParent(item, null);
	}

	/**
	 * @param parent The container to remove the items from.
	 * @param items  A collection of items to remove from the supplied container.
	 */
	public final void removeChildren(ItemType parent, Vector<ItemType> items) {
		for (ItemType item : items) {
			removeChild(parent, item);
		}
	}

	/**
	 * @param container The container to question for its item count.
	 *
	 * @return The item count of the supplied container.
	 */
	public final int getChildCount(ItemType container) {
		return getChildren(container).size();
	}

	public int[] getChildrenIndices(ItemType... items) {
		int[] indices = new int[items.length];
		for (int i = 0; i < items.length; i++) {
			indices[i] = indexOfChild(items[i]);
		}
		return indices;
	}

	/**
	 * You may override this method to implement multi-container hierarchy.
	 *
	 * @param item The item to retrieve the containers for.
	 *
	 * @return The containers of the specified item.
	 */
	public Vector<ItemType> getParents(ItemType item) {
		Vector<ItemType> containers = new Vector<>();
		containers.add(getParent(item));
		return containers;
	}

	private Vector<ItemType> getSiblings(ItemType item) {
		ItemType parent = getParent(item);
		if (parent == null) {
			return emptyVector;
		}
		return getChildren(parent);
	}

	/**
	 * @param container The questioned container.
	 *
	 * @return Whether the supplied container has items or not.
	 */
	public boolean hasChildren(ItemType container) {
		return getChildCount(container) > 0;
	}

	/**
	 * @param item The item for which its index is requested.
	 *
	 * @return The index of the item in question in the specified container.
	 */
	public final int indexOfChild(ItemType item) {
		return getSiblings(item).indexOf(item);
	}
}
