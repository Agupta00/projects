package enroll;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Message;
import com.twilio.sdk.resource.list.MessageList;

public class MessageHandler_v2 {

	final public static String key = "7b20eef130e49d0b0b81ca20e93dd5b2";
	public static final String ACCOUNT_SID = "AC77ad25f9c6824bb96dfb1b8f59257b90";
	public static final String AUTH_TOKEN = "10e515abe06031bda04f646249118a12";
	public static final String MyNumber = "+14087173593";
	public static MessageFactory messageFactory;
	Double match;
	String name;
	File file;
	public MessageHandler_v2(Double match, String name, File file){
		this.match=match;
		this.name=name;
		this.file=file;
	}
	public MessageHandler_v2(Double match, String name){
		this.match=match;
		this.name=name;
		
	}
	
	public void listen() throws Exception {
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

			try{
				System.out.println("Last received Message: " + text.get(0) + " preMessages: [" + preMessages + "] newMessages: [" + newMessages + "]");



				if (preMessages != newMessages) {

					System.out.println("Message recived: " + text.get(0));


					if(match >.7){

						switch (text.get(0).toLowerCase()) {
						case "yes":
							//enrollFile(file, name);
							sendMsg("Subject Learned!");
							FacialRecognition.enrollFile(file, name);
							break;
						case "no":
							sendMsg("Ok Subject classified into unidentified");
							break;
						case "known":
							//enrollFile(file, name);
							sendMsg("Subject Learned!");
							FacialRecognition.enrollFile(file, name);
							break;
						case "unKnown":
							sendMsg("Ok Subject classified into unidentified");
							break;
						case "Yes":
							//enrollFile(file, name);
							sendMsg("Subject Learned!");
							FacialRecognition.enrollFile(file, name);
							break;
						case "No":
							sendMsg("Ok Subject classified into unidentified");
							break;
						case "picture":
							sendMsg("sending picture");
							break;
						case "Picture":
							sendMsg("sending picture");
							break;
						case "exit":
							sendMsg("Exiting");
						    return;
						case "Exit":
							sendMsg("Exiting");
						    return;
						    
						default:
							sendMsg("Command not found: Please repeate");
							break;
						}
					}else if(match >.6){
						switch (text.get(0).toLowerCase()) {
						case "yes":
							//enrollFile(file, name);
							sendMsg("Subject Learned!");
							break;
						case "no":
							sendMsg("Ok Subject classified into unidentified");
							break;
						case "known":
							list = client.getAccount().getMessages(filters);

							text.clear();
							for (Message message : list) {
								if (message.getDirection().contains("inbound"))
									text.add(message.getBody());
							}
							FacialRecognition.enrollFile(file, text.get(0));
							sendMsg(" New Subject enrolled!");
							break;
						case "Known":
							list = client.getAccount().getMessages(filters);

							text.clear();
							for (Message message : list) {
								if (message.getDirection().contains("inbound"))
									text.add(message.getBody());
							}
							FacialRecognition.enrollFile(file, text.get(0));
							sendMsg(" New Subject enrolled!");
							break;
						case "Yes":
							//enrollFile(file, name);
							sendMsg("Subject Learned!");
							break;
						case "No":
							sendMsg("Ok Subject classified into unidentified");
							break;
						case "picture":
							sendMsg("sending picture");
							break;
						case "Picture":
							sendMsg("sending picture");
							break;
						case "exit":
							sendMsg("Exiting");
						    return;
						case "Exit":
							sendMsg("Exiting");
						    return;

						default:
							sendMsg("Command not found: Please repeate");
							break;
						}

					}else if(match<.6){
						switch (text.get(0).toLowerCase()) {
						
						case "no":
							sendMsg("Ok Subject classified into unidentified");
							break;
						case "No":
							sendMsg("Ok Subject classified into unidentified");
							break;
						case "known":
							list = client.getAccount().getMessages(filters);

							text.clear();
							for (Message message : list) {
								if (message.getDirection().contains("inbound"))
									text.add(message.getBody());
							}
							FacialRecognition.enrollFile(file, text.get(0));
							sendMsg(" New Subject enrolled!");
							break;
						case "Known":
							list = client.getAccount().getMessages(filters);

							text.clear();
							for (Message message : list) {
								if (message.getDirection().contains("inbound"))
									text.add(message.getBody());
							}
							FacialRecognition.enrollFile(file, text.get(0));
							sendMsg(" New Subject enrolled!");
							break;
						case "picture":
							sendMsg("sending picture");
							break;
						case "Picture":
							sendMsg("sending picture");
							break;
						case "exit":
							sendMsg("Exiting");
						    return;
						case "Exit":
							sendMsg("Exiting");
						    return;
						default:
							sendMsg("Command not found: Please repeate");
							break;
						}
					}else{
						System.err.println("error");
					}

				}
			}catch (IndexOutOfBoundsException e) {
				System.out.println("No new messages");
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
		params.add(new BasicNameValuePair("From", "+13218004127")); // Replace
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
