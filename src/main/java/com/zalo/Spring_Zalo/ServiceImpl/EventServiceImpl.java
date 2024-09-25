package com.zalo.Spring_Zalo.ServiceImpl;

import com.zalo.Spring_Zalo.Entities.Company;
import com.zalo.Spring_Zalo.Entities.Event;
import com.zalo.Spring_Zalo.Exception.ApiNotFoundException;
import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import com.zalo.Spring_Zalo.Repo.CompanyRepo;
import com.zalo.Spring_Zalo.Repo.EventRepo;
import com.zalo.Spring_Zalo.Service.EventService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private CompanyRepo companyRepo;

    @Override
    @Transactional(rollbackOn = { Throwable.class })
    public Event createEvent(Event event, Integer companyId) {
        if(companyId == null){
            throw new ApiNotFoundException("Please choose company !!!");
        }
        Company company =  companyRepo.findById(companyId).orElseThrow(() -> new ResourceNotFoundException("company","companyId",companyId));
        event.setCompany(company);
        return eventRepo.saveAndFlush(event);
    }

    @Override
    @Transactional(rollbackOn = { Throwable.class })
    public Event updataEvent(Event event,Integer eventId, Integer companyId) {
        Event evt =  eventRepo.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("event","eventId",eventId));
        if(companyId != null){
            Company company =  companyRepo.findById(companyId).orElseThrow(() -> new ResourceNotFoundException("company","companyId",companyId));
            evt.setCompany(company);
        }
        evt.setName(event.getName());
        evt.setTimeStartEvent(event.getTimeStartEvent());
        evt.setTimeEndEvent(event.getTimeEndEvent());

        return eventRepo.saveAndFlush(evt);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepo.findAll();
    }

    @Override
    public Event getEventById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEventById'");
    }
}
