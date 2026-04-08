package donor_patient;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a donation event in the system.
 * Manages observers and notifies them of updates.
 */
public class DonationEvent {
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Adds an observer to the donation event.
     *
     * @param observer the observer to add
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer from the donation event.
     *
     * @param observer the observer to remove
     */
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers with the specified message.
     *
     * @param message the message to notify observers with
     */
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}