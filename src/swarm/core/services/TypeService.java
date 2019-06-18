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
import swarm.core.domain.Product;
import swarm.core.domain.Session;
import swarm.core.domain.Task;
import swarm.core.domain.Type;
import swarm.core.domain.TypeWrapper;
import swarm.core.server.ElasticServer;
import swarm.core.server.SwarmServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TypeService {
	
	public static SessionService sessionService;
	public static NamespaceService namespaceService;
	public static TypeWrapperService typeWarapperService;

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

		type.setId(element.getAsJsonObject().get("id").getAsInt());
		type.setName(element.getAsJsonObject().get("name").getAsString());
		type.setFullName(element.getAsJsonObject().get("fullName").getAsString());
		type.setFullPath(element.getAsJsonObject().get("fullPath").getAsString());
		
		if(type.getSource() == null && !element.getAsJsonObject().get("artefact").isJsonNull()) {
			JsonElement e = element.getAsJsonObject().get("artefact");
			String source = e.getAsJsonObject().get("sourceCode").getAsString();
			type.setSource(source);
		}
		
		type.setSession(session);
		
		if(type.getNamespace() == null && !element.getAsJsonObject().get("namespace").isJsonNull()) {
			JsonElement e = element.getAsJsonObject().get("namespace");
			int namespace_id = e.getAsJsonObject().get("id").getAsInt();
			Namespace n = NamespaceService.get(namespace_id);
			type.setNamespace(n);
		}
		
	}

	public static void create(final Type type) throws Exception {
		SwarmServer server = SwarmServer.getInstance();
		String source = type.getSource();
		TypeWrapper typeWrapper = new TypeWrapper(type, type.getSource());
		
		String json = TypeWrapperService.getJson(typeWrapper).toString();
		String response = server.create(SwarmServer.TYPES, json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			type.setId(id);

			//ElasticServer.createType(type);
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
		type.setFullName(element.getAsJsonObject().get("fullPath").getAsString());
		
		if(type.getSource() == null && !element.getAsJsonObject().get("artefact").isJsonNull()) {
			JsonElement e = element.getAsJsonObject().get("artefact");
			String source = e.getAsJsonObject().get("sourceCode").getAsString();	
			type.setSource(source);
		}
		
		if(type.getNamespace() == null && !element.getAsJsonObject().get("namespace").isJsonNull()) {
			JsonElement e = element.getAsJsonObject().get("namespace");
			int namespace_id = e.getAsJsonObject().get("id").getAsInt();
			Namespace n = NamespaceService.get(namespace_id);
			type.setNamespace(n);
		}
		
		if(type.getSession() == null) {
			JsonElement e = element.getAsJsonObject().get("session");
			int session_id = e.getAsJsonObject().get("id").getAsInt();
			Session s = SessionService.get(session_id);
			type.setSession(s);
		}
		
	}
	
	public static JsonObject getJson(Type type) {
		
		JsonObject data = new JsonObject();
		data.addProperty("id", type.getId());
		data.addProperty("name", type.getName());
		data.addProperty("fullName", type.getFullName());
		data.addProperty("fullPath", type.getFullPath());
		data.add("session", sessionService.getJson(type.getSession()));
		data.add("namespace", namespaceService.getJson(type.getNamespace()));

		return data;
		
	}
	
}
