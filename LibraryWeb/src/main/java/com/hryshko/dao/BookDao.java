package com.hryshko.dao;

import com.hryshko.model.Book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookDao {
	
	private static final String FIND_ALL = "SELECT * FROM books";
	private static final String FIND_BY_ID = "SELECT * FROM books WHERE book_id = ?";
	private static final String CREATE = "INSERT INTO books (title, author) VALUES (?, ?)";
	private static final String UPDATE = "UPDATE books SET title = ?, author = ? WHERE book_id = ?";
	private static final String DELETE = "DELETE FROM books where book_id = ?";

	private static final String BOOK_ID = "book_id";
	private static final String TITLE = "title";
	private static final String AUTHOR = "author";
	private static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";

	private String jdbcURL;
	private String jdbcUsername;
	private String jdbcPassword;
	private Connection jdbcConnection;

	public BookDao(String jdbcURL, String jdbcUsername, String jdbcPassword) {
		this.jdbcURL = jdbcURL;
		this.jdbcUsername = jdbcUsername;
		this.jdbcPassword = jdbcPassword;
	}

	public List<Book> findAll() throws SQLException {
		connect();
		Statement statement = jdbcConnection.createStatement();
		ResultSet resultSet = statement.executeQuery(FIND_ALL);
		List<Book> books = new ArrayList<>();
		while (resultSet.next()) {
			Long id = resultSet.getLong(BOOK_ID);
			String title = resultSet.getString(TITLE);
			String author = resultSet.getString(AUTHOR);
			Book book = new Book(id, title, author);
			books.add(book);
		}
		resultSet.close();
		statement.close();
		disconnect();
		return books;
	}

	public Book find(Long id) throws SQLException {
		connect();
		PreparedStatement statement = jdbcConnection.prepareStatement(FIND_BY_ID);
		statement.setLong(1, id);
		ResultSet resultSet = statement.executeQuery();
		Book book = null;
		if (resultSet.next()) {
			String title = resultSet.getString(TITLE);
			String author = resultSet.getString(AUTHOR);
			book = new Book(id, title, author);
		}
		resultSet.close();
		statement.close();
		disconnect();
		return book;
	}

	public boolean create(Book book) throws SQLException {
		connect();
		PreparedStatement statement = jdbcConnection.prepareStatement(CREATE);
		statement.setString(1, book.getTitle());
		statement.setString(2, book.getAuthor());
		boolean rowInserted = statement.executeUpdate() > 0;
		statement.close();
		disconnect();
		return rowInserted;
	}

	public boolean delete(Book book) throws SQLException {
		connect();
		PreparedStatement statement = jdbcConnection.prepareStatement(DELETE);
		statement.setLong(1, book.getId());
		boolean rowDeleted = statement.executeUpdate() > 0;
		statement.close();
		disconnect();
		return rowDeleted;
	}

	public boolean update(Book book) throws SQLException {
		connect();
		PreparedStatement statement = jdbcConnection.prepareStatement(UPDATE);
		statement.setString(1, book.getTitle());
		statement.setString(2, book.getAuthor());
		statement.setLong(3, book.getId());
		boolean rowUpdated = statement.executeUpdate() > 0;
		statement.close();
		disconnect();
		return rowUpdated;
	}

	private void connect() throws SQLException {
		if (jdbcConnection == null || jdbcConnection.isClosed()) {
			try {
				Class.forName(POSTGRESQL_DRIVER);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("PostgreSQL JDBC Driver cannot be found", e);
			}
			jdbcConnection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
		}
	}

	private void disconnect() throws SQLException {
		if (jdbcConnection != null && !jdbcConnection.isClosed()) {
			jdbcConnection.close();
		}
	}
}