package com.example.pharma.controller.prescription;

import com.example.pharma.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v1/scan-prescription")
@RestController
public class PrescriptionController {
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<?> uploadOcr(@RequestParam("image") MultipartFile file)
    {
        return null;
    }

}
