package donor_patient;

/**
 * Represents a patient in the donation system.
 * A patient observes donation events and receives updates.
 */
public class Patient implements Observer {
    private String name;
    private String bloodType;
    private String medicalCondition;

    /**
     * Constructs a new Patient with the specified details.
     *
     * @param name             the name of the patient
     * @param bloodType        the blood type of the patient
     * @param medicalCondition the medical condition of the patient
     */
    public Patient(String name, String bloodType, String medicalCondition) {
        this.name = name;
        this.bloodType = bloodType;
        this.medicalCondition = medicalCondition;
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

    public String getMedicalCondition() {
        return medicalCondition;
    }

    public void setMedicalCondition(String medicalCondition) {
        this.medicalCondition = medicalCondition;
    }

    /**
     * Receives an update from a donation event.
     *
     * @param message the update message
     */
    @Override
    public void update(String message) {
        System.out.println("Patient " + name + " received update: " + message);
    }
}