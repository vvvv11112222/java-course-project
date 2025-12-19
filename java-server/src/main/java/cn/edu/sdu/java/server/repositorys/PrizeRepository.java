package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Prize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * Course 数据操作接口，主要实现Course数据的查询操作
 */

@Repository
public interface PrizeRepository extends JpaRepository<Prize,Integer> {
    @Query(value = "from Prize where ?1='' or num like %?1% or name like %?1% ")
    List<Prize> findPrizeListByNumName(String numName);

    Optional<Prize> findByNum(String num);
    List<Prize> findByName(String name);
}
