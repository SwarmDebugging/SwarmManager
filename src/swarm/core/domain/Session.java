package swarm.core.domain;

import java.util.Date;
import java.util.List;

import swarm.core.services.SessionService;

public class Session extends Domain {
	
	private String label = "";
	
	private Date started;
	private Date finished;

	private String purpose = "";
	private String description = "";

	private Project project;
	private Developer developer;

	public Session() {
		this.started = null;
		this.finished = null;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}


	public Date getFinished() {
		return finished;
	}


	public void setFinished(Date finished) {
		this.finished = finished;
	}


	public String getPurpose() {
		return purpose;
	}


	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Developer getDeveloper() {
		return developer;
	}

	public void setDeveloper(Developer developer) {
		this.developer = developer;
	}

	public List<Breakpoint> getBreakpoints() {
		return SessionService.getBreakpoints(this);
	}

	public List<Event> getEvents() {
		return SessionService.getEvents(this);
	}


	public boolean isActive() {
		return started != null && finished == null;
	}


	public void start() throws Exception {
		SessionService.start(this);
	}

	public void create() throws Exception {
		SessionService.create(this);
	}
	
	public void stop() throws Exception {
		SessionService.finish(this);
	}
	
	public String toString() {
		return this.label;
	}
	
	public boolean equals(Object object) {
		if(object instanceof Session) {
			return id == ((Session) object).getId();
		}
		return false;
	}
	
	public List<Type> getTypes() {
		return SessionService.getTypes(this);
	}
	
	public List<Method> getStartingMethods() {
		return SessionService.getStartingMethods(this);
	}
	
	public List<Method> getEndingMethods() {
		return SessionService.getEndingMethods(this);
	}
}