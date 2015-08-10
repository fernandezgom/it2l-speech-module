import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.italk2learn.util.TranscriptionSingleton;
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
    
    private TranscriptionSingleton sone = null;
    		
	private static final Logger logger = LoggerFactory
			.getLogger(Italk2learn.class);
	
	//JLF: Send chunks of audio to Speech Recognition engine
	public List<String> sendNewChunk(SpeechRecognitionRequestVO request) {
		System.out.println("sendNewChunk() ---Sending data from Java with instance: "+request.getInstance()+" by user="+ request.getHeaderVO().getLoginUser() +" and semaphore="+TranscriptionSingleton.getInstance().getCounter());
		instanceNum=request.getInstance();
		try {
			List<String> aux= new ArrayList<String>(TranscriptionSingleton.getInstance().getCurrentWords());
			this.sendNewAudioChunk(request.getData());
			TranscriptionSingleton.getInstance().getCurrentWords().clear();
			return aux;
		} catch (Exception e) {
			logger.error(e.toString());
			System.err.println(e);
		} 
		return null;
	}
	
	//JLF:Open the listener and retrieves true if the operation was right
	public boolean initSpeechRecognition(SpeechRecognitionRequestVO request) {
		System.out.println("initSpeechRecognition()---Open Listener from Java with instance: "+request.getInstance()+" by user="+ request.getHeaderVO().getLoginUser());
		instanceNum=request.getInstance();
		boolean result=false;
	    TranscriptionSingleton.getInstance();
		try {
			// JLF German Language de_de
			result=this.initSpeechRecognitionEngine(request.getServer(), request.getInstance(), request.getLanguage(), request.getModel());
			isInit=result;
			return result;
		} catch (Exception e) {
			logger.error(e.toString());
			System.err.println(e);
		} 
		instanceNum=request.getInstance();
		return result;
	}
	
	//JLF:Close the listener and retrieves the whole transcription
	public String closeEngine(Integer instance) {
		System.out.println("closeEngine()---Close Listener from Java with instance: "+instance.toString());
		instanceNum=instance;
		String result="";
		try {
			result=this.close();
			//System.out.println(result);
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
		if (TranscriptionSingleton.getInstance().getCounter()<=0) {
			TranscriptionSingleton.getInstance().setCounter(0);
		} else {
			TranscriptionSingleton.getInstance().setCounter(TranscriptionSingleton.getInstance().getCounter()-1);
		}
		TranscriptionSingleton.getInstance().getCurrentWords().add(text);
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
