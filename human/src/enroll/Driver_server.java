package enroll;

public class Driver_server {
	public static void main(String[] args) {

		try {
			Server.sendMsg("respond");
			Server.listen();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
