package com.teach.javafx.controller;

import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * HomeworkController 作业管理交互控制类 对应 homework-panel.fxml
 */
public class HomeworkController {
    @FXML
    private TableView<Map<String, Object>> dataTableView;
    @FXML
    private TableColumn<Map, String> studentNumColumn;
    @FXML
    private TableColumn<Map, String> studentNameColumn;
    @FXML
    private TableColumn<Map, String> classNameColumn;
    @FXML
    private TableColumn<Map, String> majorColumn;
    @FXML
    private TableColumn<Map, String> courseNameColumn;
    @FXML
    private TableColumn<Map, String> contentColumn;
    @FXML
    private TableColumn<Map, String> assignTimeColumn;
    @FXML
    private TableColumn<Map, String> dueTimeColumn;
    @FXML
    private TableColumn<Map, String> statusColumn;
    @FXML
    private TableColumn<Map, FlowPane> operateColumn;

    @FXML
    private TextField numNameTextField;
    @FXML
    private ComboBox<Map<String, Object>> studentComboBox;
    @FXML
    private ComboBox<Map<String, Object>> courseComboBox;
    @FXML
    private TextArea contentTextArea;
    @FXML
    private DatePicker dueDatePicker;
    @FXML
    private ComboBox<String> hourComboBox;// 小时
    @FXML
    private ComboBox<String> minuteComboBox;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private Label totalCountLabel;
    @FXML
    private Label lastUpdateLabel;

    private List<Map<String, Object>> homeworkList = new ArrayList<>();
    private final ObservableList<Map<String, Object>> observableList = FXCollections.observableArrayList();
    private List<Map<String, Object>> allStudentList = new ArrayList<>();
    private List<Map<String, Object>> allCourseList = new ArrayList<>();

    @FXML
    private void onQueryButtonClick() {
        String numName = numNameTextField.getText();

        DataRequest req = new DataRequest();
        req.add("numName", numName);
        DataResponse res = HttpRequestUtil.request("/api/homework/getHomeworkList", req);

        if (res != null && res.getCode() == 0) {
            List<Map<String, Object>> queryResults = (List<Map<String, Object>>) res.getData();
            showQueryResultWindow(queryResults);
        } else {
            showAlert("错误", "查询失败: " + (res != null ? res.getMsg() : "网络错误"));
        }
    }

    private void showQueryResultWindow(List<Map<String, Object>> queryResults) {
        try {
            Stage stage = new Stage();
            stage.setTitle("作业查询结果");

            TableView<Map> tableView = new TableView<>();

            TableColumn<Map, String> studentNumCol = new TableColumn<>("学号");
            studentNumCol.setCellValueFactory(new MapValueFactory<>("studentNum"));
            studentNumCol.setPrefWidth(100.0);

            TableColumn<Map, String> studentNameCol = new TableColumn<>("姓名");
            studentNameCol.setCellValueFactory(new MapValueFactory<>("studentName"));
            studentNameCol.setPrefWidth(100.0);

            TableColumn<Map, String> classNameCol = new TableColumn<>("班级");
            classNameCol.setCellValueFactory(new MapValueFactory<>("className"));
            classNameCol.setPrefWidth(120.0);

            TableColumn<Map, String> majorCol = new TableColumn<>("专业");
            majorCol.setCellValueFactory(new MapValueFactory<>("major"));
            majorCol.setPrefWidth(150.0);

            TableColumn<Map, String> courseNameCol = new TableColumn<>("课程名称");
            courseNameCol.setCellValueFactory(new MapValueFactory<>("courseName"));
            courseNameCol.setPrefWidth(180.0);

            TableColumn<Map, String> contentCol = new TableColumn<>("作业内容");
            contentCol.setCellValueFactory(new MapValueFactory<>("content"));
            contentCol.setPrefWidth(250.0);

            TableColumn<Map, String> assignTimeCol = new TableColumn<>("布置时间");
            assignTimeCol.setCellValueFactory(new MapValueFactory<>("assignTime"));
            assignTimeCol.setPrefWidth(150.0);

            TableColumn<Map, String> dueTimeCol = new TableColumn<>("截止时间");
            dueTimeCol.setCellValueFactory(new MapValueFactory<>("dueTime"));
            dueTimeCol.setPrefWidth(150.0);

            TableColumn<Map, String> statusCol = new TableColumn<>("状态");
            statusCol.setCellValueFactory(new MapValueFactory<>("statusName"));
            statusCol.setPrefWidth(100.0);

            tableView.getColumns().addAll(studentNumCol, studentNameCol, classNameCol, majorCol,
                                         courseNameCol, contentCol, assignTimeCol, dueTimeCol, statusCol);

            ObservableList<Map> data = FXCollections.observableArrayList();
            for (Map<String, Object> item : queryResults) {
                data.add(item);
            }
            tableView.setItems(data);

            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(tableView);
            borderPane.setPadding(new Insets(20));

            Scene scene = new Scene(borderPane, 1200, 600);
            stage.setScene(scene);

            stage.setOnHidden(event -> {
                loadAllHomeworks();
            });

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "显示查询结果窗口失败: " + e.getMessage());
        }
    }

    /**
     * 加载所有作业记录
     */
    private void loadAllHomeworks() {
        DataRequest req = new DataRequest();
        req.add("numName", "");
        DataResponse res = HttpRequestUtil.request("/api/homework/getHomeworkList", req);

        if (res != null && res.getCode() == 0) {
            homeworkList = (List<Map<String, Object>>) res.getData();
            System.out.println("加载到的作业数量: " + homeworkList.size());
            for (Map<String, Object> homework : homeworkList) {
                System.out.println("作业数据: " + homework);
            }
            setTableViewData();
        } else {
            System.out.println("加载作业失败: " + (res != null ? res.getMsg() : "网络错误"));
            showAlert("错误", "加载作业列表失败: " + (res != null ? res.getMsg() : "网络错误"));
        }
    }

    @FXML
    private void onAddButtonClick() {
        Map<String, Object> selectedStudent = studentComboBox.getSelectionModel().getSelectedItem();
        Map<String, Object> selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        String content = contentTextArea.getText();

        if (selectedStudent == null) {
            showAlert("错误", "请选择学生");
            return;
        }
        if (selectedCourse == null) {
            showAlert("错误", "请选择课程");
            return;
        }
        if (content == null || content.trim().isEmpty()) {
            showAlert("错误", "请输入作业内容");
            return;
        }
        if (dueDatePicker.getValue() == null) {
            showAlert("错误", "请选择截止日期");
            return;
        }

        // 构建截止时间字符串
        String dueTime = "";
        if (dueDatePicker.getValue() != null) {
            String hour = hourComboBox.getValue() != null ? hourComboBox.getValue() : "23";
            String minute = minuteComboBox.getValue() != null ? minuteComboBox.getValue() : "59";
            dueTime = dueDatePicker.getValue().toString() + " " + hour + ":" + minute + ":00";

            // 添加调试信息
            System.out.println("时间选择器调试信息:");
            System.out.println("选择的日期: " + dueDatePicker.getValue());
            System.out.println("选择的小时: " + hourComboBox.getValue());
            System.out.println("选择的分钟: " + minuteComboBox.getValue());
            System.out.println("构建的截止时间: " + dueTime);
        } else {
            System.out.println("警告: 未选择截止日期");
        }

        // 创建请求
        DataRequest req = new DataRequest();
        // 注意：学生ID应该使用personId，课程ID使用courseId
        Object studentIdObj = selectedStudent.get("studentId");
        if (studentIdObj == null) {
            studentIdObj = selectedStudent.get("personId"); // 备用字段
        }
        Object courseIdObj = selectedCourse.get("courseId");

        req.add("studentId", studentIdObj);
        req.add("courseId", courseIdObj);
        req.add("content", content.trim());
        req.add("dueTime", dueTime);

        // 根据状态选择设置状态值
        String statusValue = "0"; // 默认未提交
        String selectedStatus = statusComboBox.getValue();
        if ("已提交".equals(selectedStatus)) {
            statusValue = "1";
        } else if ("已批改".equals(selectedStatus)) {
            statusValue = "2";
        }
        req.add("status", statusValue);

        // 添加调试信息
        System.out.println("添加作业请求数据:");
        System.out.println("studentId: " + studentIdObj);
        System.out.println("courseId: " + courseIdObj);
        System.out.println("content: " + content.trim());
        System.out.println("dueTime: " + dueTime);

        // 发送请求
        DataResponse res = HttpRequestUtil.request("/api/homework/homeworkSave", req);
        if (res != null && res.getCode() == 0) {
            showAlert("成功", "作业添加成功");
            // 清空输入
            clearInputFields();
            // 刷新列表
            loadAllHomeworks();
        } else {
            String errorMsg = res != null ? res.getMsg() : "作业添加失败";
            showAlert("错误", errorMsg);
        }
    }

    private void setTableViewData() {
        observableList.clear();
        Map<String, Object> map;
        FlowPane flowPane;
        Button editButton, deleteButton;

        for (Map<String, Object> homework : homeworkList) {
            map = new HashMap<>(homework);

            flowPane = new FlowPane();
            flowPane.setHgap(10);

            editButton = new Button("编辑");
            editButton.setOnAction(e -> onEditHomework(homework));
            editButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 5 10;");

            deleteButton = new Button("删除");
            deleteButton.setOnAction(e -> onDeleteHomework(homework));
            deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 5 10;");

            flowPane.getChildren().addAll(editButton, deleteButton);
            map.put("operate", flowPane);

            observableList.add(map);
        }

        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        majorColumn.setCellValueFactory(new MapValueFactory<>("major"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        contentColumn.setCellValueFactory(new MapValueFactory<>("content"));
        assignTimeColumn.setCellValueFactory(new MapValueFactory<>("assignTime"));
        dueTimeColumn.setCellValueFactory(new MapValueFactory<>("dueTime"));
        statusColumn.setCellValueFactory(new MapValueFactory<>("statusName"));
        operateColumn.setCellValueFactory(new MapValueFactory<>("operate"));

        dataTableView.setItems(observableList);

        // 更新记录数量显示
        updateTotalCountLabel();
    }

    /**
     * 更新记录数量显示
     */
    private void updateTotalCountLabel() {
        if (totalCountLabel != null) {
            totalCountLabel.setText("共 " + homeworkList.size() + " 条记录");
        }
        // 更新最后更新时间
        updateLastUpdateTime();
    }

    /**
     * 更新最后更新时间
     */
    private void updateLastUpdateTime() {
        if (lastUpdateLabel != null) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");
            lastUpdateLabel.setText("最后更新：" + now.format(formatter));
        }
    }

    /**
     * 清空输入字段
     */
    private void clearInputFields() {
        try {
            if (studentComboBox != null) {
                studentComboBox.getSelectionModel().clearSelection();
            }
            if (courseComboBox != null) {
                courseComboBox.getSelectionModel().clearSelection();
            }
            if (contentTextArea != null) {
                contentTextArea.clear();
            }
            if (dueDatePicker != null) {
                dueDatePicker.setValue(java.time.LocalDate.now().plusDays(7));
            }
            if (hourComboBox != null) {
                hourComboBox.setValue("23");
            }
            if (minuteComboBox != null) {
                minuteComboBox.setValue("55"); // 改为55分，更常用
            }
            if (statusComboBox != null) {
                statusComboBox.setValue("未提交");
            }
            // 更新时间选择器显示
            updateTimePickerDisplay();
        } catch (Exception e) {
            System.err.println("清空输入字段时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 清空按钮点击事件
     */
    @FXML
    private void onClearButtonClick() {
        clearInputFields();
    }

    private void onEditHomework(Map<String, Object> homeworkData) {
        // 实现编辑功能
        showEditDialog(homeworkData);
    }

    private void onDeleteHomework(Map<String, Object> homeworkData) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText(null);
        alert.setContentText("确定要删除这条作业记录吗？");

        ButtonType buttonTypeYes = new ButtonType("是");
        ButtonType buttonTypeNo = new ButtonType("否");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            DataRequest req = new DataRequest();
            req.add("homeworkId", homeworkData.get("homeworkId"));

            DataResponse res = HttpRequestUtil.request("/api/homework/homeworkDelete", req);
            if (res != null && res.getCode() == 0) {
                showAlert("成功", "作业删除成功");
                loadAllHomeworks();
            } else {
                String errorMsg = res != null ? res.getMsg() : "作业删除失败";
                showAlert("错误", errorMsg);
            }
        }
    }

    private void showEditDialog(Map<String, Object> homeworkData) {
        try {
            Stage stage = new Stage();
            stage.setTitle("编辑作业");

            BorderPane borderPane = new BorderPane();
            borderPane.setPadding(new Insets(20));

            // 创建编辑表单
            VBox formBox = new VBox(15);

            // 学生选择
            HBox studentBox = new HBox(10);
            studentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Label studentLabel = new Label("学生:");
            studentLabel.setPrefWidth(80);
            ComboBox<Map<String, Object>> editStudentComboBox = new ComboBox<>();
            editStudentComboBox.setPrefWidth(200);
            editStudentComboBox.setItems(FXCollections.observableArrayList(allStudentList));
            editStudentComboBox.setConverter(new StringConverter<Map<String, Object>>() {
                @Override
                public String toString(Map<String, Object> object) {
                    if (object == null) return null;
                    return object.get("num") + " - " + object.get("name") + " (" + object.get("className") + ")";
                }
                @Override
                public Map<String, Object> fromString(String string) {
                    return null;
                }
            });

            // 设置当前选中的学生 - 添加调试信息
            System.out.println("正在设置编辑对话框的学生选择器");
            System.out.println("作业数据中的studentId: " + homeworkData.get("studentId"));
            System.out.println("可用学生列表数量: " + allStudentList.size());

            boolean studentFound = false;
            for (Map<String, Object> student : allStudentList) {
                // 学生数据中使用personId作为studentId
                Object personIdObj = student.get("personId");

                System.out.println("检查学生: " + student.get("name") + ", personId: " + personIdObj);

                // 比较时转换为字符串，避免类型不匹配
                String studentPersonIdStr = String.valueOf(personIdObj);
                String homeworkStudentIdStr = String.valueOf(homeworkData.get("studentId"));

                if (studentPersonIdStr.equals(homeworkStudentIdStr)) {
                    editStudentComboBox.getSelectionModel().select(student);
                    studentFound = true;
                    System.out.println("找到匹配的学生: " + student.get("name"));
                    break;
                }
            }

            if (!studentFound) {
                System.err.println("警告: 未找到匹配的学生，studentId = " + homeworkData.get("studentId"));
                // 输出所有学生的personId用于调试
                System.err.println("可用学生列表:");
                for (Map<String, Object> student : allStudentList) {
                    System.err.println("  - " + student.get("name") + " (personId: " + student.get("personId") + ")");
                }
            }
            studentBox.getChildren().addAll(studentLabel, editStudentComboBox);

            // 课程选择
            HBox courseBox = new HBox(10);
            courseBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Label courseLabel = new Label("课程:");
            courseLabel.setPrefWidth(80);
            ComboBox<Map<String, Object>> editCourseComboBox = new ComboBox<>();
            editCourseComboBox.setPrefWidth(200);
            editCourseComboBox.setItems(FXCollections.observableArrayList(allCourseList));
            editCourseComboBox.setConverter(new StringConverter<Map<String, Object>>() {
                @Override
                public String toString(Map<String, Object> object) {
                    if (object == null) return null;
                    return object.get("num") + " - " + object.get("name") + " (" + object.get("credit") + "学分)";
                }
                @Override
                public Map<String, Object> fromString(String string) {
                    return null;
                }
            });

            // 设置当前选中的课程 - 添加调试信息
            System.out.println("正在设置编辑对话框的课程选择器");
            System.out.println("作业数据中的courseId: " + homeworkData.get("courseId"));
            System.out.println("可用课程列表数量: " + allCourseList.size());

            boolean courseFound = false;
            for (Map<String, Object> course : allCourseList) {
                System.out.println("检查课程: " + course.get("name") + ", courseId: " + course.get("courseId"));

                // 比较时转换为字符串，避免类型不匹配
                String courseIdStr = String.valueOf(course.get("courseId"));
                String homeworkCourseIdStr = String.valueOf(homeworkData.get("courseId"));

                if (courseIdStr.equals(homeworkCourseIdStr)) {
                    editCourseComboBox.getSelectionModel().select(course);
                    courseFound = true;
                    System.out.println("找到匹配的课程: " + course.get("name"));
                    break;
                }
            }

            if (!courseFound) {
                System.err.println("警告: 未找到匹配的课程，courseId = " + homeworkData.get("courseId"));
            }
            courseBox.getChildren().addAll(courseLabel, editCourseComboBox);

            // 作业内容
            HBox contentBox = new HBox(10);
            contentBox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
            Label contentLabel = new Label("作业内容:");
            contentLabel.setPrefWidth(80);
            TextArea editContentTextArea = new TextArea();
            editContentTextArea.setPrefWidth(400);
            editContentTextArea.setPrefHeight(100);
            editContentTextArea.setText((String) homeworkData.get("content"));
            contentBox.getChildren().addAll(contentLabel, editContentTextArea);

            // 截止时间
            HBox dueTimeBox = new HBox(10);
            dueTimeBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Label dueTimeLabel = new Label("截止时间:");
            dueTimeLabel.setPrefWidth(80);
            TextField editDueTimeField = new TextField();
            editDueTimeField.setPrefWidth(200);
            editDueTimeField.setPromptText("yyyy-MM-dd HH:mm:ss");
            editDueTimeField.setText((String) homeworkData.get("dueTime"));
            dueTimeBox.getChildren().addAll(dueTimeLabel, editDueTimeField);

            // 状态选择
            HBox statusBox = new HBox(10);
            statusBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Label statusLabel = new Label("状态:");
            statusLabel.setPrefWidth(80);
            ComboBox<String> statusComboBox = new ComboBox<>();
            statusComboBox.setPrefWidth(150);
            statusComboBox.getItems().addAll("0", "1", "2");
            statusComboBox.setConverter(new StringConverter<String>() {
                @Override
                public String toString(String object) {
                    if ("0".equals(object)) return "未提交";
                    if ("1".equals(object)) return "已提交";
                    if ("2".equals(object)) return "已批改";
                    return object;
                }
                @Override
                public String fromString(String string) {
                    return string;
                }
            });
            statusComboBox.setValue((String) homeworkData.get("status"));
            statusBox.getChildren().addAll(statusLabel, statusComboBox);

            // 按钮
            HBox buttonBox = new HBox(15);
            buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
            Button saveButton = new Button("保存");
            saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 10 20;");
            Button cancelButton = new Button("取消");
            cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 10 20;");

            saveButton.setOnAction(e -> {
                Map<String, Object> selectedStudent = editStudentComboBox.getSelectionModel().getSelectedItem();
                Map<String, Object> selectedCourse = editCourseComboBox.getSelectionModel().getSelectedItem();
                String content = editContentTextArea.getText();
                String dueTime = editDueTimeField.getText();
                String status = statusComboBox.getValue();

                if (selectedStudent == null || selectedCourse == null || content.trim().isEmpty()) {
                    showAlert("错误", "请填写完整信息");
                    return;
                }

                DataRequest req = new DataRequest();
                req.add("homeworkId", homeworkData.get("homeworkId"));
                req.add("studentId", selectedStudent.get("studentId"));
                req.add("courseId", selectedCourse.get("courseId"));
                req.add("content", content.trim());
                req.add("dueTime", dueTime);
                req.add("status", status);

                DataResponse res = HttpRequestUtil.request("/api/homework/homeworkSave", req);
                if (res != null && res.getCode() == 0) {
                    showAlert("成功", "作业修改成功");
                    loadAllHomeworks();
                    stage.close();
                } else {
                    String errorMsg = res != null ? res.getMsg() : "作业修改失败";
                    showAlert("错误", errorMsg);
                }
            });

            cancelButton.setOnAction(e -> stage.close());

            buttonBox.getChildren().addAll(saveButton, cancelButton);

            formBox.getChildren().addAll(studentBox, courseBox, contentBox, dueTimeBox, statusBox, buttonBox);
            borderPane.setCenter(formBox);

            Scene scene = new Scene(borderPane, 600, 500);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "显示编辑对话框失败: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * 初始化方法，在FXML加载完成后自动调用
     */
    @FXML
    private void initialize() {
        // 初始化表格样式
        initializeTableView();
        // 初始化时间选择器
        initializeTimeSelectors();
        // 加载学生和课程数据
        loadStudentAndCourseData();
        // 加载所有作业数据
        loadAllHomeworks();
    }

    /**
     * 初始化表格视图
     */
    private void initializeTableView() {
        try {
            if (dataTableView != null) {
                // 设置表格占位符
                Label placeholder = new Label("暂无作业数据\n点击上方\"添加作业\"按钮添加新的作业");
                placeholder.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14; -fx-text-alignment: center;");
                dataTableView.setPlaceholder(placeholder);

                System.out.println("表格视图初始化完成");
            } else {
                System.err.println("dataTableView is null - FXML injection failed");
            }
        } catch (Exception e) {
            System.err.println("初始化表格视图时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 初始化时间选择器
     */
    private void initializeTimeSelectors() {
        try {
            // 检查组件是否已正确注入
            if (hourComboBox == null) {
                System.err.println("hourComboBox is null - FXML injection failed");
                return;
            }
            if (minuteComboBox == null) {
                System.err.println("minuteComboBox is null - FXML injection failed");
                return;
            }
            if (statusComboBox == null) {
                System.err.println("statusComboBox is null - FXML injection failed");
                return;
            }
            if (dueDatePicker == null) {
                System.err.println("dueDatePicker is null - FXML injection failed");
                return;
            }

            // 初始化小时选择器 (0-23)
            hourComboBox.getItems().clear();
            for (int i = 0; i < 24; i++) {
                hourComboBox.getItems().add(String.format("%02d", i));
            }
            hourComboBox.setValue("23"); // 默认23点

            // 初始化分钟选择器 (0-59分钟，每5分钟一个选项)
            minuteComboBox.getItems().clear();
            for (int i = 0; i < 60; i += 5) {
                minuteComboBox.getItems().add(String.format("%02d", i));
            }
            minuteComboBox.setValue("55"); // 默认55分

            // 初始化状态选择器
            statusComboBox.getItems().clear();
            statusComboBox.getItems().addAll("未提交", "已提交", "已批改");
            statusComboBox.setValue("未提交"); // 默认未提交

            // 设置默认截止日期为一周后
            dueDatePicker.setValue(java.time.LocalDate.now().plusDays(7));

            // 添加事件监听器来更新时间选择器显示
            dueDatePicker.setOnAction(e -> {
                System.out.println("日期选择器值改变: " + dueDatePicker.getValue());
                updateTimePickerDisplay();
            });

            hourComboBox.setOnAction(e -> {
                System.out.println("小时选择器值改变: " + hourComboBox.getValue());
                updateTimePickerDisplay();
            });

            minuteComboBox.setOnAction(e -> {
                System.out.println("分钟选择器值改变: " + minuteComboBox.getValue());
                updateTimePickerDisplay();
            });

            System.out.println("时间选择器初始化成功");
            System.out.println("默认日期: " + dueDatePicker.getValue());
            System.out.println("默认小时: " + hourComboBox.getValue());
            System.out.println("默认分钟: " + minuteComboBox.getValue());

            // 初始化时间选择器显示
            updateTimePickerDisplay();
        } catch (Exception e) {
            System.err.println("初始化时间选择器时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 加载学生和课程数据
     */
    private void loadStudentAndCourseData() {
        // 加载学生数据
        DataRequest studentReq = new DataRequest();
        studentReq.add("numName", "");
        DataResponse studentRes = HttpRequestUtil.request("/api/student/getStudentList", studentReq);
        if (studentRes != null && studentRes.getCode() == 0) {
            allStudentList = (List<Map<String, Object>>) studentRes.getData();
            updateStudentComboBox();
        }

        // 加载课程数据
        DataRequest courseReq = new DataRequest();
        courseReq.add("numName", "");
        DataResponse courseRes = HttpRequestUtil.request("/api/course/getCourseList", courseReq);
        if (courseRes != null && courseRes.getCode() == 0) {
            allCourseList = (List<Map<String, Object>>) courseRes.getData();
            updateCourseComboBox();
        }
    }

    /**
     * 更新学生下拉框
     */
    private void updateStudentComboBox() {
        // 为学生数据添加studentId字段（使用personId）
        for (Map<String, Object> student : allStudentList) {
            if (!student.containsKey("studentId") && student.containsKey("personId")) {
                student.put("studentId", student.get("personId"));
            }
        }

        studentComboBox.setItems(FXCollections.observableArrayList(allStudentList));
        studentComboBox.setConverter(new StringConverter<Map<String, Object>>() {
            @Override
            public String toString(Map<String, Object> object) {
                if (object == null) return null;
                return object.get("num") + " - " + object.get("name") + " (" + object.get("className") + ")";
            }

            @Override
            public Map<String, Object> fromString(String string) {
                return null;
            }
        });

        System.out.println("学生下拉框已更新，学生数量: " + allStudentList.size());
    }

    /**
     * 更新课程下拉框
     */
    private void updateCourseComboBox() {
        courseComboBox.setItems(FXCollections.observableArrayList(allCourseList));
        courseComboBox.setConverter(new StringConverter<Map<String, Object>>() {
            @Override
            public String toString(Map<String, Object> object) {
                if (object == null) return null;
                return object.get("num") + " - " + object.get("name") + " (" + object.get("credit") + "学分)";
            }

            @Override
            public Map<String, Object> fromString(String string) {
                return null;
            }
        });
    }

    /**
     * 更新时间选择器的显示文本
     */
    private void updateTimePickerDisplay() {
        try {
            if (dueDatePicker != null && hourComboBox != null && minuteComboBox != null) {
                String selectedDate = dueDatePicker.getValue() != null ? dueDatePicker.getValue().toString() : "";
                String selectedHour = hourComboBox.getValue() != null ? hourComboBox.getValue() : "23";
                String selectedMinute = minuteComboBox.getValue() != null ? minuteComboBox.getValue() : "55";

                if (dueDatePicker.getValue() != null) {
                    // 更新时间选择器的提示文本，显示完整的时间
                    String timeText = selectedDate + " " + selectedHour + ":" + selectedMinute;

                    // 更新小时选择器的提示文本
                    if (hourComboBox.getValue() != null) {
                        hourComboBox.setPromptText(selectedHour + "时");
                    }

                    // 更新分钟选择器的提示文本
                    if (minuteComboBox.getValue() != null) {
                        minuteComboBox.setPromptText(selectedMinute + "分");
                    }

                    // 更新日期选择器的提示文本
                    dueDatePicker.setPromptText(selectedDate);

                    System.out.println("当前选择的截止时间: " + timeText);
                }
            }
        } catch (Exception e) {
            System.err.println("更新时间选择器显示时出错: " + e.getMessage());
        }
    }



    /**
     * 刷新列表按钮点击事件
     */
    @FXML
    private void onRefreshListButtonClick() {
        loadAllHomeworks();
        showAlert("提示", "作业列表已刷新");
    }
}
