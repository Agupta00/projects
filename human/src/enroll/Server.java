package enroll;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.*;
import com.twilio.sdk.resource.factory.*;
import com.twilio.sdk.resource.instance.*;
import com.twilio.sdk.resource.list.*;

public class Server {
	public static final String ACCOUNT_SID = "ACc1b76d9f6b2d9edb2b925f75f9c7e7a6";
	public static final String AUTH_TOKEN = "0cbe8efe79615b6714514926e6f9a945";
	public static final String MyNumber = "+14087173593";
	public static MessageFactory messageFactory;

	public static void listen() throws Exception {

		TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		// System.out.println(dateFormat.format(date));

		Map<String, String> filters = new HashMap<String, String>();
		filters.put("DateSent", dateFormat.format(date));
		// MessageList messages = client.getAccount().getMessages(filters);

		MessageList list = client.getAccount().getMessages(filters); // TODO
																		// stick
																		// something
																		// here
		messageFactory = client.getAccount().getMessageFactory();
		ArrayList<String> text = new ArrayList<String>();

		for (Message message : list) {
			if (message.getDirection().contains("inbound"))
				text.add(message.getBody());
		}

		int preMessages = text.size();

		while (true) {

			list = client.getAccount().getMessages(filters);

			text.clear();
			for (Message message : list) {
				if (message.getDirection().contains("inbound"))
					text.add(message.getBody());
			}
			int newMessages = text.size();

			System.out.println("Last received Message: " + text.get(0)
					+ " preMessages: [" + preMessages + "] newMessages: ["
					+ newMessages + "]");

			if (preMessages != newMessages) {

				System.out.println("Message recived: " + text.get(0));
				switch (text.get(0).toLowerCase()) {
				case "yes":
					sendMsg("cya");
					return;
				case "Yes":
					sendMsg("cya");
					break;
				case "No":
					sendMms("Is this correct?");
					break;
				case "no":
					sendMms("Is this correct?");
					break;
				default:
					sendMsg("Command not found: Please repeate");
					break;
				}

			}

			// System.out.println(time);
			preMessages = newMessages;
			// System.out.println("Updating");
			// Thread.sleep(10);

		}

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
		params.add(new BasicNameValuePair("From", "+13218004127")); // Replace														// account.
		params.add(new BasicNameValuePair("Body", msg));

		@SuppressWarnings("unused")
		Message mms = messageFactory.create(params);

	}

	public static void sendMms(String s) {
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
		params.add(new BasicNameValuePair("From", "+13218004127")); // Replace
																	// with a
																	// valid
																	// phone
																	// number
																	// for your
																	// account.
		params.add(new BasicNameValuePair("Body", s));
		params.add(new BasicNameValuePair(
				"MediaUrl",
				"https://scontent.fsnc1-1.fna.fbcdn.net/hphotos-xtp1/t31.0-8/12109992_992380487448967_8553693472276946528_o.jpg"));

		try {
			@SuppressWarnings("unused")
			Message mms = messageFactory.create(params);
		} catch (TwilioRestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
