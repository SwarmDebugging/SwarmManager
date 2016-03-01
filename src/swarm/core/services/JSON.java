package swarm.core.services;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gson.stream.JsonWriter;

public class JSON {
	
	public static String build(Map<String, Object> data) {
		StringWriter writer = new StringWriter();
		JsonWriter w = new JsonWriter(writer);
		w.setHtmlSafe(true);
		w.setLenient(true);
		try {
			w.beginObject();
			Set<String> fields = data.keySet();
			for (Iterator<String> iterator = fields.iterator(); iterator.hasNext();) {
				String field = iterator.next();
				Object value = data.get(field) == null ? "" : data.get(field);
				w.name(field).value(value.toString());
			}
			w.endObject();
			w.flush();
			String result = writer.toString();
			w.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
