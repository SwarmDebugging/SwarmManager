package swarm.core.domain;


public abstract class Domain {
	
	int id = -1;
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * If domain was load from persistent mechanism 
	 * @return true if is loaded
	 */
	public boolean isLoaded() {
		return id > -1;
	}
	
	public abstract String getURI();
}
