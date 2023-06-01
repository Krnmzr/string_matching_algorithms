import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BoyerMooreAlgorithm {

    public static void main(String[] args) {
        long startTime = startTimer();
        String inputFile = "hobbit.html";	//get input file name
        String pattern = "example";	//pattern that we will search in text
        
        String outputFileName = "output.html";	// output file 
        File output=new File(outputFileName);	//create a file
        
        try {
            String html = readHTMLFile(inputFile);	// method read HTML file line by line
            
            System.out.println("---Boyer Moore Algorithm---");
            // We only make search at body part of HTML file so we divide content into 3 parts
            String BeforeBody=BeforeBodyContent(html);	// content from beginning to body
            String AfterBody=AfterBodyContent(html);	// content from body to end
            String bodyContent = extractBodyContent(html);	// content at body
            
            ArrayList<Integer> occurrenceIndex = new ArrayList<>();
            
            int comparison = boyerMooreAlgorithm(bodyContent, pattern,occurrenceIndex);
            
            int[] occur = occurrenceIndex.stream().mapToInt(Integer::intValue).toArray();
            
            System.out.println("Number of occurrences: " + occur.length +"\nNumber of comparisons: "+comparison);
            
            String highlightedContent= highlightIndexes(bodyContent,occur,pattern.length());	//highlight all the pattern that found in text
            String fulltext = BeforeBody+highlightedContent+AfterBody;	//concat all part of output file
            
            Files.write(Paths.get(output.getAbsolutePath()), fulltext.getBytes());	// write content to output file
            
            long timer = stopTimer(startTime);	// get the current time and calculate running time
            System.out.println("Running Time: "+timer+" milliseconds");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    // get the start time
    public static long startTimer() {
        return System.currentTimeMillis();
    }
    // get the end time and calculate run time
    public static long stopTimer(long startTime) {
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
    
    // return the content of between body tags
    public static String AfterBodyContent(String html) {		
   	    int bodyEndIndexCaps = html.indexOf("</BODY>");
        int bodyEndIndex = html.indexOf("</body>");
        int lastindex = html.length()-1;
        if (bodyEndIndexCaps != -1) {
        	String beforeBodyStringCaps = "</BODY>" +html.substring(bodyEndIndexCaps +7, lastindex);
        	return beforeBodyStringCaps;
		} else if (bodyEndIndex != -1) {
			String beforeBodyString = "</body>" +html.substring(bodyEndIndex +7, lastindex);
			return beforeBodyString;
		}
        return html;
    }
    
    // return the content before body tag
    public static String BeforeBodyContent(String html) {
		
	    int bodyStartIndexCaps = html.indexOf("<BODY>");
        int bodyStartIndex = html.indexOf("<body>");
        if (bodyStartIndexCaps != -1) {
        	String beforeBodyStringCaps = html.substring(0, bodyStartIndexCaps) + "<BODY>";
        	return beforeBodyStringCaps;
		} else if (bodyStartIndex != -1) {
			String beforeBodyString = html.substring(0, bodyStartIndex) + "<body>";
			return beforeBodyString;
		}
        return html;
    }
    
    // return the content after the body tag
    public static String extractBodyContent(String html) {
        int bodyStartIndexCaps = html.indexOf("<BODY>");
        int bodyEndIndexCaps = html.indexOf("</BODY>");
        int bodyStartIndex = html.indexOf("<body>");
        int bodyEndIndex = html.indexOf("</body>");
        if (bodyStartIndex != -1 && bodyEndIndex != -1) {
            return html.substring(bodyStartIndex + "<body>".length(), bodyEndIndex);
        } else if (bodyStartIndexCaps != -1 && bodyEndIndexCaps != -1) { 
            return html.substring(bodyStartIndexCaps + "<body>".length(), bodyEndIndexCaps);
        } else {
            return null;
        }


    }

    // get content all of the HTML file line by line
    public static String readHTMLFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    
 // The shiftTable map holds the Bad symbol table
    static Map<Character, Integer> shiftTable = new HashMap<>();

    /*
     * The badSymbol method calculates the amount of shifting for the characters in the
     * pattern it takes as a parameter, assigns it to the shiftTable map and prints the shiftTable
     * */
    public static void badSymbol(String pattern){
            for (int i = 0; i <  pattern.length() - 1; i++) {
                shiftTable.put(pattern.charAt(i),  pattern.length() - 1 - i);
            }
            System.out.println("Bad Symbol: " + shiftTable);
    }
    
    // return the last suffix of given match part at pattern
    public static int lastIndex(String pattern,String match) {
    	int index=pattern.length();
    	
    	for(int i=pattern.length()-match.length()-1; i>=0 ;i--) {
    		int j=0;
    		while(j<match.length()&&i-j>=0&&(pattern.charAt(i-j)==match.charAt(match.length()-1-j))) {//if the current char is matched it will continue comparison
    			j++;
    		}
    		if(j==match.length()) {
    			return pattern.length()-1-i;
    		}
    	}
    	//if the given pattern doesn't have match again find last suffix
    	while(match.length()!=0) {
    		int i;
    		for(i=0; i<match.length();i++) {
    			if(pattern.charAt(i)!=match.charAt(i)) {
    				break;
    			}
    		}
    		if(i==match.length()) {
    			return pattern.length()-i;
    		}
    		else {
    			match=match.substring(1);
    		}
    	}
    	
    	return index;
    }
    
    // create a good suffix table to given pattern
    public static int[] goodSuffixTable(String pattern) {
    	int[] goodSuffix = new int[pattern.length()-1];
    	
    	for(int i=0; i<goodSuffix.length;i++) {
    		String match = pattern.substring(pattern.length()-1-i);
    		goodSuffix[i]=lastIndex(pattern,match);
    		
    	}
    	
    	System.out.println("Good suffix table: "+Arrays.toString(goodSuffix));
    	return goodSuffix;
    }
    
    
    // boyer moore algorithm
    public static int boyerMooreAlgorithm(String html, String pattern,ArrayList<Integer> occurences) {
    	int comparison=0;
    	int textSize=html.length();
    	int patternSize = pattern.length();
    	
    	badSymbol(pattern);	//create bad symbol table
    	int[] suffixtable = goodSuffixTable(pattern);	//create good suffix table
    	
    	int i=patternSize-1;
    	while(i<textSize) {
    		if(i<html.length()&&html.charAt(i)!=pattern.charAt(patternSize-1)) {	//if first char is not matched act like horspool algorithm
    					comparison++;
    	    			i+=shiftTable.getOrDefault(html.charAt(i), pattern.length());	
    	    }
    		else {
    			int j=0;
    			int k=0;
    			while(j<patternSize&&i-j>=0&&(html.charAt(i-j)==pattern.charAt(patternSize-j-1))) {	//find match substring
    				comparison++;
    				k++;
    				j++;
    			}
    			if(j<=pattern.length()-1) {
    				comparison++;
    			}
    			if(j==pattern.length()) {	//if the matched substring is equal to pattern
    				occurences.add(i-j+1);
    				i++;
    			}
    			else {
    				i+=Math.max(suffixtable[k-1],shiftTable.getOrDefault(html.charAt(i-j),pattern.length())-k);	//if the matched is less than pattern find the hift value
    				
    			}
    		}
    		
    	}
    	
    	return comparison;
    }
    
    //highlight all the occurrences according to given index array
    public static String highlightIndexes(String content,int[] index,int size) {
		   int k=0;
		   int j=0;
		   for(int i=0;i<index.length;i++) {
			  k=index[i];
			  content=highlightstart(content,k+(i-j)*13,size);
			  while(i < index.length-1 && index[i]+size > index[i+1]) {
				  k=index[i+1];
				  j++;
				  i++;
			  }
			  content=highlightend(content,k+13*(i-j),size);
		   }
		   
		   return content;
	   }
	   
    //add <mark> tag to all of the given index
	   public static String highlightstart(String content,int i,int size) {
		   StringBuilder html1 = new StringBuilder(content);
		   if(i<content.length()) {
		   html1.insert(i, "<mark>");
		   }
		   content=html1.toString();
		   return content;
	   }
	   
	 //add </mark> tag to all of the given index
	   public static String highlightend(String content,int i,int size) {
		   StringBuilder html1 = new StringBuilder(content);
		   if(i+size+6<content.length()) {
			   html1.insert(i + size + 6, "</mark>");
		   }
		   content=html1.toString();
		   return content;
	   }
}