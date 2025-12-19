package com.teach.javafx.controller;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.*;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FamilyMemberController 登录交互控制类 对应 family-member_panel.fxml  对应于学生管理的后台业务处理的控制器，主要获取数据和保存数据的方法不同
 *
 * @FXML 属性 对应fxml文件中的
 * @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class FamilyMemberController extends ToolController {

    @FXML
    private TableView<Map> dataTableView;  //学生信息表
      //学生信息表 编号列
    @FXML
    private TableColumn<Map, String> nameColumn; //学生信息表 名称列
    @FXML
    private TableColumn<Map, String> relationColumn;  //学生信息表 院系列
    @FXML
    private TableColumn<Map, String> phoneColumn; //学生信息表 专业列
    @FXML
    private TableColumn<Map, String> ageColumn; //学生信息表 班级列
    @FXML
    private TableColumn<Map, String> unitColumn; //学生信息表 证件号码列
    @FXML
    private TableColumn<Map, String> genderColumn; //学生信息表 性别列



    @FXML
    private TextField nameField;  //
    @FXML
    private TextField relationField; //
    @FXML
    private TextField phoneField; //学生信息  专业输入域
    @FXML
    private TextField ageField; //学生信息  班级输入域
    @FXML
    private TextField unitField; //学生信息  证件号码输入域
    @FXML
    private ComboBox<OptionItem> genderComboBox;  //学生信息  性别输入域



    @FXML
    private TextField relationNameTextField;  //查询 姓名学号输入域

    Integer  memberId = null;
    Integer personId = null;

    private ArrayList<Map> familyMemberList = new ArrayList();  // 学生信息列表数据
    private List<OptionItem> genderList;   //性别选择列表数据
    private ObservableList<Map> observableList = FXCollections.observableArrayList();  // TableView渲染列表
    private final FamilyMemberController familyMemberController = null;

    /**
     * 将学生数据集合设置到面板上显示
     */
    private void setTableViewData() {
        observableList.clear();//清空数据
        for (int j = 0; j < familyMemberList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(familyMemberList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    /**
     * 页面加载对象创建完成初始化方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */

    @FXML
    public void initialize(Integer personId) {
        this.personId = personId;
        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        res = HttpRequestUtil.request("/api/family-member/getFamilyMemberList1", req); //从后台获取所有学生信息列表集合
        if (res != null && res.getCode() == 0) {
            familyMemberList = (ArrayList<Map>) res.getData();
        }
        relationColumn.setCellValueFactory(new MapValueFactory<>("relation"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        genderColumn.setCellValueFactory(new MapValueFactory<>("genderName"));
        phoneColumn.setCellValueFactory(new MapValueFactory<>("phone"));
        ageColumn.setCellValueFactory(new MapValueFactory<>("age"));
        unitColumn.setCellValueFactory(new MapValueFactory<>("unit"));





        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        genderList = HttpRequestUtil.getDictionaryOptionItemList("XBM");

        genderComboBox.getItems().addAll(genderList);


    }

    /**
     * 清除学生表单中输入信息
     */
    public void clearPanel() {
        memberId = null;
        relationField.setText("");
        nameField.setText("");
        genderComboBox.getSelectionModel().select(-1);
        phoneField.setText("");
        ageField.setText("");
        unitField.setText("");

    }

    protected void changeFamilyMemberInfo() {
        Map<String,Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        memberId = CommonMethod.getInteger(form, "memberId");
        DataRequest req = new DataRequest();
        req.add("memberId", memberId);
        DataResponse res = HttpRequestUtil.request("/api/family-member/getFamilyMemberInfo", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        relationField.setText(CommonMethod.getString(form, "relation"));
        nameField.setText(CommonMethod.getString(form, "name"));
        genderComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(genderList, CommonMethod.getString(form, "gender")));
        phoneField.setText(CommonMethod.getString(form, "phone"));
        ageField.setText(CommonMethod.getString(form, "age"));
        unitField.setText(CommonMethod.getString(form, "unit"));


    }

    /**
     * 点击学生列表的某一行，根据personId ,从后台查询学生的基本信息，切换学生的编辑信息
     */

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        changeFamilyMemberInfo();
    }

    /**
     * 点击查询按钮，从从后台根据输入的串，查询匹配的学生在学生列表中显示
     */
    @FXML
    protected void onQueryButtonClick() {//查询
        String relationName = relationNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("relationName", relationName);
        DataResponse res = HttpRequestUtil.request("/api/family-member/getFamilyMemberList", req);
        if (res != null && res.getCode() == 0) {
            familyMemberList = (ArrayList<Map>) res.getData();
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
        memberId = CommonMethod.getInteger(form, "memberId");
        DataRequest req = new DataRequest();
        req.add("memberId", memberId);
        DataResponse res = HttpRequestUtil.request("/api/family-member/familyMemberDelete", req);
        if(res!= null) {
            if (res.getCode() == 0) {
                MessageDialog.showDialog("删除成功！");
                // 刷新当前学生的家庭成员列表，而不是查询所有家庭成员
                refreshCurrentStudentFamilyMembers();
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
        }
    }

    /**
     * 点击保存按钮，保存当前编辑的学生信息，如果是新添加的学生，后台添加学生
     */
    /**
     * 刷新当前学生的家庭成员列表
     */
    private void refreshCurrentStudentFamilyMembers() {
        if (personId == null) {
            return;
        }
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        DataResponse res = HttpRequestUtil.request("/api/family-member/getFamilyMemberList1", req);
        if (res != null && res.getCode() == 0) {
            familyMemberList = (ArrayList<Map>) res.getData();
            setTableViewData();
        }
    }

    @FXML
    protected void onSaveButtonClick() {
        if (relationField.getText().isEmpty()) {
            MessageDialog.showDialog("关系为空，不能修改");
            return;
        }
        Map<String,Object> form = new HashMap<>();
        form.put("relation", relationField.getText());
        form.put("name", nameField.getText());
        form.put("phone", phoneField.getText());
        form.put("age",  ageField.getText());
        form.put("unit",  unitField.getText());

        if (genderComboBox.getSelectionModel() != null && genderComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("gender", genderComboBox.getSelectionModel().getSelectedItem().getValue());

        DataRequest req = new DataRequest();

        req.add("personId", personId);
        req.add("memberId", memberId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/family-member/familyMemberEditSave", req);
        if(res == null) {  // 添加空指针检查
            MessageDialog.showDialog("请求失败，服务器未响应！");
            return;
        }
        if (res.getCode() == 0) {
            MessageDialog.showDialog("提交成功！");
            // 刷新当前学生的家庭成员列表，而不是查询所有家庭成员
            refreshCurrentStudentFamilyMembers();
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




}

