package main.java.com.project.library.service;

import main.java.com.project.library.model.Borrowing;
import java.time.LocalDate;
import java.util.List;

public interface IBorrowingService {
    // Method untuk borrow book
    boolean borrowBook(int memberId, int bookId, LocalDate borrowDate, LocalDate returnDate);

    // Method untuk return book
    boolean returnBook(int borrowId);

    // Method untuk mark overdue
    boolean markOverdue(int borrowId);

    // Method untuk get all borrowings
    List<Borrowing> getAllBorrowings();

    // Method untuk search borrowings
    List<Borrowing> searchBorrowings(String keyword, String criteria);

    // Method untuk get borrowing by ID
    Borrowing getBorrowingById(int id);

    // Method untuk check if member exists
    boolean isMemberExist(int memberId);

    // Method untuk check if book exists
    boolean isBookExist(int bookId);
}