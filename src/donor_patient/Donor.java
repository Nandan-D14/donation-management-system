package donor_patient;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a donor in the donation system.
 * A donor can trigger donation events to notify observers.
 */
public class Donor {
    private String name;
    private String bloodType;
    private String contactInfo;

    /**
     * Constructs a new Donor with the specified details.
     *
     * @param name        the name of the donor
     * @param bloodType   the blood type of the donor
     * @param contactInfo the contact information of the donor
     */
    public Donor(String name, String bloodType, String contactInfo) {
        this.name = name;
        this.bloodType = bloodType;
        this.contactInfo = contactInfo;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    /**
     * Triggers a donation event and notifies all observers.
     *
     * @param event   the donation event to trigger
     * @param message the message to notify observers with
     */
    public void triggerDonationEvent(DonationEvent event, String message) {
        event.notifyObservers(message);
    }
}