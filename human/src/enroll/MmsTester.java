package enroll;

import com.twilio.sdk.TwilioRestException;

public class MmsTester {
public static void main(String[] args) {
	
	try {
		Messenger.messageUser("hi");
	} catch (TwilioRestException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
