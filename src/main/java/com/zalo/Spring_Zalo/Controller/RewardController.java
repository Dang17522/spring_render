package com.zalo.Spring_Zalo.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zalo.Spring_Zalo.DTO.EventDto;
import com.zalo.Spring_Zalo.Entities.Event;
import com.zalo.Spring_Zalo.Repo.EventRepo;
import com.zalo.Spring_Zalo.Response.ApiResponse;
import com.zalo.Spring_Zalo.Service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import com.zalo.Spring_Zalo.DTO.RewardDTO;
import com.zalo.Spring_Zalo.Entities.Reward;
import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import com.zalo.Spring_Zalo.Repo.RewardRepo;
import com.zalo.Spring_Zalo.Service.RewardService;
import org.springframework.web.multipart.MultipartFile;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/reward")
public class RewardController {
    @Autowired
    private RewardService rewardService;
    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }
    @Autowired
    private RewardRepo rewardRepo;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private CloudinaryService cloudinaryService;
    /**
     * add new reward
     * @param reward
     * @return reward 
     */
    @PostMapping("/add")
    public ResponseEntity<?> createReward(@RequestPart("name") String name, @RequestPart("pointReward") String pointReward, @RequestPart("quantity") String quantity, @RequestPart(value = "file", required = false) MultipartFile image, @RequestPart("rewardType") String rewardType, @RequestPart("eventId") String eventId){
        Reward reward = new Reward();
        Event event = eventRepo.findById(Integer.parseInt(eventId)).orElseThrow(() -> new ResourceNotFoundException("event","eventId",Integer.parseInt(eventId)));
        reward.setName(name);
        reward.setPointReward(Integer.parseInt(pointReward));
        reward.setQuantity(Integer.parseInt(quantity));
        reward.setReward_type(Integer.parseInt(rewardType));
        reward.setEvent(event);
        reward.setCreateAt(LocalDateTime.now());
        if(image != null){
            Map data = cloudinaryService.upload(image);
            reward.setImage(String.valueOf(data.get("secure_url")));
            reward.setPublicId(String.valueOf(data.get("public_id")));
        }
        Reward newReward = rewardService.addReward(reward);
        return new ResponseEntity<>(newReward,HttpStatus.CREATED);
    }
    /**
     * Get infomation of 1 reward 
     * @param eventId
     * @param rewardId
     * @return
     */
     @GetMapping("/event/{eventId}/reward/{rewardId}")
    public ResponseEntity<?> getRewardInfo(@PathVariable Integer eventId,@PathVariable Integer rewardId){
     Reward rewardInfo = rewardRepo.getInfoReward(eventId,rewardId);
     if (rewardInfo == null) {
         ApiResponse apiResponse = new ApiResponse("Reward is null", false, 400);
         return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
     } 
     return ResponseEntity.ok(rewardInfo);
    }
       /**
     * get List reward from database by event Id
     * @param event_id
     * @return List Reward
     */ 
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<RewardDTO>> getListRewardWithPagination(
            @PathVariable Integer eventId,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Reward> rewardsPage = rewardRepo.findByEvent_Id(eventId, pageable);

        if (rewardsPage.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<RewardDTO> rewardDTOList = rewardsPage.getContent().stream()
                .map(this::mapToRewardDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(rewardDTOList);
    }

    // Method to map Reward entity data to RewardDTO
    private RewardDTO mapToRewardDTO(Reward reward) {
        return  RewardDTO.builder().id(reward.getId())
                .name(reward.getName())
                .pointReward(reward.getPointReward())
                .quantity(reward.getQuantity())
                .image(reward.getImage())
                .reward_Type(reward.getReward_type())
                .eventId(reward.getEvent().getId())
                .eventName(reward.getEvent().getName())
                .build();
    }


    @GetMapping("/{rewardId}")
    public ResponseEntity<Reward> getRewardId(@PathVariable Integer rewardId) {
        Reward reward = rewardRepo.findById(rewardId).orElseThrow(() -> new ResourceNotFoundException("reward","rewardId",rewardId));
        return ResponseEntity.ok(reward);
    }

   @GetMapping("/")
    public ResponseEntity<List<RewardDTO>> getAllRewardsWithEventIds() {
        List<Reward> rewards = rewardRepo.findAll(); // Fetch all rewards
        List<RewardDTO> rewardDTOList = rewards.stream()
                .map(this::mapToRewardDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(rewardDTOList);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllEventsWithPagination(@RequestParam(value = "pageSize",defaultValue = "5") int pageSize,
                                                        @RequestParam(value = "pageNumber" ,defaultValue = "1") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Reward> pages = rewardRepo.findALlPage(pageable);
        Page<RewardDTO> map = pages.map(this::mapToRewardDTO);
        return ResponseEntity.ok(map);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getAllEventsByCompanyWithPagination(@PathVariable("companyId") Integer companyId,
                                                                 @RequestParam(defaultValue = "5") int pageSize,
                                                                 @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Reward> pages = rewardRepo.findByCompany_Id(companyId,pageable);
        Page<RewardDTO> map = pages.map(this::mapToRewardDTO);
        return ResponseEntity.ok(map);
    }

    private Reward mapToReward(RewardDTO rewardDTO) {
        Event event = eventRepo.findById(rewardDTO.getEventId()).orElseThrow(() -> new ResourceNotFoundException("event","eventId",rewardDTO.getEventId()));
        Reward reward = Reward.builder()
                .id(rewardDTO.getId())
                .name(rewardDTO.getName())
                .pointReward(rewardDTO.getPointReward())
                .quantity(rewardDTO.getQuantity())
                .image(rewardDTO.getImage())
                .reward_type(rewardDTO.getReward_Type())
                .event(event)
                .build();
        return reward;
    }
    
}
