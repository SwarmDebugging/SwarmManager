package swarm.core.server;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import swarm.core.domain.Invocation;
import swarm.core.domain.Method;
import swarm.core.services.MethodService;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Neo4JServer {

	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final String PUT = "PUT";
	private static final String DELETE = "DELETE";

	private static String DEFAULT_URL = "http://localhost:7474/db/data/";
	private static Neo4JServer server;

	private String serverUrl;

	public static Neo4JServer getInstance() {
		if (server == null) {
			server = new Neo4JServer(DEFAULT_URL);
		}
		return server;
	}

	public static Neo4JServer getInstance(String url) {
		if (server == null || !server.getServerUrl().equals(url)) {
			if (!url.endsWith("/")) {
				url += "/";
			}

			server = new Neo4JServer(url);
		}

		return server;
	}

	private Neo4JServer(String url) {
		this.serverUrl = url;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public String get(String message) throws Exception {
		URL url = new URL(serverUrl + message);
		return request(url,"",GET);
	}
	
	public String get(String message, String content) throws Exception {
		URL url = new URL(serverUrl + message);
		return request(url,content,GET);
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

	private String request(URL url, String request, String method) throws Exception {
		HttpURLConnection urlConnection;
		urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setDoOutput(true);
		urlConnection.setRequestMethod(method);
		urlConnection.setUseCaches(false);
		urlConnection.setConnectTimeout(10000);
		urlConnection.setReadTimeout(10000);
		urlConnection.setRequestProperty("Accept", "application/json; charset=UTF-8");
		urlConnection.setRequestProperty("Content-Type", "application/json");
		urlConnection.connect();
		
//		String userpass = "p8dasvnuxn:7tm40gzfnp";
//		String basicAuth = "Basic " + new String(Base64.encodeBytes(userpass.getBytes()));
//		connection.setRequestProperty("Authorization", basicAuth);

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
			String status = get("");
			return status.contains("\"neo4j_version\" : \"2.2.1\"");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void createInvocation(Invocation invocation) throws Exception {
		String source = jsonBuilder().startObject()
				.field("query", "MATCH (invoking:Method),(invoked:Method) WHERE invoking.id = {invokingId} AND invoked.id = {invokedId} "
						+ "CREATE (invoking)-[invocation:INVOKES {propsInvocation}]->(invoked)")
				.field("params").startObject()
					.field("invokingId",invocation.getInvoking().getId())
					.field("invokedId",invocation.getInvoked().getId())
					.field("propsInvocation")
						.startObject()
							.field("id", invocation.getId())
							.field("sessionId", invocation.getSession().getId())
							.field("description", invocation.getSession().getDescription() == null ? "" : invocation.getSession().getDescription())
							.field("developer", invocation.getSession().getDeveloper().getName())
							.field("project", invocation.getSession().getProject().getName())
							.field("purpose", invocation.getSession().getPurpose() == null ? "" : invocation.getSession().getPurpose())
							.field("label", invocation.getSession().getLabel() == null ? "" : invocation.getSession().getLabel())
							.field("invokingId", invocation.getInvoking().getId())
							.field("invokedId", invocation.getInvoked().getId())
							.field("invoking", invocation.getInvoking().toString())
							.field("invoked", invocation.getInvoked().toString())
							.field("invokedType", invocation.getInvoked().getType().getFullName())
							.field("invokingType", invocation.getInvoking().getType().getFullName()).field("timestamp", new Date())
						.endObject().
				endObject().string();

		String response = getInstance().create("cypher", source);
		
		System.out.println(response);
	}

	public static Method getMethodBySQLId(int sqlId) throws Exception {
		String source = "{\"query\": \"MATCH (m:Method) where m.id = " + sqlId + " return m\"}";
		String response = getInstance().create("/cypher", source);
		
        JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject() && element.getAsJsonObject().get("metadata").getAsJsonObject().get("id") != null) {
			int idMethod = element.getAsJsonObject().get("metadata").getAsJsonObject().get("id").getAsInt();
			if(idMethod > 0) {
				return MethodService.get(idMethod);
			}
		}
		return null;
	}

	public static void createMethod(Method method) throws Exception {
		String source = jsonBuilder().startObject()
				.field("query", "CREATE (method:Method) SET method = {props}")
				.field("params").startObject()
					.field("props").startObject()
						.field("id", method.getId())
						.field("name", method.getName())
						.field("key", method.getKey())
						.field("typeName", method.getType().getFullName())
						.field("session", method.getType().getSession().getId())
						.field("developer", method.getType().getSession().getDeveloper().getName())
					.endObject().
				endObject().string();

		String response = getInstance().create("cypher", source);
		
		System.out.println(response);
	}

	public static boolean containsInvocation(Invocation invocation) throws Exception {
		String source = jsonBuilder().startObject()
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																			.field("query", "MATCH ()-[i:INVOKES]->() "
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																				+ "WHERE i.sessionId = {sessionId} AND i.invokingId = {invokingId} and i.invokedId = {invokedId} RETURN i.id")
					.field("params").startObject()
						.field("sessionId", invocation.getSession().getId())
						.field("invokingId", invocation.getInvoking().getId())
						.field("invokedId", invocation.getInvoked().getId())
					.endObject()
				.endObject().string();

		String response = getInstance().get("cypher", source);
		

        JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);
		
		
		JsonArray data = (JsonArray) element.getAsJsonObject().get("data").getAsJsonArray();
		for (JsonElement jsonElement : data) {
			if(jsonElement.getAsJsonObject().get("id").getAsInt() > 0) {
				return true;
			}
		}
		
		return false;
	}
}