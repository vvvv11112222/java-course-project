package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.CourseSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CourseSelection 数据操作接口，主要实现选课数据的查询操作
 */
@Repository
public interface CourseSelectionRepository extends JpaRepository<CourseSelection, Integer> {
    
    /**
     * 根据学生ID和课程ID查找选课记录
     */
    Optional<CourseSelection> findByStudentPersonIdAndCourseCourseId(Integer studentId, Integer courseId);
    
    /**
     * 根据学生ID查找该学生的所有选课记录
     */
    List<CourseSelection> findByStudentPersonId(Integer studentId);
    
    /**
     * 根据课程ID查找选择该课程的所有学生
     */
    List<CourseSelection> findByCourseCourseId(Integer courseId);
    
    /**
     * 根据选课状态查找选课记录
     */
    List<CourseSelection> findByStatus(String status);
    
    /**
     * 根据学生姓名或学号、课程名称或编号进行模糊查询
     */
    @Query(value = "SELECT cs FROM CourseSelection cs " +
            "WHERE (?1 = '' OR cs.student.person.num LIKE %?1% OR cs.student.person.name LIKE %?1% " +
            "OR cs.course.num LIKE %?1% OR cs.course.name LIKE %?1%) " +
            "AND cs.status = '0' " +
            "ORDER BY cs.selectionTime DESC")
    List<CourseSelection> findCourseSelectionListByNumName(String numName);
    
    /**
     * 查找所有有效的选课记录（状态为0）
     */
    @Query(value = "SELECT cs FROM CourseSelection cs WHERE cs.status = '0' ORDER BY cs.selectionTime DESC")
    List<CourseSelection> findAllActiveCourseSelections();
    
    /**
     * 检查学生是否已经选择了某门课程
     */
    @Query(value = "SELECT COUNT(cs) FROM CourseSelection cs " +
            "WHERE cs.student.personId = ?1 AND cs.course.courseId = ?2 AND cs.status = '0'")
    Long countByStudentAndCourse(Integer studentId, Integer courseId);
}
