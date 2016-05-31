package swarm.recommender;

import java.util.Comparator;


public class CandidateComparator implements Comparator<Candidate>{

		@Override
		public int compare(Candidate o1, Candidate o2) {

			if (o1.getConfidence() > o2.getConfidence())
				return -1;
			else if (o1.getConfidence() < o2.getConfidence()) 
				return 1;
			else
				return 0;
		}
		
}
