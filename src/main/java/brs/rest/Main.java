 package brs.rest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Main {
	private  int port = 8081;
	private  String hostName = "0.0.0.0";

	public static void main(String[] args) throws Exception {
		new Main().start(args);
	}

	private  void start(String[] args) throws IOException {
		loadParams(args);
		ResourceConfig config = new ResourceConfig();
		config.register(ConfigurationContextResolver.class);
		config.register(BrsController.class);
		config.register(LoggingFilter.class);
		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(getURI(),
				config,false);
		try {
			server.start();
			System.out.println("--Press Enter to STOP the server--");
			System.in.read();
		} finally {
			server.shutdown();
			System.out.println("Server stoped!");
		}
	}

	private  void loadParams(String[] args) {
		if (args == null || args.length == 0)
			return;

		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-port":
				try {
					port = Integer.valueOf(args[i + 1]);
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					throw new RuntimeException(
							" '-port' param must be accompanied by port number");
				}
				break;
			case "-host":
				try {
					hostName = args[i + 1];
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					throw new RuntimeException(
							" '-host' param must be accompanied by host name or IP");
				}
				break;
			default:
				break;
			}
		}
	}

	private  URI getURI() {
		return UriBuilder.fromUri("http://" + getHostName() + "/").port(port)
				.build();
	}

	private  String getHostName() {
		if (hostName == null || "".equals(hostName)) {
			try {
				hostName = InetAddress.getLocalHost().getCanonicalHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return hostName;
	}
}
