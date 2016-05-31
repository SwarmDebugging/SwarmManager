package swarm.recommender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CandidateList {

	private List<Candidate> candidates = new ArrayList<Candidate>();
	
	
	public void add(Candidate aCandidate) {
		getCandidates().add(aCandidate);
	}
	
	public void add(String entity) {
		if (entity == null)
			return;

		if (this.contains(entity)) {
			increaseOccurence(entity);
		} else {
			getCandidates().add(new Candidate(entity, 1));
		}
	}
	
	public boolean contains(String entity) {
		for (Candidate aCandidate : getCandidates()) {
			if (aCandidate.getName().equals(entity)) {
				return true;
			}
		}
		return false;
	}
	
	public void increaseOccurence(String entity) {

		if (getCandidates().isEmpty())
			return;
		Iterator iterator = getCandidates().iterator();
		while (iterator.hasNext()) {
			Candidate candidate = (Candidate) iterator.next();
			if (candidate.getName().equals(entity))
				candidate.incrementOccurence();
		}
	}
	
	public void rank(int numTask) {
		for (Candidate candidate : getCandidates()) {
			candidate.setConfidence((double) candidate.getOccurence() / numTask);
		}
		CandidateComparator comparator = new CandidateComparator();
		Collections.sort(getCandidates(), comparator);
	}

	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}

}
