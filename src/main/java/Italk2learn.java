import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.italk2learn.vo.SpeechRecognitionRequestVO;

public class Italk2learn {
	
	private int instanceNum;

	
	//JLF: Send chunks of audio to Speech Recognition engine each 5 seconds
    public native void sendNewAudioChunk(byte[] buf);
    //JLF: Open the listener and retrieves true if the operation was right. It is executed when the user is logged in the platform and change the exercise
    public native boolean initSpeechRecognitionEngine(String server, int instance, String languageCode, String model);
    //JLF: Close the listener and retrieves the whole transcription. It is executed each time the exercise change to another
    public native String close();
    //JLF Indicates if ASREngine is initialised or no
    private boolean isInit=false;
    
    private List<String> currentWords;
    
	private static final Logger logger = LoggerFactory
			.getLogger(Italk2learn.class);
	
	//JLF: Send chunks of audio to Speech Recognition engine
	public List<String> sendNewChunk(SpeechRecognitionRequestVO request) {
		System.out.println("sendNewChunk() ---Sending data from Java!");
		instanceNum=request.getInstance();
		try {
			List<String> aux= currentWords;
			this.sendNewAudioChunk(request.getData());
			currentWords.clear();
			return aux;
		} catch (Exception e) {
			logger.error(e.toString());
			System.err.println(e);
		} 
		return null;
	}
	
	//JLF:Open the listener and retrieves true if the operation was right
	public boolean initSpeechRecognition(int instance) {
		System.out.println("initSpeechRecognition()---Open Listener from Java!");
		instanceNum=instance;
		boolean result=false;
		currentWords= new ArrayList<String>();
		try {
			// JLF German Language de_de
			result=this.initSpeechRecognitionEngine("localhost", instance, "en_ux", "base");
			System.out.println("initSpeechRecognition()---");
			isInit=result;
			return result;
		} catch (Exception e) {
			logger.error(e.toString());
			System.err.println(e);
		} 
		instanceNum=instance;
		return result;
	}
	
	//JLF:Close the listener and retrieves the whole transcription
	public String closeEngine(int instance) {
		System.out.println("closeEngine()---Close Listener from Java!");
		instanceNum=instance;
		String result="";
		try {
			result=this.close();
			System.out.println(result);
			return result;
		} catch (Exception e) {
			logger.error(e.toString());
			System.err.println(e);
		}
		instanceNum=0; // mark as not-connected
		return result;
	}
	
	// JLF: Retrieves data from ASRResult on real time
	public String realTimeSpeech(String text) {
		logger.info(text);
		currentWords.add(text);
		System.out.println("\nJava: "+text);
	    return text;
	}
	
	static {
		try {
			System.loadLibrary("iT2L");
		} catch (Exception e) {
			logger.error(e.toString());
			System.err.println(e);
		}
	}

}