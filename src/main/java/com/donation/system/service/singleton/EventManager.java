package com.donation.system.service.singleton;

import com.donation.system.model.entity.Donation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton Pattern — EventManager
 * Thread-safe double-checked locking implementation
 */
public class EventManager {

    private static volatile EventManager instance;
    private final List<Donation> events;

    private EventManager() {
        this.events = new ArrayList<>();
    }

    /**
     * Singleton: Get the single instance (thread-safe)
     */
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
        return Collections.unmodifiableList(events);
    }

    public int getEventCount() {
        return events.size();
    }
}
