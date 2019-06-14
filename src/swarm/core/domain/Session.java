package swarm.core.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import swarm.core.server.SwarmServer;
import swarm.core.services.SessionService;

public class Session extends Domain {
	
	private String label = "";
	private Date started;
	private Date finished;
	private String purpose = "";
	private String description = "";
	private Task task;
	private Developer developer;
	private Project project;

	public Session() {
		this.started = Calendar.getInstance().getTime();
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


	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
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
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return df.format(started);
	}
	
	public boolean equals(Object object) {
		if(object != null && object instanceof Session) {
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
	
	public String getURI() {
		return SwarmServer.getInstance().getServerUrl() + SwarmServer.SESSIONS + "/" + getId(); 
	}
	
	public Map getData() {
		Map data = new HashMap<>();
		data.put("id", this.getId());
		data.put("description", this.getDescription());
		data.put("started", this.getStarted());
		data.put("finished", this.getFinished());
		data.put("label", this.getLabel());
		data.put("project", this.getProject());
		data.put("purpose", this.getPurpose());
		data.put("developer", this.getDeveloper().getData());
		data.put("task", this.getTask().getData());
		return data;
	}
	
}