package donor_patient;

/**
 * Represents an observer in the Observer design pattern.
 * Observers receive updates from subjects they are observing.
 */
public interface Observer {
    /**
     * Receives an update from the subject.
     *
     * @param message the update message
     */
    void update(String message);
}