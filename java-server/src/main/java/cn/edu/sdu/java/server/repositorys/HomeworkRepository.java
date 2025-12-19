package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Homework;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Homework 数据操作接口，主要实现Homework数据的查询操作
 */
@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Integer> {
    
    /**
     * 根据学生编号或姓名、课程编号或名称查询作业列表
     */
    @Query(value = "from Homework h where ?1='' or h.student.person.num like %?1% or h.student.person.name like %?1% or h.course.num like %?1% or h.course.name like %?1%")
    List<Homework> findHomeworkListByNumName(String numName);

    /**
     * 根据学生ID查询作业列表
     */
    List<Homework> findByStudentPersonId(Integer studentId);

    /**
     * 根据课程ID查询作业列表
     */
    List<Homework> findByCourseCourseId(Integer courseId);

    /**
     * 根据学生ID和课程ID查询作业
     */
    List<Homework> findByStudentPersonIdAndCourseCourseId(Integer studentId, Integer courseId);

    /**
     * 分页查询作业列表
     */
    @Query(value = "from Homework h where ?1='' or h.student.person.num like %?1% or h.student.person.name like %?1% or h.course.num like %?1% or h.course.name like %?1%",
            countQuery = "SELECT count(h.homeworkId) from Homework h where ?1='' or h.student.person.num like %?1% or h.student.person.name like %?1% or h.course.num like %?1% or h.course.name like %?1%")
    Page<Homework> findHomeworkPageByNumName(String numName, Pageable pageable);
}
