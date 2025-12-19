package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Homework;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.HomeworkRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * HomeworkService 作业管理服务类
 */
@Service
public class HomeworkService {

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    /**
     * 获取作业列表
     */
    public DataResponse getHomeworkList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if (numName == null) {
            numName = "";
        }
        List<Homework> hList = homeworkRepository.findHomeworkListByNumName(numName);
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (Homework h : hList) {
            m = new HashMap<>();
            m.put("homeworkId", h.getHomeworkId() + "");
            m.put("content", h.getContent());
            
            // 学生信息
            Student student = h.getStudent();
            if (student != null && student.getPerson() != null) {
                Person person = student.getPerson();
                m.put("studentId", student.getPersonId() + "");
                m.put("studentNum", person.getNum());
                m.put("studentName", person.getName());
                m.put("className", student.getClassName());
                m.put("major", student.getMajor());
            }
            
            // 课程信息
            Course course = h.getCourse();
            if (course != null) {
                m.put("courseId", course.getCourseId() + "");
                m.put("courseNum", course.getNum());
                m.put("courseName", course.getName());
                m.put("credit", course.getCredit() + "");
            }
            
            // 时间信息
            if (h.getAssignTime() != null) {
                m.put("assignTime", sdf.format(h.getAssignTime()));
            }
            if (h.getDueTime() != null) {
                m.put("dueTime", sdf.format(h.getDueTime()));
            }
            if (h.getSubmitTime() != null) {
                m.put("submitTime", sdf.format(h.getSubmitTime()));
            }
            
            // 状态信息
            m.put("status", h.getStatus());
            String statusName = "";
            if ("0".equals(h.getStatus())) {
                statusName = "未提交";
            } else if ("1".equals(h.getStatus())) {
                statusName = "已提交";
            } else if ("2".equals(h.getStatus())) {
                statusName = "已批改";
            }
            m.put("statusName", statusName);
            
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    /**
     * 保存作业信息
     */
    public DataResponse homeworkSave(DataRequest dataRequest) {
        Integer homeworkId = dataRequest.getInteger("homeworkId");
        Integer studentId = dataRequest.getInteger("studentId");
        Integer courseId = dataRequest.getInteger("courseId");
        String content = dataRequest.getString("content");
        String assignTimeStr = dataRequest.getString("assignTime");
        String dueTimeStr = dataRequest.getString("dueTime");
        String status = dataRequest.getString("status");
        
        Optional<Homework> op;
        Homework h = null;
        
        if (homeworkId != null) {
            op = homeworkRepository.findById(homeworkId);
            if (op.isPresent()) {
                h = op.get();
            }
        }
        if (h == null) {
            h = new Homework();
        }
        
        // 设置学生
        Student student = null;
        if (studentId != null) {
            Optional<Student> sOp = studentRepository.findById(studentId);
            if (sOp.isPresent()) {
                student = sOp.get();
            }
        }
        
        // 设置课程
        Course course = null;
        if (courseId != null) {
            Optional<Course> cOp = courseRepository.findById(courseId);
            if (cOp.isPresent()) {
                course = cOp.get();
            }
        }
        
        h.setStudent(student);
        h.setCourse(course);
        h.setContent(content);
        h.setStatus(status != null ? status : "0");
        
        // 设置时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (assignTimeStr != null && !assignTimeStr.isEmpty()) {
                h.setAssignTime(sdf.parse(assignTimeStr));
            } else {
                h.setAssignTime(new Date()); // 默认当前时间
            }
            if (dueTimeStr != null && !dueTimeStr.isEmpty()) {
                h.setDueTime(sdf.parse(dueTimeStr));
            }
        } catch (Exception e) {
            h.setAssignTime(new Date());
        }
        
        homeworkRepository.save(h);
        return CommonMethod.getReturnMessageOK();
    }

    /**
     * 删除作业
     */
    public DataResponse homeworkDelete(DataRequest dataRequest) {
        Integer homeworkId = dataRequest.getInteger("homeworkId");
        Optional<Homework> op;
        Homework h = null;
        if (homeworkId != null) {
            op = homeworkRepository.findById(homeworkId);
            if (op.isPresent()) {
                h = op.get();
                homeworkRepository.delete(h);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    /**
     * 获取作业详细信息
     */
    public DataResponse getHomeworkInfo(DataRequest dataRequest) {
        Integer homeworkId = dataRequest.getInteger("homeworkId");
        Homework h = null;
        Optional<Homework> op;
        if (homeworkId != null) {
            op = homeworkRepository.findById(homeworkId);
            if (op.isPresent()) {
                h = op.get();
            }
        }
        
        Map<String, Object> m = new HashMap<>();
        if (h != null) {
            m.put("homeworkId", h.getHomeworkId() + "");
            m.put("content", h.getContent());
            m.put("status", h.getStatus());
            
            if (h.getStudent() != null) {
                m.put("studentId", h.getStudent().getPersonId() + "");
            }
            if (h.getCourse() != null) {
                m.put("courseId", h.getCourse().getCourseId() + "");
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (h.getAssignTime() != null) {
                m.put("assignTime", sdf.format(h.getAssignTime()));
            }
            if (h.getDueTime() != null) {
                m.put("dueTime", sdf.format(h.getDueTime()));
            }
            if (h.getSubmitTime() != null) {
                m.put("submitTime", sdf.format(h.getSubmitTime()));
            }
        }
        
        return CommonMethod.getReturnData(m);
    }
}
