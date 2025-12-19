package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Prize;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.PrizeRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PrizeService {
    private final PrizeRepository prizeRepository;
    public PrizeService(PrizeRepository prizeRepository) {
        this.prizeRepository = prizeRepository;
    }

    public DataResponse getPrizeList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Prize> cList = prizeRepository.findPrizeListByNumName(numName);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        Prize pc;
        for (Prize c : cList) {
            m = new HashMap<>();
            m.put("prizeId", c.getPrizeId()+"");
            m.put("num",c.getNum());
            m.put("name",c.getName());
            m.put("prizeLevel",c.getPrizeLevel());
            m.put("prizePath",c.getPrizePath());
            pc =c.getPrePrize();
            if(pc != null) {
                m.put("prePrize",pc.getName());
                m.put("prePrizeId",pc.getPrizeId());
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse prizeSave(DataRequest dataRequest) {
        Integer prizeId = dataRequest.getInteger("prizeId");
        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        String prizePath = dataRequest.getString("prizePath");
        String prizeLevel = dataRequest.getString("prizeLevel");
        Integer prePrizeId = dataRequest.getInteger("prePrizeId");
        Optional<Prize> op;
        Prize c= null;

        if(prizeId != null) {
            op = prizeRepository.findById(prizeId);
            if(op.isPresent())
                c= op.get();
        }
        if(c== null)
            c = new Prize();
        Prize pc =null;
        if(prePrizeId != null) {
            op = prizeRepository.findById(prePrizeId);
            if(op.isPresent())
                pc = op.get();
        }
        c.setNum(num);
        c.setName(name);
        c.setPrizeLevel(prizeLevel);
        c.setPrizePath(prizePath);
        c.setPrePrize(pc);
        prizeRepository.save(c);
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse prizeDelete(DataRequest dataRequest) {
        Integer prizeId = dataRequest.getInteger("prizeId");
        Optional<Prize> op;
        Prize c= null;
        if(prizeId != null) {
            op = prizeRepository.findById(prizeId);
            if(op.isPresent()) {
                c = op.get();
                prizeRepository.delete(c);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

}
