import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BruteForce {

    public static void main(String[] args) {
        long startTime = startTimer();
        String inputFile = "hobbit.html";	//get input file name
        String pattern = "example";	//pattern that we will search in text

        String outputFileName = "output.html";	// output file
        File output=new File(outputFileName);	//create a file

        try {
            String html = readHTMLFile(inputFile);	// method read HTML file line by line

            System.out.println("---Brute Force String Matching Algorithm---");
            // We only make search at body part of HTML file so we divide content into 3 parts
            String BeforeBody=BeforeBodyContent(html);	// content from beginning to body
            String AfterBody=AfterBodyContent(html);	// content from body to end
            String bodyContent = extractBodyContent(html);	// content at body

            ArrayList<Integer> occurrenceIndex = new ArrayList<>();

            int comparison = bruteForceStringMatch(bodyContent, pattern,occurrenceIndex);

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

    //This method searches the pattern according to the brute force algorithm and returns the number of comparisons.
    public static int bruteForceStringMatch(String html, String pattern,ArrayList<Integer> occurences) throws IOException {
        int comparison = 0;
        int n = html.length();
        int m = pattern.length();

        for (int i = 0; i <= n - m; i++) {
            int j;
            for (j = 0; j < m; j++) {
                if (html.charAt(i + j) != pattern.charAt(j)) {
                    if(j>0){ //This condition is valid when the initial letter of the patter is found and other letters are started to be received.
                        comparison+=j;
                    }
                    comparison++;
                    break;
                }
            }
            if (j == m) { //This condition means pattern found.
                comparison+=m;
                occurences.add(i);

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