package main.java.tpjad;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@WebServlet("/dispatcher")
public class DispatcherServlet extends HttpServlet {
    private volatile boolean isPolling = false;

    private final List<String> receivedMessages = new CopyOnWriteArrayList<>();

    @Override
    public void init() {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");

        if ("start".equalsIgnoreCase(action)) {
            if (isPolling) {
                resp.getWriter().write("Polling is already running.");
                return;
            }
            resp.getWriter().write("Polling started successfully.");
        } else if ("stop".equalsIgnoreCase(action)) {
            if (!isPolling) {
                resp.getWriter().write("Polling is not running.");
                return;
            }
            resp.getWriter().write("Polling stopped successfully.");
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid action. Use 'start' or 'stop'.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.getWriter().write(receivedMessages.toString());
    }
}
