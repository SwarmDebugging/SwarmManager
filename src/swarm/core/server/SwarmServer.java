package swarm.core.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SwarmServer {

	public static final String TASKS = "tasks";
	public static final String SESSIONS = "sessions";
	public static final String DEVELOPERS = "developers";
	//public static final String PROJECTS = "projects";
	public static final String NAMESPACES = "namespaces";
	public static final String TYPES = "types";
	public static final String BREAKPOINTS = "breakpoints";
	public static final String EVENTS = "events";
	public static final String METHODS = "methods";
	public static final String INVOCATIONS = "invocations";

	private static final String POST = "POST";
	private static final String PUT = "PUT";

	//private static String DEFAULT_URL = "http://server.swarmdebugging.org";
	private static String DEFAULT_URL = "http://localhost:8080";
	private static SwarmServer server;

	private String serverUrl;
	
	private static final Logger tracer = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public static SwarmServer getInstance() {
		if (server == null) {
			String url = System.getenv("SWARM_SERVER_URL");
			if(url == null) {
				url = DEFAULT_URL;
			}
			
			try {
				tracer.addHandler(new FileHandler("SwarmDebuggingTracer.log", true));
				tracer.setLevel(Level.ALL);

			} catch (Exception e) {
				e.printStackTrace();
			}
				
			server = getInstance(url);
		}
		return server;
	}

	public static SwarmServer getInstance(String url) {
		if (server == null || !server.getServerUrl().equals(url)) {
			if (!url.endsWith("/")) {
				url += "/";
			}

			server = new SwarmServer(url);
		}

		return server;
	}

	private SwarmServer(String url) {
		this.serverUrl = url;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public String get(String message, boolean hasHost) throws Exception {
		if(!hasHost) {
			message = serverUrl + message; 
		}
		URL url = new URL(message);
		System.out.println(url);
		return response(url);
	}
	
	public String get(String message) throws Exception {
		return get(message,false);
	}

	public String create(String message, String request) throws Exception {
		URL url = new URL(serverUrl + message);
		
		tracer.log(Level.ALL, request);
		
		return request(url, request, POST);
	}

	public String update(String message, String request) throws Exception {
		URL url = new URL(serverUrl + message);
		return request(url, request, PUT);
	}

	private String response(URL url) throws Exception {
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		StringBuffer response = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {
			response.append(line);
		}

		reader.close();
		
		return response.toString();
	}

	private String request(URL url, String request, String method) throws Exception {
		HttpURLConnection urlConnection;
		urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setDoOutput(true);
		urlConnection.setRequestMethod(method);
		urlConnection.setUseCaches(false);
		urlConnection.setConnectTimeout(10000);
		urlConnection.setReadTimeout(10000);
		urlConnection.setRequestProperty("Content-Type", "application/json");
		urlConnection.connect();

		OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
		out.write(request);
		out.close();

		StringBuilder response = new StringBuilder();
		int result = urlConnection.getResponseCode();


		try {
			if (result == HttpURLConnection.HTTP_CREATED || result == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					response.append(line);
				}
				br.close();

				return response.toString();
			} else {
				return "";
			}
		} finally {
			urlConnection.disconnect();
		}
	}

	public boolean isOk() {
		try {
			String status = get("health");
			return status.contains("UP");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}