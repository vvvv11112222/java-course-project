package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(	name = "family_member",
        uniqueConstraints = {
        })
public class FamilyMember {
    @Id//主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增
    private Integer memberId;//主键

    @ManyToOne//多对一
    @JoinColumn(name="personId")//外键
    private Student student;
    @Size(max=10)
    private String relation;
    @Size(max=30)
    private String name;
    @Size(max=20)
    private String phone;
    @Size(max=10)
    private String gender;
    private Integer age;
    @Size(max=50)
    private String unit;




}
