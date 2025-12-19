package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.LocalDateStringConverter;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.util.CommonMethod;
import com.teach.javafx.controller.base.MessageDialog;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * StudentController 登录交互控制类 对应 student_panel.fxml  对应于学生管理的后台业务处理的控制器，主要获取数据和保存数据的方法不同
 *
 * @FXML 属性 对应fxml文件中的
 * @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class StudentController extends ToolController {
    private ImageView photoImageView;
    @FXML
    private TableView<Map> dataTableView;  //学生信息表
    @FXML
    private TableColumn<Map, String> numColumn;   //学生信息表 编号列
    @FXML
    private TableColumn<Map, String> nameColumn; //学生信息表 名称列
    @FXML
    private TableColumn<Map, String> deptColumn;  //学生信息表 院系列
    @FXML
    private TableColumn<Map, String> majorColumn; //学生信息表 专业列
    @FXML
    private TableColumn<Map, String> classNameColumn; //学生信息表 班级列
    @FXML
    private TableColumn<Map, String> cardColumn; //学生信息表 证件号码列
    @FXML
    private TableColumn<Map, String> genderColumn; //学生信息表 性别列
    @FXML
    private TableColumn<Map, String> birthdayColumn; //学生信息表 出生日期列
    @FXML
    private TableColumn<Map, String> emailColumn; //学生信息表 邮箱列
    @FXML
    private TableColumn<Map, String> phoneColumn; //学生信息表 电话列
    @FXML
    private TableColumn<Map, String> addressColumn;//学生信息表 地址列
    @FXML
    private Button photoButton;  //照片显示和上传按钮

    @FXML
    private TextField numField; //学生信息  学号输入域
    @FXML
    private TextField nameField;  //学生信息  名称输入域
    @FXML
    private TextField deptField; //学生信息  院系输入域
    @FXML
    private TextField majorField; //学生信息  专业输入域
    @FXML
    private TextField classNameField; //学生信息  班级输入域
    @FXML
    private TextField cardField; //学生信息  证件号码输入域
    @FXML
    private ComboBox<OptionItem> genderComboBox;  //学生信息  性别输入域
    @FXML
    private DatePicker birthdayPick;  //学生信息  出生日期选择域
    @FXML
    private TextField emailField;  //学生信息  邮箱输入域
    @FXML
    private TextField phoneField;   //学生信息  电话输入域
    @FXML
    private TextField addressField;  //学生信息  地址输入域

    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域

    private Integer personId = null;  //当前编辑修改的学生的主键

    private ArrayList<Map> studentList = new ArrayList();  // 学生信息列表数据
    private List<OptionItem> genderList;   //性别选择列表数据
    private ObservableList<Map> observableList = FXCollections.observableArrayList();  // TableView渲染列表
    private final FamilyMemberController familyMemberController = null;

    /**
     * 将学生数据集合设置到面板上显示
     */
    private void setTableViewData() {
        observableList.clear();//清空数据
        for (int j = 0; j < studentList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(studentList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    /**
     * 页面加载对象创建完成初始化方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */

    @FXML
    public void initialize() {
        photoImageView = new ImageView();
        photoImageView.setFitHeight(100);
        photoImageView.setFitWidth(100);
        photoButton.setGraphic(photoImageView);
        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("numName", "");
        res = HttpRequestUtil.request("/api/student/getStudentList", req); //从后台获取所有学生信息列表集合
        if (res != null && res.getCode() == 0) {
            studentList = (ArrayList<Map>) res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory<>("num"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        deptColumn.setCellValueFactory(new MapValueFactory<>("dept"));
        majorColumn.setCellValueFactory(new MapValueFactory<>("major"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        cardColumn.setCellValueFactory(new MapValueFactory<>("card"));
        genderColumn.setCellValueFactory(new MapValueFactory<>("genderName"));
        birthdayColumn.setCellValueFactory(new MapValueFactory<>("birthday"));
        emailColumn.setCellValueFactory(new MapValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new MapValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new MapValueFactory<>("address"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        genderList = HttpRequestUtil.getDictionaryOptionItemList("XBM");

        genderComboBox.getItems().addAll(genderList);
        birthdayPick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));

    }

    /**
     * 清除学生表单中输入信息
     */
    public void clearPanel() {
        personId = null;
        numField.setText("");
        nameField.setText("");
        deptField.setText("");
        majorField.setText("");
        classNameField.setText("");
        cardField.setText("");
        genderComboBox.getSelectionModel().select(-1);
        birthdayPick.getEditor().setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
    }

    protected void changeStudentInfo() {
        Map<String,Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        personId = CommonMethod.getInteger(form, "personId");
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentInfo", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        numField.setText(CommonMethod.getString(form, "num"));
        nameField.setText(CommonMethod.getString(form, "name"));
        deptField.setText(CommonMethod.getString(form, "dept"));
        majorField.setText(CommonMethod.getString(form, "major"));
        classNameField.setText(CommonMethod.getString(form, "className"));
        cardField.setText(CommonMethod.getString(form, "card"));
        genderComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(genderList, CommonMethod.getString(form, "gender")));
        birthdayPick.getEditor().setText(CommonMethod.getString(form, "birthday"));
        emailField.setText(CommonMethod.getString(form, "email"));
        phoneField.setText(CommonMethod.getString(form, "phone"));
        addressField.setText(CommonMethod.getString(form, "address"));
        displayPhoto();
    }

    /**
     * 点击学生列表的某一行，根据personId ,从后台查询学生的基本信息，切换学生的编辑信息
     */

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        changeStudentInfo();
    }

    /**
     * 点击查询按钮，从从后台根据输入的串，查询匹配的学生在学生列表中显示
     */
    @FXML
    protected void onQueryButtonClick() {//查询
        String numName = numNameTextField.getText();

        if (numName == null || numName.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText(null);
            alert.setContentText("请输入学号或姓名");
            alert.showAndWait();
            return;
        }

        DataRequest req = new DataRequest();
        req.add("numName", numName);
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentList", req);

        if (res != null && res.getCode() == 0) {
            ArrayList<Map> queryResults = (ArrayList<Map>) res.getData();

            if (queryResults.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText(null);
                alert.setContentText("未找到符合条件的学生");
                alert.showAndWait();
                return;
            }

            // 显示查询结果窗口
            showStudentQueryResultWindow(queryResults, numName);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("查询学生失败");
            alert.showAndWait();
        }
    }

    /**
     * 显示学生查询结果窗口
     */
    private void showStudentQueryResultWindow(ArrayList<Map> queryResults, String queryText) {
        try {
            // 创建新窗口
            Stage stage = new Stage();
            stage.setTitle("学生查询结果: " + queryText);

            // 创建表格
            TableView<Map> resultTableView = new TableView<>();

            // 创建列
            TableColumn<Map, Object> numCol = new TableColumn<>("学号");
            numCol.setCellValueFactory(new MapValueFactory<>("num"));
            numCol.setPrefWidth(120.0);

            TableColumn<Map, Object> nameCol = new TableColumn<>("姓名");
            nameCol.setCellValueFactory(new MapValueFactory<>("name"));
            nameCol.setPrefWidth(100.0);

            TableColumn<Map, Object> deptCol = new TableColumn<>("学院");
            deptCol.setCellValueFactory(new MapValueFactory<>("dept"));
            deptCol.setPrefWidth(150.0);

            TableColumn<Map, Object> majorCol = new TableColumn<>("专业");
            majorCol.setCellValueFactory(new MapValueFactory<>("major"));
            majorCol.setPrefWidth(150.0);

            TableColumn<Map, Object> classNameCol = new TableColumn<>("班级");
            classNameCol.setCellValueFactory(new MapValueFactory<>("className"));
            classNameCol.setPrefWidth(100.0);

            TableColumn<Map, Object> genderCol = new TableColumn<>("性别");
            genderCol.setCellValueFactory(new MapValueFactory<>("genderName"));
            genderCol.setPrefWidth(80.0);

            TableColumn<Map, Object> birthdayCol = new TableColumn<>("出生日期");
            birthdayCol.setCellValueFactory(new MapValueFactory<>("birthday"));
            birthdayCol.setPrefWidth(120.0);

            // 添加列到表格
            resultTableView.getColumns().addAll(numCol, nameCol, deptCol, majorCol, classNameCol, genderCol, birthdayCol);

            // 设置数据
            ObservableList<Map> data = FXCollections.observableArrayList(queryResults);
            resultTableView.setItems(data);

            // 创建布局
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(resultTableView);
            borderPane.setPadding(new Insets(20));

            // 设置场景
            Scene scene = new Scene(borderPane, 900, 600);
            stage.setScene(scene);

            // 窗口关闭时的事件
            stage.setOnHidden(event -> {
                // 刷新主界面的学生列表，显示所有学生
                loadAllStudents();
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

    /**
     * 加载所有学生
     */
    private void loadAllStudents() {
        DataRequest req = new DataRequest();
        req.add("numName", "");
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentList", req);

        if (res != null && res.getCode() == 0) {
            studentList = (ArrayList<Map>) res.getData();
            setTableViewData();
        }
    }


    /**
     * 添加新学生， 清空输入信息， 输入相关信息，点击保存即可添加新的学生
     */
    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }

    /**
     * 点击删除按钮 删除当前编辑的学生的数据
     */
    @FXML
    protected void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        personId = CommonMethod.getInteger(form, "personId");
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        DataResponse res = HttpRequestUtil.request("/api/student/studentDelete", req);
        if(res!= null) {
            if (res.getCode() == 0) {
                MessageDialog.showDialog("删除成功！");
                loadAllStudents(); // 直接加载所有学生，不要弹出查询窗口
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
        }
    }

    /**
     * 点击保存按钮，保存当前编辑的学生信息，如果是新添加的学生，后台添加学生
     */
    @FXML
    protected void onSaveButtonClick() {
        if (numField.getText().isEmpty()) {
            MessageDialog.showDialog("学号为空，不能修改");
            return;
        }
        Map<String,Object> form = new HashMap<>();
        form.put("num", numField.getText());
        form.put("name", nameField.getText());
        form.put("dept", deptField.getText());
        form.put("major", majorField.getText());
        form.put("className", classNameField.getText());
        form.put("card", cardField.getText());
        if (genderComboBox.getSelectionModel() != null && genderComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("gender", genderComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("birthday", birthdayPick.getEditor().getText());
        form.put("email", emailField.getText());
        form.put("phone", phoneField.getText());
        form.put("address", addressField.getText());
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/student/studentEditSave", req);
        if (res.getCode() == 0) {
            personId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            loadAllStudents(); // 直接加载所有学生，不要弹出查询窗口
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    /**
     * doNew() doSave() doDelete() 重写 ToolController 中的方法， 实现选择 新建，保存，删除 对学生的增，删，改操作
     */
    public void doNew() {
        clearPanel();
    }

    public void doSave() {
        onSaveButtonClick();
    }

    public void doDelete() {
        onDeleteButtonClick();
    }

    /**
     * 导出学生信息表的示例 重写ToolController 中的doExport 这里给出了一个导出学生基本信息到Excl表的示例， 后台生成Excl文件数据，传回前台，前台将文件保存到本地
     */
    public void doExport() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName", numName);
        byte[] bytes = HttpRequestUtil.requestByteData("/api/student/getStudentListExcl", req);
        if (bytes != null) {
            try {
                FileChooser fileDialog = new FileChooser();
                fileDialog.setTitle("前选择保存的文件");
                fileDialog.setInitialDirectory(new File("C:/"));
                fileDialog.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
                File file = fileDialog.showSaveDialog(null);
                if (file != null) {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(bytes);
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


@FXML
protected void onFamilyButtonClick() throws IOException {
        if(personId == null) {
            MessageDialog.showDialog("请先选择学生");
            return;
        }
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/familymember-panel.fxml"));
    Parent root = loader.load();
    Stage stage = new Stage();
    stage.setWidth(1200);
    stage.setHeight(900);
    stage.setResizable(false);
    stage.setTitle("家庭成员");
    Scene scene = new Scene(root,1800,1200);
    stage.setScene(scene);
    stage.initModality(Modality.APPLICATION_MODAL);
    FamilyMemberController familyMemberController = loader.getController();
    familyMemberController.initialize(personId);
    stage.showAndWait();
}

public void displayPhoto(){
    if (personId == null) {
        return;
    }

    try {
        // 直接从本地读取照片
        String filePath = "D:/upload/photo/" + personId + ".jpg";
        File file = new File(filePath);

        if (file.exists()) {
            Image img = new Image(file.toURI().toString());
            photoImageView.setImage(img);
            System.out.println("成功加载照片: " + filePath);
        } else {
            // 如果本地文件不存在，尝试从服务器获取
            DataRequest req = new DataRequest();
            req.add("fileName", "photo/" + personId + ".jpg");
            byte[] bytes = HttpRequestUtil.requestByteData("/api/base/getFileByteData", req);

            if (bytes != null && bytes.length > 0) {
                System.out.println("从服务器获取照片数据，长度：" + bytes.length);
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                Image img = new Image(in);
                photoImageView.setImage(img);

                // 将服务器照片保存到本地
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    fos.write(bytes);
                }
            } else {
                System.out.println("未找到学生照片: " + personId);
                // 设置默认图片或清除当前图片
                photoImageView.setImage(null);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("加载照片失败: " + e.getMessage());
    }
}


@FXML

public void onPhotoButtonClick(){
    if (personId == null) {
        MessageDialog.showDialog("请先选择学生");
        return;
    }

    FileChooser fileDialog = new FileChooser();
    fileDialog.setTitle("图片上传");
    fileDialog.setInitialDirectory(new File("D:/"));
    fileDialog.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("JPG 文件", "*.jpg"));
    File file = fileDialog.showOpenDialog(null);
    if(file == null)
        return;

    try {
        // 直接使用文件路径上传
        String fileName = "photo/" + personId + ".jpg";
        DataResponse res = HttpRequestUtil.uploadFile("/api/base/uploadPhoto", file.getPath(), fileName);

        if (res != null && res.getCode() == 0) {
            // 上传成功，复制文件到本地目录
            String localDir = "D:/upload/photo";
            File localDirFile = new File(localDir);
            if (!localDirFile.exists()) {
                localDirFile.mkdirs();
            }

            // 复制文件到本地目录
            File destFile = new File(localDir + "/" + personId + ".jpg");
            Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            MessageDialog.showDialog("上传成功！");
            displayPhoto();
        } else {
            String errorMsg = (res != null) ? res.getMsg() : "上传失败，请检查网络或服务器设置";
            MessageDialog.showDialog(errorMsg);
        }
    } catch (Exception e) {
        e.printStackTrace();
        MessageDialog.showDialog("上传失败: " + e.getMessage());
    }
}


}
