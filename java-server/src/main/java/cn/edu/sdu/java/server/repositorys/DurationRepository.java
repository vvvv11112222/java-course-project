package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Duration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Duration 数据操作接口，主要实现Duration数据的查询操作
 */
@Repository
public interface DurationRepository extends JpaRepository<Duration, Integer> {
    List<Duration> findByStudentPersonId(Integer personId);

    @Query(value = "from Duration where (?1=0 or student.personId=?1) and (?2=0 or volunteering.volunteeringId=?2)")
    List<Duration> findByStudentVolunteering(Integer personId, Integer volunteeringId);

    @Query(value = "from Duration where student.personId=?1 and (?2 is null or volunteering.name like %?2%)")
    List<Duration> findByStudentVolunteeringName(Integer personId, String volunteeringName);
}