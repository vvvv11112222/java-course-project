package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Volunteering;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.VolunteeringRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class VolunteeringService {
    private final VolunteeringRepository volunteeringRepository;

    public VolunteeringService(VolunteeringRepository volunteeringRepository) {
        this.volunteeringRepository = volunteeringRepository;
    }

    public DataResponse getVolunteeringList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Volunteering> vList = volunteeringRepository.findVolunteeringListByNumName(numName);
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        Volunteering pv;
        for (Volunteering v : vList) {
            m = new HashMap<>();
            m.put("volunteeringId", v.getVolunteeringId()+"");
            m.put("num", v.getNum());
            m.put("name", v.getName());
            m.put("credit", v.getCredit()+"");
            m.put("volunteeringPath", v.getVolunteeringPath());
            pv = v.getPreVolunteering();
            if(pv != null) {
                m.put("preVolunteering", pv.getName());
                m.put("preVolunteeringId", pv.getVolunteeringId());
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse volunteeringSave(DataRequest dataRequest) {
        Integer volunteeringId = dataRequest.getInteger("volunteeringId");
        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        String volunteeringPath = dataRequest.getString("volunteeringPath");
        Integer credit = dataRequest.getInteger("credit");
        Integer preVolunteeringId = dataRequest.getInteger("preVolunteeringId");
        Optional<Volunteering> op;
        Volunteering v = null;

        if(volunteeringId != null) {
            op = volunteeringRepository.findById(volunteeringId);
            if(op.isPresent())
                v = op.get();
        }
        if(v == null)
            v = new Volunteering();
        Volunteering pv = null;
        if(preVolunteeringId != null) {
            op = volunteeringRepository.findById(preVolunteeringId);
            if(op.isPresent())
                pv = op.get();
        }
        v.setNum(num);
        v.setName(name);
        v.setCredit(credit);
        v.setVolunteeringPath(volunteeringPath);
        v.setPreVolunteering(pv);
        volunteeringRepository.save(v);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse volunteeringDelete(DataRequest dataRequest) {
        Integer volunteeringId = dataRequest.getInteger("volunteeringId");
        Optional<Volunteering> op;
        Volunteering v = null;
        if(volunteeringId != null) {
            op = volunteeringRepository.findById(volunteeringId);
            if(op.isPresent()) {
                v = op.get();
                volunteeringRepository.delete(v);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
}