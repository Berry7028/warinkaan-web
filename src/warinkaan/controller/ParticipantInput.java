package warinkaan.controller;

import warinkaan.model.Participant;
import warinkaan.model.WarikanSession;
import warinkaan.service.WarikanService;
import warinkaan.util.InputUtil;

public class ParticipantInput {
	WarikanService service;
	InputUtil input;

	public ParticipantInput(WarikanService service, InputUtil input) {
		this.service = service;
		this.input = input;
	}

	public int readParticipantCount() {
		while (true) {
			int participantCount = input.readPositiveInt("参加人数を入力してください: ");

			if (participantCount >= 2) {
				return participantCount;
			}

			System.out.println("参加人数は2人以上にしてください");
		}
	}

	public Participant readParticipant(WarikanSession session) {
		Participant participant = new Participant();
		participant.setParticipantId(readNewParticipantId(session));
		participant.setName(readNewParticipantName(session));
		participant.setWeight(input.readPositiveInt("負担割合を入力してください: "));
		return participant;
	}

	public int readPayerId(WarikanSession session) {
		while (true) {
			int payerId = input.readPositiveInt("立替者の参加者IDを入力してください: ");

			if (service.findParticipantById(session, payerId) != null) {
				return payerId;
			}

			System.out.println("登録されている参加者IDを入力してください");
		}
	}

	public int readNewParticipantId(WarikanSession session) {
		while (true) {
			int participantId = input.readPositiveInt("参加者IDを入力してください: ");

			if (service.findParticipantById(session, participantId) == null) {
				return participantId;
			}

			System.out.println("その参加者IDはすでに使われています");
		}
	}

	public String readNewParticipantName(WarikanSession session) {
		while (true) {
			String name = input.readString("参加者名を入力してください: ");

			if (!service.participantNameExists(session, name)) {
				return name;
			}

			System.out.println("その参加者名はすでに使われています");
		}
	}
}
