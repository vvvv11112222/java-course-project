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
 * CourseController 登录交互控制类 对应 course-panel.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class CourseController {
    @FXML
    private TableView<Map<String, Object>> dataTableView;
    @FXML
    private TableColumn<Map,String> numColumn;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,String> creditColumn;
    @FXML
    private TableColumn<Map,String> preCourseColumn;
    @FXML
    private TableColumn<Map,FlowPane> operateColumn;

    @FXML
    private TextField numField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField creditField;
    @FXML
    private ComboBox<Map<String, Object>> preCourseComboBox;

    private List<Map<String,Object>> courseList = new ArrayList<>();  // 课程信息列表数据
    private final ObservableList<Map<String,Object>> observableList = FXCollections.observableArrayList();  // TableView渲染列表
    private List<Map<String,Object>> allCourseList = new ArrayList<>(); // 所有课程列表，用于前置课程选择

    @FXML
    private void onQueryButtonClick(){
        DataResponse res;
        DataRequest req = new DataRequest();
        String numName = "";
        if (numField.getText() != null && !numField.getText().isEmpty()) {
            numName = numField.getText();
        } else if (nameField.getText() != null && !nameField.getText().isEmpty()) {
            numName = nameField.getText();
        }

        req.add("numName", numName);
        res = HttpRequestUtil.request("/api/course/getCourseList", req); // 从后台获取课程信息列表集合

        if(res != null && res.getCode() == 0) {
            List<Map<String, Object>> queryResults = (List<Map<String, Object>>) res.getData();

            if (queryResults.isEmpty() && !numName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText(null);
                alert.setContentText("未找到符合条件的课程");
                alert.showAndWait();
                return;
            }

            if (!numName.isEmpty()) {
                // 如果有查询条件，显示查询结果窗口
                showQueryResultWindow(queryResults, numName);
            } else {
                // 如果没有查询条件，直接更新主界面
                courseList = queryResults;
                updateAllCourseList();
                setTableViewData();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("查询课程失败");
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
            TableColumn<Map, String> numColumn = new TableColumn<>("课程编号");
            numColumn.setCellValueFactory(new MapValueFactory<>("num"));
            numColumn.setPrefWidth(180.0);

            TableColumn<Map, String> nameColumn = new TableColumn<>("课程名称");
            nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
            nameColumn.setPrefWidth(250.0);

            TableColumn<Map, String> creditColumn = new TableColumn<>("学分");
            creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
            creditColumn.setPrefWidth(100.0);

            TableColumn<Map, String> preCourseColumn = new TableColumn<>("前置课程");
            preCourseColumn.setCellValueFactory(new MapValueFactory<>("preCourseName"));
            preCourseColumn.setPrefWidth(250.0);

            // 添加列到表格
            tableView.getColumns().addAll(numColumn, nameColumn, creditColumn, preCourseColumn);

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
                // 刷新主界面的课程列表，显示所有课程
                DataRequest req = new DataRequest();
                req.add("numName", "");
                DataResponse res = HttpRequestUtil.request("/api/course/getCourseList", req);

                if(res != null && res.getCode() == 0) {
                    courseList = (List<Map<String, Object>>) res.getData();
                    // 更新所有课程列表，用于前置课程选择
                    updateAllCourseList();
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

    // 更新所有课程列表，用于前置课程选择
    private void updateAllCourseList() {
        DataResponse res;
        DataRequest req = new DataRequest();
        res = HttpRequestUtil.request("/api/course/getCourseList", req);
        if(res != null && res.getCode() == 0) {
            allCourseList = (List<Map<String, Object>>) res.getData();

            // 更新前置课程下拉框
            ObservableList<Map<String, Object>> preCourseOptions = FXCollections.observableArrayList();
            // 添加一个空选项
            Map<String, Object> emptyOption = new HashMap<>();
            emptyOption.put("courseId", "");
            emptyOption.put("name", "无前置课程");
            preCourseOptions.add(emptyOption);

            // 添加所有课程作为选项
            preCourseOptions.addAll(allCourseList);
            preCourseComboBox.setItems(preCourseOptions);

            // 设置下拉框显示课程名称
            preCourseComboBox.setConverter(new StringConverter<Map<String, Object>>() {
                @Override
                public String toString(Map<String, Object> course) {
                    return course != null ? (String) course.get("name") : "";
                }

                @Override
                public Map<String, Object> fromString(String string) {
                    return null; // 不需要实现，因为我们不允许用户输入
                }
            });

            // 默认选择第一项（无前置课程）
            preCourseComboBox.getSelectionModel().selectFirst();
        }
    }

    private void setTableViewData() {
       observableList.clear();
       Map<String,Object> map;
       FlowPane flowPane;
       Button saveButton, deleteButton;

       for (int j = 0; j < courseList.size(); j++) {
            map = courseList.get(j);
            flowPane = new FlowPane();
            flowPane.setHgap(10);
            flowPane.setAlignment(Pos.CENTER);

            saveButton = new Button("修改");
            saveButton.setId("save" + j);
            saveButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 8 15; -fx-cursor: hand;");
            saveButton.setOnAction(e -> {
                editItem(((Button)e.getSource()).getId());
            });

            deleteButton = new Button("删除");
            deleteButton.setId("delete" + j);
            deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 8 15; -fx-cursor: hand;");
            deleteButton.setOnAction(e -> {
                deleteItem(((Button)e.getSource()).getId());
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
            showAlert("错误", "请输入课程编号");
            return;
        }
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            showAlert("错误", "请输入课程名称");
            return;
        }
        if (creditField.getText() == null || creditField.getText().isEmpty()) {
            showAlert("错误", "请输入学分");
            return;
        }

        // 创建请求
        DataRequest req = new DataRequest();
        req.add("num", numField.getText());
        req.add("name", nameField.getText());
        req.add("credit", creditField.getText());

        // 获取选中的前置课程
        Map<String, Object> selectedPreCourse = preCourseComboBox.getSelectionModel().getSelectedItem();
        if (selectedPreCourse != null && selectedPreCourse.get("courseId") != null && !selectedPreCourse.get("courseId").toString().isEmpty()) {
            req.add("preCourseId", selectedPreCourse.get("courseId"));
        }

        // 发送请求
        DataResponse res = HttpRequestUtil.request("/api/course/courseSave", req);
        if (res != null && res.getCode() == 0) {
            showAlert("成功", "课程添加成功");
            // 清空输入框
            numField.clear();
            nameField.clear();
            creditField.clear();
            preCourseComboBox.getSelectionModel().selectFirst();
            // 刷新课程列表
            onQueryButtonClick();
        } else {
            showAlert("错误", "课程添加失败");
        }
    }

    public void editItem(String name) {
        if (name == null)
            return;
        int j = Integer.parseInt(name.substring(4));
        Map<String, Object> data = courseList.get(j);

        // 打开编辑对话框
        openEditDialog(data);
    }

    private void openEditDialog(Map<String, Object> courseData) {
        // 创建对话框
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("编辑课程");
        dialog.setHeaderText("修改课程信息");

        // 设置按钮
        ButtonType saveButtonType = new ButtonType("确认修改", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // 创建表单
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField editNumField = new TextField();
        editNumField.setText((String) courseData.get("num"));
        TextField editNameField = new TextField();
        editNameField.setText((String) courseData.get("name"));
        TextField editCreditField = new TextField();
        editCreditField.setText((String) courseData.get("credit"));

        // 创建前置课程下拉框
        ComboBox<Map<String, Object>> editPreCourseComboBox = new ComboBox<>();
        ObservableList<Map<String, Object>> preCourseOptions = FXCollections.observableArrayList();

        // 添加一个空选项
        Map<String, Object> emptyOption = new HashMap<>();
        emptyOption.put("courseId", "");
        emptyOption.put("name", "无前置课程");
        preCourseOptions.add(emptyOption);

        // 添加所有课程作为选项（除了当前课程）
        for (Map<String, Object> course : allCourseList) {
            if (!course.get("courseId").equals(courseData.get("courseId"))) {
                preCourseOptions.add(course);
            }
        }

        editPreCourseComboBox.setItems(preCourseOptions);

        // 设置下拉框显示课程名称
        editPreCourseComboBox.setConverter(new StringConverter<Map<String, Object>>() {
            @Override
            public String toString(Map<String, Object> course) {
                return course != null ? (String) course.get("name") : "";
            }

            @Override
            public Map<String, Object> fromString(String string) {
                return null;
            }
        });

        // 设置默认选中的前置课程
        if (courseData.containsKey("preCourseId") && courseData.get("preCourseId") != null) {
            for (Map<String, Object> course : preCourseOptions) {
                if (course.get("courseId") != null &&
                    course.get("courseId").toString().equals(courseData.get("preCourseId").toString())) {
                    editPreCourseComboBox.getSelectionModel().select(course);
                    break;
                }
            }
        } else {
            editPreCourseComboBox.getSelectionModel().selectFirst(); // 默认选择"无前置课程"
        }

        grid.add(new Label("课程编号:"), 0, 0);
        grid.add(editNumField, 1, 0);
        grid.add(new Label("课程名称:"), 0, 1);
        grid.add(editNameField, 1, 1);
        grid.add(new Label("学分:"), 0, 2);
        grid.add(editCreditField, 1, 2);
        grid.add(new Label("前置课程:"), 0, 3);
        grid.add(editPreCourseComboBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // 请求焦点在第一个文本框
        Platform.runLater(() -> editNumField.requestFocus());

        // 转换结果
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Map<String, Object> result = new HashMap<>();
                result.put("num", editNumField.getText());
                result.put("name", editNameField.getText());
                result.put("credit", editCreditField.getText());
                result.put("preCourse", editPreCourseComboBox.getSelectionModel().getSelectedItem());
                return result;
            }
            return null;
        });

        // 显示对话框并处理结果
        Optional<Map<String, Object>> result = dialog.showAndWait();

        result.ifPresent(formData -> {
            // 验证输入
            if (formData.get("num") == null || formData.get("num").toString().isEmpty()) {
                showAlert("错误", "请输入课程编号");
                return;
            }
            if (formData.get("name") == null || formData.get("name").toString().isEmpty()) {
                showAlert("错误", "请输入课程名称");
                return;
            }
            if (formData.get("credit") == null || formData.get("credit").toString().isEmpty()) {
                showAlert("错误", "请输入学分");
                return;
            }

            // 创建请求
            DataRequest req = new DataRequest();
            req.add("courseId", courseData.get("courseId"));
            req.add("num", formData.get("num"));
            req.add("name", formData.get("name"));
            req.add("credit", formData.get("credit"));

            // 获取选中的前置课程
            Map<String, Object> selectedPreCourse = (Map<String, Object>) formData.get("preCourse");
            if (selectedPreCourse != null && selectedPreCourse.get("courseId") != null &&
                !selectedPreCourse.get("courseId").toString().isEmpty()) {
                req.add("preCourseId", selectedPreCourse.get("courseId"));
            }

            // 发送请求
            DataResponse res = HttpRequestUtil.request("/api/course/courseSave", req);
            if (res != null && res.getCode() == 0) {
                showAlert("成功", "课程修改成功");
                // 刷新课程列表
                onQueryButtonClick();
            } else {
                showAlert("错误", "课程修改失败");
            }
        });
    }

    public void deleteItem(String name) {
        if (name == null)
            return;
        int j = Integer.parseInt(name.substring(6));
        Map<String, Object> data = courseList.get(j);

        // 创建确认对话框
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText(null);
        alert.setContentText("确定要删除课程 " + data.get("name") + " 吗？");

        // 等待用户响应
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // 创建请求
                DataRequest req = new DataRequest();
                req.add("courseId", data.get("courseId"));

                // 发送请求
                DataResponse res = HttpRequestUtil.request("/api/course/courseDelete", req);
                if (res != null && res.getCode() == 0) {
                    showAlert("成功", "课程删除成功");
                    // 刷新课程列表
                    onQueryButtonClick();
                } else {
                    // 显示具体的错误信息
                    String errorMsg = res.getMsg();
                    if (errorMsg != null && !errorMsg.isEmpty()) {
                        showAlert("无法删除", errorMsg);
                    } else {
                        showAlert("错误", "课程删除失败");
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
        // 设置表格列
        numColumn.setCellValueFactory(new MapValueFactory<>("num"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        preCourseColumn.setCellValueFactory(new MapValueFactory<>("preCourseName"));
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

        creditColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        creditColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("credit", event.getNewValue());
        });

        preCourseColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        preCourseColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("preCourseName", event.getNewValue());
        });

        operateColumn.setCellValueFactory(new MapValueFactory<>("operate"));

        // 设置表格可编辑
        dataTableView.setEditable(true);

        // 初始化前置课程下拉框
        updateAllCourseList();

        // 加载课程数据
        loadAllCourses();
    }

    /**
     * 加载所有课程
     */
    private void loadAllCourses() {
        DataRequest req = new DataRequest();
        req.add("numName", "");
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseList", req);

        if(res != null && res.getCode() == 0) {
            courseList = (List<Map<String, Object>>) res.getData();
            // 更新所有课程列表，用于前置课程选择
            updateAllCourseList();
            setTableViewData();
        }
    }


    }

