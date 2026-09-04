package warinkaan.model;

public class BurdenResult {
	int participantId;
	String participantName;
	int burdenAmount;

	public BurdenResult(int participantId, String participantName, int burdenAmount) {
		super();
		this.participantId = participantId;
		this.participantName = participantName;
		this.burdenAmount = burdenAmount;
	}

	public int getParticipantId() {
		return participantId;
	}

	public String getParticipantName() {
		return participantName;
	}

	public int getBurdenAmount() {
		return burdenAmount;
	}

}
