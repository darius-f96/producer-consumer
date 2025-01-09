package main.java.tpjad;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

@WebServlet("/dispatcher/*")
public class DispatcherServlet extends HttpServlet {

    // Consumer endpoints
    private static final String CONSUMER1_URL = "http://consumer:8080/tomcat-consumer-app/consumer";
    private static final String CONSUMER2_URL = "http://consumer-jetty:8080/consumer";

    // Producer endpoints
    private static final String PRODUCER1_URL = "http://producer:8080/tomcat-producer-app/producer";
    private static final String PRODUCER2_URL = "http://producer-jetty:8080/producer";

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Add CORS headers
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "No valid path provided. Use /tomcat-consumer/start, /jetty-consumer/start, etc.");
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length < 3) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Incomplete path. Expected something like /tomcat/start");
            return;
        }

        String service = parts[1].toLowerCase();
        String operation = parts[2].toLowerCase();

        String result;
        switch (service) {
            case "tomcat-consumer":
                if ("start".equals(operation)) {
                    result = doPostRequest(CONSUMER1_URL, "action=start");
                    response.getWriter().write("POST /tomcat/start -> " + result);
                } else if ("stop".equals(operation)) {
                    result = doPostRequest(CONSUMER1_URL, "action=stop");
                    response.getWriter().write("POST /tomcat/stop -> " + result);
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Unknown operation for tomcat consumer: " + operation);
                }
                break;

            case "jetty-consumer":
                if ("start".equals(operation)) {
                    result = doPostRequest(CONSUMER2_URL, "action=start");
                    response.getWriter().write("POST /jetty/start -> " + result);
                } else if ("stop".equals(operation)) {
                    result = doPostRequest(CONSUMER2_URL, "action=stop");
                    response.getWriter().write("POST /jetty/stop -> " + result);
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Unknown operation for jetty consumer: " + operation);
                }
                break;

            case "tomcat-producer":
                if ("send".equals(operation)) {
                    String msg = request.getParameter("msg");
                    if (msg == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                "Missing 'msg' parameter for tomcat producer.");
                        return;
                    }
                    result = doPostRequest(PRODUCER1_URL, "action=send&msg=" + urlEncode(msg));
                    response.getWriter().write("POST /tomcat/send -> " + result);
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Unknown operation for tomcat producer: " + operation);
                }
                break;

            case "jetty-producer":
                if ("send".equals(operation)) {
                    String msg = request.getParameter("msg");
                    if (msg == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                "Missing 'msg' parameter for jetty producer.");
                        return;
                    }
                    result = doPostRequest(PRODUCER2_URL, "action=send&msg=" + urlEncode(msg));
                    response.getWriter().write("POST /jetty/send -> " + result);
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Unknown operation for jetty producer: " + operation);
                }
                break;

            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Unknown service: " + service + ". Expected tomcat-consumer, jetty-consumer, tomcat-producer, or jetty-producer.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "No valid path provided. E.g. /tomcat-consumer/messages");
            return;
        }
        String[] parts = pathInfo.split("/");

        if (parts.length < 3) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Incomplete path. E.g. /tomcat-consumer/messages");
            return;
        }

        String service = parts[1].toLowerCase();
        String operation = parts[2].toLowerCase();
        String result;

        switch (service) {
            case "tomcat-consumer":
                if ("messages".equals(operation)) {
                    result = doGetRequest(CONSUMER1_URL);
                    response.getWriter().write("GET /tomcat-consumer/messages -> " + result);
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Unknown GET operation for tomcat-consumer: " + operation);
                }
                break;
            case "jetty-consumer":
                if ("messages".equals(operation)) {
                    result = doGetRequest(CONSUMER2_URL);
                    response.getWriter().write("GET /jetty-consumer/messages -> " + result);
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Unknown GET operation for jetty-consumer: " + operation);
                }
                break;
            case "tomcat-producer":
                if ("stats".equals(operation)) {
                    // e.g. GET /dispatcher/producer1/stats -> doGetRequest(PRODUCER1_URL)
                    result = doGetRequest(PRODUCER1_URL);
                    response.getWriter().write("GET /tomcat-producer/stats -> " + result);
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Unknown GET operation for tomcat-producer: " + operation);
                }
                break;
            case "jetty-producer":
                if ("stats".equals(operation)) {
                    result = doGetRequest(PRODUCER2_URL);
                    response.getWriter().write("GET /jetty-producer/stats -> " + result);
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Unknown GET operation for jetty-producer: " + operation);
                }
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Unknown service: " + service);
        }
    }

    private String doPostRequest(String endpointUrl, String paramData) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(endpointUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Cookie", "");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(paramData.getBytes());
            }

            int status = conn.getResponseCode();
            String responseBody = readStream(conn.getInputStream());

            return "HTTP " + status + " | " + responseBody;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling " + endpointUrl + ": " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String doGetRequest(String endpointUrl) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(endpointUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Cookie", "");
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            int status = conn.getResponseCode();
            String responseBody = readStream(conn.getInputStream());

            return "HTTP " + status + " | " + responseBody;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error GETing " + endpointUrl + ": " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String readStream(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            return br.lines().collect(Collectors.joining("\n"));
        }
    }

    private String urlEncode(String input) {
        return input.replace(" ", "%20");
    }
}
