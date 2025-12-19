
package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.*;
import java.util.List;

@Service
public class FamilyMemberService {
    private static final Logger log = LoggerFactory.getLogger(FamilyMemberService.class);
    private final PersonRepository personRepository;  //人员数据操作自动注入
    private final FamilyMemberRepository familyMemberRepository;  //学生数据操作自动注入
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;//学生数据操作自动注入
    private final UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    private final PasswordEncoder encoder;  //密码服务自动注入


    private final SystemService systemService;
    public FamilyMemberService(PersonRepository personRepository, FamilyMemberRepository FamilyMemberRepository, UserRepository userRepository, UserTypeRepository userTypeRepository, PasswordEncoder encoder,    SystemService systemService,StudentRepository studentRepository  ) {
        this.personRepository = personRepository;
        this.familyMemberRepository = FamilyMemberRepository;
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.studentRepository = studentRepository;
        this.encoder = encoder;

        this.systemService = systemService;
    }

    public Map<String,Object> getMapFromFamilyMember(FamilyMember s) {
        Map<String,Object> m = new HashMap<>();
        Student p;
        if(s == null)
            return m;
        m.put("relation",s.getRelation());
        m.put("name",s.getName());
        p = s.getStudent();
        if(p == null)
            return m;
        m.put("memberId", s.getMemberId());
        m.put("phone",s.getPhone());
        m.put("personId",p.getPersonId());
        m.put("age",s.getAge());
        m.put("unit",s.getUnit());
        String gender = s.getGender();
        m.put("gender",gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        return m;
    }

    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， StudentController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的赋值，
    // StudentController中的方法可以直接使用

    public List<Map<String,Object>> getFamilyMemberMapList(String relationName) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<FamilyMember> sList = familyMemberRepository.findFamilyMemberListByRelationName(relationName);  //数据库查询操作
        if (sList == null || sList.isEmpty())
            return dataList;
        for (FamilyMember  familyMember : sList) {
            dataList.add(getMapFromFamilyMember( familyMember));
        }
        return dataList;
    }
    public List<Map<String,Object>> getFamilyMemberMapList1(Integer personId) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<FamilyMember> sList = familyMemberRepository.findByStudentPersonId( personId);  //数据库查询操作
        if (sList == null || sList.isEmpty())
            return dataList;
        for (FamilyMember  familyMember : sList) {
            dataList.add(getMapFromFamilyMember( familyMember));
        }
        return dataList;
    }

    //在家庭信息列表里查询家庭成员
    public DataResponse getFamilyMemberList(DataRequest dataRequest) {
        String relationName = dataRequest.getString("relationName");
        List<Map<String,Object>> dataList = getFamilyMemberMapList(relationName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //初始化家庭信息列表
    public DataResponse getFamilyMemberList1(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        List<Map<String,Object>> dataList = getFamilyMemberMapList1(personId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }



    public DataResponse familyMemberDelete(DataRequest dataRequest) {
        Integer  memberId = dataRequest.getInteger("memberId");  //获取student_id值

        Optional< FamilyMember> op;
        if (memberId != null && memberId > 0) {
            op = familyMemberRepository.findById(memberId);   //查询获得实体对象
            op.ifPresent(familyMemberRepository::delete);

                  //首先数据库永久删除学生信息
                  // 然后数据库永久删除学生信息
            }

        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    public DataResponse getFamilyMemberInfo(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");// 获取主键值
        FamilyMember s = null;
        List<FamilyMember> op = familyMemberRepository.findByStudentPersonId(personId);
//        if (personId != null) {
//            op =  familyMemberRepository.findByStudentPersonId(personId); //根据学生主键从数据库查询学生的信息
////            if (op.isPresent()) {//如果查询到数据
////                s = op.get();//获取对象 赋给s
////            }
//        }
        ArrayList<Map<String,Object>> dataList = new ArrayList<>();
        for(FamilyMember f:op){
            Map<String,Object> m = getMapFromFamilyMember(f);
            dataList.add(m);
            }

        return CommonMethod.getReturnData(dataList); //这里回传包含学生信息的Map对象
    }

    public DataResponse familyMemberEditSave(DataRequest dataRequest) {//修改保存
        Integer personId = dataRequest.getInteger("personId");
        Integer memberId = dataRequest.getInteger("memberId");//获取主键值
        Map<String,Object> form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form, "num");  //Map 获取属性的值
        FamilyMember s = null;
        Person p;
        User u;
        Optional<FamilyMember> op;
        boolean isNew = false;
        if (memberId != null) {
            op = familyMemberRepository.findById(memberId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {//如果查询到数据
                s = op.get();//获取实体对象
            }
        }

        if (s == null) {
            s = new FamilyMember();
            assert personId != null;
            s.setStudent(studentRepository.findById(personId).get());
            isNew = true;
        }
        s.setRelation(CommonMethod.getString(form, "relation"));
        s.setName(CommonMethod.getString(form, "name"));
        s.setPhone(CommonMethod.getString(form, "phone"));
        s.setGender(CommonMethod.getString(form, "gender"));
        s.setAge(CommonMethod.getInteger(form, "age"));
        s.setUnit(CommonMethod.getString(form, "unit"));
        familyMemberRepository.save(s);  //修改保存学生信息
        systemService.modifyLog(s,isNew);
        return CommonMethod.getReturnMessageOK();
    }









    public DataResponse getFamilyMemberPageData(DataRequest dataRequest) {
        String relationName = dataRequest.getString("relationName");
        Integer cPage = dataRequest.getCurrentPage();
        int dataTotal = 0;
        int size = 40;
        List<Map<String,Object>> dataList = new ArrayList<>();
        Page<FamilyMember> page = null;
        Pageable pageable = PageRequest.of(cPage, size);
        page = familyMemberRepository.findFamilyMemberPageByRelationName(relationName, pageable);
        Map<String,Object> m;
        if (page != null) {
            dataTotal = (int) page.getTotalElements();
            List<FamilyMember> list = page.getContent();
            if (!list.isEmpty()) {
                for (FamilyMember familyMember : list) {
                    m = getMapFromFamilyMember(familyMember);
                    dataList.add(m);
                }
            }
        }
        Map<String,Object> data = new HashMap<>();
        data.put("dataTotal", dataTotal);
        data.put("pageSize", size);
        data.put("dataList", dataList);
        return CommonMethod.getReturnData(data);
    }

}
