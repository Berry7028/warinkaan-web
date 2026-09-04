package warinkaan.controller;

import warinkaan.model.Participant;
import warinkaan.model.WarikanSession;
import warinkaan.service.WarikanService;
import warinkaan.util.InputUtil;
import warinkaan.view.Menu;

public class SessionController {
	WarikanService service;
	InputUtil input;
	ParticipantInput participantInput;

	public SessionController(WarikanService service, InputUtil input) {
		this.service = service;
		this.input = input;
		this.participantInput = new ParticipantInput(service, input);
	}

	public void createSession() {
		WarikanSession session = new WarikanSession();
		session.setSessionId(readNewSessionId());
		session.setTotalAmount(input.readPositiveInt("合計金額を入力してください: "));

		int participantCount = participantInput.readParticipantCount();
		for (int i = 0; i < participantCount; i++) {
			System.out.println((i + 1) + "人目の参加者を入力します");
			Participant participant = participantInput.readParticipant(session);
			session.getParticipants().add(participant);
		}

		Menu.showParticipants(session.getParticipants());
		session.setPayerId(participantInput.readPayerId(session));
		service.createSession(session);
		System.out.println("セッションを作成しました");
	}

	public void showSessions() {
		Menu.showSessions(service.getSessions());
	}

	public void updateSession() {
		if (service.getSessions().isEmpty()) {
			System.out.println("更新できるセッションがありません");
			return;
		}

		Menu.showSessions(service.getSessions());
		int sessionId = input.readPositiveInt("更新するセッションIDを入力してください: ");
		WarikanSession session = service.findSessionById(sessionId);

		if (session == null) {
			System.out.println("セッションが見つかりません");
			return;
		}

		int totalAmount = input.readPositiveInt("新しい合計金額を入力してください: ");
		Menu.showParticipants(session.getParticipants());
		int payerId = participantInput.readPayerId(session);

		service.updateSession(sessionId, totalAmount, payerId);
		System.out.println("セッションを更新しました");
	}

	public void deleteSession() {
		if (service.getSessions().isEmpty()) {
			System.out.println("削除できるセッションがありません");
			return;
		}

		Menu.showSessions(service.getSessions());
		int sessionId = input.readPositiveInt("削除するセッションIDを入力してください: ");

		if (service.deleteSession(sessionId)) {
			System.out.println("セッションを削除しました");
		} else {
			System.out.println("セッションが見つかりません");
		}
	}

	public void calculateBurdens() {
		if (service.getSessions().isEmpty()) {
			System.out.println("計算できるセッションがありません");
			return;
		}

		Menu.showSessions(service.getSessions());
		int sessionId = input.readPositiveInt("計算するセッションIDを入力してください: ");

		if (service.findSessionById(sessionId) == null) {
			System.out.println("セッションが見つかりません");
			return;
		}

		Menu.showResult(service.calculateBurdens(sessionId));
	}

	public int readNewSessionId() {
		while (true) {
			int sessionId = input.readPositiveInt("セッションIDを入力してください: ");

			if (service.findSessionById(sessionId) == null) {
				return sessionId;
			}

			System.out.println("そのセッションIDはすでに使われています");
		}
	}
}
