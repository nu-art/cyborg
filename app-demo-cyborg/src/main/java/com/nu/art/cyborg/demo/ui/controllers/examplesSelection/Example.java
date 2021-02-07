

package com.nu.art.cyborg.demo.ui.controllers.examplesSelection;

import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_ABTesting;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_BlurredImage;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_CameraView;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_Contacts;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_DialogContent;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_Fingerprint;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_GoogleMaps;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_HttpTransactions;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_InternetConnection;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_WifiExample;
import com.nu.art.cyborg.demo.ui.controllers.Controller_FileLogger;
import com.nu.art.cyborg.demo.ui.controllers.Controller_Material;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_MediaTester;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_NativeCalls;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_Recycler;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_RoundedImage;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_Storage;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_VideoView;
import com.nu.art.cyborg.demo.ui.controllers.Controller_AudioRecorder;
import com.nu.art.cyborg.demo.ui.controllers.Controller_EventDispatching;
import com.nu.art.cyborg.demo.ui.controllers.Controller_STT;
import com.nu.art.cyborg.demo.ui.controllers.Controller_ScreenOrientation;
import com.nu.art.cyborg.demo.ui.controllers.Controller_StackTest;
import com.nu.art.cyborg.demo.ui.controllers.Controller_SystemVolume;
import com.nu.art.cyborg.demo.ui.controllers.Controller_ViewPager;
import com.nu.art.cyborg.demo.ui.controllers.customAttributeExample.ControllerV1_CustomAttribute;
import com.nu.art.cyborg.demo.ui.controllers.dynamicStackExample.Controller_DynamicStack;
import com.nu.art.cyborg.demo.ui.controllers.injection.ControllerV1_Injection;
import com.nu.art.cyborg.demo.ui.controllers.liveModule.Controller_LiveModule;
import com.nu.art.cyborg.demo.ui.controllers.servicesTypeHandling.ControllerV1_ServicesHandling;
import com.nu.art.cyborg.demo.ui.controllers.systemOverlay.Controller_SystemOverlay;
import com.nu.art.cyborg.demo.ui.controllers.transitionAnimation.ControllerV1_TransitionAnimationBegin;
import com.nu.art.cyborg.logcat.ui.Controller_LogcatViewer;

/**
 * A list of examples and their layouts provided in this demo project.
 */
public enum Example {
	WifiExample(R.string.ExampleLabel_WifiExample, ControllerV1_WifiExample.class),
	ScreenOrientation(R.string.ExampleLabel_ScreenOrientation, Controller_ScreenOrientation.class),
	SystemOverlay(R.string.ExampleLabel_SystemOverlay, Controller_SystemOverlay.class),
	SystemVolume(R.string.ExampleLabel_SystemVolume, Controller_SystemVolume.class),
	FileLogger(R.string.ExampleLabel_FileLogger, Controller_FileLogger.class),
	LiveModule(R.string.ExampleLabel_LiveModule, Controller_LiveModule.class),
	Material(R.string.ExampleLabel_Material, Controller_Material.class),
	ViewPager(R.string.ExampleLabel_ViewPager, Controller_ViewPager.class),
	InternetConnectivity(R.string.ExampleLabel_InternetConnectivity, ControllerV1_InternetConnection.class),
	VideoView(R.string.ExampleLabel_VideoView, ControllerV1_VideoView.class),
	CameraLayer(R.string.ExampleLabel_CameraLayer, ControllerV1_CameraView.class),
	Contacts(R.string.ExampleLabel_Contacts, ControllerV1_Contacts.class),
	NativeCalls(R.string.ExampleLabel_NativeCalls, ControllerV1_NativeCalls.class),
	Fingerprint(R.string.ExampleLabel_Fingerprint, ControllerV1_Fingerprint.class),
	HttpTransactions(R.string.ExampleLabel_HttpTransactions, ControllerV1_HttpTransactions.class),
	BlurredImage(R.string.ExampleLabel_BlurredImage, ControllerV1_BlurredImage.class),
	RoundedImage(R.string.ExampleLabel_RoundedImage, ControllerV1_RoundedImage.class),
	LogcatViewer(R.string.ExampleLabel_LogcatViewer, Controller_LogcatViewer.class),
	STT(R.string.ExampleLabel_STT, Controller_STT.class),
	AudioRecorder(R.string.ExampleLabel_AudioRecorder, Controller_AudioRecorder.class),
	StackTest(R.string.ExampleLabel_StackTest, Controller_StackTest.class),
	MediaPlayer(R.string.ExampleLabel_MediaPLayer, ControllerV1_MediaTester.class),
	Maps(R.string.ExampleLabel_Maps, ControllerV1_GoogleMaps.class),
	Injection(R.string.ExampleLabel_Injection, ControllerV1_Injection.class),
	Storage(R.string.ExampleLabel_Storage, ControllerV1_Storage.class),
	Recycler(R.string.ExampleLabel_Recycler, ControllerV1_Recycler.class),
	ABTesting(R.string.ExampleLabel_ABTesting, ControllerV1_ABTesting.class),
	Dialogs(R.string.ExampleLabel_Dialog, ControllerV1_DialogContent.class),
	EventDispatching(R.string.ExampleLabel_EventDispatching, Controller_EventDispatching.class),
	Stack(R.string.ExampleLabel_Stack, Controller_DynamicStack.class),
	Animations(R.string.ExampleLabel_Animations, ControllerV1_TransitionAnimationBegin.class),
	CustomAttribute(R.string.ExampleLabel_CustomAttributes, ControllerV1_CustomAttribute.class),
	ServicesHandling(R.string.ExampleLabel_ServicesHandling, ControllerV1_ServicesHandling.class),
	//	Payment(R.string.ExampleLabel_Payment, R.layout.v1_activity__payment_example),
	//
	;

	private final int labelId;

	private final Class<? extends CyborgController> controllerType;

	Example(int labelId, Class<? extends CyborgController> controllerType) {
		this.labelId = labelId;
		this.controllerType = controllerType;
	}

	public int getLabelId() {
		return labelId;
	}

	public Class<? extends CyborgController> getControllerType() {
		return controllerType;
	}
}
