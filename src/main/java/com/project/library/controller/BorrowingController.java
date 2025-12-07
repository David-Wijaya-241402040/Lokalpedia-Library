package main.java.com.project.library.controller;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import main.java.com.project.library.service.IBorrowingService;
import main.java.com.project.library.service.BorrowingService;
import main.java.com.project.library.dao.MemberDAO;
import main.java.com.project.library.dao.BookDAO;
import main.java.com.project.library.model.Book;
import main.java.com.project.library.model.Borrowing;
import main.java.com.project.library.model.Member;

import java.time.LocalDate;
import java.util.List;

public class BorrowingController {

    @FXML private TextField searchArea;
    @FXML private TextField idMemberField;
    @FXML private TextField idBookField;
    @FXML private DatePicker dateBorrow;
    @FXML private DatePicker dateReturn;
    @FXML private Button btnRefresh;
    @FXML private Button btnSearch;
    @FXML private Button btnBackMain;
    @FXML private Button btnBorrow;
    @FXML private ChoiceBox<String> searchCriteria;

    @FXML private TableView<Borrowing> tableBorrows;
    @FXML private TableColumn<Borrowing, Integer> colMemberId;
    @FXML private TableColumn<Borrowing, Integer> colBookId;
    @FXML private TableColumn<Borrowing, String> colBorrowDate;
    @FXML private TableColumn<Borrowing, String> colReturnDate;
    @FXML private TableColumn<Borrowing, String> colStatus;

    @FXML private TableView<Member> tableMembers;
    @FXML private TableColumn<Member, Integer> miniMemberId;
    @FXML private TableColumn<Member, String> miniMemberName;

    @FXML private TableView<Book> tableBooks;
    @FXML private TableColumn<Book, Integer> miniBookId;
    @FXML private TableColumn<Book, String> miniBookTitle;
    @FXML private TableColumn<Book, Integer> miniBookStocks;

    private IBorrowingService borrowingService;
    private MemberDAO memberDAO = new MemberDAO();
    private BookDAO bookDAO = new BookDAO();

    private ObservableList<Borrowing> borrowList;
    private ObservableList<Member> memberList;
    private ObservableList<Book> bookList;

    @FXML
    public void initialize() {
        System.out.println("Borrowing Page Loaded!");

        borrowingService = new BorrowingService();

        searchCriteria.getItems().addAll("Member", "Book", "Status");
        setupBorrowTable();
        setupMemberMiniTable();
        setupBookMiniTable();
        loadBorrowData();
        loadMiniTables();

        tableMembers.setOnMouseClicked(ev -> {
            Member m = tableMembers.getSelectionModel().getSelectedItem();
            if (m != null) idMemberField.setText(String.valueOf(m.getId()));
        });
        tableBooks.setOnMouseClicked(ev -> {
            Book b = tableBooks.getSelectionModel().getSelectedItem();
            if (b != null) idBookField.setText(String.valueOf(b.getId()));
        });

        tableBorrows.setRowFactory(tv -> {
            TableRow<Borrowing> row = new TableRow<>();
            row.setOnContextMenuRequested(ev -> {
                if (!row.isEmpty()) {
                    ContextMenu contextMenu = new ContextMenu();

                    MenuItem returnItem = new MenuItem("Return");
                    returnItem.setOnAction(event -> handleReturnSelected(event));

                    MenuItem overdueItem = new MenuItem("Mark Overdue");
                    overdueItem.setOnAction(event -> handleMarkOverdue(event));

                    contextMenu.getItems().addAll(returnItem, overdueItem);
                    contextMenu.show(row, ev.getScreenX(), ev.getScreenY());
                }
            });
            return row;
        });
    }

    private Stage getStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    private void goToPage(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/main/resources/com/project/library/fxml/" + fxmlFile));
            Stage stage = getStage(event);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupBorrowTable() {
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupMemberMiniTable() {
        miniMemberId.setCellValueFactory(new PropertyValueFactory<>("id"));
        miniMemberName.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    private void setupBookMiniTable() {
        miniBookId.setCellValueFactory(new PropertyValueFactory<>("id"));
        miniBookTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        miniBookStocks.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    private void loadBorrowData() {
        List<Borrowing> list = borrowingService.getAllBorrowings();
        borrowList = FXCollections.observableArrayList(list);
        tableBorrows.setItems(borrowList);
    }

    private void loadMiniTables() {
        memberList = FXCollections.observableArrayList(memberDAO.getAll());
        tableMembers.setItems(memberList);

        bookList = FXCollections.observableArrayList(bookDAO.getAll());
        tableBooks.setItems(bookList);
    }

    public void handleBorrow(ActionEvent event) {
        try {
            String mem = idMemberField.getText();
            String bok = idBookField.getText();
            LocalDate bDate = dateBorrow.getValue();
            LocalDate rDate = dateReturn.getValue();

            if (mem == null || mem.isEmpty() || bok == null || bok.isEmpty() || bDate == null) {
                showAlert(Alert.AlertType.ERROR, "Input error", "Member, Book, and Borrow date are required.");
                return;
            }

            int memberId = Integer.parseInt(mem.trim());
            int bookId = Integer.parseInt(bok.trim());

            boolean ok = borrowingService.borrowBook(memberId, bookId, bDate, rDate);

            if (ok) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book borrowed successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Borrow failed. Maybe stock is 0 or data invalid.");
            }

            loadBorrowData();
            loadMiniTables();
            clearInputs();

        } catch (NumberFormatException nfe) {
            showAlert(Alert.AlertType.ERROR, "Input error", "Member ID and Book ID must be numbers.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong: " + e.getMessage());
        }
    }

    public void handleReturnSelected(ActionEvent event) {
        Borrowing sel = tableBorrows.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Select a borrow record first.");
            return;
        }
        if ("Returned".equalsIgnoreCase(sel.getStatus())) {
            showAlert(Alert.AlertType.INFORMATION, "Info", "This book already returned.");
            return;
        }

        boolean ok = borrowingService.returnBook(sel.getId());

        if (ok) {
            sel.setStatus("Returned");
            sel.setReturnDate(LocalDate.now().toString());
            showAlert(Alert.AlertType.INFORMATION, "Returned", "Book marked as returned.");
            loadBorrowData();
            loadMiniTables();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed", "Failed to return the book.");
        }
    }

    public void handleMarkOverdue(ActionEvent event) {
        Borrowing sel = tableBorrows.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Select a borrow record first.");
            return;
        }

        if (sel.getReturnDate() == null || sel.getReturnDate().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No return date", "Return date not set for this borrow.");
            return;
        }

        LocalDate returnDate = LocalDate.parse(sel.getReturnDate());
        LocalDate today = LocalDate.now();

        if (today.isAfter(returnDate) && !"Returned".equalsIgnoreCase(sel.getStatus())) {
            boolean ok = borrowingService.markOverdue(sel.getId());

            if (ok) {
                sel.setStatus("Overdue");
                showAlert(Alert.AlertType.INFORMATION, "Overdue", "Borrow marked as overdue.");
                loadBorrowData();
                loadMiniTables();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Failed to mark as overdue.");
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Info", "Book is not overdue yet or already returned.");
        }
    }

    public void handleSearch(ActionEvent event) {
        String key = searchArea.getText().trim();
        String criteria = searchCriteria.getValue();

        if (key.isEmpty() || criteria == null) {
            tableBorrows.setItems(borrowList);
            return;
        }

        List<Borrowing> filteredList = borrowingService.searchBorrowings(key, criteria);
        ObservableList<Borrowing> filtered = FXCollections.observableArrayList(filteredList);
        tableBorrows.setItems(filtered);
    }

    public void handleBackMain(ActionEvent event) {
        System.out.println("Back to main menu");
        goToPage(event, "mainmenu.fxml");
    }

    public void handleRefresh(ActionEvent event) {
        loadBorrowData();
        loadMiniTables();
        clearInputs();
    }

    private void clearInputs() {
        searchArea.clear();
        idBookField.clear();
        idMemberField.clear();
        dateBorrow.setValue(null);
        dateReturn.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}