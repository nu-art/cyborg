package com.nu.art.software.cyborg.demo.model;

import com.nu.art.software.cyborg.annotations.ModuleDescriptor;
import com.nu.art.software.cyborg.core.CyborgModule;

import java.util.ArrayList;

/**
 * Created by tacb0ss on 4/16/15.
 */
@ModuleDescriptor
public class MyModule
		extends CyborgModule {


	private ArrayList<String> listOfStrings = new ArrayList<String>();

	@Override
	protected void init() {
		// if your module require initialization it can be performed here, but be ware the modules starts synchronously thus if it is a heavy action, consider doing it on another thread.
		listOfStrings.add("1");
		listOfStrings.add("2");
		listOfStrings.add("3");
	}

	public String getString(int index) {
		return listOfStrings.get(index);
	}

	public void addString(String string) {
		listOfStrings.add(string);
	}

	public int getCount() {
		return listOfStrings.size();
	}


}
