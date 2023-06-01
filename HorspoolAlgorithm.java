import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HorspoolAlgorithm {

    public static void main(String[] args) {
    	long startTime = startTimer();
        String inputFile = "hobbit.html";	//get input file name
        String pattern = "example";	//pattern that we will search in text
        
        String outputFileName = "output.html";	// output file 
        File output=new File(outputFileName);	//create a file
        
        try {
            String html = readHTMLFile(inputFile);	// method read HTML file line by line
            
            System.out.println("---Horspool Algorithm---");
            // We only make search at body part of HTML file so we divide content into 3 parts
            String BeforeBody=BeforeBodyContent(html);	// content from beginning to body
            String AfterBody=AfterBodyContent(html);	// content from body to end
            String bodyContent = extractBodyContent(html);	// content at body
            
            ArrayList<Integer> occurrenceIndex = new ArrayList<>();
            
            int comparison = horspoolAlgorithm(bodyContent, pattern,occurrenceIndex);
            
            int[] occur = occurrenceIndex.stream().mapToInt(Integer::intValue).toArray();
            
            System.out.println("Number of occurrences: " + occur.length +"\nNumber of comparisons: "+comparison);
            
            String highlightedContent= highlightIndexes(bodyContent,occur,pattern.length());	//highlight all the pattern that found in text
            String fulltext = BeforeBody+highlightedContent+AfterBody;	//collect all part of output file
            
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
               System.out.println("Bad Symbol Table: " + shiftTable);
       }
  
    
       // The horspoolAlgorithm method searches for the pattern it receives as a parameter in the text it receives as a parameter
       public static int horspoolAlgorithm(String html, String pattern, ArrayList<Integer> occurences) {
    	badSymbol(pattern);
       	int comp = 0; // Number of occurrences
       	int n = html.length(); // Length of text
       	int m = pattern.length(); // Length of pattern
       	
       	int i = m-1; // Index of the last character of the pattern as the starting point for comparisons
       	while(i <= n-1) { // Loop for text
       		int k = 0;
       		
               while(k <= m-1 && (pattern.charAt(m-1-k) == html.charAt(i-k))) {
       			comp++;
       			k++;
       		}
       		if(k <= m-1) {
       			comp++;
       		}
       		if (k == m) { // Pattern found
       			occurences.add(i-k+1);
       			i++;
       			
       		}
       		else { // Shift the pattern
                   i += shiftTable.getOrDefault(html.charAt(i), pattern.length());
       		}
       	}
       	return comp;
       	
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