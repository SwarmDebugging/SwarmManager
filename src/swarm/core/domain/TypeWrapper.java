package swarm.core.domain;

public class TypeWrapper {

	private Type type;
	
	private String source;
		
	public TypeWrapper(Type type, String source) {
		this.type = type;
		this.source = source;
	}
		
	public TypeWrapper() {}
		
	public Type getType() {
			return type;
	}

	public void setType(Type type) {
			this.type = type;
	}

	public String getSource() {
			return source;
	}

	public void setSource(String source) {
			this.source = source;
	}
	
}
