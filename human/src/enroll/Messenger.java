package enroll;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Message;
 
public class Messenger {
 
    /* Find your sid and token at twilio.com/user/account */
    public static final String ACCOUNT_SID = "AC77ad25f9c6824bb96dfb1b8f59257b90";
    public static final String AUTH_TOKEN = "10e515abe06031bda04f646249118a12";
 
    public static void messageUser(String msg) throws TwilioRestException {//sends a message to a user TODO:add number parameter to method
 
        TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
 
        Account account = client.getAccount();
 
		//File img = new File(System.getProperty("user.home"), "Desktop/akul/1.jpg");
        MessageFactory messageFactory = account.getMessageFactory();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("To", "+14087173593")); // Replace with a valid phone number for your account.
        params.add(new BasicNameValuePair("From", "+16692363704")); // Replace with a valid phone number for your account.
        //params.add(new BasicNameValuePair("Body", msg ));
        //params.add(new BasicNameValuePair("MediaUrl", "https://www.dropbox.com/s/82x188vk7i80spm/pic1.jpg?dl=0"));
        
        @SuppressWarnings("unused")
		Message mms = messageFactory.create(params);

        
    }
    
    public static void sendMsg(String msg) throws TwilioRestException {// sends
		// a
		// message
		// to a
		// user
		// TODO:add
		// number
		// parameter
		// to
		// method

		TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

		Account account = client.getAccount();

		// File img = new File(System.getProperty("user.home"),
		// "Desktop/akul/1.jpg");
		MessageFactory messageFactory = account.getMessageFactory();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("To", "+14087173593")); // Replace
		// with a
		// valid
		// phone
		// number
		// for your
		// account.
		params.add(new BasicNameValuePair("From", "+16692363704")); // Replace
		// with a
		// valid
		// phone
		// number
		// for your
		// account.
		params.add(new BasicNameValuePair("Body", msg));

		@SuppressWarnings("unused")
		Message mms = messageFactory.create(params);

	}
}
