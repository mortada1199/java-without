package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.List;

public class StudentServer {

    private static final int PORT = 8080;
    private static final String URL = "jdbc:mysql://localhost/schooljbdc";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private ObjectMapper  objectMapper = new ObjectMapper();
    private StudentDAO studentDAO;

    public StudentServer() throws SQLException {
        studentDAO = new StudentDAO(URL, USER, PASSWORD);
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/students", new StudentHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + PORT);
    }

    private class StudentHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGetRequest(exchange);
                    break;
                case "POST":
                    handlePostRequest(exchange);
                    break;
                case "PUT":
                    handlePutRequest(exchange);
                    break;
                case "DELETE":
                    handleDeleteRequest(exchange);
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                    break;
            }
        }

        private void handleGetRequest(HttpExchange exchange) throws IOException {
            try {
                List<Student> students = studentDAO.getAllStudents();
                String response = objectMapper.writeValueAsString(students);
                exchange.sendResponseHeaders(200, response.length());
                OutputStream output = exchange.getResponseBody();
                output.write(response.getBytes());
                output.close();
            } catch (SQLException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1); // Internal Server Error
            }
        }

        private void handlePostRequest(HttpExchange exchange) throws IOException {
            try {
                InputStream input = exchange.getRequestBody();
                Student student = objectMapper.readValue(input, Student.class);
                studentDAO.addStudent(student);
                String response = objectMapper.writeValueAsString(student);
                exchange.sendResponseHeaders(201, response.length());
                OutputStream output = exchange.getResponseBody();
                output.write(response.getBytes());
                output.close();
            } catch (SQLException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1); // Internal Server Error
            }
        }

        private void handlePutRequest(HttpExchange exchange) throws IOException {
            try {
                InputStream input = exchange.getRequestBody();
                Student student = objectMapper.readValue(input, Student.class);
                studentDAO.updateStudent(student);
                String response = objectMapper.writeValueAsString(student);
                exchange.sendResponseHeaders(200, response.length());
                OutputStream output = exchange.getResponseBody();
                output.write(response.getBytes());
                output.close();
            } catch (SQLException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1); // Internal Server Error
            }
        }

        private void handleDeleteRequest(HttpExchange exchange) throws IOException {
            try {
                InputStream input = exchange.getRequestBody();
                Student student = objectMapper.readValue(input, Student.class);
                studentDAO.deleteStudent(student.getId());
                exchange.sendResponseHeaders(204, -1); // No Content
            } catch (SQLException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1); // Internal Server Error
            }
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        StudentServer server = new StudentServer();
        server.start();
    }
}
