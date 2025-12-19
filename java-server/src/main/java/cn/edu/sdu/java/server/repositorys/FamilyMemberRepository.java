package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.FamilyMember;
import cn.edu.sdu.java.server.models.Student;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember,Integer> {
//帮我完成根据personId查询该学生的所有家庭成员
@Query(value = "select mb from FamilyMember mb where mb.student.person.personId = :personId")
List<FamilyMember> findByStudentPersonId(@Param("personId") Integer personId);




//    @Query(value = "from FamilyMember where ?1='' or  f.relation like %?1% or f.name like %?1% ",
//            countQuery = "SELECT count(memberId) from FamilyMember where ?1='' or f.relation like %?1% or f.name like %?1% ")
//    Page<FamilyMember> findFamilyMemberPageByRelationName(String relationName, Pageable pageable);


    // 在 FamilyMemberRepository 中添加以下方法


    // 模糊查询（不分页）
//    @Query("SELECT f FROM FamilyMember f WHERE " +
//            "(:keyword = '' OR f.relation LIKE %:keyword% OR f.name LIKE %:keyword%)")
//    List<FamilyMember> findFamilyMemberListByRelationName(@Param("keyword") String keyword);
//
//    // 分页模糊查询
//
//
//        // 修正后的分页查询方法
//        @Query(value = "SELECT f FROM FamilyMember f WHERE " +
//                "(:keyword = '' OR f.relation LIKE CONCAT('%', :keyword, '%') OR f.name LIKE CONCAT('%', :keyword, '%'))",
//                countQuery = "SELECT COUNT(f.memberId) FROM FamilyMember f WHERE " +
//                        "(:keyword = '' OR f.relation LIKE CONCAT('%', :keyword, '%') OR f.name LIKE CONCAT('%', :keyword, '%'))")
//        Page<FamilyMember> findFamilyMemberPageByRelationName(@Param("keyword") String keyword, Pageable pageable);
    @Query(value = "from FamilyMember where ?1='' or relation like %?1% or name like %?1% ")// JPQL 注解
    List<FamilyMember> findFamilyMemberListByRelationName(String numName);//


    @Query(value = "from FamilyMember where ?1='' or relation like %?1% or name like %?1% ",
            countQuery = "SELECT count(memberId) from FamilyMember where ?1='' or relation like %?1% or name like %?1% ")
    Page<FamilyMember> findFamilyMemberPageByRelationName(String numName,  Pageable pageable);
    }



