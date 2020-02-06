package opencv;

import com.twilio.sdk.TwilioRestException;

import enroll.Messenger;

public class tst {
public static void main(String[] args) {
	try {
		Messenger.sendMsg("ANOAMLY DETECTED:!");
	} catch (TwilioRestException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}
}
