package uz.proger.books;

public class Book {

  private long isbn;
  private String name;

  public Book() {
  }

  public Book(long isbn, String name) {
    this.isbn = isbn;
    this.name = name;
  }

  public long getIsbn() {
    return isbn;
  }

  public void setIsbn(long isbn) {
    this.isbn = isbn;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
