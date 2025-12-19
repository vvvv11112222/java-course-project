package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.PrizeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/prize")

public class PrizeController {
    private final PrizeService prizeService;
    public PrizeController(PrizeService prizeService) {
        this.prizeService = prizeService;
    }
    @PostMapping("/getPrizeList")
    public DataResponse getPrizeList(@Valid @RequestBody DataRequest dataRequest) {
        return prizeService.getPrizeList(dataRequest);
    }

    @PostMapping("/prizeSave")
    public DataResponse prizeSave(@Valid @RequestBody DataRequest dataRequest) {
        return prizeService.prizeSave(dataRequest);
    }
    @PostMapping("/prizeDelete")
    public DataResponse prizeDelete(@Valid @RequestBody DataRequest dataRequest) {
        return prizeService.prizeDelete(dataRequest);
    }
}
