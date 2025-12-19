package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.services.HomeworkService;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * HomeworkController 作业管理控制器
 * 提供作业管理相关的REST API接口
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/homework")
public class HomeworkController {

    @Autowired
    private HomeworkService homeworkService;

    /**
     * 获取作业列表
     * 前台请求参数 numName 学号、姓名、课程编号或课程名称的查询串
     * 返回前端 存储作业信息的 MapList
     */
    @PostMapping("/getHomeworkList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getHomeworkList(@Valid @RequestBody DataRequest dataRequest) {
        return homeworkService.getHomeworkList(dataRequest);
    }

    /**
     * 保存作业信息
     * 前端把所有数据打包成一个Json对象作为参数传回后端
     * 如果是添加一条记录，homeworkId 为空，新建作业记录
     * 如果是编辑原来的信息，homeworkId不为空，则查询出实体对象，修改相关属性，保存后修改数据库信息
     */
    @PostMapping("/homeworkSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse homeworkSave(@Valid @RequestBody DataRequest dataRequest) {
        return homeworkService.homeworkSave(dataRequest);
    }

    /**
     * 删除作业信息
     * 前端将该记录的homeworkId回传到后端，方法从参数获取homeworkId，查出相关记录，调用delete方法删除
     */
    @PostMapping("/homeworkDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse homeworkDelete(@Valid @RequestBody DataRequest dataRequest) {
        return homeworkService.homeworkDelete(dataRequest);
    }

    /**
     * 获取作业详细信息
     * 前端点击作业列表时前端获取作业详细信息请求服务
     */
    @PostMapping("/getHomeworkInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getHomeworkInfo(@Valid @RequestBody DataRequest dataRequest) {
        return homeworkService.getHomeworkInfo(dataRequest);
    }
}
