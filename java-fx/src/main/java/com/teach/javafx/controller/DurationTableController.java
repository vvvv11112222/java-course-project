package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DurationTableController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> studentNumColumn;
    @FXML
    private TableColumn<Map, String> studentNameColumn;
    @FXML
    private TableColumn<Map, String> classNameColumn;
    @FXML
    private TableColumn<Map, String> volunteeringNumColumn;
    @FXML
    private TableColumn<Map, String> volunteeringNameColumn;
    @FXML
    private TableColumn<Map, String> markColumn; // 时长字段
    @FXML
    private TableColumn<Map, javafx.scene.layout.HBox> editColumn;

    private ArrayList<Map> durationList = new ArrayList<>();  // 志愿活动时长信息列表数据
    private ObservableList<Map> observableList = FXCollections.observableArrayList();  // TableView渲染列表

    @FXML
    private ComboBox<OptionItem> studentComboBox;

    private List<OptionItem> studentList;
    @FXML
    private ComboBox<OptionItem> volunteeringComboBox;

    private List<OptionItem> volunteeringList;

    private DurationEditController durationEditController = null;
    private Stage stage = null;

    public List<OptionItem> getStudentList() {
        return studentList;
    }

    public List<OptionItem> getVolunteeringList() {
        return volunteeringList;
    }

    @FXML
    private void onQueryButtonClick() {
        Integer personId = 0;
        Integer volunteeringId = 0;
        OptionItem op;
        op = studentComboBox.getSelectionModel().getSelectedItem();
        if (op != null)
            personId = Integer.parseInt(op.getValue());
        op = volunteeringComboBox.getSelectionModel().getSelectedItem();
        if (op != null)
            volunteeringId = Integer.parseInt(op.getValue());
        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("volunteeringId", volunteeringId);
        res = HttpRequestUtil.request("/api/duration/getDurationList", req); // 从后台获取所有学生志愿活动信息列表集合
        if (res != null && res.getCode() == 0) {
            durationList = (ArrayList<Map>) res.getData();
        }
        setTableViewData();
    }

    private void setTableViewData() {
        observableList.clear();
        Map map;
        Button editButton, deleteButton;
        for (int j = 0; j < durationList.size(); j++) {
            map = durationList.get(j);
            editButton = new Button("编辑");
            editButton.setId("edit" + j);
            editButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 12; -fx-padding: 5 10; -fx-background-radius: 3;");
            editButton.setOnAction(e -> {
                editItem(((Button) e.getSource()).getId());
            });

            deleteButton = new Button("删除");
            deleteButton.setId("delete" + j);
            deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12; -fx-padding: 5 10; -fx-background-radius: 3;");
            deleteButton.setOnAction(e -> {
                deleteItem(((Button) e.getSource()).getId());
            });

            javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(5);
            buttonBox.getChildren().addAll(editButton, deleteButton);
            buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

            map.put("edit", buttonBox);
            observableList.addAll(FXCollections.observableArrayList(map));
        }
        dataTableView.setItems(observableList);
    }

    public void editItem(String name) {
        if (name == null)
            return;
        int j = Integer.parseInt(name.substring(4, name.length()));
        Map data = durationList.get(j);
        initDialog();
        durationEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    public void deleteItem(String name) {
        if (name == null)
            return;
        int j = Integer.parseInt(name.substring(6, name.length()));
        Map<String, Object> form = durationList.get(j);
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        Integer durationId = CommonMethod.getInteger(form, "durationId");
        DataRequest req = new DataRequest();
        req.add("durationId", durationId);
        DataResponse res = HttpRequestUtil.request("/api/duration/durationDelete", req);
        if (res.getCode() == 0) {
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    public void initialize() {
        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));  // 设置列值工程属性
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        volunteeringNumColumn.setCellValueFactory(new MapValueFactory<>("volunteeringNum"));
        volunteeringNameColumn.setCellValueFactory(new MapValueFactory<>("volunteeringName"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark")); // 时长字段
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));

        DataRequest req = new DataRequest();
        studentList = HttpRequestUtil.requestOptionItemList("/api/duration/getStudentItemOptionList", req); // 从后台获取所有学生信息列表集合
        volunteeringList = HttpRequestUtil.requestOptionItemList("/api/duration/getVolunteeringItemOptionList", req); // 从后台获取所有志愿活动信息列表集合
        OptionItem item = new OptionItem(null, "0", "请选择");
        studentComboBox.getItems().addAll(item);
        studentComboBox.getItems().addAll(studentList);
        volunteeringComboBox.getItems().addAll(item);
        volunteeringComboBox.getItems().addAll(volunteeringList);
        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        onQueryButtonClick();
    }

    private void initDialog() {
        if (stage != null)
            return;
        FXMLLoader fxmlLoader;
        Scene scene = null;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("duration-edit-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 400, 300);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.NONE);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.setTitle("志愿活动时长管理");
            stage.setOnCloseRequest(event -> {
                MainApplication.setCanClose(true);
            });
            durationEditController = (DurationEditController) fxmlLoader.getController();
            durationEditController.setDurationTableController(this);
            durationEditController.init();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void doClose(String cmd, Map<String, Object> data) {
        MainApplication.setCanClose(true);
        stage.close();
        if (!"ok".equals(cmd))
            return;
        DataResponse res;
        Integer personId = CommonMethod.getInteger(data, "personId");
        if (personId == null) {
            MessageDialog.showDialog("没有选中学生不能添加保存！");
            return;
        }
        Integer volunteeringId = CommonMethod.getInteger(data, "volunteeringId");
        if (volunteeringId == null) {
            MessageDialog.showDialog("没有选中志愿活动不能添加保存！");
            return;
        }
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("volunteeringId", volunteeringId);
        req.add("durationId", CommonMethod.getInteger(data, "durationId"));
        req.add("mark", CommonMethod.getString(data, "mark")); // 时长字段
        res = HttpRequestUtil.request("/api/duration/durationSave", req); // 从后台获取所有学生信息列表集合
        if (res.getCode() == 0) {
            MessageDialog.showDialog("保存成功！");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    /**
     * 添加记录按钮点击事件
     */
    @FXML
    public void onAddButtonClick() {
        initDialog();
        durationEditController.showDialog(null);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    /**
     * 编辑记录按钮点击事件
     */
    @FXML
    public void onEditButtonClick() {
        int selectedIndex = dataTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            MessageDialog.showDialog("请选择要编辑的记录！");
            return;
        }
        Map data = durationList.get(selectedIndex);
        initDialog();
        durationEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    /**
     * 删除记录按钮点击事件
     */
    @FXML
    public void onDeleteButtonClick() {
        int selectedIndex = dataTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            MessageDialog.showDialog("请选择要删除的记录！");
            return;
        }
        Map<String, Object> form = durationList.get(selectedIndex);
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        Integer durationId = CommonMethod.getInteger(form, "durationId");
        DataRequest req = new DataRequest();
        req.add("durationId", durationId);
        DataResponse res = HttpRequestUtil.request("/api/duration/durationDelete", req);
        if (res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
