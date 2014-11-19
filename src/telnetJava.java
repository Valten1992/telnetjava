import org.apache.commons.net.telnet.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class telnetJava {
	TelnetClient telnet = null;
	InputStream in = null;
	PrintStream out = null;
	String next = "";
	String[] list = new String[] { "notepad.exe", "firefox.exe", "iTunes.exe"};

	public telnetJava() {
		telnet = new TelnetClient();
		try {
			telnet.connect("127.0.0.1", 23);
			// Get input and output stream references
			in = telnet.getInputStream();
			out = new PrintStream(telnet.getOutputStream());

			// Log the user on
			readUntil("login: ");
			write("test");

			readUntil("password: ");
			write("pass");

			readUntil("test>");

			for (int i = 0; i <= list.length; i++) {
				
				
				write("tasklist /fi \"IMAGENAME eq "+list[i]+"\"");

				if (readUntil("test>").contains("PID")) {

					write("Taskkill /IM "+list[i]+" /F");
					break;
				}

			}

			readUntil("terminated");
			telnet.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void write(String value) {
		try {
			out.println(value);
			out.flush();
			//System.out.println(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String readUntil(String pattern) {
		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();

			char ch = (char) in.read();

			while (true) {

				System.out.print(ch);
				
				sb.append(ch);
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						return sb.toString().trim();
					}
				}
				ch = (char) in.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String read() throws IOException {

		StringBuffer sb = new StringBuffer();
		// sb.append(readChar(in));
		int b = 0;
		while ((b = readChar(in)) != -1) {
			System.out.print((char) b);
			sb.append((char) b);
		}

		return sb.toString();
	}

	public static void main(String args[]) {
		telnetJava rtc = new telnetJava();
	}

	private int readChar(final InputStream in) {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		// set the executor thread working
		Callable<Integer> task = new Callable<Integer>() {
			public Integer call() {
				try {
					return in.read();
				} catch (Exception e) {
					// do nothing
				}
				return null;
			}
		};

		Future<Integer> future = executor.submit(task);
		Integer result = null;
		try {
			result = future.get(500, TimeUnit.MILLISECONDS); // timeout of 1 sec
		} catch (TimeoutException ex) {
			// do nothing
		} catch (InterruptedException e) {
			// handle the interrupts
		} catch (ExecutionException e) {
			// handle other exceptions
		} finally {
			future.cancel(false);
			executor.shutdownNow();
		}

		if (result == null) {
			return (char) -1;
		}
		return (char) result.intValue();
	}
}