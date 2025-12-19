package com.teach.javafx.controller;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * VolunteeringController 交互控制类 对应 volunteering-panel.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class VolunteeringController {
    @FXML
    private TableView<Map<String, Object>> dataTableView;
    @FXML
    private TableColumn<Map, String> numColumn;
    @FXML
    private TableColumn<Map, String> nameColumn;
    @FXML
    private TableColumn<Map, FlowPane> operateColumn;

    @FXML
    private TextField numField;
    @FXML
    private TextField nameField;

    private List<Map<String, Object>> volunteeringList = new ArrayList<>();  // 志愿活动信息列表数据
    private final ObservableList<Map<String, Object>> observableList = FXCollections.observableArrayList();  // TableView渲染列表

    @FXML
    private void onQueryButtonClick() {
        DataResponse res;
        DataRequest req = new DataRequest();
        String numName = "";
        if (numField.getText() != null && !numField.getText().isEmpty()) {
            numName = numField.getText();
        } else if (nameField.getText() != null && !nameField.getText().isEmpty()) {
            numName = nameField.getText();
        }

        req.add("numName", numName);
        res = HttpRequestUtil.request("/api/volunteering/getVolunteeringList", req); // 从后台获取志愿活动信息列表集合

        if (res != null && res.getCode() == 0) {
            List<Map<String, Object>> queryResults = (List<Map<String, Object>>) res.getData();

            if (queryResults.isEmpty() && !numName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText(null);
                alert.setContentText("未找到符合条件的志愿活动");
                alert.showAndWait();
                return;
            }

            if (!numName.isEmpty()) {
                // 如果有查询条件，显示查询结果窗口
                showQueryResultWindow(queryResults, numName);
            } else {
                // 如果没有查询条件，直接更新主界面
                volunteeringList = queryResults;
                setTableViewData();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("查询志愿活动失败");
            alert.showAndWait();
        }
    }

    /**
     * 显示查询结果窗口
     */
    private void showQueryResultWindow(List<Map<String, Object>> queryResults, String queryText) {
        try {
            // 创建新窗口
            Stage stage = new Stage();
            stage.setTitle("查询结果: " + queryText);

            // 创建表格
            TableView<Map> tableView = new TableView<>();

            // 创建列
            TableColumn<Map, String> numColumn = new TableColumn<>("活动编号");
            numColumn.setCellValueFactory(new MapValueFactory<>("num"));
            numColumn.setPrefWidth(180.0);

            TableColumn<Map, String> nameColumn = new TableColumn<>("活动名称");
            nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
            nameColumn.setPrefWidth(250.0);

            // 添加列到表格（移除学分列）
            tableView.getColumns().addAll(numColumn, nameColumn);

            // 转换数据类型
            ObservableList<Map> data = FXCollections.observableArrayList();
            for (Map<String, Object> item : queryResults) {
                data.add(item);
            }
            tableView.setItems(data);

            // 创建布局
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(tableView);
            borderPane.setPadding(new Insets(20));

            // 设置场景
            Scene scene = new Scene(borderPane, 800, 600);
            stage.setScene(scene);

            // 窗口关闭时的事件
            stage.setOnHidden(event -> {
                // 刷新主界面的志愿活动列表，显示所有活动
                DataRequest req = new DataRequest();
                req.add("numName", "");
                DataResponse res = HttpRequestUtil.request("/api/volunteering/getVolunteeringList", req);

                if (res != null && res.getCode() == 0) {
                    volunteeringList = (List<Map<String, Object>>) res.getData();
                    setTableViewData();
                }
            });

            // 显示窗口
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("显示查询结果窗口失败: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void setTableViewData() {
        observableList.clear();
        Map<String, Object> map;
        FlowPane flowPane;
        Button saveButton, deleteButton;

        for (int j = 0; j < volunteeringList.size(); j++) {
            map = volunteeringList.get(j);
            flowPane = new FlowPane();
            flowPane.setHgap(10);
            flowPane.setAlignment(Pos.CENTER);

            saveButton = new Button("修改");
            saveButton.setId("save" + j);
            saveButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 8 15; -fx-cursor: hand;");
            saveButton.setOnAction(e -> {
                editItem(((Button) e.getSource()).getId());
            });

            deleteButton = new Button("删除");
            deleteButton.setId("delete" + j);
            deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 8 15; -fx-cursor: hand;");
            deleteButton.setOnAction(e -> {
                deleteItem(((Button) e.getSource()).getId());
            });

            flowPane.getChildren().addAll(saveButton, deleteButton);
            map.put("operate", flowPane);
            observableList.addAll(FXCollections.observableArrayList(map));
        }
        dataTableView.setItems(observableList);
    }

    @FXML
    private void onAddButtonClick() {
        // 验证输入
        if (numField.getText() == null || numField.getText().isEmpty()) {
            showAlert("错误", "请输入活动编号");
            return;
        }
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            showAlert("错误", "请输入活动名称");
            return;
        }

        // 创建请求
        DataRequest req = new DataRequest();
        req.add("num", numField.getText());
        req.add("name", nameField.getText());

        // 发送请求
        DataResponse res = HttpRequestUtil.request("/api/volunteering/volunteeringSave", req);
        if (res != null && res.getCode() == 0) {
            showAlert("成功", "志愿活动添加成功");
            // 清空输入框
            numField.clear();
            nameField.clear();
            // 刷新志愿活动列表
            onQueryButtonClick();
        } else {
            showAlert("错误", "志愿活动添加失败");
        }
    }

    public void editItem(String name) {
        if (name == null)
            return;
        int j = Integer.parseInt(name.substring(4));
        Map<String, Object> data = volunteeringList.get(j);

        // 打开编辑对话框（移除学分相关控件）
        openEditDialog(data);
    }

    private void openEditDialog(Map<String, Object> volunteeringData) {
        // 创建对话框
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("编辑志愿活动");
        dialog.setHeaderText("修改志愿活动信息");

        // 设置按钮
        ButtonType saveButtonType = new ButtonType("确认修改", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // 创建表单（仅保留编号、名称）
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField editNumField = new TextField();
        editNumField.setText((String) volunteeringData.get("num"));
        TextField editNameField = new TextField();
        editNameField.setText((String) volunteeringData.get("name"));

        grid.add(new Label("活动编号:"), 0, 0);
        grid.add(editNumField, 1, 0);
        grid.add(new Label("活动名称:"), 0, 1);
        grid.add(editNameField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // 请求焦点在第一个文本框
        Platform.runLater(() -> editNumField.requestFocus());

        // 转换结果
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Map<String, Object> result = new HashMap<>();
                result.put("num", editNumField.getText());
                result.put("name", editNameField.getText());
                return result;
            }
            return null;
        });

        // 显示对话框并处理结果
        Optional<Map<String, Object>> result = dialog.showAndWait();

        result.ifPresent(formData -> {
            // 验证输入
            if (formData.get("num") == null || formData.get("num").toString().isEmpty()) {
                showAlert("错误", "请输入活动编号");
                return;
            }
            if (formData.get("name") == null || formData.get("name").toString().isEmpty()) {
                showAlert("错误", "请输入活动名称");
                return;
            }

            // 创建请求
            DataRequest req = new DataRequest();
            req.add("volunteeringId", volunteeringData.get("volunteeringId"));
            req.add("num", formData.get("num"));
            req.add("name", formData.get("name"));

            // 发送请求
            DataResponse res = HttpRequestUtil.request("/api/volunteering/volunteeringSave", req);
            if (res != null && res.getCode() == 0) {
                showAlert("成功", "志愿活动修改成功");
                // 刷新志愿活动列表
                onQueryButtonClick();
            } else {
                showAlert("错误", "志愿活动修改失败");
            }
        });
    }

    public void deleteItem(String name) {
        if (name == null)
            return;
        int j = Integer.parseInt(name.substring(6));
        Map<String, Object> data = volunteeringList.get(j);

        // 创建确认对话框
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText(null);
        alert.setContentText("确定要删除志愿活动 " + data.get("name") + " 吗？");

        // 等待用户响应
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // 创建请求
                DataRequest req = new DataRequest();
                req.add("volunteeringId", data.get("volunteeringId"));

                // 发送请求
                DataResponse res = HttpRequestUtil.request("/api/volunteering/volunteeringDelete", req);
                if (res != null && res.getCode() == 0) {
                    showAlert("成功", "志愿活动删除成功");
                    // 刷新志愿活动列表
                    onQueryButtonClick();
                } else {
                    // 显示具体的错误信息
                    String errorMsg = res.getMsg();
                    if (errorMsg != null && !errorMsg.isEmpty()) {
                        showAlert("无法删除", errorMsg);
                    } else {
                        showAlert("错误", "志愿活动删除失败");
                    }
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        // 设置表格列（移除学分列）
        numColumn.setCellValueFactory(new MapValueFactory<>("num"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        operateColumn.setCellValueFactory(new MapValueFactory<>("operate"));

        // 设置可编辑列
        numColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        numColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("num", event.getNewValue());
        });

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("name", event.getNewValue());
        });

        operateColumn.setCellValueFactory(new MapValueFactory<>("operate"));

        // 设置表格可编辑
        dataTableView.setEditable(true);

        // 加载所有志愿活动数据（移除学分相关逻辑）
        loadAllVolunteerings();
    }

    /**
     * 加载所有志愿活动
     */
    private void loadAllVolunteerings() {
        DataRequest req = new DataRequest();
        req.add("numName", "");
        DataResponse res = HttpRequestUtil.request("/api/volunteering/getVolunteeringList", req);

        if (res != null && res.getCode() == 0) {
            volunteeringList = (List<Map<String, Object>>) res.getData();
            setTableViewData();
        }
    }
}