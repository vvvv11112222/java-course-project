package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.CourseSelectionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * CourseSelectionController 选课管理控制器
 * 处理选课相关的HTTP请求
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courseSelection")
public class CourseSelectionController {
    
    private final CourseSelectionService courseSelectionService;
    
    public CourseSelectionController(CourseSelectionService courseSelectionService) {
        this.courseSelectionService = courseSelectionService;
    }
    
    /**
     * 获取选课列表
     * 前台请求参数 numName 学号、姓名、课程编号或课程名称的查询串
     * 返回前端 存储选课信息的 MapList
     */
    @PostMapping("/getCourseSelectionList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getCourseSelectionList(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.getCourseSelectionList(dataRequest);
    }
    
    /**
     * 获取选课详细信息
     * 前台请求参数 selectionId 选课记录ID
     * 返回前端 选课详细信息Map
     */
    @PostMapping("/getCourseSelectionInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getCourseSelectionInfo(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.getCourseSelectionInfo(dataRequest);
    }
    
    /**
     * 保存选课信息（新增或修改）
     * 前台把所有数据打包成一个Json对象作为参数传回后端
     * 如果是添加一条记录，selectionId 为空，新建选课记录
     * 如果是编辑原来的信息，selectionId不为空，修改现有选课记录
     */
    @PostMapping("/courseSelectionEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseSelectionEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.courseSelectionEditSave(dataRequest);
    }
    
    /**
     * 删除选课记录
     * 前台请求参数 selectionId 选课记录ID
     */
    @PostMapping("/courseSelectionDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseSelectionDelete(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.courseSelectionDelete(dataRequest);
    }
    
    /**
     * 获取学生选项列表（用于下拉框）
     * 返回所有学生的基本信息，用于前端下拉框选择
     */
    @PostMapping("/getStudentOptions")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentOptions(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.getStudentOptions(dataRequest);
    }
    
    /**
     * 获取课程选项列表（用于下拉框）
     * 返回所有课程的基本信息，用于前端下拉框选择
     */
    @PostMapping("/getCourseOptions")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getCourseOptions(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.getCourseOptions(dataRequest);
    }
}
