package com.teach.javafx.controller;

import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MessageController 登录交互控制类 对应 base/message-dialog.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */

public class HonorEditController {
    @FXML
    private ComboBox<OptionItem> studentComboBox;
    private List<OptionItem> studentList;
    @FXML
    private ComboBox<OptionItem> prizeComboBox;
    private List<OptionItem> prizeList;
    @FXML
    private TextField markField;
    private HonorTableController honorTableController = null;
    private Integer honorId = null;
    @FXML
    public void initialize() {
    }

    @FXML
    public void okButtonClick(){
        Map<String,Object> data = new HashMap<>();
        OptionItem op;
        op = studentComboBox.getSelectionModel().getSelectedItem();
        if(op != null) {
            data.put("personId",Integer.parseInt(op.getValue()));
        }
        op = prizeComboBox.getSelectionModel().getSelectedItem();
        if(op != null) {
            data.put("prizeId", Integer.parseInt(op.getValue()));
        }
        data.put("honorId", honorId);
        data.put("mark",markField.getText());
        honorTableController.doClose("ok",data);
    }
    @FXML
    public void cancelButtonClick(){
        honorTableController.doClose("cancel",null);
    }

    public void setHonorTableController(HonorTableController honorTableController) {
        this.honorTableController = honorTableController;
    }
    public void init(){
        studentList = honorTableController.getStudentList();
        prizeList = honorTableController.getPrizeList();
        studentComboBox.getItems().addAll(studentList );
        prizeComboBox.getItems().addAll(prizeList);
    }
    public void showDialog(Map data){
        if(data == null) {
            honorId = null;
            studentComboBox.getSelectionModel().select(-1);
            prizeComboBox.getSelectionModel().select(-1);
            studentComboBox.setDisable(false);
            prizeComboBox.setDisable(false);
            markField.setText("");
        }else {
            honorId = CommonMethod.getInteger(data,"honorId");
            studentComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(studentList, CommonMethod.getString(data, "personId")));
            prizeComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(prizeList, CommonMethod.getString(data, "prizeId")));
            studentComboBox.setDisable(true);
            prizeComboBox.setDisable(true);
            markField.setText(CommonMethod.getString(data, "mark"));
        }
    }
}
