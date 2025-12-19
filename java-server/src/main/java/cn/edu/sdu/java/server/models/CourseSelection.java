package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * CourseSelection 选课表实体类 保存学生选课信息
 * Integer selectionId 选课表主键 selection_id
 * Student student 关联学生对象 student_id 关联 student 表主键 person_id
 * Course course 关联课程对象 course_id 关联 course 表主键 course_id
 * Date selectionTime 选课时间
 * String status 选课状态 (0-已选课, 1-已退课)
 */
@Getter
@Setter
@Entity
@Table(name = "course_selection",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "course_id"}) // 学生和课程的组合唯一
        })
public class CourseSelection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "selection_id")
    private Integer selectionId; // 主键

    @ManyToOne
    @JoinColumn(name = "student_id") // 外键，关联学生表
    @JsonIgnore
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id") // 外键，关联课程表
    @JsonIgnore
    private Course course;

    @Column(name = "selection_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date selectionTime; // 选课时间

    @Column(name = "status", length = 1)
    private String status; // 选课状态 0-已选课, 1-已退课

    public CourseSelection() {
        this.selectionTime = new Date();
        this.status = "0"; // 默认为已选课状态
    }
}
