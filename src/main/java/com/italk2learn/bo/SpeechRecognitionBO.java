package com.italk2learn.bo;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.italk2learn.bo.inter.ISpeechRecognitionBO;
import com.italk2learn.exception.ITalk2LearnException;
import com.italk2learn.util.TranscriptionSingleton;
import com.italk2learn.vo.SpeechRecognitionRequestVO;
import com.italk2learn.vo.SpeechRecognitionResponseVO;

@Service("speechRecognitionBO")
@Transactional(rollbackFor = { ITalk2LearnException.class, ITalk2LearnException.class })
public class SpeechRecognitionBO implements ISpeechRecognitionBO {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SpeechRecognitionBO.class);
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	private Class asrClass;
	private Method asrMethod;
	private boolean isInit=false;

	
	/*
	 * Calling ASREngine through JNI 
	 * Only is possible call JNI in default java package for that reason I use reflection
	 * Open a new connection with ASREngine 
	 */
	public SpeechRecognitionResponseVO initASREngine(SpeechRecognitionRequestVO request) throws ITalk2LearnException{
		logger.debug("initASREngine()--- Init ASREngine");
		SpeechRecognitionResponseVO res=new SpeechRecognitionResponseVO();
		//We have the user and also the instance
		try {
			if (isInit==false) {
				TranscriptionSingleton.getInstance().setCounter(0);
				asrClass = Class.forName("Italk2learn");
				asrMethod = asrClass.getMethod("initSpeechRecognition", new Class[] { SpeechRecognitionRequestVO.class });
				isInit = (Boolean)asrMethod.invoke(asrClass.newInstance(),new SpeechRecognitionRequestVO[] { request});
				res.setOpen(isInit);
				logger.debug("initASREngine()--- ASREngine initialised");
			} else {
				logger.debug("initASREngine()--- ASR Engine already initialised");
				System.out.println("sendNewAudioChunk()--- ASR Engine already initialised");
			}
			return res;
		} catch (Exception e) {
			logger.error(e.toString());
			//System.err.println(e);
		}
		return res;
	}
	
	/*
	 * Calling ASREngine through JNI 
	 * Only is possible call JNI in default java package for that reason I use reflection
	 * Close connection with ASREngine and retrieves the whole transcription 
	 */
	public SpeechRecognitionResponseVO closeASREngine(SpeechRecognitionRequestVO request) throws ITalk2LearnException{
		logger.debug("closeASREngine()--- Closing ASREngine");
		SpeechRecognitionResponseVO res=new SpeechRecognitionResponseVO();
		try {
			if (isInit==true) {
				asrMethod = asrClass.getMethod("closeEngine", new Class[] { Integer.class });
				String asrReturned = asrMethod.invoke(asrClass.newInstance(),new Integer[] { request.getInstance()}).toString();
				String value=parseTranscription(convertStringToDocument(asrReturned));
				res.setResponse(value);
				isInit=false;
				logger.debug("closeASREngine()--- ASREngine closed");
			} else {
				logger.debug("closeASREngine()--- ASR Engine not initialised");
				System.out.println("closeEngine()--- ASR Engine not initialised");
			}
			return res;
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return res;
	}
	
	/*
	 * Calling ASREngine through JNI 
	 * Only is possible call JNI in default java package for that reason I use reflection 
	 */
	public SpeechRecognitionResponseVO sendNewAudioChunk(SpeechRecognitionRequestVO request) throws ITalk2LearnException{
		logger.debug("sendNewAudioChunk()--- Sending new audio chunk");
		SpeechRecognitionResponseVO res=new SpeechRecognitionResponseVO();
		try {
			if (isInit==true && (TranscriptionSingleton.getInstance().getCounter()<=12)) {
				TranscriptionSingleton.getInstance().setCounter(TranscriptionSingleton.getInstance().getCounter()+1);
				asrMethod = asrClass.getMethod("sendNewChunk", new Class[] { SpeechRecognitionRequestVO.class });
				List<String> asrReturned= (List<String>)asrMethod.invoke(asrClass.newInstance(),new SpeechRecognitionRequestVO[] { request});
				res.setLiveResponse(asrReturned);
				logger.debug("sendNewAudioChunk()--- Audio chunk sent");
			} else {
				if (TranscriptionSingleton.getInstance().getCounter()>12) {
					logger.debug("sendNewAudioChunk()--- Semaphore is blocking sending new chunks");
					System.out.println("Send new chunk is bloked because Speech module semaphore is blocking sending more chunks since it is not receiving info from ASREngine");
					System.out.println("This can be due webbrowser is not sending audio or ASREngine is frozen");
				} else {
					logger.debug("sendNewAudioChunk()--- ASR Engine not initialised");
					System.out.println("sendNewAudioChunk()--- ASR Engine not initialised");
				}
			}	
			return res;
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return res;
	}
	
	/*
	 * Return parsed transcription
	 * 
	 */
	public String parseTranscription(Document doc) throws ITalk2LearnException{
		try {
			StringBuffer text = new StringBuffer();
			doc.getDocumentElement().normalize();

			System.out.println("root of xml file" + doc.getDocumentElement().getNodeName());
			NodeList nodes = doc.getElementsByTagName("nbest");
			System.out.println("==========================");

			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					System.out.println("word: " + getValues("word", element));
					text.append(getValues("word", element)+ " ");
				}
			}
			return text.toString();

		}
		catch (Exception e){
			logger.error(e.toString());
		}
		return null;
	}
	
	private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
        DocumentBuilder builder; 
        try 
        { 
            builder = factory.newDocumentBuilder(); 
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
            return doc;
        } catch (Exception e) {
        	logger.error(e.toString());
            e.printStackTrace(); 
        }
        return null;
    }
	
	
	private static String getValues(String tag, Element element) {
		StringBuffer text= new StringBuffer();
		for (int i = 0; i < element.getElementsByTagName(tag).getLength(); i++) {
			NodeList nodes = element.getElementsByTagName(tag).item(i).getChildNodes();
			Node node = (Node) nodes.item(0);
			text.append(node.getNodeValue()+ " ");
		}
		return text.toString();
	}
	
	public static boolean isWindows() {
		 
		return (OS.indexOf("win") >= 0);
 
	}
 
	public static boolean isUnix() {
 
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
 
	}

}
