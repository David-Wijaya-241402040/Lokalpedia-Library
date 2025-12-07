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
import main.java.com.project.library.dao.BookDAO;
import main.java.com.project.library.model.Book;

public class BookController {
    @FXML private TextField titleArea;
    @FXML private TextField authorArea;
    @FXML private TextField stocksArea;
    @FXML private TextField searchArea;

    @FXML private Button btnSearch;
    @FXML private Button btnRefresh;
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnOpenUpdate;
    @FXML private Button btnBackMain;
    @FXML private Button btnDelete;

    @FXML private TableView<Book> tableBooks;
    @FXML private TableColumn<Book, Integer> colId;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, Integer> colStocks;

    private BookDAO bookDAO = new BookDAO();
    private ObservableList<Book> bookList;
    private boolean isOpenUpdate = false;

    @FXML public void initialize() {
        System.out.println("Book page loaded!");
        if (btnUpdate != null) btnUpdate.setVisible(false);
        if (btnAdd != null) btnAdd.setVisible(true);
        setupTable();
        loadData();
        tableBooks.setOnMouseClicked(event -> {
            if(!isOpenUpdate) return;

            Book b = tableBooks.getSelectionModel().getSelectedItem();
            if (b != null) {
                titleArea.setText(b.getTitle());
                authorArea.setText(b.getAuthor());
                stocksArea.setText(String.valueOf(b.getStock()));

                // Biar user tahu ini update mode
                btnAdd.setVisible(false);
                btnUpdate.setVisible(true);
            }
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

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colStocks.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    private void loadData() {
        bookList = FXCollections.observableArrayList(bookDAO.getAll());
        tableBooks.setItems(bookList);
    }

    public void handleBack(ActionEvent event) {
        System.out.println("Back to main menu");
        goToPage(event, "mainmenu.fxml");
    }

    public void handleAdd(ActionEvent event) {
        try {
            String title = titleArea.getText();
            String author = authorArea.getText();
            int stocks = Integer.parseInt(stocksArea.getText());

            Book book = new Book(0, title, author, stocks);
            bookDAO.insert(book);

            clearInput();
            loadData();
            System.out.println("Book added!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleShowUpdateMode(ActionEvent event) {
        System.out.println("Switch to update mode");
        if (btnAdd != null) btnAdd.setVisible(false);
        if (btnUpdate != null) btnUpdate.setVisible(true);

        isOpenUpdate = true;
    }

    public void handleRealUpdate(ActionEvent event) {
        Book selected = tableBooks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("No row selected!");
            return;
        }

        try {
            selected.setTitle(titleArea.getText());
            selected.setAuthor(authorArea.getText());
            selected.setStock(Integer.parseInt(stocksArea.getText()));

            bookDAO.update(selected);

            clearInput();
            loadData();

            // kembali ke mode add
            btnAdd.setVisible(true);
            btnUpdate.setVisible(false);
            isOpenUpdate = false;

            System.out.println("Book updated!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleDelete(ActionEvent event) {
        Book selected = tableBooks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("No row selected!");
            return;
        }

        try {
            bookDAO.delete(selected.getId());
            clearInput();
            loadData();
            System.out.println("Book deleted!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleRefresh(ActionEvent event) {
        loadData();
        clearInput();
        btnAdd.setVisible(true);
        btnUpdate.setVisible(false);
        System.out.println("Refreshed!");
    }

    public void handleSearch(ActionEvent event) {
        String keyword = searchArea.getText().toLowerCase();

        ObservableList<Book> filtered = bookList.filtered(b ->
                b.getTitle().toLowerCase().contains(keyword) ||
                        b.getAuthor().toLowerCase().contains(keyword)
        );

        tableBooks.setItems(filtered);

        System.out.println("Search: " + keyword);
    }

    private void clearInput() {
        titleArea.clear();
        authorArea.clear();
        stocksArea.clear();
    }

}
