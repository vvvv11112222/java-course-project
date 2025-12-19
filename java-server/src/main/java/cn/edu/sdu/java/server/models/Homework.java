package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Homework 作业表实体类 保存学生作业信息
 * Integer homeworkId 作业表主键 homework_id
 * Student student 关联学生对象 student_id 关联 student 表主键 person_id
 * Course course 关联课程对象 course_id 关联 course 表主键 course_id
 * String content 作业内容
 * Date assignTime 布置时间
 * Date dueTime 截止时间
 * Date submitTime 提交时间
 * String status 作业状态 (0-未提交, 1-已提交, 2-已批改)
 */
@Getter
@Setter
@Entity
@Table(name = "homework",
        uniqueConstraints = {
        })
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "homework_id")
    private Integer homeworkId; // 主键

    @ManyToOne
    @JoinColumn(name = "student_id") // 外键，关联学生表
    @JsonIgnore
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id") // 外键，关联课程表
    @JsonIgnore
    private Course course;

    @Size(max = 1000)
    private String content; // 作业内容

    @Temporal(TemporalType.TIMESTAMP)
    private Date assignTime; // 布置时间

    @Temporal(TemporalType.TIMESTAMP)
    private Date dueTime; // 截止时间

    @Temporal(TemporalType.TIMESTAMP)
    private Date submitTime; // 提交时间

    @Size(max = 2)
    private String status; // 作业状态 0-未提交, 1-已提交, 2-已批改
}
