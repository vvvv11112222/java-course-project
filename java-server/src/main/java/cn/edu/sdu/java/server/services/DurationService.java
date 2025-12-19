package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DurationService {
    private final VolunteeringRepository volunteeringRepository;
    private final DurationRepository durationRepository;
    private final StudentRepository studentRepository;

    public DurationService(VolunteeringRepository volunteeringRepository,
                           DurationRepository durationRepository,
                           StudentRepository studentRepository) {
        this.volunteeringRepository = volunteeringRepository;
        this.durationRepository = durationRepository;
        this.studentRepository = studentRepository;
    }

    public OptionItemList getStudentItemOptionList(DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : sList) {
            itemList.add(new OptionItem(s.getPersonId(), s.getPersonId()+"",
                    s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    public OptionItemList getVolunteeringItemOptionList(DataRequest dataRequest) {
        List<Volunteering> vList = volunteeringRepository.findAll();
        List<OptionItem> itemList = new ArrayList<>();
        for (Volunteering v : vList) {
            itemList.add(new OptionItem(v.getVolunteeringId(), v.getVolunteeringId()+"",
                    v.getNum()+"-"+v.getName()));
        }
        return new OptionItemList(0, itemList);
    }

    public DataResponse getDurationList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        if (personId == null) personId = 0;

        Integer volunteeringId = dataRequest.getInteger("volunteeringId");
        if (volunteeringId == null) volunteeringId = 0;

        List<Duration> dList = durationRepository.findByStudentVolunteering(personId, volunteeringId);
        List<Map<String, Object>> dataList = new ArrayList<>();

        for (Duration d : dList) {
            Map<String, Object> m = new HashMap<>();
            m.put("durationId", d.getDurationId()+"");
            m.put("personId", d.getStudent().getPersonId()+"");
            m.put("volunteeringId", d.getVolunteering().getVolunteeringId()+"");
            m.put("studentNum", d.getStudent().getPerson().getNum());
            m.put("studentName", d.getStudent().getPerson().getName());
            m.put("className", d.getStudent().getClassName());
            m.put("volunteeringNum", d.getVolunteering().getNum());
            m.put("volunteeringName", d.getVolunteering().getName());
            m.put("credit", ""+d.getVolunteering().getCredit());
            m.put("mark", ""+d.getMark());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse durationSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Integer volunteeringId = dataRequest.getInteger("volunteeringId");
        Integer durationId = dataRequest.getInteger("durationId");
        Integer mark = dataRequest.getInteger("mark");

        Optional<Duration> op;
        Duration d = null;

        if (durationId != null) {
            op = durationRepository.findById(durationId);
            if (op.isPresent()) d = op.get();
        }

        if (d == null) {
            d = new Duration();
            d.setStudent(studentRepository.findById(personId).get());
            d.setVolunteering(volunteeringRepository.findById(volunteeringId).get());
        }

        d.setMark(mark);
        durationRepository.save(d);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse durationDelete(DataRequest dataRequest) {
        Integer durationId = dataRequest.getInteger("durationId");
        Optional<Duration> op;

        if (durationId != null) {
            op = durationRepository.findById(durationId);
            if (op.isPresent()) {
                durationRepository.delete(op.get());
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
}