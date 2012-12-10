package controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.Controller;

public class Application extends Controller {

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
    public static void index() {
        render();
    }

}