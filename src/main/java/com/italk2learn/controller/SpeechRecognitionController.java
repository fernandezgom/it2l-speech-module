package com.italk2learn.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.italk2learn.bo.inter.ISpeechRecognitionBO;
import com.italk2learn.vo.HeaderVO;
import com.italk2learn.vo.SpeechRecognitionRequestVO;
import com.italk2learn.vo.SpeechRecognitionResponseVO;

/**
 * JLF: Handles requests for the application speech recognition.
 */
@Controller
@Scope("session")
@RequestMapping("/speechRecognition")
public class SpeechRecognitionController {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SpeechRecognitionController.class);
	
	/*Services*/
	private ISpeechRecognitionBO speechRecognitionService;

    @Autowired
    public SpeechRecognitionController(ISpeechRecognitionBO speechRecognition) {
    	this.setSpeechRecognitionService(speechRecognition);
    }
    
	/**
	 * Main method to get a transcription of Sails Software
	 */
	@RequestMapping(value = "/sendData",method = RequestMethod.POST)
	@ResponseBody
	public SpeechRecognitionResponseVO sendData(@RequestBody SpeechRecognitionRequestVO request) {
		logger.info("JLF --- Speech Recognition Main Controller");
		try {
			SpeechRecognitionResponseVO response= new SpeechRecognitionResponseVO();
			response=((SpeechRecognitionResponseVO) getSpeechRecognitionService().sendNewAudioChunk(request));
			return response;
		} catch (Exception e){
			logger.error(e.toString());
		}
		return null;
	}
	
	/**
	 * Method that initialises ASREngine to be prepared to accept chunks of audio
	 */
	@RequestMapping(value = "/initEngine",method = RequestMethod.GET)
	@ResponseBody
	public Boolean initASREngine(@RequestParam(value = "user") String user, @RequestParam(value = "instance") String instance, 
			@RequestParam(value = "server") String server, @RequestParam(value = "language") String language, @RequestParam(value = "model") String model) {
		logger.info("JLF --- Speech Recognition Main Controller");
		SpeechRecognitionRequestVO request= new SpeechRecognitionRequestVO();
		request.setHeaderVO(new HeaderVO());
		request.getHeaderVO().setLoginUser(user);
		request.setInstance(Integer.parseInt(instance));
		request.setServer(server);
		request.setLanguage(language);
		request.setModel(model);
		try {
			SpeechRecognitionResponseVO response= new SpeechRecognitionResponseVO();
			response=((SpeechRecognitionResponseVO) getSpeechRecognitionService().initASREngine(request));
			return response.isOpen();
		} catch (Exception e){
			logger.error(e.toString());
		}
		return null;
	}
	
	/**
	 * Main method to get a transcription of Sails Software
	 */
	@RequestMapping(value = "/closeEngine",method = RequestMethod.GET)
	@ResponseBody
	public String closeASREngine(@RequestParam(value = "instance") String instance) {
		logger.info("JLF --- Speech Recognition Main Controller");
		SpeechRecognitionRequestVO request= new SpeechRecognitionRequestVO();
		request.setHeaderVO(new HeaderVO());
		request.setInstance(Integer.parseInt(instance));
		try {
			SpeechRecognitionResponseVO response= new SpeechRecognitionResponseVO();
			response=((SpeechRecognitionResponseVO) getSpeechRecognitionService().closeASREngine(request));
			return response.getResponse();
		} catch (Exception e){
			logger.error(e.toString());
		}
		return null;
	}
	
	public ISpeechRecognitionBO getSpeechRecognitionService() {
		return speechRecognitionService;
	}

	public void setSpeechRecognitionService(ISpeechRecognitionBO speechRecognitionService) {
		this.speechRecognitionService = speechRecognitionService;
	}


}
