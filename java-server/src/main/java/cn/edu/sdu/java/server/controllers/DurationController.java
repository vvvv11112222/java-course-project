package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.DurationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/duration")
public class DurationController {
    private final DurationService durationService;

    public DurationController(DurationService durationService) {
        this.durationService = durationService;
    }

    @PostMapping("/getStudentItemOptionList")
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return durationService.getStudentItemOptionList(dataRequest);
    }

    @PostMapping("/getVolunteeringItemOptionList")
    public OptionItemList getVolunteeringItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return durationService.getVolunteeringItemOptionList(dataRequest);
    }

    @PostMapping("/getDurationList")
    public DataResponse getDurationList(@Valid @RequestBody DataRequest dataRequest) {
        return durationService.getDurationList(dataRequest);
    }

    @PostMapping("/durationSave")
    public DataResponse durationSave(@Valid @RequestBody DataRequest dataRequest) {
        return durationService.durationSave(dataRequest);
    }

    @PostMapping("/durationDelete")
    public DataResponse durationDelete(@Valid @RequestBody DataRequest dataRequest) {
        return durationService.durationDelete(dataRequest);
    }
}