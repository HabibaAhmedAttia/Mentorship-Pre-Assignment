package mentorShipAssignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Assignment {
	private static Connection con;
	private static Logger logger = Logger.getLogger(Assignment.class.getName());

	public static void main(String[] args) {
		try {
			con = createConnection();
			File studentFile = new File("students.txt");
			readFile(studentFile);
			queryAllStudentsWithSpecificGrade("A");
		} catch (Exception e) {
			logger.log(Level.SEVERE,"error in main function", e);
		} finally {
			closeConnection();
		}
	}

	public static void readFile(File studentFile) {
		try (Scanner scan = new Scanner(studentFile)) {
			while (scan.hasNextLine()) {
				String[] nextLineOfFile = parseDataLineByLine(scan.nextLine());
				insertDataWithPreparedStaement(nextLineOfFile);
			}
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE,"File not found", e);
		}
	}

	public static String[] parseDataLineByLine(String nextLineOfFile) {
		String[] splitValues = nextLineOfFile.split(",");
		try {
			if (splitValues.length != 4 || splitValues[0] == "") {
				throw new IllegalArgumentException("there is missing values in line:------>  " + nextLineOfFile);
			}
			System.out.println(nextLineOfFile + "     -----> there is no missing value");
		} catch (IllegalArgumentException e) {
			logger.log(Level.SEVERE,e.getMessage(), e);
		}

		int studentId = 0;
		try {
			studentId = Integer.parseInt(splitValues[0].trim());
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE,"studentId must be in integer format", e);
		}

		String studentName = splitValues[1].trim();
		try {
			if (!studentName.matches("[a-zA-Z]+ [a-zA-Z]+")) {
				throw new IllegalArgumentException("studentName should contain only characters and spaces");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE,e.getMessage(), e);
		}

		int studentAge = 0;
		try {
			studentAge = Integer.parseInt(splitValues[2].trim());
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE,"studentAge must be in integer format", e);
		}

		String studentGrade = splitValues[3].trim();
		try {
			if (studentGrade.length() != 1 || (!"ABCDF".contains(studentGrade.toUpperCase()))) {
				throw new IllegalArgumentException("studentGrade must be 1 character and only one of  ABCDF");
			}
		} catch (IllegalArgumentException e) {
			logger.log(Level.SEVERE,e.getMessage(), e);

		}
		Integer.toString(studentId);
		Integer.toString(studentAge);
		return splitValues;
	}

	public static Connection createConnection() {
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE,"mysql jdbc driver not found", e);
		}
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MentorshipDB", "Habiba", "1234");
		} catch (SQLException e) {
			logger.log(Level.SEVERE,"can't connect to database", e);
		}
		System.out.println("Connected successfully");
		return con;
	}

	public static void closeConnection() {
		try {
			if (con != null && !con.isClosed()) {
				con.close();
				System.out.println("connection closed");
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE,e.getMessage(), e);
		}
	}

	public static void insertDataWithPreparedStaement(String[] studentData) {
		int id = Integer.parseInt(studentData[0].trim());
		String name = studentData[1].trim();
		int age = Integer.parseInt(studentData[2].trim());
		String grade = studentData[3].trim();
		String sqlQuery = "INSERT INTO Students (id, name, age, grade) VALUES (?, ?, ?, ?)";
		try {
			PreparedStatement pstmt = con.prepareStatement(sqlQuery);
			pstmt.setInt(1, id);
			pstmt.setString(2, name);
			pstmt.setInt(3, age);
			pstmt.setString(4, grade);
			pstmt.executeUpdate();
			System.out.println("Record added successfully");
		} catch (SQLException e) {
			logger.log(Level.SEVERE,e.getMessage(), e);
		}
	}

	public static void queryAllStudentsWithSpecificGrade(String studentGrade) {
		try {
			String sqlQuery = "SELECT * FROM Students WHERE grade = ?";
			PreparedStatement pstmt = con.prepareStatement(sqlQuery);
			pstmt.setString(1, studentGrade);
			ResultSet result = pstmt.executeQuery();
			System.out.println("data:");
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				int age = result.getInt("age");
				String grade = result.getString("grade");
				System.out.println(id);
				System.out.println(name);
				System.out.println(age);
				System.out.println(grade);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE,e.getMessage(), e);
		}

	}
}
