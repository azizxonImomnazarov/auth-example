package uz.proger.books;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class BooksStore {
  private final Map<Long, Book> bookStore = new HashMap<>();

  public BooksStore() {
    bookStore.put(1L, new Book(1L, "VERTX IN ACTION"));
    bookStore.put(2L, new Book(2L, "micro serveicses"));
    bookStore.put(3L, new Book(3L, "clean code"));
  }

  public JsonArray getAllBooks() {
    JsonArray jsonArray = new JsonArray();
    bookStore.values().forEach(book -> jsonArray.add(JsonObject.mapFrom(book)));
    return jsonArray;
  }

  public void add(Book book) {
    bookStore.put(book.getIsbn(), book);
  }

  public Book update(String isbn, Book book) {
    final long bookIsbn = Long.parseLong(isbn);
    if (bookIsbn != book.getIsbn()) {
      throw new IllegalArgumentException("path isbn is not valid to book isbn");
    }
    bookStore.put(bookIsbn, book);
    return book;
  }

  public Book getBookByIsbn(String isbn) {
    final long bookIsbn = Long.parseLong(isbn);
    final Book book = bookStore.get(bookIsbn);
    if (book == null) {
      throw new IllegalArgumentException("Threre is't book with this isbn");
    } else {
      return book;
    }
  }

  public Book delete(String isbn, Book book) {
    final long bookIsbn = Long.parseLong(isbn);
    if (bookIsbn != book.getIsbn()) {
      throw new IllegalArgumentException("path isbn is not valid to book isbn");
    }
    bookStore.remove(bookIsbn);
    return book;
  }
}
