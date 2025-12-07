package main.java.com.project.library.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.com.project.library.dao.BookDAO;
import main.java.com.project.library.dao.BorrowingDAO;
import main.java.com.project.library.dao.MemberDAO;
import main.java.com.project.library.model.Book;
import main.java.com.project.library.model.Borrowing;
import main.java.com.project.library.model.Member;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class ReportController {

    @FXML private ComboBox<String> comboJLaporan;
    @FXML private TextField searchArea;
    @FXML private TextField totalBukuField;
    @FXML private TextField totalMemberField;
    @FXML private TextField totalBorrowField;
    @FXML private TextField totalBorrowActiveField;
    @FXML private TextField totalBorrowOverdueField;
    @FXML private ChoiceBox<String> choiceSearch;

    @FXML private TableView<Object> tableReports;

    @FXML private Button btnShow;
    @FXML private Button btnExport;
    @FXML private Button btnBackMain;
    @FXML private Button btnSearch;
    @FXML private Button btnRefresh;

    private ObservableList<Object> currentList = FXCollections.observableArrayList();

    private MemberDAO memberDAO = new MemberDAO();
    private BookDAO bookDAO = new BookDAO();
    private BorrowingDAO borrowingDAO = new BorrowingDAO();
    private boolean isBorrowingTable = false;

    @FXML
    public void initialize() {
        comboJLaporan.setItems(FXCollections.observableArrayList("Member", "Book", "Borrowing"));
        comboJLaporan.getSelectionModel().selectFirst();
        choiceSearch.setVisible(false);
        choiceSearch.getItems().addAll("Member", "Book", "Status");
        setupStats();
    }

    private void setupStats() {
        totalMemberField.setText(String.valueOf(memberDAO.getAll().size()));
        totalBukuField.setText(String.valueOf(bookDAO.getAll().size()));
        totalBorrowField.setText(String.valueOf(borrowingDAO.getAll().size()));

        long active = borrowingDAO.getAll().stream().filter(b -> "Borrowed".equalsIgnoreCase(b.getStatus())).count();
        long overdue = borrowingDAO.getAll().stream().filter(b -> "Overdue".equalsIgnoreCase(b.getStatus())).count();

        totalBorrowActiveField.setText(String.valueOf(active));
        totalBorrowOverdueField.setText(String.valueOf(overdue));

        totalMemberField.setEditable(false);
        totalBukuField.setEditable(false);
        totalBorrowField.setEditable(false);
        totalBorrowActiveField.setEditable(false);
        totalBorrowOverdueField.setEditable(false);
    }

    @FXML
    private void handleShow(ActionEvent event) {
        String reportType = comboJLaporan.getValue();
        if (reportType == null) return;

        tableReports.getColumns().clear(); // reset columns
        currentList.clear();

        switch (reportType) {
            case "Member":
                choiceSearch.setVisible(false);
                isBorrowingTable = false;
                TableColumn<Object, Object> colId = new TableColumn<>("ID");
                colId.setCellValueFactory(data -> new SimpleObjectProperty<>(((Member)data.getValue()).getId()));

                TableColumn<Object, Object> colName = new TableColumn<>("Name");
                colName.setCellValueFactory(data -> new SimpleObjectProperty<>(((Member)data.getValue()).getName()));

                tableReports.getColumns().addAll(colId, colName);

                currentList.addAll(memberDAO.getAll()); // ganti sesuai DAO
                break;

            case "Book":
                choiceSearch.setVisible(false);
                isBorrowingTable = false;
                TableColumn<Object, Object> colBookId = new TableColumn<>("ID");
                colBookId.setCellValueFactory(data -> new SimpleObjectProperty<>(((Book)data.getValue()).getId()));

                TableColumn<Object, Object> colTitle = new TableColumn<>("Title");
                colTitle.setCellValueFactory(data -> new SimpleObjectProperty<>(((Book)data.getValue()).getTitle()));

                TableColumn<Object, Object> colStock = new TableColumn<>("Stock");
                colStock.setCellValueFactory(data -> new SimpleObjectProperty<>(((Book)data.getValue()).getStock()));

                tableReports.getColumns().addAll(colBookId, colTitle, colStock);

                currentList.addAll(bookDAO.getAll());
                break;

            case "Borrowing":
                choiceSearch.setVisible(true);
                isBorrowingTable = true;
                TableColumn<Object, Object> colBorrowId = new TableColumn<>("ID");
                colBorrowId.setCellValueFactory(data -> new SimpleObjectProperty<>(((Borrowing)data.getValue()).getId()));

                TableColumn<Object, Object> colMemberId = new TableColumn<>("Member ID");
                colMemberId.setCellValueFactory(data -> new SimpleObjectProperty<>(((Borrowing)data.getValue()).getMemberId()));

                TableColumn<Object, Object> colBookIdBorrow = new TableColumn<>("Book ID");
                colBookIdBorrow.setCellValueFactory(data -> new SimpleObjectProperty<>(((Borrowing)data.getValue()).getBookId()));

                TableColumn<Object, Object> colBorrowDate = new TableColumn<>("Borrow Date");
                colBorrowDate.setCellValueFactory(data -> new SimpleObjectProperty<>(((Borrowing)data.getValue()).getBorrowDate()));

                TableColumn<Object, Object> colReturnDate = new TableColumn<>("Return Date");
                colReturnDate.setCellValueFactory(data -> new SimpleObjectProperty<>(((Borrowing)data.getValue()).getReturnDate()));

                TableColumn<Object, Object> colStatus = new TableColumn<>("Status");
                colStatus.setCellValueFactory(data -> new SimpleObjectProperty<>(((Borrowing)data.getValue()).getStatus()));

                tableReports.getColumns().addAll(colBorrowId, colMemberId, colBookIdBorrow, colBorrowDate, colReturnDate, colStatus);

                currentList.addAll(borrowingDAO.getAll());
                break;
        }

        tableReports.setItems(currentList);
        updateStatistics(); // method ambil total member, buku, borrow dsb
    }

    private void updateStatistics() {
        MemberDAO memberDAO = new MemberDAO();
        BookDAO bookDAO = new BookDAO();
        BorrowingDAO borrowingDAO = new BorrowingDAO();

        totalMemberField.setText(String.valueOf(memberDAO.getAll().size()));
        totalBukuField.setText(String.valueOf(bookDAO.getAll().size()));

        List<Borrowing> borrows = borrowingDAO.getAll();
        totalBorrowField.setText(String.valueOf(borrows.size()));
        totalBorrowActiveField.setText(String.valueOf(
                borrows.stream().filter(b -> "Borrowed".equalsIgnoreCase(b.getStatus())).count()
        ));
        totalBorrowOverdueField.setText(String.valueOf(
                borrows.stream().filter(b -> "Overdue".equalsIgnoreCase(b.getStatus())).count()
        ));
    }

    @FXML
    private void handleExport(ActionEvent event) {
        if(currentList.isEmpty()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File file = fileChooser.showSaveDialog(tableReports.getScene().getWindow());
        if(file == null) return;

        try(FileWriter fw = new FileWriter(file)) {
            // write header
            for(TableColumn<Object, ?> col : tableReports.getColumns()) {
                fw.append(col.getText()).append(",");
            }
            fw.append("\n");

            // write data
            for(Object obj : currentList) {
                for(TableColumn<Object, ?> col : tableReports.getColumns()) {
                    Object val = col.getCellData(obj);
                    fw.append(val != null ? val.toString() : "").append(",");
                }
                fw.append("\n");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void handleRefresh(ActionEvent event) {
        comboJLaporan.setValue(null);
        tableReports.getColumns().clear();
        currentList.clear();
        searchArea.clear();
        setupStats();
    }

    @FXML private void handleSearch(ActionEvent event) {
        String key = (searchArea != null) ? searchArea.getText().trim() : "";
        String criteria = choiceSearch.getValue();

        if(key.isEmpty() || isBorrowingTable == true && (criteria == null || key.isEmpty())) {
            tableReports.setItems(currentList);
            return;
        }

        ObservableList<Object> filtered = currentList.filtered(obj -> {
            if(obj instanceof Member m) {
                return String.valueOf(m.getId()).contains(key) || m.getName().contains(key);
            } else if(obj instanceof Book b) {
                return String.valueOf(b.getId()).contains(key) || b.getTitle().contains(key);
            } else if(obj instanceof Borrowing br) {
                switch(criteria) {
                    case "Member":
                        return String.valueOf(br.getMemberId()).contains(key);
                    case "Book":
                        return String.valueOf(br.getBookId()).contains(key);
                    case "Status":
                        return br.getStatus() != null && br.getStatus().toLowerCase().contains(key.toLowerCase());
                    default:
                        return false;
                }
            }
            return false;
        });

        tableReports.setItems(filtered);
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

    public void handleBack(ActionEvent event) {
        System.out.println("Back to main menu");
        goToPage(event, "mainmenu.fxml");
    }
}
