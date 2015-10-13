package swarm.core.server;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.junit.Test;

import swarm.core.domain.Method;
import swarm.core.domain.Project;

import com.google.gson.stream.JsonWriter;

public class ServerTests {
	
	@Test
	public void healthTest() {
		ElasticServer server = ElasticServer.getInstance();
		assertTrue(server.isOk());
	}
	
	
	@Test
	public void getTest() throws Exception {
		String query = "swarm/invocation/_search?q=project:DesignPatterns&search_type=count";
		String content =  "";
		
		StringWriter writer = new StringWriter();
		JsonWriter w = new JsonWriter(writer);
		w.setHtmlSafe(true);
		w.setLenient(true);
		try {
			w.beginObject().name("aggregations")
				.beginObject()
					.name("invokingMethods")
						.beginObject().name("terms")
							.beginObject()
								.name("field").value("invoking")
							.endObject()
					.endObject()
				.name("invokendMethods")
					.beginObject().name("terms")
						.beginObject()
							.name("field").value("invoked")
						.endObject()
				.endObject()
			.endObject()
			.endObject();
			w.flush();
			content = writer.toString();
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		System.out.println(ElasticServer.search(query, content));
		
	}
	
	@Test
	public void getStartingMethods() throws Exception {
		Project p = new Project();
		p.setName("DesignPatterns");
		List<Method> methods = ElasticServer.getStartingMethods(p);
		
		for (Method method : methods) {
			System.out.println(method.getKey());
		}
	}
	
	@Test
	public void isNeo4JOk() {
		assertTrue(Neo4JServer.getInstance().isOk());
	}
	

}
