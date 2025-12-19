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
public class HonorService {
    private final PrizeRepository prizeRepository;
    private final HonorRepository honorRepository;
    private final StudentRepository studentRepository;

    public HonorService(PrizeRepository prizeRepository, HonorRepository honorRepository, StudentRepository studentRepository) {
        this.prizeRepository = prizeRepository;
        this.honorRepository = honorRepository;
        this.studentRepository = studentRepository;
    }

    public OptionItemList getStudentItemOptionList(DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : sList) {
            itemList.add(new OptionItem(s.getPersonId(), s.getPersonId() + "", s.getPerson().getNum() + "-" + s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    public OptionItemList getPrizeItemOptionList(DataRequest dataRequest) {
        List<Prize> sList = prizeRepository.findAll();  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Prize c : sList) {
            itemList.add(new OptionItem(c.getPrizeId(), c.getPrizeId() + "", c.getNum() + "-" + c.getName()));
        }
        return new OptionItemList(0, itemList);
    }

    public DataResponse getHonorList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        if (personId == null)
            personId = 0;
        Integer prizeId = dataRequest.getInteger("prizeId");
        if (prizeId == null)
            prizeId = 0;
        List<Honor> sList = honorRepository.findByStudentPrize(personId, prizeId);  //数据库查询操作
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        for (Honor s : sList) {
            m = new HashMap<>();
            m.put("honorId", s.getHonorId() + "");
            m.put("personId", s.getStudent().getPersonId() + "");
            m.put("prizeId", s.getPrize().getPrizeId() + "");
            m.put("studentNum", s.getStudent().getPerson().getNum());
            m.put("studentName", s.getStudent().getPerson().getName());
            m.put("className", s.getStudent().getClassName());
            m.put("prizeNum", s.getPrize().getNum());
            m.put("prizeName", s.getPrize().getName());
            m.put("prizeLevel", s.getPrize().getPrizeLevel());
            m.put("mark", "" + s.getMark());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse honorSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Integer prizeId = dataRequest.getInteger("prizeId");
        Integer mark = dataRequest.getInteger("mark");
        Integer honorId = dataRequest.getInteger("honorId");
        Optional<Honor> op;
        Honor s = null;
        if (honorId != null) {
            op = honorRepository.findById(honorId);
            if (op.isPresent())
                s = op.get();
        }
        if (s == null) {
            s = new Honor();
            s.setStudent(studentRepository.findById(personId).get());
            s.setPrize(prizeRepository.findById(prizeId).get());
        }
        s.setMark(mark);
        honorRepository.save(s);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse honorDelete(DataRequest dataRequest) {
        Integer honorId = dataRequest.getInteger("honorId");
        Optional<Honor> op;
        Honor s = null;
        if (honorId != null) {
            op = honorRepository.findById(honorId);
            if (op.isPresent()) {
                s = op.get();
                honorRepository.delete(s);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
}
