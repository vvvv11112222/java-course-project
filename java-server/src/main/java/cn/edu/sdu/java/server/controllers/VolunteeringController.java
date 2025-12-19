package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.VolunteeringService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/volunteering")
public class VolunteeringController {
    private final VolunteeringService volunteeringService;

    public VolunteeringController(VolunteeringService volunteeringService) {
        this.volunteeringService = volunteeringService;
    }

    @PostMapping("/getVolunteeringList")
    public DataResponse getVolunteeringList(@Valid @RequestBody DataRequest dataRequest) {
        return volunteeringService.getVolunteeringList(dataRequest);
    }

    @PostMapping("/volunteeringSave")
    public DataResponse volunteeringSave(@Valid @RequestBody DataRequest dataRequest) {
        return volunteeringService.volunteeringSave(dataRequest);
    }

    @PostMapping("/volunteeringDelete")
    public DataResponse volunteeringDelete(@Valid @RequestBody DataRequest dataRequest) {
        return volunteeringService.volunteeringDelete(dataRequest);
    }
}