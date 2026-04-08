package donor_patient;

/**
 * Provides notification services for donors and patients.
 */
public class NotificationService {

    /**
     * Sends a notification to a donor.
     *
     * @param donor   the donor to notify
     * @param message the notification message
     */
    public void notifyDonor(Donor donor, String message) {
        System.out.println("Notifying Donor: " + donor.getName() + " - " + message);
    }

    /**
     * Sends a notification to a patient.
     *
     * @param patient the patient to notify
     * @param message the notification message
     */
    public void notifyPatient(Patient patient, String message) {
        patient.update(message);
    }
}