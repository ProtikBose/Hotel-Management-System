/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class StayingGuestInfoController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<stayGuest> table;

    @FXML
    private TableColumn<stayGuest, String> column1;

    @FXML
    private TableColumn<stayGuest, String> column2;

    @FXML
    private TextField col1;

    @FXML
    private TextField col2;

    @FXML
    private TextField col3;

    @FXML
    private TextField col4;

    @FXML
    private TextField idtext;

    @FXML
    void backtorecept(ActionEvent event) throws IOException {
        Parent root1 = FXMLLoader.load(getClass().getResource("receptionist.fxml"));

        Scene scene1 = new Scene(root1);
        Stage stage1 = new Stage();

        stage1.setScene(scene1);
        stage1.show();
    }

    public void getRoom() throws ClassNotFoundException, SQLException {
        String sql = "SELECT ROOM.ROOM_ID\n"
                + "from ROOM,RESERVATION,GUEST\n"
                + "WHERE RESERVATION.GUEST_ID=GUEST.GUEST_ID\n"
                + "AND RESERVATION.ROOM_ID=ROOM.ROOM_ID\n"
                + "AND (TO_DATE(RESERVATION.CHECKIN_DATE,'YYYY-MM-DD')<= TO_DATE(TO_CHAR(SYSDATE,'YYYY-MM-DD'),'YYYY-MM-DD')) \n"
                + " AND(TO_DATE(RESERVATION.CHECKOUT_DATE,'YYYY-MM-DD')>=TO_DATE(TO_CHAR(SYSDATE,'YYYY-MM-DD'),'YYYY-MM-DD'))\n"
                + "AND GUEST.GUEST_ID= ?";
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection con = new OracleDBMS().getConnection();
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, idtext.getText());
        ResultSet rs = pst.executeQuery();
        String save;
        save = "";
        while (rs.next()) {
            save = save + rs.getString(1) + ",";
        }
        col4.setText(save);
        pst.close();
        con.close();
        
        
    }

    @FXML
    void searchGuest(ActionEvent event) throws ClassNotFoundException, SQLException {

        String sql = "SELECT NAME,GUEST_ID,EMAIL,PHONE\n"
                + "FROM GUEST WHERE GUEST_ID=?";
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection con = new OracleDBMS().getConnection();
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, idtext.getText());
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            col1.setText(rs.getString(1));
            col2.setText(rs.getString(3));
            col3.setText(rs.getString(4));
        }

        pst.close();
        con.close();

        getRoom();

    }

    @FXML
    void initialize() throws ClassNotFoundException, SQLException {
        assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'stayingGuestInfo.fxml'.";
        assert column1 != null : "fx:id=\"column1\" was not injected: check your FXML file 'stayingGuestInfo.fxml'.";
        assert column2 != null : "fx:id=\"column2\" was not injected: check your FXML file 'stayingGuestInfo.fxml'.";
        assert col1 != null : "fx:id=\"col1\" was not injected: check your FXML file 'stayingGuestInfo.fxml'.";
        assert col2 != null : "fx:id=\"col2\" was not injected: check your FXML file 'stayingGuestInfo.fxml'.";
        assert col3 != null : "fx:id=\"col3\" was not injected: check your FXML file 'stayingGuestInfo.fxml'.";
        assert col4 != null : "fx:id=\"col4\" was not injected: check your FXML file 'stayingGuestInfo.fxml'.";

        ObservableList<stayGuest> data;

        data = FXCollections.observableArrayList();

        column1.setCellValueFactory(new PropertyValueFactory<>("name"));
        column2.setCellValueFactory(new PropertyValueFactory<>("gid"));

        String sql = "SELECT DISTINCT GUEST.NAME,GUEST.GUEST_ID\n"
                + "FROM GUEST,BILL,INVOICE,RESERVATION\n"
                + "WHERE BILL.INVOICE_ID=INVOICE.INVOICE_ID AND \n"
                + "INVOICE.GUEST_ID=GUEST.GUEST_ID AND\n"
                + "RESERVATION.GUEST_ID=GUEST.GUEST_ID AND\n"
                + "(TO_DATE(RESERVATION.CHECKOUT_DATE,'YYYY-MM-DD')>=TO_DATE(TO_CHAR(SYSDATE,'YYYY-MM-DD'),'YYYY-MM-DD'))\n"
                + " and BILL.APPROVED='YES'";

        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection con = new OracleDBMS().getConnection();
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            data.add(new stayGuest(rs.getString(1), rs.getString(2)));
        }

        table.setItems(data);
        pst.close();
        con.close();
    }
}
