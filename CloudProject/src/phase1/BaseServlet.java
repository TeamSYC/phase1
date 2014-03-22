package phase1;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Sairam Krishnan (sbkrishn)
 */
public class BaseServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getServletPath();
		PrintWriter out = response.getWriter();
		
		if (path.equals("/q1")) {
			response.setContentType("text/plain");
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			out.println("SYC,8635-0832-4410");
			out.print(fmt.format(new Date()));
		}
	}
}
