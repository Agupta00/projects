package enroll;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Message;
import com.twilio.sdk.resource.list.MessageList;

public class FacialRecognition {
	// final public static String key1 = "3e8dd5fbf158f73e2161b7a902d7cd71";
	final public static String key = "7b20eef130e49d0b0b81ca20e93dd5b2";
	public static final String ACCOUNT_SID = "AC77ad25f9c6824bb96dfb1b8f59257b90";
	public static final String AUTH_TOKEN = "10e515abe06031bda04f646249118a12";
	public static final String MyNumber = "+14087173593";
	public static MessageFactory messageFactory;

	public static void increaseCount() { // used to keep track of the api usage
		// to keep it under 1000 uses
		File fil = new File("count.txt");

		try {
			FileReader inputFil = new FileReader(fil);
			BufferedReader in = new BufferedReader(inputFil);
			String s = in.readLine();
			String replace = new Integer((Integer.parseInt(s) + 1)).toString();
			String newText = s.replaceAll(s, (replace));
			System.out.println(replace);
			FileWriter writer = new FileWriter("count.txt");
			writer.write(newText);

			writer.close();
			in.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int getCount() {// gets the amount of uses of the api
		File fil = new File("count.txt");

		FileReader inputFil;
		try {
			inputFil = new FileReader(fil);
			BufferedReader in = new BufferedReader(inputFil);
			String s = in.readLine();
			return (Integer.parseInt(s));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 2000;
	}

	public static void enrollFile(File file, String name) {// adds a file to a
		// gallery TODO:
		// un-hardwire
		// gallery name
		if (getCount() == 999) {// checks if api is under 1000 uses
			System.err.println("At 1000 Limit! New K needed");
			return;
		}
		HttpResponse<JsonNode> detect = null;
		try {// makes call to api with a image to detect the images'facial
			// features
			detect = Unirest
					.post("https://animetrics.p.mashape.com/detect?api_key="
							+ key)
							.header("X-Mashape-Key",
									"mSRMvOpsd5mshxxBPUIgF4zIylzPp1a2uH1jsnNtapKXwmnNhG")
									.field("image", file).field("selector", "FACE").asJson();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject jsonobj = detect.getBody().getObject();// gets the JSON data
		// from the call to
		// the api

		if (jsonobj.getJSONArray("images").getJSONObject(0)
				.getJSONArray("faces").length() != 0) {// checks to see if there
			// is a face detected

			// @pre-condition: Only one face is in the picture otherwise there
			// may be a logic error
			JSONObject faceobj = jsonobj.getJSONArray("images")
					.getJSONObject(0).getJSONArray("faces").getJSONObject(0);// gets
			// facial
			// feature
			// of
			// the
			// face

			// saves data for facial features
			int topLeftX = (int) faceobj.getDouble("topLeftX");
			int topLeftY = (int) faceobj.getDouble("topLeftY");
			int width = (int) faceobj.getDouble("width");
			int height = (int) faceobj.getDouble("height");
			String image_id = detect.getBody().getObject()
					.getJSONArray("images").getJSONObject(0)
					.getString("image_id");
			String subject_id = name;

			// System.out.println(image_id); //used for testing

			// the call url to be passed to the api enroll call
			String run = "https://animetrics.p.mashape.com/enroll?api_key="
					+ key + "&gallery_id=sciencefair&height="
					+ Integer.toString(height) + "&image_id=" + image_id
					+ "&subject_id=" + subject_id + "&topLeftX="
					+ Integer.toString(topLeftX) + "&topLeftY="
					+ Integer.toString(topLeftY) + "&width="
					+ Integer.toString(width);

			try {// enrolls the face from the picture
				HttpResponse<JsonNode> enroll = Unirest
						.get(run)
						.header("X-Mashape-Key",
								"mSRMvOpsd5mshxxBPUIgF4zIylzPp1a2uH1jsnNtapKXwmnNhG")
								.header("Accept", "application/json").asJson();

				JSONObject ejsonobj = enroll.getBody().getObject();
				JSONArray eimageArray = ejsonobj.getJSONArray("images");
				JSONObject eimgobj = eimageArray.getJSONObject(0)
						.getJSONObject("transaction");

				String result = eimgobj.getString("status");// prints out the
				// result of the
				// result call
				System.out.println(result);
				increaseCount();

			} catch (UnirestException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("bad pic");// prints if there was no face found
			// in the picture
		}

	}

	public static void enrollFolder(File folder, String name) {// enrolls a
		// folder of
		// picture
		for (final File fileEntry : folder.listFiles()) {

			if (fileEntry.getName().toLowerCase().endsWith(".jpg")
					|| fileEntry.getName().toLowerCase().endsWith(".jpeg")) {
				enrollFile(fileEntry, name);
			}

		}

	}
 
	public static void recognizeFile(File file) {
		if (getCount() == 999) {// checks if the api usage is under 1000
			System.err.println("At 1000 Limit! New K needed");
			return;
		}
		HttpResponse<JsonNode> detect = null;
		try {// detect features call
			detect = Unirest
					.post("https://animetrics.p.mashape.com/detect?api_key="
							+ key)
							.header("X-Mashape-Key",
									"mSRMvOpsd5mshxxBPUIgF4zIylzPp1a2uH1jsnNtapKXwmnNhG")
									.field("image", file).field("selector", "FACE").asJson();
			increaseCount();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject jsonobj = detect.getBody().getObject();// JSON data from
		// detect call

		if (jsonobj.getJSONArray("images").getJSONObject(0)
				.getJSONArray("faces").length() != 0) {// check if there is a
			// face found in the
			// picture

			try {
				for (int i = 0; i < jsonobj.getJSONArray("images")
						.getJSONObject(0).getJSONArray("faces").length(); i++) {// itereate
					// through
					// all
					// the
					// faces
					// in
					// the
					// picture

					JSONObject faceobj = jsonobj.getJSONArray("images")
							.getJSONObject(0).getJSONArray("faces")
							.getJSONObject(i);

					// data from detect call needed for recognize call
					int topLeftX = (int) faceobj.getDouble("topLeftX");
					int topLeftY = (int) faceobj.getDouble("topLeftY");
					int width = (int) faceobj.getDouble("width");
					int height = (int) faceobj.getDouble("height");
					String image_id = detect.getBody().getObject()
							.getJSONArray("images").getJSONObject(0)
							.getString("image_id");

					System.out.println(image_id);
					// url for recognize call
					String run = "https://animetrics.p.mashape.com/recognize?api_key="
							+ key
							+ "&gallery_id=sciencefair&height="
							+ Integer.toString(height)
							+ "&image_id="
							+ image_id
							+ "&topLeftX="
							+ Integer.toString(topLeftX)
							+ "&topLeftY="
							+ Integer.toString(topLeftY)
							+ "&width="
							+ Integer.toString(width);

					HttpResponse<JsonNode> recognize = Unirest
							.get(run)
							.header("X-Mashape-Key",
									"mSRMvOpsd5mshxxBPUIgF4zIylzPp1a2uH1jsnNtapKXwmnNhG")
									.header("Accept", "application/json").asJson();
					increaseCount();

					JSONObject rjsonobj = recognize.getBody().getObject();
					JSONArray rimageArray = rjsonobj.getJSONArray("images");
					JSONObject canidates = rimageArray.getJSONObject(0)
							.getJSONObject("candidates");// canidates contains
					// all the people in
					// the gallery as
					// key pairs with a
					// value between 0-1
					// for the match to
					// the image

					double max = 0;
					int index = 0;
					for (int itr = 0; itr < canidates.length(); itr++) {// find
						// the
						// canidate
						// that
						// has
						// the
						// highest
						// match
						if (Double.parseDouble((String) canidates.get(canidates
								.names().getString(itr))) > max) {
							max = Double.parseDouble((String) canidates
									.get(canidates.names().getString(itr)));
							index = itr;
						}
					}

					// System.out.println(canidates.getString((canidates.names().getString(index))));
					Double match = Double.parseDouble(canidates.getString((canidates
							.names().getString(index))));
					String name = canidates.names().getString(index);
					//handles the comunication with the user and enrolls picutre
					//System.err.println("sadlfahksdf;lkashd");

					informUser(match, name, file);//helper method to send message to user depending on the match it has
					MessageHandler_v2 handler = new MessageHandler_v2(match, name, file);
					handler.listen(); //listens for user's response and takes action based on that

				}
				// moves file from the active snap directory into the saved
				// directory TODO: move files from snap directory to database,
				// allows usage of mms messaging to send photos to user
				
		/* temporarly commented out for testing
				File destinationDir = new File(
						"/Users/akul/FI9831W_00626E5275F2/saved");
				FileUtils.moveFileToDirectory(file, destinationDir, true);
		*/
			} catch (UnirestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TwilioRestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			System.out.println("bad pic");
		}
	}

	public static void recognizeFolder(File folder) {// recognizes a folder of
		// images (the active
		// snap directory)
		for (final File fileEntry : folder.listFiles()) {

			if (fileEntry.getName().toLowerCase().endsWith(".jpg")
					|| fileEntry.getName().toLowerCase().endsWith(".jpeg")|| fileEntry.getName().toLowerCase().endsWith(".PNG")) {
				recognizeFile(fileEntry);

			}

		}

	}

	public static void listen(Double match, String name, File file) throws Exception {

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

			 System.out.println("Last received Message: " + text.get(0) + " preMessages: [" + preMessages + "] newMessages: [" + newMessages + "]");



			if (preMessages != newMessages) {

				System.out.println("Message recived: " + text.get(0));


				if(match >.7){

					switch (text.get(0).toLowerCase()) {
					case "yes":
						//enrollFile(file, name);
						sendMsg("Confirmed");
						return;
					case "no":
						sendMms("correction noted");
						return;
					default:
						sendMsg("Command not found: Please repeate");
						break;
					}
				}else if(match >.6){
					switch (text.get(0).toLowerCase()) {
					case "yes":
						//enrollFile(file, name);
						System.err.println("here");
						sendMsg("Confirmed");
						return;
					case "no":
						sendMms("Noted");
						return;
					default:
						sendMsg("Command not found: Please repeate");
						break;
					}

				}else if(match<.6){
					switch (text.get(0).toLowerCase()) {
					case "yes":
						//enrollFile(file, name);
						sendMsg("Confirmed");
						return;
					case "no":
						sendMms("Noted");
						return;
					default:
						sendMsg("Command not found: Please repeate");
						break;
					}
				}else{
					System.err.println("error");
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

	public static void sendMms(String s) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("To", MyNumber));
		params.add(new BasicNameValuePair("From", "+13218004127"));
		params.add(new BasicNameValuePair("Body", s));
		params.add(new BasicNameValuePair(
				"MediaUrl",
				"https://scontent.fsnc1-1.fna.fbcdn.net/hphotos-xla1/v/t1.0-9/s720x720/12065641_992380487448967_8553693472276946528_n.jpg?oh=22bc5ba59cb086ec71356de82f6f5b37&oe=57473262"));

		try {
			Message message = messageFactory.create(params);
		} catch (TwilioRestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void informUser(Double match, String name, File file) throws TwilioRestException{
		if(match >.7){
			System.out.println(name
					+ " match %= "
					+ match);

			// sends the user a message on thier phone informing
			// them who is at the door

			String msg = "Hi there "
					+ name
					+ " is at your door. Confidence: "
					+ match;
			sendMsg(msg);
			sendMsg("You can use the following commands:" + "Picture- for a picture of the subject" + "Exit- for when your done");



		}else if(match >.6){
			System.out.println(name
					+ ":"
					+ match);// debug
			String msg = "Hi there, we think that "
					+ name
					+ " might be at your door. Is this him? (Yes)"
					+ "If it is not, but you know who it is please type \"known\" in then thier name"
					+ "If you dont know type \"no\" ";
			sendMsg(msg);
			sendMsg("You can use the following commands:" + "Picture- for a picture of the subject" + "Exit- for when your done");


		}else if(match<.6){
			System.out.println(name
					+ ":"
					+ match);// debug

			String msg = "Hi there, an unknown person is at your door."
					+ "If you know who it is please type \"known\" in then thier name"
					+ "If you dont know type \"no\" ";
			sendMsg(msg);
			sendMsg("You can use the following commands:" + "Picture- for a picture of the subject" + "Exit- for when your done");

		}else{
			System.err.println("error");
		}
	}
	}
