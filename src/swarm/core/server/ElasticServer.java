package swarm.core.server;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.elasticsearch.common.Base64;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import swarm.core.domain.Breakpoint;
import swarm.core.domain.Developer;
import swarm.core.domain.Event;
import swarm.core.domain.Invocation;
import swarm.core.domain.Method;
import swarm.core.domain.Project;
import swarm.core.domain.Session;
import swarm.core.domain.Type;
import swarm.core.services.MethodService;

public class ElasticServer {

	private static enum SearchMode { BASIC, FUZZY, MATCH, WILDCARD };
	
	public static final String SESSIONS = "sessions";
	public static final String DEVELOPERS = "developers";
	public static final String PROJECTS = "projects";
	public static final String NAMESPACES = "namespaces";
	public static final String TYPES = "types";
	public static final String BREAKPOINTS = "breakpoints";
	public static final String EVENTS = "events";
	public static final String METHODS = "methods";
	public static final String INVOCATIONS = "invocations";

	private static final String POST = "POST";
	private static final String PUT = "PUT";
	private static final String DELETE = "DELETE";

	private static String DEFAULT_URL = "http://elastic.swarmdebugging.org";
	private static ElasticServer server;

	private String serverUrl;

	public static ElasticServer getInstance() {
		if (server == null) {
			String url = System.getenv("ELASTIC_SERVER_URL");
			if(url == null) {
				url = DEFAULT_URL;
			}
				
			server = getInstance(url);
		}
		return server;
	}

	public static ElasticServer getInstance(String url) {
		if (server == null || !server.getServerUrl().equals(url)) {
			if (!url.endsWith("/")) {
				url += "/";
			}

			server = new ElasticServer(url);
		}

		return server;
	}

	private ElasticServer(String url) {
		this.serverUrl = url;
	}

	public String getServerUrl() {
		return serverUrl;
	}
	
	public String search(String query, String content) throws Exception {
		URL url = new URL(serverUrl + query);
		return request(url, content, "GET");
	}	

	public String get(String message) throws Exception {
		URL url = new URL(serverUrl + message);
		return response(url);
	}

	public String create(String message, String request) throws Exception {
		URL url = new URL(serverUrl + message);
		return request(url, request, POST);
	}

	public String update(String message, String request) throws Exception {
		URL url = new URL(serverUrl + message);
		return request(url, request, PUT);
	}

	public String delete(String message, String request) throws Exception {
		URL url = new URL(serverUrl + message);
		return request(url, request, DELETE);
	}

	private String response(URL url) throws Exception {
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);

		// Bonsai user
		String userpass = "p8dasvnuxn:7tm40gzfnp";
		String basicAuth = "Basic " + new String(Base64.encodeBytes(userpass.getBytes()));
		connection.setRequestProperty("Authorization", basicAuth);

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
		
		// Bonsai user
		String userpass = "p8dasvnuxn:7tm40gzfnp";
		String basicAuth = "Basic " + new String(Base64.encodeBytes(userpass.getBytes()));
		urlConnection.setRequestProperty("Authorization", basicAuth);
		
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
				response.append(urlConnection.getResponseMessage());
				throw new Exception("Fail to submit " + request + " to " + url + ". Server Response -> " + result + " "
						+ response);
			}
		} finally {
			urlConnection.disconnect();
		}
	}

	public boolean isOk() {
		try {
			String status = get("_cat/health");
			return status.contains("green");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void createBreakpoint(Breakpoint breakpoint) throws Exception {
		String[] lines = breakpoint.getType().getSource().split(System.getProperty("line.separator"));

		String source = jsonBuilder()
				.startObject()
					.field("breakpointId", breakpoint.getId())
					.field("sessionId", breakpoint.getSession().getId())
					.field("description", breakpoint.getSession().getDescription())
					.field("developer", breakpoint.getSession().getDeveloper().getName())
					.field("project", breakpoint.getSession().getProject().getName())
					.field("purpose", breakpoint.getSession().getPurpose())
					.field("label", breakpoint.getSession().getLabel())
					.field("typeName", breakpoint.getType().getName())
					.field("typeFullName", breakpoint.getType().getFullName())
					.field("typeSource", breakpoint.getType().getSource())
					.field("lineNumber", breakpoint.getLineNumber())
					.field("charStart", breakpoint.getCharStart())
					.field("charEnd", breakpoint.getCharEnd())
					.field("breakpointCode", lines[breakpoint.getLineNumber() - 1].trim())
					.field("timestamp", new Date())
				.endObject().string();

		String response = getInstance().create("swarm/breakpoint", source);
		System.out.println(response);
	}

	public static void createEvent(Event event) throws Exception {
		String source = jsonBuilder()
				.startObject()
					.field("eventId", event.getId())
					.field("sessionId", event.getSession().getId())
					.field("description", event.getSession().getDescription())
					.field("developer", event.getSession().getDeveloper().getName())
					.field("project", event.getSession().getProject().getName())
					.field("purpose", event.getSession().getPurpose()).field("label", event.getSession().getLabel())
					.field("kind", event.getKind()).field("detail", event.getDetail())
					.field("methodSignature", event.getMethod().getSignature())
					.field("typeName", event.getMethod().getType().getFullName()).field("timestamp", new Date())
				.endObject().string();

		String response = getInstance().create("swarm/event", source);
		System.out.println(response);

	}

	public static void createInvocation(Invocation invocation) throws Exception {
		String source = jsonBuilder()
				.startObject()
					.field("invocationId", invocation.getId())
					.field("sessionId", invocation.getSession().getId())
                    .field("taskId", invocation.getSession().getTask().getId())
                    .field("taskTitle", invocation.getSession().getTask().getTitle())
					.field("description", invocation.getSession().getDescription())
					.field("developer", invocation.getSession().getDeveloper().getName())
					.field("project", invocation.getSession().getProject().getName())
					.field("purpose", invocation.getSession().getPurpose())
					.field("label", invocation.getSession().getLabel())
					.field("invokingId", invocation.getInvoking().getId())
					.field("invokedId", invocation.getInvoked().getId())
					.field("invoking", invocation.getInvoking().toString())
					.field("invoked", invocation.getInvoked().toString())
					.field("invokedType", invocation.getInvoked().getType().getFullName())
					.field("invokingType", invocation.getInvoking().getType().getFullName()).field("timestamp", new Date())
				.endObject().string();

		String response = getInstance().create("swarm/invocation", source);
		System.out.println(response);
	}

	public static void createMethod(Method method) throws Exception {
		String source = jsonBuilder()
				.startObject()
                    .field("methodId", method.getId())
                    .field("taskId", method.getType().getSession().getTask().getId())
                    .field("taskTitle", method.getType().getSession().getTask().getTitle())
                    .field("sessionId", method.getType().getSession().getId())
                    .field("description", method.getType().getSession().getDescription())
                    .field("developer", method.getType().getSession().getDeveloper().getName())
                    .field("project", method.getType().getSession().getProject().getName())
                    .field("typeName", method.getType().getFullName())
                    .field("name", method.getName())
                    .field("key", method.getKey())
                    .field("timestamp", new Date())
               .endObject().string();
		        
		        String response = getInstance().create("swarm/method", source);
				System.out.println(response);	
	}

	public static void createType(Type type) throws Exception {
		String source = jsonBuilder()
				.startObject()
                    .field("typeId", type.getId())
                    .field("sessionId", type.getSession().getId())
                    .field("taskId", type.getSession().getTask().getId())
                    .field("taskTitle", type.getSession().getTask().getTitle())
                    .field("description", type.getSession().getDescription())
                    .field("developer", type.getSession().getDeveloper().getName())
                    .field("project", type.getSession().getProject().getName())
                    .field("fullName", type.getFullName())
                    .field("name", type.getName())
                    .field("source", type.getSource())
                    .field("namespace", type.getNamespace().getFullPath())
                    .field("timestamp", new Date())
                .endObject().string();
		        
        String response = getInstance().create("swarm/type", source);
		System.out.println(response);		
	}

	public static List<Method> getStartingMethods(Project project) throws Exception {
		return getStartingMethods(project, "");
	}


	public static List<Method> getStartingMethods(Project project, String search) throws Exception {
		List<Method> methods = new ArrayList<Method>();
		
		Set<Integer> invokedMethods = new HashSet<Integer>();
		Set<Integer> invokingMethods = new HashSet<Integer>();
		
		String query = "swarm/invocation/_search?q=project:" + project.getName() + "&search_type=count";
		String content =  "";
		
		StringWriter writer = new StringWriter();
		JsonWriter w = new JsonWriter(writer);
		w.setHtmlSafe(true);
		w.setLenient(true);
		w.beginObject().name("aggregations")
			.beginObject()
				.name("invokingMethods")
					.beginObject().name("terms")
						.beginObject()
							.name("field").value("invokingId")
						.endObject()
				.endObject()
			.name("invokedMethods")
				.beginObject().name("terms")
					.beginObject()
						.name("field").value("invokedId")
					.endObject()
			.endObject()
		.endObject()
		.endObject();

		w.flush();
		content = writer.toString();
		w.close();
		
		String response = ElasticServer.getInstance().search(query, content);

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);
		
		JsonArray invokingArray = (JsonArray) element.getAsJsonObject().get("aggregations").getAsJsonObject().get("invokingMethods").getAsJsonObject().get("buckets").getAsJsonArray();
		
		for (JsonElement invokingElement : invokingArray) {
			invokingMethods.add(invokingElement.getAsJsonObject().get("key").getAsInt());
		}

		JsonArray invokedArray = (JsonArray) element.getAsJsonObject().get("aggregations").getAsJsonObject().get("invokedMethods").getAsJsonObject().get("buckets").getAsJsonArray();
		for (JsonElement invokedElement : invokedArray) {
			invokedMethods.add(invokedElement.getAsJsonObject().get("key").getAsInt());
		}

		Iterator<Integer> iterator = invokedMethods.iterator();
		
		while(iterator.hasNext()) {
			Integer invokedMethod = iterator.next();
			if(invokingMethods.contains(invokedMethod)) {
				invokingMethods.remove(invokedMethod);
			}
		}
		
		iterator = invokingMethods.iterator();
		
		while(iterator.hasNext()) {
			Method method = MethodService.get(iterator.next());
			if(search != null && search.length() > 0 && !method.toString().toLowerCase().contains(search.toLowerCase())) {
				continue;
			}

			boolean found = false;
			for (Method m : methods) {
				if(m.getKey().equals(method.getKey())) {
					found = true;
				}
				
			}
			
			if(!found) {
				methods.add(method);
			}
		}
		
		return methods;
	}
	
	public static List<Breakpoint> getBreakpoints(Project project, String search) throws Exception {
		List<Breakpoint> breakpoints = new ArrayList<Breakpoint>();
		
		String query = "swarm/breakpoint/_search";
		
		runSearch(project, search, breakpoints, query, SearchMode.BASIC);
		
		if(search.length() > 0) {
			runSearch(project, search, breakpoints, query, SearchMode.FUZZY);
			runSearch(project, search, breakpoints, query, SearchMode.MATCH);
			runSearch(project, search, breakpoints, query, SearchMode.WILDCARD);
		}

		return breakpoints;
	}

	private static void runSearch(Project project, String search, List<Breakpoint> breakpoints, String query, SearchMode searchMode)
			throws IOException, Exception {

		String response;
		if(searchMode == SearchMode.BASIC) {
			response = ElasticServer.getInstance().search(query+"?q=project:" + project.getName() + "&q=" + search , "");
		} else {
			String content;
			StringWriter writer = new StringWriter();
			JsonWriter w = new JsonWriter(writer);
			w.setHtmlSafe(true);
			w.setLenient(true);
			w.beginObject().name("query").beginObject()
					.name("filtered").beginObject();

			w.name("query").beginObject();
			
			switch (searchMode) {
			case FUZZY:
				{
					w.name("fuzzy").beginObject()
						.name("_all").value(search)
					.endObject();
				}
			break;
			case MATCH:
				{
					w.name("multi_match")
					.beginObject()
						.name("query").value(search)
						.name("fields")
							.beginArray()
								.value("label")
								.value("description")
	     						.value("purpose")
	     						.value("developer")
							.endArray()
					.endObject();
				}
			break;
			case WILDCARD:
				{
					w.name("wildcard").beginObject()
						.name("_all").value(search)
					.endObject();
				}
			break;
			default:
				break;
			}
				

			w.endObject();

			//Project filter
//			w.name("filter").beginObject().name("bool")
//								.beginObject().name("must")
//									.beginArray()
//										.beginObject().name("term")
//											.beginObject().name("project")
//												.value(project.getName())
//											.endObject()
//										.endObject()
//									.endArray()
//								.endObject()
//							.endObject();
			
			w.endObject().endObject().endObject();
				
			w.flush();
			content = writer.toString();
			w.close();
			
			System.out.println(ElasticServer.getInstance().getServerUrl() + query + "?q=" + search);
			System.out.println(content);
			
			response = ElasticServer.getInstance().search(query, content);
		}

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		JsonArray breakpointArray = (JsonArray) element.getAsJsonObject().get("hits")
					.getAsJsonObject().get("hits").getAsJsonArray();
		
		for (JsonElement breakpointElement : breakpointArray) {
			try {
				
				JsonObject source = breakpointElement.getAsJsonObject().get("_source").getAsJsonObject();
				
				if (source != null && (source.has("project") && source.has("breakpointId"))) {
					String projectName = source.getAsJsonObject().get("project").getAsString();
					if(projectName.equals(project.getName())) {
						Breakpoint breakpoint = new Breakpoint();
						breakpoint.setId(source.getAsJsonObject().get("breakpointId").getAsInt());
						breakpoint.setLineNumber(source.getAsJsonObject().get("lineNumber").getAsInt());
						breakpoint.setCharStart(source.getAsJsonObject().get("charStart").getAsInt());
						breakpoint.setCharEnd(source.getAsJsonObject().get("charEnd").getAsInt());
						breakpoint.setCode(source.getAsJsonObject().get("breakpointCode").getAsString());
						
						Type type = new Type();
						type.setName(source.getAsJsonObject().get("typeName").getAsString());
						type.setFullName(source.getAsJsonObject().get("typeFullName").getAsString());
						type.setSource(source.getAsJsonObject().get("typeSource").getAsString());
						breakpoint.setType(type);
		
						Session session = new Session();
						session.setId(source.getAsJsonObject().get("sessionId").getAsInt());
						session.setDescription(source.getAsJsonObject().get("description").getAsString());
						session.setLabel(source.getAsJsonObject().get("label").getAsString());
						session.setPurpose(source.getAsJsonObject().get("purpose").getAsString());
						
						Developer developer = new Developer();
						developer.setName(source.getAsJsonObject().get("developer").getAsString());
						session.setDeveloper(developer);
						
						breakpoint.setSession(session);
						
						if(!breakpoints.contains(breakpoint)) {
							breakpoints.add(breakpoint);
						}
				}

				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
}