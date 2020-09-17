package zad1;

import org.apache.derby.jdbc.EmbeddedDriver;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class Main extends HttpServlet {
    public static String urlDb = "jdbc:derby:random"; // EDIT WITH YOUR DB
    private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().print("<html><head><title>Book finder</title></head><body>" + "put your data:" +
                "<form action=\"http://localhost:8080/BookWebApp/main\" method=\"post\"><br>" +
                "    <input type=\"text\" name=\"author\" placeholder=\"Author / autory\"><br>" +
                "    <input type=\"text\" name=\"title\" placeholder=\"Title\"><br>" +
                "    <input type=\"text\" name=\"publisher\" placeholder=\"Publisher\"><br>" +
                "    <input type=\"date\" name=\"year\" placeholder=\"Year\"><br>" +
                "    <input type=\"submit\" value=\"Find book\" name=\"submit\"><br>" +
                "</form></body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws  IOException {
        PrintWriter writer = response.getWriter();

        writer.print("<html><head><title>Search Book</title></head><body><form>" +
                "<input type=\"button\" value=\"Back\" onclick=\"history.back()\">" +
                "</form>" + retrieve(request.getParameter("author"),
                request.getParameter("title"),
                request.getParameter("publisher"),
                request.getParameter("year")) + "</body></html>");
    }

    protected String retrieve(String author, String title, String publisher, String year) {
        java.sql.Connection connection;
        Statement statement;
        List<String> books = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT ISBN, AUTHOR.NAME, TITLE, PUBLISHER.NAME, YEAR, COST FROM BOOK " +
                "JOIN AUTHOR ON BOOK.AUTID = AUTHOR.AUTID JOIN PUBLISHER ON PUBLISHER.PUBID = BOOK.PUBID WHERE 1=1 ");
        if (!year.isEmpty()) query.append("AND YEAR = ").append(year).append(" ");
        if (!title.isEmpty()) query.append("AND TITLE = '").append(title).append("' ");
        if (!author.isEmpty()) query.append("AND AUTHOR.name = '").append(author).append("' ");
        if (!publisher.isEmpty()) query.append("AND PUBLISHER.name = '").append(publisher).append("' ");
        try {
            String format = "%-20s %-35s %-50s %-30s %-20s %-20s";
            DriverManager.registerDriver(new EmbeddedDriver());
            connection = DriverManager.getConnection(Main.urlDb);
            statement = connection.createStatement();
            ResultSet resStruct = statement.executeQuery(query.toString());
            books.add(String.format(format, "ISBN", "Author", "Title", "Publisher", "Year", "Cost"));
            while (resStruct.next()) {
                String book = String.format(format, resStruct.getString(1), resStruct.getString(2), resStruct.getString(3),
                        resStruct.getString(4), resStruct.getString(5), resStruct.getString(6));
                System.out.println(book);
                books.add(book);
            }
            resStruct.close();
        } catch (SQLException ignored) {}
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while (i < books.size()) {
            if (i == 0) {
                stringBuilder.append("<div>").append(books.get(i)).append("</div>");
                i++;
                continue;
            }
            stringBuilder.append("<div>").append(books.get(i)).append("</div>");
            i++;
        }
        return stringBuilder.toString();
    }
}
