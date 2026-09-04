package warinkaan.controller;

import warinkaan.service.WarikanService;
import warinkaan.util.InputUtil;
import warinkaan.view.Menu;

public class WarikanController {
	InputUtil input = new InputUtil();
	SessionController sessionController = new SessionController(new WarikanService(), input);

	public void start() {
		boolean running = true;

		while (running) {
			Menu.showMainMenu();
			int choice = input.readPositiveInt("操作を選択してください: ");

			switch (choice) {
			case 1:
				sessionController.createSession();
				break;
			case 2:
				sessionController.showSessions();
				break;
			case 3:
				sessionController.updateSession();
				break;
			case 4:
				sessionController.deleteSession();
				break;
			case 5:
				sessionController.calculateBurdens();
				break;
			case 6:
				running = false;
				System.out.println("終了します");
				break;
			default:
				System.out.println("1から6の番号を選択してください");
			}

			System.out.println();
		}
	}
}
