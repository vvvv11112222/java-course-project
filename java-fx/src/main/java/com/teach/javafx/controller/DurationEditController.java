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
 * DurationEditController 志愿活动时长编辑对话框控制类
 */
public class DurationEditController {
    @FXML
    private ComboBox<OptionItem> studentComboBox;
    private List<OptionItem> studentList;
    @FXML
    private ComboBox<OptionItem> volunteeringComboBox;
    private List<OptionItem> volunteeringList;
    @FXML
    private TextField markField; // 时长字段
    private DurationTableController durationTableController = null;
    private Integer durationId = null;

    @FXML
    public void initialize() {
    }

    @FXML
    public void okButtonClick() {
        Map<String, Object> data = new HashMap<>();
        OptionItem op;
        op = studentComboBox.getSelectionModel().getSelectedItem();
        if (op != null) {
            data.put("personId", Integer.parseInt(op.getValue()));
        }
        op = volunteeringComboBox.getSelectionModel().getSelectedItem();
        if (op != null) {
            data.put("volunteeringId", Integer.parseInt(op.getValue()));
        }
        data.put("durationId", durationId);
        data.put("mark", markField.getText()); // 时长字段



        durationTableController.doClose("ok", data);
    }

    @FXML
    public void cancelButtonClick() {
        durationTableController.doClose("cancel", null);
    }

    public void setDurationTableController(DurationTableController durationTableController) {
        this.durationTableController = durationTableController;
    }

    public void init() {
        studentList = durationTableController.getStudentList();
        volunteeringList = durationTableController.getVolunteeringList();
        studentComboBox.getItems().addAll(studentList);
        volunteeringComboBox.getItems().addAll(volunteeringList);
    }

    public void showDialog(Map data) {
        if (data == null) {
            durationId = null;
            studentComboBox.getSelectionModel().select(-1);
            volunteeringComboBox.getSelectionModel().select(-1);
            studentComboBox.setDisable(false);
            volunteeringComboBox.setDisable(false);
            markField.setText(""); // 时长字段
        } else {
            durationId = CommonMethod.getInteger(data, "durationId");
            studentComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(studentList, CommonMethod.getString(data, "personId")));
            volunteeringComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(volunteeringList, CommonMethod.getString(data, "volunteeringId")));
            studentComboBox.setDisable(true);
            volunteeringComboBox.setDisable(true);
            markField.setText(CommonMethod.getString(data, "mark")); // 时长字段
        }
    }
}