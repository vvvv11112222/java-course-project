
package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.FamilyMemberService;
import cn.edu.sdu.java.server.services.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * StudentController 主要是为学生管理数据管理提供的Web请求服务
 */

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)//允许跨域访问
@RestController//允许使用@RestController
@RequestMapping("/api/family-member")//映射路径

public class FamilyMemberController {
    private final FamilyMemberService familyMemberService;//注入FamilyMemberService

    public FamilyMemberController(FamilyMemberService familyMemberService) {

        this.familyMemberService = familyMemberService;
    }//构造函数

    /**
     * getStudentList 学生管理 点击查询按钮请求
     * 前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     *
     */


    @PostMapping("/getFamilyMemberList")//映射路径
    @PreAuthorize("hasRole('ADMIN')or hasRole('STUDENT')")//权限控制
    //功能：在家庭信息列表里查询家庭成员
    public DataResponse getFamilyMemberList(@Valid @RequestBody DataRequest dataRequest) {
        return familyMemberService.getFamilyMemberList(dataRequest);
    }
    @PostMapping("/getFamilyMemberList1")//映射路径
    @PreAuthorize("hasRole('ADMIN')or hasRole('STUDENT')")//权限控制
    //功能：初始化家庭信息列表
    public DataResponse getFamilyMemberList1(@Valid @RequestBody DataRequest dataRequest) {
        return familyMemberService.getFamilyMemberList1(dataRequest);
    }


    /**
     * studentDelete 删除学生信息Web服务 Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
     * 这里注意删除顺序，应为user关联person,Student关联Person 所以要先删除Student,User，再删除Person
     *
     * @param dataRequest 前端personId 要删除的学生的主键 person_id
     * @return 正常操作
     */

    @PostMapping("/familyMemberDelete")
    public DataResponse familyMemberDelete(@Valid @RequestBody DataRequest dataRequest) {//
        return familyMemberService.familyMemberDelete(dataRequest);
    }

    /**
     * getStudentInfo 前端点击学生列表时前端获取学生详细信息请求服务
     *
     * @param dataRequest 从前端获取 personId 查询学生信息的主键 person_id
     * @return 根据personId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getFamilyMemberInfo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getFamilyMemberInfo(@Valid @RequestBody DataRequest dataRequest) {
        return familyMemberService.getFamilyMemberInfo(dataRequest);
    }

    /**
     * studentEditSave 前端学生信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Person, User,Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * personId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     *
     * @return 新建修改学生的主键 student_id 返回前端
     */
    @PostMapping("/familyMemberEditSave")
    @PreAuthorize(" hasRole('ADMIN') or  hasRole('STUDENT')")
    public DataResponse familyMemberEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return familyMemberService.familyMemberEditSave(dataRequest);
    }




    @PostMapping("/getFamilyMemberPageData")
    @PreAuthorize(" hasRole('ADMIN') or  hasRole('STUDENT')")
    public DataResponse getFamilyMemberPageData(@Valid @RequestBody DataRequest dataRequest) {
        return familyMemberService.getFamilyMemberPageData(dataRequest);//返回分页数据，返回分页数据，包含分页信息，和数据列表
    }





}
