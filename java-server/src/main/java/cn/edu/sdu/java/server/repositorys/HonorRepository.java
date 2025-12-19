package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Honor;
import cn.edu.sdu.java.server.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
/*
 * Score 数据操作接口，主要实现Score数据的查询操作
 * List<Score> findByStudentPersonId(Integer personId);  根据关联的Student的student_id查询获得List<Score>对象集合,  命名规范
 */

@Repository
public interface HonorRepository extends JpaRepository<Honor,Integer> {
    List<Honor> findByStudentPersonId(Integer personId);
    @Query(value="from Honor where (?1=0 or student.personId=?1) and (?2=0 or prize.prizeId=?2)" )
    List<Honor> findByStudentPrize(Integer personId, Integer prizeId);

    @Query(value="from Honor where student.personId=?1 and (?2=0 or prize.name like %?2%)" )
    List<Honor> findByStudentPrizeName(Integer personId, String prizeName);

}
