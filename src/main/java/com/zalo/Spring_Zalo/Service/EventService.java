package com.zalo.Spring_Zalo.Service;

import com.zalo.Spring_Zalo.Entities.Event;

import java.util.List;

public interface EventService {
    Event createEvent(Event event, Integer companyId);
    Event updataEvent(Event event, Integer eventId,Integer companyId);
    List<Event> getAllEvents();
    Event getEventById(Long id);
}
