package net.maxqia.gradeparser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Hello world!
 *
 */
public class Main {
	
	static ArrayList<Assignment> assignments = new ArrayList<Assignment>();
	static ArrayList<Category> categories = new ArrayList<Category>();
	static double realScore;
	
    public static void main( String[] args ) throws IOException {
    	setupTables();
    	
    	realScore = getTotalScore();
    	for (Category category : categories) {
    		double score = category.getScore() * 100;
    		System.out.println(category.getName() + " : " + score);
    		//finalScore += score * category.getWeight() / 100;
    	}
    	System.out.println("Total Score : " + realScore);
    	System.out.println();
    	
    	printImpact(false);
    	printImpact(true);
    }
    
    public static void printImpact(boolean printHighest) {
    	Assignment lowest = null;
    	double scoreWithoutLowest = 0;
    	for (Assignment assignment : assignments) {
    		double scoreWithout = getTotalScoreWithout(assignment);
    		if (lowest == null || (printHighest ? scoreWithout < scoreWithoutLowest 
    				: scoreWithout > scoreWithoutLowest)) {
    			lowest = assignment;
    			scoreWithoutLowest = scoreWithout;
    			continue;
    		}
    	}
    	
    	// jeez that's long...
    	String toPrint = "The assignment with the most ";
    	toPrint += (printHighest ? "positive" : "negative");
    	toPrint += " impact to your score is \"";
    	toPrint += lowest.getName() + "\" with a ";
    	toPrint += (realScore - scoreWithoutLowest) + "% impact to your grade.";
    	
    	System.out.println(toPrint);
    	/*System.out.println("The assignment with the most " (? negative" impact to your score is \"" 
    	   + lowest.getName() + "\" with a " + (realScore - scoreWithoutLowest) 
    	   + "% impact to your grade.");*/
    }
    public static void setupTables() throws IOException {
    	File file = new File("../../report.html");
        Document doc = Jsoup.parse(file, null);
        
        // first get the categories
        Element categoryTable = doc.select("td.padding_r20").get(2)
        		.select("table").get(0)
        		.select("tbody").get(0);
        
        for (Element row : categoryTable.select("tr").next()) {
            Elements tds = row.select("td");
            Category category = new Category();
            category.setName(tds.get(0).text());
            category.setWeight(Double.parseDouble(tds.get(1).text().replace("%", "")));
            categories.add(category);
        }
        
        
        for (Element table : doc.select("tbody.general_body")) {
            for (Element row : table.select("tr")) {
                Elements tds = row.select("td");
                Assignment assignment = new Assignment();
                assignment.setCategory(tds.get(0).text());
                assignment.setName(tds.get(2).text());
                parseScore(assignment, tds);
                
                Category category = matchCategory(tds.get(0).text());
                category.assignments.add(assignment);
                assignments.add(assignment);
            }
        }
    }
    
    public static void parseScore(Assignment assignment, Elements tds) {
    	//try {
    	assignment.setScore(Double.parseDouble(tds.get(3).text()));
    	
    	String percent = tds.get(4).text();
    	if (percent.contains("-")) return;
    	String[] strings = percent.split("\\s+");
    	//System.out.println(strings[2]);

    	assignment.setTotalScore(Double.parseDouble(strings[2]));
    	//} catch (Exception e) {
    	//	System.out.println(assignment.getName());
    	//}
    }
    
    public static Category matchCategory(String name) {
    	for (Category category : categories) {
    		if (category.getName().equals(name)) 
    			return category;
    	}
    	throw new RuntimeException("Unknown Name : " + name);
    }
    
    public static double getTotalScore() {
    	return getTotalScoreWithout(null);
    }
    
    public static double getTotalScoreWithout(Assignment ignored) {
    	double finalScore = 0;
    	for (Category category : categories) {
    		//double score = category.getScoreWithout(ignored) * 100;
    		//finalScore += score * category.getWeight() / 100;
    		finalScore += category.getScoreWithout(ignored) * category.getWeight();
    	}
    	return finalScore;
    }
}
