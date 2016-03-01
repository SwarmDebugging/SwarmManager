package swarm.core.services;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;

import swarm.core.domain.Method;
import swarm.core.domain.Namespace;
import swarm.core.domain.Session;
import swarm.core.domain.Type;
import swarm.core.server.ElasticServer;
import swarm.core.server.SwarmServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class TypeService {

	public static List<Method> getMethods(Type type) throws Exception {
		List<Method> methods = new ArrayList<>();
		SwarmServer server = SwarmServer.getInstance();

		String response;
		response = server.get("methods/getByTypeId?typeId=" + type.getId());
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonArray()) {
			JsonArray jsonTypes = (JsonArray) element.getAsJsonArray();
			for (JsonElement methodElement : jsonTypes) {
				Method method = new Method();
				method.setType(type);
				MethodService.populate(methodElement, method);
				methods.add(method);
			}
		}

		return methods;
	}

	public static String loadSource(IResource resource) throws Exception {
		StringBuffer source = new StringBuffer();

		List<String> lines = Files.readAllLines(resource.getLocation().toFile().toPath(), Charset.defaultCharset());
		for (String line : lines) {
			source.append(line + System.lineSeparator());
		}

		return source.toString();
	}

	public static void populate(JsonElement element, Type type, Session session) {
		type.setSession(session);

		Namespace n = NamespaceService.get(element.getAsJsonObject().get("namespace").getAsJsonObject().get("id")
				.getAsInt());
		type.setNamespace(n);

		type.setId(element.getAsJsonObject().get("id").getAsInt());
		type.setName(element.getAsJsonObject().get("name").getAsString());
		type.setFullName(element.getAsJsonObject().get("fullName").getAsString());
		type.setFullPath(element.getAsJsonObject().get("fullPath").getAsString());
		type.setSource(element.getAsJsonObject().get("source").getAsString());
	}

	public static void create(final Type type) throws Exception {
		SwarmServer server = SwarmServer.getInstance();

		Map<String, Object> data = new HashMap<>();
		data.put("name", type.getName());
		data.put("fullName", type.getFullName());
		data.put("fullPath", type.getFullPath());
		data.put("session", type.getSession().getURI());
		data.put("namespace", type.getNamespace().getURI());
		data.put("source", type.getSource());

		String json = JSON.build(data);
		String response = server.create(SwarmServer.TYPES, json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			type.setId(id);

			ElasticServer.createType(type);
		}
	}

	public static Type createByPath(Session session, String fullPath) throws Exception {
		IPackageFragment[] packages = session.getProject().getJavaProject().getPackageFragments();

		for (IPackageFragment aPackage : packages) {
			if (aPackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				for (ICompilationUnit unit : aPackage.getCompilationUnits()) {
					IType[] types = unit.getAllTypes();
					for (IType iType : types) {
						String iTypePath = iType.getResource().getFullPath().toString();
						if (iTypePath.equals(fullPath)) {
							Namespace namespace = NamespaceService.getNamespaceByFullPath(aPackage.getResource()
									.getFullPath().toString());

							if (namespace == null) {
								namespace = new Namespace();
								namespace.setFullPath(aPackage.getResource().getFullPath().toString());
								namespace.setName(aPackage.getElementName());
								namespace.create();

								if (!namespace.isLoaded()) {
									throw new Exception("Problem to create the new namespace " + namespace.toString());
								}
							}

							Type type = new Type();

							type.setSession(session);
							type.setNamespace(namespace);
							type.setName(iType.getElementName());
							type.setFullPath(iType.getResource().getFullPath().toString());
							type.setFullName(iType.getFullyQualifiedName());

							type.setSource(loadSource(iType.getResource()));
							type.create();

							if (!type.isLoaded()) {
								throw new Exception("Problem to create the new type " + type.toString());
							}
							return type;
						}
					}
				}
			}
		}
		return null;
	}

	public static Type get(int id) {
		SwarmServer server = SwarmServer.getInstance();
		
		String response;
		try {
			response = server.get(SwarmServer.TYPES + "/"+ id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject() && element.getAsJsonObject().get("id") != null) {
			Type type = new Type();
			populate(element, type);
			return type;
		} else {
			return null;
		}
	}

	private static void populate(JsonElement element, Type type) {
		type.setId(element.getAsJsonObject().get("id").getAsInt());
		type.setName(element.getAsJsonObject().get("name").getAsString());
		type.setFullName(element.getAsJsonObject().get("fullName").getAsString());
		type.setSource(element.getAsJsonObject().get("source").getAsString());
		
		//TODO to populate namespace and session.
	}
}
