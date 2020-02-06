package enroll;

public class UserMessageRunnerTest {

	public static void main(String[] args) {
		MessageHandler_v2 manager = new MessageHandler_v2(0.766, "gregory");
		
		try {
			manager.listen();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
