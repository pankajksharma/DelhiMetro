package project.android.delhimetro;
import java.io.*;
import java.net.*;

class WebSurfer{

	// variables to store hidden elements.....
        String __VIEWSTATE,__EVENTVALIDATION;
	
	//Result variables
	String route,fare;
	String[] route_array;
	
	public void printResult(){
				//System.out.println("Fare is : Rs. "+fare+"\n\n");
                //System.out.println("Route is :\n");
                for(int i=0;i<route_array.length;i++){
			if(route_array[i].indexOf("Change Train Here")!=-1)
				route_array[i] = route_array[i].substring(0,route_array[i].indexOf("<b"))+" ** CHANGE TRAIN HERE **";
                       // System.out.println(route_array[i]);
                       // System.out.println("      V      ");
                }

	}
	
	public void makeGetRequest(){
		 //Making get request to fetch values of hidden elements for a particular session
                String str = DownloadText("http://www.delhimetrorail.com/metro-fares.aspx",null);

                str = str.substring(str.indexOf("__VIEWSTATE"));
                __VIEWSTATE = str.substring(str.indexOf("__VIEWSTATE\" value=\"")+20, str.indexOf("\" />") );

                str = str.substring( str.indexOf("__EVENTVALIDATION") );
                __EVENTVALIDATION = str.substring( str.indexOf("value=\"")+7, str.indexOf("\" />") );

	}
	
	public void makePostRequest(String from_station_id, String to_station_id){
		try{
                	//Post data string....
                	String postdata="__VIEWSTATE="+URLEncoder.encode(__VIEWSTATE,"UTF-8")+"&__EVENTTARGET=&__EVENTARGUMENT=&__EVENTVALIDATION="+URLEncoder.encode(__EVENTVALIDATION,"UTF-8")+"&ctl00%24txtSearch=Search...&ctl00%24InnerContent%24ddlFrom="+from_station_id.trim()+"&ctl00%24InnerContent%24ddlTo="+to_station_id.trim()+"&ctl00%24InnerContent%24btnShowFare=Show+me+fare";

                	//Making post request to fetch route and fare between desired stations.
                	String post_result = DownloadText("http://www.delhimetrorail.com/metro-fares.aspx",postdata);
                	post_result = post_result.substring(post_result.indexOf("ctl00_InnerContent_lblFare"));
                	fare = post_result.substring(post_result.indexOf("/>")+2,post_result.indexOf("</span>"));
                	post_result = post_result.substring( post_result.indexOf("ctl00_InnerContent_lblRoute")+29);
                	route = post_result.substring(0,post_result.indexOf("</span>"));
                }catch(Exception e){
                }

	}

	public void tokenizeResult(){
		route_array = route.split("-->");	
	}
	
	public InputStream OpenHTTPGetURLConnection(String urlString)throws IOException{
    		InputStream in = null;
    		int response =-1;
    	
    		URL url = new URL(urlString);
    		URLConnection conn = url.openConnection();
    	
    		if(!(conn instanceof HttpURLConnection))
    		throw new IOException("Not an HTTP connection");
    	
    		try{
    			HttpURLConnection httpconn = (HttpURLConnection)conn;
    			httpconn.setAllowUserInteraction(false);
    			httpconn.setInstanceFollowRedirects(true);
    			httpconn.setRequestMethod("GET");
    			httpconn.connect();
    		
    			response=httpconn.getResponseCode();
    			if(response==HttpURLConnection.HTTP_OK){
    				in = httpconn.getInputStream();    			
    			}
    		}catch(Exception ex){
    			throw new IOException("Error Connecting");    		
    		}
    		return in;
    	}

	public InputStream OpenHTTPPostURLConnection(String urlString, String post) throws IOException{
		InputStream in = null;
	
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();

		try{
			conn.setDoOutput(true);
            		OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(post);
            		writer.flush();

			in = conn.getInputStream();
			writer.close();	
			
		}catch(Exception ex){
			throw new IOException("Error Connecting");
		}
		return in;
	}

	public String DownloadText(String URL,String post){
    		int BUFFER_SIZE=2000;
    		InputStream in =null;
    		try{
			if(post==null)
    				in = OpenHTTPGetURLConnection(URL);
			else
				in = OpenHTTPPostURLConnection(URL,post);
    		}catch(IOException e){
    			e.printStackTrace();
    			return "3";
    		}
    	
    		InputStreamReader isr = new InputStreamReader(in);
    		int charRead;
        	String str = "";
        	char[] inputBuffer = new char[BUFFER_SIZE];          
        	try{
           		while((charRead = isr.read(inputBuffer))>0)
           		{                    
               			//---convert the chars to a String---
               			String readString = String.copyValueOf(inputBuffer, 0, charRead);                    
               			str += readString;
               			inputBuffer = new char[BUFFER_SIZE];
           		}
           		in.close();
           
        	}catch (IOException e) {
           		e.printStackTrace();
           		return "1";
        	}    
        	return str;        
    	}
}
