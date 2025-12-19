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
 * CourseSelectionController 选课管理交互控制类 对应 courseselection-panel.fxml
 */
public class CourseSelectionController {
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
    private TableColumn<Map, String> courseNumColumn;
    @FXML
    private TableColumn<Map, String> courseNameColumn;
    @FXML
    private TableColumn<Map, String> creditColumn;
    @FXML
    private TableColumn<Map, String> selectionTimeColumn;
    @FXML
    private TableColumn<Map, FlowPane> operateColumn;

    @FXML
    private TextField numNameTextField;
    @FXML
    private ComboBox<Map<String, Object>> studentComboBox;
    @FXML
    private ComboBox<Map<String, Object>> courseComboBox;

    private List<Map<String, Object>> courseSelectionList = new ArrayList<>();
    private final ObservableList<Map<String, Object>> observableList = FXCollections.observableArrayList();
    private List<Map<String, Object>> allStudentList = new ArrayList<>();
    private List<Map<String, Object>> allCourseList = new ArrayList<>();

    @FXML
    private void onQueryButtonClick() {
        String numName = numNameTextField.getText();

        DataRequest req = new DataRequest();
        req.add("numName", numName);
        DataResponse res = HttpRequestUtil.request("/api/courseSelection/getCourseSelectionList", req);

        if (res != null && res.getCode() == 0) {
            List<Map<String, Object>> queryResults = (List<Map<String, Object>>) res.getData();

            if (queryResults.isEmpty() && numName != null && !numName.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText(null);
                alert.setContentText("未找到符合条件的选课记录");
                alert.showAndWait();
                return;
            }

            if (numName != null && !numName.trim().isEmpty()) {
                // 如果有查询条件，显示查询结果窗口
                showQueryResultWindow(queryResults, numName);
            } else {
                // 如果没有查询条件，直接更新主界面
                courseSelectionList = queryResults;
                setTableViewData();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("查询选课记录失败");
            alert.showAndWait();
        }
    }

    /**
     * 显示查询结果窗口
     */
    private void showQueryResultWindow(List<Map<String, Object>> queryResults, String queryText) {
        try {
            Stage stage = new Stage();
            stage.setTitle("选课查询结果: " + queryText);

            TableView<Map> tableView = new TableView<>();

            // 创建列
            TableColumn<Map, String> studentNumCol = new TableColumn<>("学号");
            studentNumCol.setCellValueFactory(new MapValueFactory<>("studentNum"));
            studentNumCol.setPrefWidth(120.0);

            TableColumn<Map, String> studentNameCol = new TableColumn<>("姓名");
            studentNameCol.setCellValueFactory(new MapValueFactory<>("studentName"));
            studentNameCol.setPrefWidth(100.0);

            TableColumn<Map, String> classNameCol = new TableColumn<>("班级");
            classNameCol.setCellValueFactory(new MapValueFactory<>("className"));
            classNameCol.setPrefWidth(120.0);

            TableColumn<Map, String> majorCol = new TableColumn<>("专业");
            majorCol.setCellValueFactory(new MapValueFactory<>("major"));
            majorCol.setPrefWidth(150.0);

            TableColumn<Map, String> courseNumCol = new TableColumn<>("课程编号");
            courseNumCol.setCellValueFactory(new MapValueFactory<>("courseNum"));
            courseNumCol.setPrefWidth(120.0);

            TableColumn<Map, String> courseNameCol = new TableColumn<>("课程名称");
            courseNameCol.setCellValueFactory(new MapValueFactory<>("courseName"));
            courseNameCol.setPrefWidth(200.0);

            TableColumn<Map, String> creditCol = new TableColumn<>("学分");
            creditCol.setCellValueFactory(new MapValueFactory<>("credit"));
            creditCol.setPrefWidth(80.0);

            TableColumn<Map, String> selectionTimeCol = new TableColumn<>("选课时间");
            selectionTimeCol.setCellValueFactory(new MapValueFactory<>("selectionTime"));
            selectionTimeCol.setPrefWidth(150.0);

            tableView.getColumns().addAll(studentNumCol, studentNameCol, classNameCol, majorCol,
                                         courseNumCol, courseNameCol, creditCol, selectionTimeCol);

            ObservableList<Map> data = FXCollections.observableArrayList();
            for (Map<String, Object> item : queryResults) {
                data.add(item);
            }
            tableView.setItems(data);

            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(tableView);
            borderPane.setPadding(new Insets(20));

            Scene scene = new Scene(borderPane, 1000, 600);
            stage.setScene(scene);

            stage.setOnHidden(event -> {
                loadAllCourseSelections();
            });

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

    /**
     * 加载所有选课记录
     */
    private void loadAllCourseSelections() {
        DataRequest req = new DataRequest();
        req.add("numName", "");
        DataResponse res = HttpRequestUtil.request("/api/courseSelection/getCourseSelectionList", req);

        if (res != null && res.getCode() == 0) {
            courseSelectionList = (List<Map<String, Object>>) res.getData();
            setTableViewData();
        }
    }

    private void setTableViewData() {
        observableList.clear();
        Map<String, Object> map;
        FlowPane flowPane;
        Button editButton, deleteButton;

        for (int j = 0; j < courseSelectionList.size(); j++) {
            map = courseSelectionList.get(j);
            flowPane = new FlowPane();
            flowPane.setHgap(10);
            flowPane.setAlignment(Pos.CENTER);

            editButton = new Button("修改");
            editButton.setId("edit" + j);
            editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 8 15; -fx-cursor: hand;");
            editButton.setOnAction(e -> {
                editItem(((Button) e.getSource()).getId());
            });

            deleteButton = new Button("删除");
            deleteButton.setId("delete" + j);
            deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 8 15; -fx-cursor: hand;");
            deleteButton.setOnAction(e -> {
                deleteItem(((Button) e.getSource()).getId());
            });

            flowPane.getChildren().addAll(editButton, deleteButton);
            map.put("operate", flowPane);
            observableList.add(map);
        }
        dataTableView.setItems(observableList);
    }

    @FXML
    private void onAddButtonClick() {
        // 验证输入
        Map<String, Object> selectedStudent = studentComboBox.getSelectionModel().getSelectedItem();
        Map<String, Object> selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            showAlert("错误", "请选择学生");
            return;
        }
        if (selectedCourse == null) {
            showAlert("错误", "请选择课程");
            return;
        }

        // 创建请求
        DataRequest req = new DataRequest();
        Map<String, Object> form = new HashMap<>();
        form.put("studentId", selectedStudent.get("studentId"));
        form.put("courseId", selectedCourse.get("courseId"));
        req.add("form", form);

        // 发送请求
        DataResponse res = HttpRequestUtil.request("/api/courseSelection/courseSelectionEditSave", req);
        if (res != null && res.getCode() == 0) {
            showAlert("成功", "选课记录添加成功");
            // 清空选择
            studentComboBox.getSelectionModel().clearSelection();
            courseComboBox.getSelectionModel().clearSelection();
            // 刷新列表
            loadAllCourseSelections();
        } else {
            String errorMsg = res != null ? res.getMsg() : "选课记录添加失败";
            showAlert("错误", errorMsg);
        }
    }

    public void editItem(String name) {
        if (name == null) return;
        int j = Integer.parseInt(name.substring(4));
        Map<String, Object> data = courseSelectionList.get(j);
        openEditDialog(data);
    }

    private void openEditDialog(Map<String, Object> selectionData) {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("编辑选课记录");
        dialog.setHeaderText("修改选课信息");

        ButtonType saveButtonType = new ButtonType("确认修改", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // 创建学生下拉框
        ComboBox<Map<String, Object>> editStudentComboBox = new ComboBox<>();
        editStudentComboBox.setItems(FXCollections.observableArrayList(allStudentList));
        editStudentComboBox.setConverter(new StringConverter<Map<String, Object>>() {
            @Override
            public String toString(Map<String, Object> student) {
                return student != null ? (String) student.get("displayText") : "";
            }
            @Override
            public Map<String, Object> fromString(String string) {
                return null;
            }
        });

        // 创建课程下拉框
        ComboBox<Map<String, Object>> editCourseComboBox = new ComboBox<>();
        editCourseComboBox.setItems(FXCollections.observableArrayList(allCourseList));
        editCourseComboBox.setConverter(new StringConverter<Map<String, Object>>() {
            @Override
            public String toString(Map<String, Object> course) {
                return course != null ? (String) course.get("displayText") : "";
            }
            @Override
            public Map<String, Object> fromString(String string) {
                return null;
            }
        });

        // 设置默认选中的学生
        for (Map<String, Object> student : allStudentList) {
            if (student.get("studentId").equals(selectionData.get("studentId"))) {
                editStudentComboBox.getSelectionModel().select(student);
                break;
            }
        }

        // 设置默认选中的课程
        for (Map<String, Object> course : allCourseList) {
            if (course.get("courseId").equals(selectionData.get("courseId"))) {
                editCourseComboBox.getSelectionModel().select(course);
                break;
            }
        }

        grid.add(new Label("学生:"), 0, 0);
        grid.add(editStudentComboBox, 1, 0);
        grid.add(new Label("课程:"), 0, 1);
        grid.add(editCourseComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> editStudentComboBox.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Map<String, Object> result = new HashMap<>();
                result.put("student", editStudentComboBox.getSelectionModel().getSelectedItem());
                result.put("course", editCourseComboBox.getSelectionModel().getSelectedItem());
                return result;
            }
            return null;
        });

        Optional<Map<String, Object>> result = dialog.showAndWait();

        result.ifPresent(formData -> {
            Map<String, Object> selectedStudent = (Map<String, Object>) formData.get("student");
            Map<String, Object> selectedCourse = (Map<String, Object>) formData.get("course");

            if (selectedStudent == null) {
                showAlert("错误", "请选择学生");
                return;
            }
            if (selectedCourse == null) {
                showAlert("错误", "请选择课程");
                return;
            }

            DataRequest req = new DataRequest();
            req.add("selectionId", selectionData.get("selectionId"));
            Map<String, Object> form = new HashMap<>();
            form.put("studentId", selectedStudent.get("studentId"));
            form.put("courseId", selectedCourse.get("courseId"));
            req.add("form", form);

            DataResponse res = HttpRequestUtil.request("/api/courseSelection/courseSelectionEditSave", req);
            if (res != null && res.getCode() == 0) {
                showAlert("成功", "选课记录修改成功");
                loadAllCourseSelections();
            } else {
                String errorMsg = res != null ? res.getMsg() : "选课记录修改失败";
                showAlert("错误", errorMsg);
            }
        });
    }

    public void deleteItem(String name) {
        if (name == null) return;
        int j = Integer.parseInt(name.substring(6));
        Map<String, Object> data = courseSelectionList.get(j);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText(null);
        alert.setContentText("确定要删除学生 " + data.get("studentName") + " 的课程 " + data.get("courseName") + " 选课记录吗？");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DataRequest req = new DataRequest();
                req.add("selectionId", data.get("selectionId"));

                DataResponse res = HttpRequestUtil.request("/api/courseSelection/courseSelectionDelete", req);
                if (res != null && res.getCode() == 0) {
                    showAlert("成功", "选课记录删除成功");
                    loadAllCourseSelections();
                } else {
                    String errorMsg = res != null ? res.getMsg() : "选课记录删除失败";
                    showAlert("错误", errorMsg);
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

    private void updateStudentOptions() {
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/courseSelection/getStudentOptions", req);
        if (res != null && res.getCode() == 0) {
            allStudentList = (List<Map<String, Object>>) res.getData();
            ObservableList<Map<String, Object>> studentOptions = FXCollections.observableArrayList(allStudentList);
            studentComboBox.setItems(studentOptions);
            studentComboBox.setConverter(new StringConverter<Map<String, Object>>() {
                @Override
                public String toString(Map<String, Object> student) {
                    return student != null ? (String) student.get("displayText") : "";
                }
                @Override
                public Map<String, Object> fromString(String string) {
                    return null;
                }
            });
        }
    }

    private void updateCourseOptions() {
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/courseSelection/getCourseOptions", req);
        if (res != null && res.getCode() == 0) {
            allCourseList = (List<Map<String, Object>>) res.getData();
            ObservableList<Map<String, Object>> courseOptions = FXCollections.observableArrayList(allCourseList);
            courseComboBox.setItems(courseOptions);
            courseComboBox.setConverter(new StringConverter<Map<String, Object>>() {
                @Override
                public String toString(Map<String, Object> course) {
                    return course != null ? (String) course.get("displayText") : "";
                }
                @Override
                public Map<String, Object> fromString(String string) {
                    return null;
                }
            });
        }
    }

    @FXML
    public void initialize() {
        // 设置表格列
        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        majorColumn.setCellValueFactory(new MapValueFactory<>("major"));
        courseNumColumn.setCellValueFactory(new MapValueFactory<>("courseNum"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        selectionTimeColumn.setCellValueFactory(new MapValueFactory<>("selectionTime"));
        operateColumn.setCellValueFactory(new MapValueFactory<>("operate"));

        // 初始化下拉框选项
        updateStudentOptions();
        updateCourseOptions();

        // 加载选课数据
        loadAllCourseSelections();
    }
}
