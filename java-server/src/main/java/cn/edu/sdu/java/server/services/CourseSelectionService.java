package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CourseSelectionService {
    
    private final CourseSelectionRepository courseSelectionRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    
    public CourseSelectionService(CourseSelectionRepository courseSelectionRepository,
                                StudentRepository studentRepository,
                                CourseRepository courseRepository) {
        this.courseSelectionRepository = courseSelectionRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }
    
    /**
     * 将CourseSelection对象转换为Map
     */
    public Map<String, Object> getMapFromCourseSelection(CourseSelection cs) {
        Map<String, Object> m = new HashMap<>();
        if (cs == null) return m;
        
        m.put("selectionId", cs.getSelectionId());
        m.put("status", cs.getStatus());
        
        // 格式化选课时间
        if (cs.getSelectionTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            m.put("selectionTime", sdf.format(cs.getSelectionTime()));
        } else {
            m.put("selectionTime", "");
        }
        
        // 获取学生信息
        Student student = cs.getStudent();
        if (student != null && student.getPerson() != null) {
            Person p = student.getPerson();
            m.put("studentId", p.getPersonId());
            m.put("studentNum", p.getNum());
            m.put("studentName", p.getName());
            m.put("major", student.getMajor());
            m.put("className", student.getClassName());
            m.put("dept", p.getDept());
        }
        
        // 获取课程信息
        Course course = cs.getCourse();
        if (course != null) {
            m.put("courseId", course.getCourseId());
            m.put("courseNum", course.getNum());
            m.put("courseName", course.getName());
            m.put("credit", course.getCredit());
            
            // 获取前置课程信息
            if (course.getPreCourse() != null) {
                m.put("preCourseName", course.getPreCourse().getName());
            } else {
                m.put("preCourseName", "无");
            }
        }
        
        return m;
    }
    
    /**
     * 获取选课列表
     */
    public List<Map<String, Object>> getCourseSelectionMapList(String numName) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<CourseSelection> csList;
        
        if (numName == null || numName.trim().isEmpty()) {
            csList = courseSelectionRepository.findAllActiveCourseSelections();
        } else {
            csList = courseSelectionRepository.findCourseSelectionListByNumName(numName);
        }
        
        if (csList != null && !csList.isEmpty()) {
            for (CourseSelection cs : csList) {
                dataList.add(getMapFromCourseSelection(cs));
            }
        }
        return dataList;
    }
    
    /**
     * 获取选课列表接口
     */
    public DataResponse getCourseSelectionList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> dataList = getCourseSelectionMapList(numName);
        return CommonMethod.getReturnData(dataList);
    }
    
    /**
     * 获取选课详细信息
     */
    public DataResponse getCourseSelectionInfo(DataRequest dataRequest) {
        Integer selectionId = dataRequest.getInteger("selectionId");
        CourseSelection cs = null;
        Optional<CourseSelection> op;
        
        if (selectionId != null) {
            op = courseSelectionRepository.findById(selectionId);
            if (op.isPresent()) {
                cs = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromCourseSelection(cs));
    }
    
    /**
     * 保存选课信息（新增或修改）
     */
    public DataResponse courseSelectionEditSave(DataRequest dataRequest) {
        Integer selectionId = dataRequest.getInteger("selectionId");
        Map<String, Object> form = dataRequest.getMap("form");
        
        Integer studentId = CommonMethod.getInteger(form, "studentId");
        Integer courseId = CommonMethod.getInteger(form, "courseId");
        
        if (studentId == null || courseId == null) {
            return CommonMethod.getReturnMessageError("学生和课程信息不能为空！");
        }
        
        // 检查学生是否存在
        Optional<Student> studentOp = studentRepository.findByPersonPersonId(studentId);
        if (!studentOp.isPresent()) {
            return CommonMethod.getReturnMessageError("学生不存在！");
        }
        
        // 检查课程是否存在
        Optional<Course> courseOp = courseRepository.findById(courseId);
        if (!courseOp.isPresent()) {
            return CommonMethod.getReturnMessageError("课程不存在！");
        }
        
        CourseSelection cs = null;
        boolean isNew = false;
        
        if (selectionId != null) {
            // 修改现有选课记录
            Optional<CourseSelection> op = courseSelectionRepository.findById(selectionId);
            if (op.isPresent()) {
                cs = op.get();
            }
        }
        
        if (cs == null) {
            // 新增选课记录
            // 检查是否已经选择过该课程
            Long count = courseSelectionRepository.countByStudentAndCourse(studentId, courseId);
            if (count > 0) {
                return CommonMethod.getReturnMessageError("该学生已经选择过此课程！");
            }
            
            cs = new CourseSelection();
            cs.setStudent(studentOp.get());
            cs.setCourse(courseOp.get());
            cs.setSelectionTime(new Date());
            cs.setStatus("0"); // 已选课状态
            isNew = true;
        } else {
            // 修改选课记录时，如果更换了学生或课程，需要检查重复
            if (!cs.getStudent().getPersonId().equals(studentId) || 
                !cs.getCourse().getCourseId().equals(courseId)) {
                Long count = courseSelectionRepository.countByStudentAndCourse(studentId, courseId);
                if (count > 0) {
                    return CommonMethod.getReturnMessageError("该学生已经选择过此课程！");
                }
                cs.setStudent(studentOp.get());
                cs.setCourse(courseOp.get());
            }
        }
        
        courseSelectionRepository.save(cs);
        return CommonMethod.getReturnData(cs.getSelectionId());
    }
    
    /**
     * 删除选课记录
     */
    public DataResponse courseSelectionDelete(DataRequest dataRequest) {
        Integer selectionId = dataRequest.getInteger("selectionId");
        
        if (selectionId != null && selectionId > 0) {
            Optional<CourseSelection> op = courseSelectionRepository.findById(selectionId);
            if (op.isPresent()) {
                courseSelectionRepository.delete(op.get());
                System.out.println("成功删除选课记录: " + selectionId);
            } else {
                System.out.println("未找到选课记录, selectionId: " + selectionId);
            }
        } else {
            System.out.println("无效的selectionId: " + selectionId);
        }
        
        return CommonMethod.getReturnMessageOK();
    }
    
    /**
     * 获取所有学生列表（用于下拉框）
     */
    public DataResponse getStudentOptions(DataRequest dataRequest) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Student> studentList = studentRepository.findAll();
        
        for (Student student : studentList) {
            if (student.getPerson() != null) {
                Map<String, Object> m = new HashMap<>();
                m.put("studentId", student.getPersonId());
                m.put("studentNum", student.getPerson().getNum());
                m.put("studentName", student.getPerson().getName());
                m.put("major", student.getMajor());
                m.put("className", student.getClassName());
                m.put("displayText", student.getPerson().getNum() + " - " + student.getPerson().getName());
                dataList.add(m);
            }
        }
        
        return CommonMethod.getReturnData(dataList);
    }
    
    /**
     * 获取所有课程列表（用于下拉框）
     */
    public DataResponse getCourseOptions(DataRequest dataRequest) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Course> courseList = courseRepository.findAll();
        
        for (Course course : courseList) {
            Map<String, Object> m = new HashMap<>();
            m.put("courseId", course.getCourseId());
            m.put("courseNum", course.getNum());
            m.put("courseName", course.getName());
            m.put("credit", course.getCredit());
            m.put("displayText", course.getNum() + " - " + course.getName() + " (" + course.getCredit() + "学分)");
            dataList.add(m);
        }
        
        return CommonMethod.getReturnData(dataList);
    }
}
