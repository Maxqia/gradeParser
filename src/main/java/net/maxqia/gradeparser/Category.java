package net.maxqia.gradeparser;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {
	
	public String name;
	
	/**
	 * Percentage of Grade
	 */
	public double weight;
	
	public ArrayList<Assignment> assignments = new ArrayList<Assignment>();
	
	public double getScore() {
		return getScoreWithout(null);
	}
	
	public double getScoreWithout(Assignment ignored) {
		double totalEarned = 0;
		double totalScore = 0;
		
		for(Assignment assignment : assignments) {
			if (assignment.equals(ignored)) continue;
			totalEarned += assignment.getScore();
			totalScore += assignment.getTotalScore();
		}
		
		return totalEarned / totalScore;
	}
}
