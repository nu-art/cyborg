

package com.nu.art.cyborg.demo.model;

import com.nu.art.core.generics.Processor;
import com.nu.art.cyborg.annotations.ModuleDescriptor;
import com.nu.art.cyborg.core.CyborgModule;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_MediaTester.MediaStream;
import com.nu.art.cyborg.demo.ui.controllers.customAttributeExample.AttributeSetterV1_CustomValue;
import com.nu.art.cyborg.media.CyborgMediaPlayer;
import com.nu.art.cyborg.modules.AttributeModule;
import com.nu.art.modules.STT_Client.STT_Listener;
import com.nu.art.storage.PreferencesModule;

import java.util.ArrayList;
import java.util.List;

@ModuleDescriptor
public class MyModule
	extends CyborgModule
	implements STT_Listener {

	private ArrayList<String> listOfStrings = new ArrayList<>();
	private ArrayList<MediaStream> streams = new ArrayList<>();
	private PreferencesModule module;

	@Override
	protected void init() {
		// if your module require initialization it can be performed here, but be ware the modules starts synchronously thus if it is a heavy action, consider doing it on another thread.
		listOfStrings.add("1");
		listOfStrings.add("2");
		listOfStrings.add("3");
		getModule(AttributeModule.class).registerAttributesSetter(AttributeSetterV1_CustomValue.class);
		streams.add(new MediaStream("Good Bad Ugly", "https://www.myinstants.com/media/sounds/goodbadugly-whistle-long.mp3"));
		streams.add(new MediaStream("Ghost Busters", "https://www.myinstants.com/media/sounds/ghostbusters_2.mp3"));
		streams.add(new MediaStream("Can't Touch This", "https://www.myinstants.com/media/sounds/mc-hammer-u-cant-touch-this.mp3"));
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

	public List<MediaStream> getMediaStreams() {
		return streams;
	}

	public CyborgMediaPlayer createPlayer() {
		return createModuleItem(CyborgMediaPlayer.class);
	}

	@Override
	public void onPrepared() {
		dispatchEvent("delegate to UI", STT_Listener.class, new Processor<STT_Listener>() {
			@Override
			public void process(STT_Listener stt_listener) {
				stt_listener.onPrepared();
			}
		});
	}

	@Override
	public void onStopped() {
		dispatchEvent("delegate to UI", STT_Listener.class, new Processor<STT_Listener>() {
			@Override
			public void process(STT_Listener stt_listener) {
				stt_listener.onStopped();
			}
		});
	}

	@Override
	public void onRecognized(final String message) {
		dispatchEvent("delegate to UI", STT_Listener.class, new Processor<STT_Listener>() {
			@Override
			public void process(STT_Listener stt_listener) {
				stt_listener.onRecognized(message);
			}
		});
	}

	@Override
	public void onPartialResults(final String partialResults) {
		dispatchEvent("delegate to UI", STT_Listener.class, new Processor<STT_Listener>() {
			@Override
			public void process(STT_Listener stt_listener) {
				stt_listener.onPartialResults(partialResults);
			}
		});
	}

	@Override
	public void onCancelled() {
		dispatchEvent("delegate to UI", STT_Listener.class, new Processor<STT_Listener>() {
			@Override
			public void process(STT_Listener stt_listener) {
				stt_listener.onCancelled();
			}
		});
	}
}
