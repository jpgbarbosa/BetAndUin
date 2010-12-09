package server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class SoccerReader {

	private String API_KEY = "pnrjfz6rdpdtfscjn8ccj2xv";

	public ArrayList<String> mergeHeadlines(String [] headlines, String section){
		
		ArrayList<String> answer = new ArrayList<String>();
		
		for (int i = 0; i < headlines.length; i++){
			String [] outcome = latestHeadlines(headlines[i], section);
			if(outcome == null)
				return null;
			for (int z = 0; z < outcome.length; z++){
				answer.add(outcome[z]);
			}
		}
		
		return answer;
	}
	
	public String [] latestHeadlines(String query, String section) {
		// Used to store the last ID.
		String [] array = null;
		
		try {  
			// Initiate the REST client.
			URL url = new URL("http://content.guardianapis.com/search?q="+query+"&section="+section+"&order-by=newest&format=xml&api-key=" + API_KEY);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        
	        // HTTP Verb
	        connection.setRequestMethod("GET");
	        // Get requests data from the server.
	        
	        connection.setDoOutput(true);
	        connection.setInstanceFollowRedirects(false); 
	        connection.setRequestProperty("User-agent", "ToDo Manager");
	        
	        
	        // If we get a Redirect or an Error (3xx, 4xx and 5xx)
	        if (connection.getResponseCode() >= 300) {
	        	// We want more information about what went wrong.
	        	//debug(connection);
	        	return null;
	        }
	        
	        
	        // Response body from InputStream.
	        InputSource inputSource = new InputSource(connection.getInputStream());
	        
	        // XPath is a way of reading XML files.
	        XPathFactory  factory=XPathFactory.newInstance();
	        XPath xPath=factory.newXPath();
	        
	        
	        // here we are querying the document (much like SQL) for all the todo tags inside todo elements.
	        NodeList nodes = (NodeList) xPath.evaluate("/response/results/content", inputSource, XPathConstants.NODESET);
	        // The last argument defines the type of result we are looking for. Might be NODESEQ for a list of Nodes
	        // or NODE for a single node.
	        

	        // We don't need the connection anymore once we get the nodes.
	        connection.disconnect();
	        
	        array = new String[10];
	        String lastID;
	        // Pretty printing of output
	        for (int i=0;i<nodes.getLength();i++) {
	        	Node node = nodes.item(i);
	        	
	        	// Fetching the atributes of the node element
	        	String title = node.getAttributes().getNamedItem("web-title").getTextContent();
	 			lastID = node.getAttributes().getNamedItem("id").getTextContent();
	        	array[i]=lastID+"<>"+title;
	        	
	        }
	        
		} catch(IOException e) { 
	    	e.printStackTrace();
	    } catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return array;
	}	
	
	
	public String [] recentBody(String lastID) {
		// This function should print the body of the last news item.
		try {  
			URL url = new URL("http://content.guardianapis.com/" + lastID + "?format=xml&show-fields=all&api-key="+API_KEY);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	        // HTTP Verb
	        connection.setRequestMethod("GET");
	        connection.setDoOutput(true);
	        connection.setInstanceFollowRedirects(false); 
	        connection.setRequestProperty("User-agent", "ToDo Manager");
			
			// If we get a Redirect or an Error (3xx, 4xx and 5xx)
	        if (connection.getResponseCode() >= 300) {
	        	//debug(connection);
	        	return null;
	        }	        
	        
	        // Response body from InputStream.
	        InputSource inputSource = new InputSource(connection.getInputStream());
	        
	        // XPath is a way of reading XML files.
	        XPathFactory  factory=XPathFactory.newInstance();
	        XPath xPath=factory.newXPath();
	        
	        String [] data = new String[3];

	        NodeList nodes = (NodeList) xPath.evaluate("/response/content/fields/field", inputSource, XPathConstants.NODESET);
			for (int i=0;i<nodes.getLength();i++) {
				Node node = nodes.item(i);
				if(node.getAttributes().getNamedItem("name").getNodeValue().equals("headline")){
					data[0]= node.getTextContent();
				}
				if(node.getAttributes().getNamedItem("name").getNodeValue().equals("trail-text")){
					data[1] = node.getTextContent();
				}
				if(node.getAttributes().getNamedItem("name").getNodeValue().equals("thumbnail")){
					data[2] = node.getTextContent();
				}

			}
			
			return data;
	
		} catch(IOException e) {
	    	e.printStackTrace();
	    } catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

}
