package base;

import java.text.SimpleDateFormat;
import java.util.Date;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class Home {

	public static void main(String[] args) {
		final String info = "SYC,8635-0832-4410\n";
		final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String HOST = args[0];
		
		Undertow server = Undertow.builder()
				.addHttpListener(80, HOST)
				.setHandler(new HttpHandler() {

					public void handleRequest(final HttpServerExchange exchange) throws Exception {
						String path = exchange.getRequestPath();
						exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");

						if (path.equals("/q1")) {
							exchange.getResponseSender().send(info + fmt.format(new Date()));
						}
					}
				}).build();
		server.start();
	}
}
