package main.java.com.project.library.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.java.com.project.library.Main;
import main.java.com.project.library.dao.BookDAO;
import main.java.com.project.library.model.Book;

public class GuestHomeController {
    @FXML private TextField searchArea;

    @FXML private Button btnBack;
    @FXML private Button btnRefresh;
    @FXML private Button btnSearch;

    @FXML private TableView<Book> tableBooks;
    @FXML private TableColumn<Book, Integer> colId;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, Integer> colStocks;

    private BookDAO bookDAO = new BookDAO();
    private ObservableList<Book> bookList;

    @FXML public void initialize() {
        System.out.println("Book page loaded!");
        setupTable();
        loadData();
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
        Main.setRoot("loginguest");
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

    public void handleRefresh(ActionEvent event) {
        loadData();
        clearInput();
        System.out.println("Refreshed!");
    }

    private void clearInput() {
        searchArea.clear();
    }
}
