package swarm.core.domain;

import swarm.core.services.BreakpointService;

public class Breakpoint extends Domain {

	private Session session;
	private Type type;

	private int lineNumber;
	private int charStart;
	private int charEnd;
	
	private String code;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getCharStart() {
		return charStart;
	}

	public void setCharStart(int charStart) {
		this.charStart = charStart;
	}

	public int getCharEnd() {
		return charEnd;
	}

	public void setCharEnd(int charEnd) {
		this.charEnd = charEnd;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void create() throws Exception {
		BreakpointService.create(this);
	}
	
	public boolean equals(Object object) {
		if(object instanceof Breakpoint) {
			return id == ((Breakpoint) object).getId();
		}
		return false;
	}}
