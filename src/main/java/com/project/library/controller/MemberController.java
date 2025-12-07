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
import main.java.com.project.library.dao.MemberDAO;
import main.java.com.project.library.model.Member;

public class MemberController {

    @FXML private TextField searchArea;
    @FXML private TextField nameArea;
    @FXML private TextField emailArea;
    @FXML private TextField phoneArea;

    @FXML private Button btnRefresh;
    @FXML private Button btnOpenUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnSearch;
    @FXML private Button btnBackMenu;
    @FXML private Button btnUpdate;
    @FXML private Button btnAdd;

    @FXML private TableView<Member> tableMembers;
    @FXML private TableColumn<Member, Integer> colId;
    @FXML private TableColumn<Member, String> colMember;
    @FXML private TableColumn<Member, String> colEmail;
    @FXML private TableColumn<Member, String> colPhone;

    private MemberDAO memberDAO = new MemberDAO();
    private ObservableList<Member> memberList;
    private boolean isOpenUpdate = false;

    @FXML
    public void initialize() {
        System.out.println("Member page loaded!");

        btnUpdate.setVisible(false);
        btnAdd.setVisible(true);

        setupTable();
        loadData();

        // AUTO-FILL INPUT SAAT KLIK TABEL
        tableMembers.setOnMouseClicked(event -> {
            if(!isOpenUpdate) return;

            Member m = tableMembers.getSelectionModel().getSelectedItem();
            if (m != null) {
                nameArea.setText(m.getName());
                emailArea.setText(m.getEmail());
                phoneArea.setText(m.getPhone());

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
        colMember.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    private void loadData() {
        memberList = FXCollections.observableArrayList(memberDAO.getAll());
        tableMembers.setItems(memberList);
    }

    // ============================
    // CRUD HANDLERS
    // ============================

    public void handleAdd(ActionEvent event) {
        try {
            String name = nameArea.getText();
            String email = emailArea.getText();
            String phone = phoneArea.getText();

            Member m = new Member(0, name, email, phone);
            memberDAO.add(m);

            clearInput();
            loadData();

            System.out.println("Member added!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleOpenUpdate(ActionEvent event) {
        System.out.println("Update mode active");
        btnAdd.setVisible(false);
        btnUpdate.setVisible(true);

        isOpenUpdate = true;
    }

    public void handleUpdate(ActionEvent event) {
        Member selected = tableMembers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("No row selected!");
            return;
        }

        try {
            selected.setName(nameArea.getText());
            selected.setEmail(emailArea.getText());
            selected.setPhone(phoneArea.getText());

            memberDAO.update(selected);

            clearInput();
            loadData();

            btnAdd.setVisible(true);
            btnUpdate.setVisible(false);
            isOpenUpdate = false;

            System.out.println("Member updated!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleDelete(ActionEvent event) {
        Member selected = tableMembers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("No row selected!");
            return;
        }

        try {
            memberDAO.delete(selected.getId());
            clearInput();
            loadData();

            System.out.println("Member deleted!");
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

        ObservableList<Member> filtered = memberList.filtered(m ->
                m.getName().toLowerCase().contains(keyword) ||
                        m.getEmail().toLowerCase().contains(keyword) ||
                        m.getPhone().toLowerCase().contains(keyword)
        );

        tableMembers.setItems(filtered);

        System.out.println("Search: " + keyword);
    }

    // ============================

    private void clearInput() {
        nameArea.clear();
        emailArea.clear();
        phoneArea.clear();
        searchArea.clear();
    }

    public void handleBackMenu(ActionEvent event) {
        System.out.println("Back to main menu");
        goToPage(event, "mainmenu.fxml");
    }
}

