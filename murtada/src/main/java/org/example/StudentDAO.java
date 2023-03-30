package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private Connection connection;

    public StudentDAO(String url, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    public void addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, age, grade, email) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, student.getName());
        statement.setInt(2, student.getAge());
        statement.setString(3, student.getGrade());
        statement.setString(4, student.getEmail());
        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            student.setId(generatedKeys.getInt(1));
        }
    }

    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int age = resultSet.getInt("age");
            String grade = resultSet.getString("grade");
            String email = resultSet.getString("email");
            students.add(new Student(id, name, age, grade, email));
        }
        return students;
    }

    public Student getStudentById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            String name = resultSet.getString("name");
            int age = resultSet.getInt("age");
            String grade = resultSet.getString("grade");
            String email = resultSet.getString("email");
            return new Student(id, name, age, grade, email);
        } else {
            return null;
        }
    }

    public void deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    public void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, age = ?, grade = ?, email = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, student.getName());
        statement.setInt(2, student.getAge());
        statement.setString(3, student.getGrade());
        statement.setString(4, student.getEmail());
        statement.setInt(5, student.getId());
        statement.executeUpdate();
    }

    public void close() throws SQLException {
        connection.close();
    }
}