using System.Globalization;
using UnityEngine;

public sealed class CubePing : MonoBehaviour
{
	static readonly Color[] Palette =
	{
		new Color(0.91f, 0.30f, 0.24f),
		new Color(0.18f, 0.80f, 0.44f),
		new Color(0.20f, 0.60f, 0.86f),
		new Color(0.95f, 0.77f, 0.06f),
		new Color(0.61f, 0.35f, 0.71f),
		new Color(0.90f, 0.49f, 0.13f)
	};

	Renderer cubeRenderer;
	int colorIndex;

	void Start()
	{
		cubeRenderer = GetComponent<Renderer>();
		ApplyColor(false);
	}

	void Update()
	{
		transform.Rotate(0f, 30f * Time.deltaTime, 0f);
	}

	public void PingFromAndroid(string payload)
	{
		CycleColor(true);
		Debug.Log("Cyborg ping: " + payload);
	}

	public void TapFromAndroid(string payload)
	{
		int comma = payload.IndexOf(',');
		if (comma <= 0)
			return;

		if (!float.TryParse(payload.Substring(0, comma), NumberStyles.Float, CultureInfo.InvariantCulture, out float x))
			return;
		if (!float.TryParse(payload.Substring(comma + 1), NumberStyles.Float, CultureInfo.InvariantCulture, out float y))
			return;

		Camera camera = Camera.main;
		if (camera == null)
			return;

		Vector3 screenPos = new Vector3(x, Screen.height - y, 0f);
		Ray ray = camera.ScreenPointToRay(screenPos);
		if (!Physics.Raycast(ray, out RaycastHit hit) || hit.transform != transform)
			return;

		CycleColor(true);
	}

	void CycleColor(bool reportToAndroid)
	{
		colorIndex = (colorIndex + 1) % Palette.Length;
		ApplyColor(reportToAndroid);
	}

	void ApplyColor(bool reportToAndroid)
	{
		Color color = Palette[colorIndex];
		if (cubeRenderer != null)
		{
			cubeRenderer.material.color = color;
			if (cubeRenderer.material.HasProperty("_BaseColor"))
				cubeRenderer.material.SetColor("_BaseColor", color);
		}

		if (reportToAndroid)
			SendToCyborg("#" + ColorUtility.ToHtmlStringRGB(color));
	}

	static void SendToCyborg(string payload)
	{
		if (Application.platform != RuntimePlatform.Android)
			return;

		using (var bridge = new AndroidJavaClass("com.nu.art.cyborg.unity.UnityBridge"))
			bridge.CallStatic("onUnityPing", payload);
	}
}
