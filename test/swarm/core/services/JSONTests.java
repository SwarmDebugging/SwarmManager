package swarm.core.services;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class JSONTests {

	@Test
	public void build() {
		Map<String,Object> data = new HashMap<>();
		data.put("id", 1);
		data.put("name", "John");
		
		String j = JSON.build(data);
		assertEquals("{\"id\": 1, \"name\": \"John\"}", j);
	}
}
