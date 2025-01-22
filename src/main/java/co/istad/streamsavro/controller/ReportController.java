package co.istad.streamsavro.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    @GetMapping("/download/report-1")
    public ResponseEntity<?> downloadReport1() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Report 1 downloaded successfully");
        data.put("file", "report1.csv");
        return ResponseEntity.ok(data);
    }

    @GetMapping("/download/report-2")
    public ResponseEntity<?> downloadReport2() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Report 2 downloaded successfully");
        data.put("file", "report2.csv");
        return ResponseEntity.ok(data);
    }

}
