import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;



public class test {

	private static TelnetClient telnet = new TelnetClient();
	private static InputStream in;
	private static PrintStream out;
	
	public test() throws SocketException, IOException{
		
		telnet.connect("127.0.0.1", 23);
		in = telnet.getInputStream();
		out = new PrintStream(telnet.getOutputStream());
		

 		// Log the user on
 		readUntil("login: ");

 		out.println("test");
		out.flush();
 		readUntil( "password: " );

 		out.println("pass");
		out.flush();
 		readUntil( "test>" );

 		out.println("Taskkill /IM notepad.exe /F");
		out.flush();

 		readUntil( "terminated" );
 		telnet.disconnect();
		
	}
	
	public static void main(String[] args) throws SocketException, IOException{
		
			test t = new test();
	}
	
	public void disconnect() {
		try {
			in.close();
			out.close();
			telnet.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	  public static String readUntil( String pattern )
	  {
	   try
	   {
		 char lastChar = pattern.charAt( pattern.length() - 1 );
		 StringBuffer sb = new StringBuffer();
		 char ch = ( char )in.read();
		 while( true ) {
		  System.out.print( ch );
		  sb.append( ch );
		  if( ch == lastChar ) {
		    if( sb.toString().endsWith( pattern ) ) {
			 return sb.toString();
		    }
		  }
		  ch = ( char )in.read();
		 }
	   }
	   catch( Exception e ) {
		 e.printStackTrace();
	   }
	   return null;
	  }
}
