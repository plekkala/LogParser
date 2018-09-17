import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

	static Map<String, String> applicationProperties = new HashMap<String, String>();
	static String applicationFile = "application.properties";
	static Map<String, String> configProperties = new HashMap<String, String>();
	static List<String> matchedStrings = new ArrayList<String>();
	static String configFile = "configFile";
	static String logFileName = "logFile";
	static String resultsFileName = "resultsDir";
	static String logFile;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// applicationProperties = new HashMap<String,String>();
		// Load the config
		applicationProperties = loadProperties(applicationFile);
		configProperties = loadProperties(applicationProperties.get(configFile));
		logFile = applicationProperties.get(logFileName);
		patternMatching();
		printResults(applicationProperties.get(resultsFileName));

		// export and print the results
	}

	private static void printResults(String resultsDir) throws IOException {
		 //String str = "World";
		 
		    BufferedWriter writer = new BufferedWriter(new FileWriter(resultsDir+"\\"+System.currentTimeMillis(), true));
		    writer.append("--------"+System.currentTimeMillis()+"--------");
		    writer.newLine();
		    for (String name : configProperties.keySet()) {
		    	if(matchedStrings.contains(configProperties.get(name))){
		    		 writer.append("Found : "+configProperties.get(name));
		    		 writer.newLine();
		    	}
		    	else
		    	{
		    		 writer.append("Not Found : "+configProperties.get(name));
		    		 writer.newLine();
		    	}
		    	 
		    }
		   
		     
		    writer.close();
		
	}

	private static void patternMatching() throws IOException {

		// Pattern patt = Pattern.compile("[A-Za-z][a-z]+");

		LineNumberReader r = loadFile(logFile);

		String line;
	//	while (true)
			for (String name : configProperties.keySet()) {
				while ((line = r.readLine()) != null) {
					boolean matchFound = false;
					Pattern patt = Pattern.compile(configProperties.get(name));
					Matcher m = patt.matcher(line);
					while (m.find()) {
						matchFound = true;
						int start = m.start(0);
						int end = m.end(0);
						System.out.println(line.substring(start, end));
						matchedStrings.add(line.substring(start, end));
						r.mark(r.getLineNumber());
						r.reset();
						break;
					}
					if (matchFound) {
						System.out.println("Line Number:" + r.getLineNumber());
						break;
					}

				}

			}

	}

	private static LineNumberReader loadFile(String fileName) throws IOException {

		LineNumberReader r = new LineNumberReader(new FileReader(fileName));
		return r;

	}

	private static Map<String, String> loadProperties(String fileName) {

		Properties prop = new Properties();
		InputStream input = null;

		Map<String, String> properties = new HashMap<String, String>();
		try {

			input = LogParser.class.getClassLoader().getResourceAsStream(fileName);
			if (input == null) {
				System.out.println("Sorry, unable to find " + fileName + " In the class path");
				input = new FileInputStream(fileName);
				System.out.println("Load from the filessytem " + fileName);

			}
			prop.load(input);
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				properties.put(key, value);
				System.out.println("Key : " + key + ", Value : " + value);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return properties;

	}

	static void printMap(Map<String, String> props) {
		for (String name : props.keySet()) {

			String key = name.toString();
			String value = props.get(key);
			System.out.println(key + " " + value);

		}
	}

}
