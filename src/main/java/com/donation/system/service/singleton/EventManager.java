package com.donation.system.service.singleton;

import com.donation.system.model.entity.Donation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton Pattern: EventManager
 * Thread-safe singleton using double-checked locking.
 *
 * @author Nandan (SRN 363)
 */
public class EventManager {

    private static volatile EventManager instance;
    private final List<Donation> events;

    private EventManager() {
        this.events = Collections.synchronizedList(new ArrayList<>());
    }

    public static EventManager getInstance() {
        if (instance == null) {
            synchronized (EventManager.class) {
                if (instance == null) {
                    instance = new EventManager();
                }
            }
        }
        return instance;
    }

    public void logEvent(Donation donation) {
        events.add(donation);
    }

    public List<Donation> getEvents() {
        synchronized (events) {
            return List.copyOf(events);
        }
    }

    public int getEventCount() {
        return events.size();
    }
}