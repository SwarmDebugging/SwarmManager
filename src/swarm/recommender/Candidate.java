package swarm.recommender;

public class Candidate {

	private String name;
	private int occurence;
	private double confidence;
	
	public Candidate(String n, int occ) {
		name = n;
		occurence = occ;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOccurence() {
		return occurence;
	}
	public void setOccurence(int occurence) {
		this.occurence = occurence;
	}
	public double getConfidence() {
		return confidence;
	}
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
	public void incrementOccurence() {
		occurence ++;
	}
	
}
