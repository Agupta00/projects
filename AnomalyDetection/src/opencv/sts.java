package opencv;

import com.twilio.sdk.TwilioRestException;

import enroll.Messenger;

public class sts {
	public static void main(String[] args) {
		try {
			Messenger.sendMsg("hi");
		} catch (TwilioRestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
