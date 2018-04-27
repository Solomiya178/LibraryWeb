package com.hryshko.controller;

import com.hryshko.dao.BookDao;
import com.hryshko.model.Book;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Controller extends HttpServlet {

	private static final String BOOK_ATTR = "book";
	private static final String BOOKS_ATTR = "books";

	private static final String ID_PARAM = "id";
	private static final String AUTHOR_PARAM = "author";
	private static final String TITLE_PARAM = "title";

	private static final String BOOKS_JSP = "books.jsp";
	private static final String BOOK_FORM_JSP = "book-form.jsp";

	private static final String LIST_VIEW = "list";

	private BookDao bookDao;

	@Override
	public void init() {
		String jdbcURL = getServletContext().getInitParameter("jdbcURL");
		String jdbcUsername = getServletContext().getInitParameter("jdbcUsername");
		String jdbcPassword = getServletContext().getInitParameter("jdbcPassword");
		bookDao = new BookDao(jdbcURL, jdbcUsername, jdbcPassword);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			switch (request.getServletPath()) {
				case "/new":
					createBook(request, response);
					break;
				case "/insert":
					insertBook(request, response);
					break;
				case "/delete":
					deleteBook(request, response);
					break;
				case "/edit":
					editBook(request, response);
					break;
				case "/update":
					updateBook(request, response);
					break;
				default:
					listBooks(request, response);
					break;
			}
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}

	private void listBooks(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
		List<Book> books = bookDao.findAll();
		request.setAttribute(BOOKS_ATTR, books);
		RequestDispatcher dispatcher = request.getRequestDispatcher(BOOKS_JSP);
		dispatcher.forward(request, response);
	}

	private void createBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(BOOK_FORM_JSP);
		dispatcher.forward(request, response);
	}

	private void editBook(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
		Long id = Long.parseLong(request.getParameter(ID_PARAM));
		Book existingBook = bookDao.find(id);
		RequestDispatcher dispatcher = request.getRequestDispatcher(BOOK_FORM_JSP);
		request.setAttribute(BOOK_ATTR, existingBook);
		dispatcher.forward(request, response);
	}

	private void insertBook(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		String title = request.getParameter(TITLE_PARAM);
		String author = request.getParameter(AUTHOR_PARAM);
		Book newBook = new Book(title, author);
		bookDao.create(newBook);
		response.sendRedirect(LIST_VIEW);
	}

	private void updateBook(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		Long id = Long.parseLong(request.getParameter(ID_PARAM));
		String title = request.getParameter(TITLE_PARAM);
		String author = request.getParameter(AUTHOR_PARAM);
		Book book = new Book(id, title, author);
		bookDao.update(book);
		response.sendRedirect(LIST_VIEW);
	}

	private void deleteBook(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		Long id = Long.parseLong(request.getParameter(ID_PARAM));
		Book book = new Book(id);
		bookDao.delete(book);
		response.sendRedirect(LIST_VIEW);
	}
}