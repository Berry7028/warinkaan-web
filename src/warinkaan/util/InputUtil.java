package warinkaan.util;

import java.util.Scanner;

public class InputUtil {
	Scanner scanner = new Scanner(System.in);

	public String readString(String message) {
		while (true) {
			System.out.print(message);
			String value = scanner.nextLine().trim();

			if (!value.isEmpty()) {
				return value;
			}

			System.out.println("文字を入力してください。");
		}
	}

	public int readPositiveInt(String message) {
		while (true) {
			System.out.print(message);

			try {
				int value = Integer.parseInt(scanner.nextLine());

				if (value >= 1) {
					return value;
				}
			} catch (NumberFormatException e) {
			}

			System.out.println("1以上の整数を入力してください。");
		}
	}
}
