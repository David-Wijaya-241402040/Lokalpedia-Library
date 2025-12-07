package main.java.com.project.library.service;

import main.java.com.project.library.dao.BorrowingDAO;
import main.java.com.project.library.dao.BookDAO;
import main.java.com.project.library.dao.MemberDAO;
import main.java.com.project.library.model.Borrowing;
import main.java.com.project.library.model.Book;
import main.java.com.project.library.model.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowingService implements IBorrowingService {
    private BorrowingDAO borrowingDAO;
    private BookDAO bookDAO;
    private MemberDAO memberDAO;

    public BorrowingService() {
        this.borrowingDAO = new BorrowingDAO();
        this.bookDAO = new BookDAO();
        this.memberDAO = new MemberDAO();
    }

    @Override
    public boolean borrowBook(int memberId, int bookId, LocalDate borrowDate, LocalDate returnDate) {
        try {
            System.out.println("[Service] Borrowing: Member=" + memberId + ", Book=" + bookId);

            // Cek apakah member dan book ada
            if (!isMemberExist(memberId)) {
                System.out.println("[Service] Member tidak ditemukan: " + memberId);
                return false;
            }

            if (!isBookExist(bookId)) {
                System.out.println("[Service] Book tidak ditemukan: " + bookId);
                return false;
            }

            // Cek stok buku
            if (!isBookAvailable(bookId)) {
                System.out.println("[Service] Book tidak tersedia: " + bookId);
                return false;
            }

            String borrowDateStr = borrowDate.toString();
            String returnDateStr = (returnDate == null) ? null : returnDate.toString();

            Borrowing borrowing = new Borrowing(memberId, bookId, borrowDateStr, returnDateStr, "Borrowed");

            return borrowingDAO.add(borrowing);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean returnBook(int borrowId) {
        try {
            System.out.println("[Service] Returning: Borrow ID=" + borrowId);
            String today = LocalDate.now().toString();
            return borrowingDAO.returnBook(borrowId, today);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean markOverdue(int borrowId) {
        try {
            System.out.println("[Service] Marking overdue: Borrow ID=" + borrowId);
            return borrowingDAO.updateStatus(borrowId, "Overdue");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Borrowing> getAllBorrowings() {
        try {
            return borrowingDAO.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Borrowing> searchBorrowings(String keyword, String criteria) {
        try {
            if (keyword == null || keyword.trim().isEmpty() || criteria == null) {
                return getAllBorrowings();
            }

            List<Borrowing> all = borrowingDAO.getAll();
            List<Borrowing> result = new ArrayList<>();
            String key = keyword.trim().toLowerCase();

            for (Borrowing b : all) {
                boolean match = false;

                if ("Member".equals(criteria)) {
                    match = String.valueOf(b.getMemberId()).contains(key);
                } else if ("Book".equals(criteria)) {
                    match = String.valueOf(b.getBookId()).contains(key);
                } else if ("Status".equals(criteria)) {
                    match = b.getStatus() != null && b.getStatus().toLowerCase().contains(key);
                }

                if (match) {
                    result.add(b);
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Borrowing getBorrowingById(int id) {
        List<Borrowing> all = getAllBorrowings();
        for (Borrowing b : all) {
            if (b.getId() == id) {
                return b;
            }
        }
        return null;
    }

    @Override
    public boolean isMemberExist(int memberId) {
        try {
            List<Member> allMembers = memberDAO.getAll();
            for (Member member : allMembers) {
                if (member.getId() == memberId) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isBookExist(int bookId) {
        try {
            List<Book> allBooks = bookDAO.getAll();
            for (Book book : allBooks) {
                if (book.getId() == bookId) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method untuk cek ketersediaan buku
    private boolean isBookAvailable(int bookId) {
        try {
            List<Book> allBooks = bookDAO.getAll();
            for (Book book : allBooks) {
                if (book.getId() == bookId) {
                    return book.getStock() > 0;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Debugging Method untuk Cek Buku dan Members
    public void printAllBooks() {
        try {
            List<Book> books = bookDAO.getAll();
            System.out.println("[Service] Daftar buku:");
            for (Book b : books) {
                System.out.println("  ID: " + b.getId() + ", Title: " + b.getTitle() + ", Stock: " + b.getStock());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printAllMembers() {
        try {
            List<Member> members = memberDAO.getAll();
            System.out.println("[Service] Daftar member:");
            for (Member m : members) {
                System.out.println("  ID: " + m.getId() + ", Name: " + m.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}