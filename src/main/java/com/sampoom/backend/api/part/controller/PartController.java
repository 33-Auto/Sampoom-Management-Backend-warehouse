package com.sampoom.backend.api.part.controller;

import com.sampoom.backend.api.part.entity.Part;
import com.sampoom.backend.common.response.ApiResponse;
import com.sampoom.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/part")
public class PartController {

    @GetMapping()
    public ResponseEntity<ApiResponse<List<Part>>> getParts() {
        List<Part> list = new ArrayList<>();
        Part part1 = Part.builder()
                .id(1L)
                .name("타이어")
                .count(5L)
                .build();
        Part part2 = Part.builder()
                .id(2L)
                .name("핸들")
                .count(13L)
                .build();
        Part part3 = Part.builder()
                .id(3L)
                .name("브레이크 패달")
                .count(15L)
                .build();

        list.add(part1);
        list.add(part2);
        list.add(part3);


        return ApiResponse.success(SuccessStatus.OK,list);
    }


}
