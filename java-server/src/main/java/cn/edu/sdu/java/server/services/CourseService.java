package cn.edu.sdu.java.server.services;
import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.ArrayList;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public DataResponse getCourseList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");//课程编号或课程名
        if(numName == null)
            numName = "";
        List<Course> cList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        Course pc;
        for (Course c : cList) {
            m = new HashMap<>();
            m.put("courseId", c.getCourseId()+"");
            m.put("num",c.getNum());
            m.put("name",c.getName());
            m.put("credit",c.getCredit()+"");
            m.put("coursePath",c.getCoursePath());
            pc =c.getPreCourse();
            if(pc != null) {
                m.put("preCourse",pc.getName());
                m.put("preCourseName",pc.getName());
                m.put("preCourseId",pc.getCourseId());
            } else {
                m.put("preCourse","");
                m.put("preCourseName","");
                m.put("preCourseId",null);
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse courseSave(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        String coursePath = dataRequest.getString("coursePath");
        Integer credit = dataRequest.getInteger("credit");
        Integer preCourseId = dataRequest.getInteger("preCourseId");
        Optional<Course> op;
        Course c= null;

        if(courseId != null) {
            op = courseRepository.findById(courseId);
            if(op.isPresent())
                c= op.get();
        }
        if(c== null)
            c = new Course();
        Course pc =null;
        if(preCourseId != null) {
            op = courseRepository.findById(preCourseId);
            if(op.isPresent())
                pc = op.get();
        }
        c.setNum(num);
        c.setName(name);
        c.setCredit(credit);
        c.setCoursePath(coursePath);
        c.setPreCourse(pc);
        courseRepository.save(c);
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse courseDelete(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Optional<Course> op;
        Course c = null;
        if(courseId != null) {
            op = courseRepository.findById(courseId);
            if(op.isPresent()) {
                c = op.get();

                // 手动查询是否有其他课程将此课程作为前置课程
                List<Course> allCourses = courseRepository.findAll();
                List<Course> dependentCourses = new ArrayList<>();

                for (Course course : allCourses) {
                    if (course.getPreCourse() != null && course.getPreCourse().getCourseId().equals(c.getCourseId())) {
                        dependentCourses.add(course);
                    }
                }

                if (!dependentCourses.isEmpty()) {
                    // 构建错误消息，列出所有依赖的课程
                    StringBuilder errorMsg = new StringBuilder("该课程是");
                    for (int i = 0; i < dependentCourses.size(); i++) {
                        if (i > 0) {
                            errorMsg.append("、");
                        }
                        errorMsg.append(dependentCourses.get(i).getName());
                    }
                    errorMsg.append("的前置课程，无法删除");
                    return CommonMethod.getReturnMessageError(errorMsg.toString());
                }

                // 如果没有依赖，则删除课程
                courseRepository.delete(c);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

}
