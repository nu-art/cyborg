using System;
using System.IO;
using UnityEditor;
using UnityEditor.Build;
using UnityEditor.Build.Reporting;
using UnityEditor.SceneManagement;
using UnityEngine;
using UnityEngine.SceneManagement;

public static class CyborgPocBuilder
{
	const string ScenePath = "Assets/Scenes/CyborgPoc.unity";

	public static void ExportAndroidLibrary()
	{
		EnsureScene();
		ConfigureAndroid();

		string output = Environment.GetEnvironmentVariable("CYBORG_UNITY_EXPORT");
		if (string.IsNullOrEmpty(output))
			output = Path.GetFullPath("Builds/Android");
		Directory.CreateDirectory(output);

		var options = new BuildPlayerOptions
		{
			scenes = new[] {ScenePath},
			locationPathName = output,
			target = BuildTarget.Android,
			options = BuildOptions.Development
		};

		BuildReport report = BuildPipeline.BuildPlayer(options);
		if (report.summary.result != BuildResult.Succeeded)
			throw new Exception("Unity Android export failed: " + report.summary.result);
	}

	static void ConfigureAndroid()
	{
		NamedBuildTarget android = NamedBuildTarget.Android;
		PlayerSettings.SetScriptingBackend(android, ScriptingImplementation.IL2CPP);
		PlayerSettings.Android.targetArchitectures = AndroidArchitecture.ARM64;
		PlayerSettings.SetApplicationIdentifier(android, "com.nu.art.cyborg.unity");
		PlayerSettings.productName = "CyborgUnityPoc";
		EditorUserBuildSettings.exportAsGoogleAndroidProject = true;
		EditorUserBuildSettings.androidBuildSubtarget = MobileTextureSubtarget.ASTC;
		EditorUserBuildSettings.development = true;
		EditorUserBuildSettings.SwitchActiveBuildTarget(BuildTargetGroup.Android, BuildTarget.Android);
	}

	static void EnsureScene()
	{
		Scene scene = EditorSceneManager.NewScene(NewSceneSetup.DefaultGameObjects, NewSceneMode.Single);
		GameObject cube = GameObject.CreatePrimitive(PrimitiveType.Cube);
		cube.name = "Cube";
		cube.AddComponent<CubePing>();
		cube.transform.position = Vector3.zero;

		Camera camera = Camera.main;
		if (camera != null)
		{
			camera.transform.position = new Vector3(0f, 1.2f, -4f);
			camera.transform.LookAt(cube.transform);
		}

		Directory.CreateDirectory("Assets/Scenes");
		EditorSceneManager.SaveScene(scene, ScenePath);
		EditorBuildSettings.scenes = new[] {new EditorBuildSettingsScene(ScenePath, true)};
	}
}
