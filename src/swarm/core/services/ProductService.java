package swarm.core.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import swarm.core.domain.Product;
import swarm.core.domain.Task;
import swarm.core.server.SwarmServer;

public class ProductService {
	
	public static void create(final Product product) throws Exception {
		
		SwarmServer server = SwarmServer.getInstance();
		
		String json = getJson(product).toString();
		String response = server.create(SwarmServer.PRODUCTS, json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			product.setId(id);
		}
		
	}
	
	public static Product get(int id) {
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get(SwarmServer.PRODUCTS + "/" +id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject() && element.getAsJsonObject().get("id") != null) {
			Product p = new Product();
			ProductService.populate(element, p);
			return p;
		} else {
	 		return null;
		}	
	}
	
	public static void populate(JsonElement element, Product product) {
		product.setId(element.getAsJsonObject().get("id").getAsInt());
		product.setName(element.getAsJsonObject().get("name").getAsString());
	}
	
	public static JsonObject getJson(Product product) {
		
		JsonObject data = new JsonObject();
		data.addProperty("id", Integer.toString(product.getId()));
		data.addProperty("name", product.getName());
		
		return data;
		
	}

}
