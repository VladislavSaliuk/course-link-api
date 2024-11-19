package com.courselink.api.controller;

import com.courselink.api.dto.DefenceSessionDTO;
import com.courselink.api.service.DefenceSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DefenceSessionRestController {

    private final DefenceSessionService defenceSessionService;
    @PostMapping("/defence-sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public DefenceSessionDTO createDefenceSession(@RequestBody @Valid DefenceSessionDTO defenceSessionDTO) {
        return defenceSessionService.createDefenceSession(defenceSessionDTO);
    }

    @PutMapping("/defence-sessions")
    @ResponseStatus(HttpStatus.OK)
    public DefenceSessionDTO updateDefenceSession(@RequestBody @Valid DefenceSessionDTO defenceSessionDTO) {
        return defenceSessionService.updateDefenceSession(defenceSessionDTO);
    }

    @GetMapping("/defence-sessions")
    @ResponseStatus(HttpStatus.OK)
    public List<DefenceSessionDTO> getAll() {
        return defenceSessionService.getAll();
    }

    @GetMapping("/defence-sessions/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DefenceSessionDTO getById(@PathVariable("id") long defenceSessionId) {
        return defenceSessionService.getById(defenceSessionId);
    }

    @DeleteMapping("/defence-sessions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeById(@PathVariable("id") long defenceSessionId) {
        defenceSessionService.removeById(defenceSessionId);
    }


}
