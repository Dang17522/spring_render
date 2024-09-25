package com.zalo.Spring_Zalo.Controller;
import com.zalo.Spring_Zalo.DTO.EventDto;
import com.zalo.Spring_Zalo.Entities.Company;
import com.zalo.Spring_Zalo.Entities.Event;
import com.zalo.Spring_Zalo.Entities.Product;
import com.zalo.Spring_Zalo.Exception.ApiNotFoundException;
import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import com.zalo.Spring_Zalo.Repo.EventRepo;
import com.zalo.Spring_Zalo.Repo.ProductRepo;
import com.zalo.Spring_Zalo.Response.ApiResponse;
import com.zalo.Spring_Zalo.Service.CloudinaryService;
import com.zalo.Spring_Zalo.Service.CompanyService;
import com.zalo.Spring_Zalo.Service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/events")
@CrossOrigin("*")
public class EventController {


    @Autowired
    private EventService eventService;
     @Autowired
    private EventRepo eventRepo;

    @Autowired
    private CompanyService companyService;
    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CloudinaryService cloudinaryService;

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    @PostMapping("/")
    public ResponseEntity<Event> createEvent(@RequestBody Event event, @RequestParam(name = "companyId", required = false) Integer companyId){
        Event e = eventService.createEvent(event,companyId);
        return new ResponseEntity<>(e, HttpStatus.CREATED);
    }

    @PostMapping("/admin/")
    public ResponseEntity<Event> createEventForAdmin(@RequestPart("name") String name,
                                                     @RequestPart("description") String description,
                                                     @RequestPart("timeStartEvent") String timeStartEvent,
                                                     @RequestPart("timeEndEvent") String timeEndEvent,
                                                     @RequestPart("companyId") String companyId,
                                                     @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        Company company = companyService.getCompanyById(Integer.parseInt(companyId));
        String fileName = "";
        Event event = new Event();
        if(file != null){
            Map data = cloudinaryService.upload(file);
            fileName = String.valueOf(data.get("secure_url"));
            event.setPublicId(String.valueOf(data.get("public_id")));
        }
        event.setName(name);
        event.setDescription(description);
        event.setTimeStartEvent(LocalDate.parse(timeStartEvent));
        event.setTimeEndEvent(LocalDate.parse(timeEndEvent));
        event.setCompany(company);
        event.setBanner(fileName);

        Event e = eventService.createEvent(event,company.getId());
        return new ResponseEntity<>(e, HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventInof(@PathVariable Integer eventId){
        Event e = eventRepo.findById(eventId).orElseThrow(()-> new ResourceNotFoundException("Event","eventId" ,eventId));
        return new ResponseEntity<>(e,HttpStatus.OK);
    }
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<ApiResponse> deleteEvent(@PathVariable Integer eventId){
        Event e = eventRepo.findById(eventId).orElseThrow(()-> new ResourceNotFoundException("Event","eventId" ,eventId));
        eventRepo.delete(e);
        cloudinaryService.deleteImageUpload(e.getPublicId());
        ApiResponse apiResponse = new ApiResponse("delete event success !!!", true, 200);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    // @PutMapping("/{eventId}")
    // public ResponseEntity<Event> updateEvent(@RequestBody Event event,@PathVariable Integer eventId ,@RequestParam(name = "companyId", required = false) Integer companyId){
    //     Event e = eventService.updataEvent(event,eventId,companyId);
    //     return new ResponseEntity<>(e, HttpStatus.OK);
    // }

    @GetMapping("/")
    public ResponseEntity<?> getAllEventsWithPagination(@RequestParam(defaultValue = "5") int pageSize,
                                                                  @RequestParam(defaultValue = "1") int pageNumber) {
    Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
    Page<Event> eventsPage = eventRepo.findAll(pageable);

    if (eventsPage.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    Page<EventDto> map = eventsPage.map(this::mapToDto);
    return ResponseEntity.ok(map);
}

    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getAllEventsByCompanyWithPagination(@PathVariable("companyId") Integer companyId,
                                                                @RequestParam(defaultValue = "5") int pageSize,
                                                                @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Event> eventsPage = eventRepo.findAllbyCompanyId(companyId,pageable);


        Page<EventDto> map = eventsPage.map(this::mapToDto);
        return ResponseEntity.ok(map);
    }
@PutMapping("/{id}")
public ResponseEntity<Event> updateEventVisibility(@PathVariable("id") Integer id, @RequestPart("name") String name,
                                                   @RequestPart("description") String description,
                                                   @RequestPart(value = "timeStartEvent", required = false) String timeStartEvent,
                                                   @RequestPart(value = "timeEndEvent", required = false) String timeEndEvent,
                                                   @RequestPart("visible") String visible,
                                                   @RequestPart(value = "file", required = false) MultipartFile file) {

    Event event = eventRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event","eventId",id));
    event.setName(name);
    event.setDescription(description);
    if(timeStartEvent != null){
        event.setTimeStartEvent(LocalDate.parse(timeStartEvent));
    }
    if(timeEndEvent != null){
        event.setTimeEndEvent(LocalDate.parse(timeEndEvent));
    }
    event.setVisible(Boolean.parseBoolean(visible));
    if(file != null){
        cloudinaryService.deleteImageUpload(event.getPublicId());

        Map data = cloudinaryService.upload(file);
        event.setBanner(String.valueOf(data.get("secure_url")));
        event.setPublicId(String.valueOf(data.get("public_id")));
    }
    eventRepo.save(event);
    return new ResponseEntity<>(event, HttpStatus.OK);

}
    @PutMapping("/status/{id}")
    public ResponseEntity<Event> updateStatusEvent(@PathVariable Integer id) {
        Event event = eventRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event","eventId",id));
        event.setVisible(!event.isVisible());
        Event e = eventRepo.save(event);
        return new ResponseEntity<>(e, HttpStatus.OK);
    }

    public String saveFile(MultipartFile file)  {
        File directory = new File("images/banner");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());

        String uploadDir = System.getProperty("user.dir") + "/images/banner/";
        File destination = new File(uploadDir + fileName);

        try {
            file.transferTo(destination);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileName;
    }

    private EventDto mapToDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .name(event.getName())
                .banner(event.getBanner())
                .timeStartEvent(event.getTimeStartEvent())
                .timeEndEvent(event.getTimeEndEvent())
                .visible(event.isVisible())
                .company(event.getCompany().getName())
                .build();
    }

}
