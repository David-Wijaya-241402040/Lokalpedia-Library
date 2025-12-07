package main.java.com.project.library.dao;

import main.java.com.project.library.model.Borrowing;
import main.java.com.project.library.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDAO {

    // GET ALL
    public List<Borrowing> getAll() {
        List<Borrowing> list = new ArrayList<>();
        String sql = "SELECT * FROM borrowings ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Borrowing(
                        rs.getInt("id"),
                        rs.getInt("member_id"),
                        rs.getInt("book_id"),
                        rs.getString("borrow_date"),
                        rs.getString("return_date"),
                        rs.getString("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ADD BORROW (transactional) — decrement book stock
    public boolean add(Borrowing b) {
        String checkSql = "SELECT stock FROM books WHERE id = ?";
        String insertSql = "INSERT INTO borrowings (member_id, book_id, borrow_date, return_date, status) VALUES (?, ?, ?, ?, ?)";
        String updateStockSql = "UPDATE books SET stock = stock - 1 WHERE id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // check stock
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, b.getBookId());
                ResultSet rs = psCheck.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return false; // book not found
                }
                int stock = rs.getInt("stock");
                if (stock <= 0) {
                    conn.rollback();
                    return false; // no stock
                }
            }

            // insert borrow
            try (PreparedStatement psIns = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                psIns.setInt(1, b.getMemberId());
                psIns.setInt(2, b.getBookId());
                psIns.setDate(3, Date.valueOf(b.getBorrowDate()));
                if (b.getReturnDate() == null || b.getReturnDate().isEmpty()) {
                    psIns.setNull(4, Types.DATE);
                } else {
                    psIns.setDate(4, Date.valueOf(b.getReturnDate()));
                }
                psIns.setString(5, b.getStatus());
                psIns.executeUpdate();
            }

            // update stock
            try (PreparedStatement psUpd = conn.prepareStatement(updateStockSql)) {
                psUpd.setInt(1, b.getBookId());
                psUpd.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // RETURN BOOK — set status + return_date, increment stock
    public boolean returnBook(int borrowId, String returnDate) {
        String getSql = "SELECT book_id, status FROM borrowings WHERE id = ?";
        String updateBorrowSql = "UPDATE borrowings SET status = ?, return_date = ? WHERE id = ?";
        String updateStockSql = "UPDATE books SET stock = stock + 1 WHERE id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int bookId;
            String status;
            try (PreparedStatement psGet = conn.prepareStatement(getSql)) {
                psGet.setInt(1, borrowId);
                ResultSet rs = psGet.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }
                bookId = rs.getInt("book_id");
                status = rs.getString("status");
                if ("Returned".equalsIgnoreCase(status)) {
                    conn.rollback();
                    return false; // already returned
                }
            }

            try (PreparedStatement psUpdateBorrow = conn.prepareStatement(updateBorrowSql)) {
                psUpdateBorrow.setString(1, "Returned");
                psUpdateBorrow.setDate(2, Date.valueOf(returnDate));
                psUpdateBorrow.setInt(3, borrowId);
                psUpdateBorrow.executeUpdate();
            }

            try (PreparedStatement psUpdStock = conn.prepareStatement(updateStockSql)) {
                psUpdStock.setInt(1, bookId);
                psUpdStock.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // BorrowingDAO.java
    public boolean updateStatus(int borrowId, String status) {
        String sql = "UPDATE borrowings SET status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, borrowId);

            int updated = ps.executeUpdate();
            return updated > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
