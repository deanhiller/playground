tinyMCE.init({
	// General options
	mode : "textareas",
	theme : "advanced",
	plugins : "preview,paste,fullscreen",
	relative_urls : true,

	// Theme options
	//Bold broken in this version as it uses <strong> and should use style=""
	//	theme_advanced_buttons1 : "bold,italic,underline,|,bullist,numlist,|,justifyleft,justifycenter,justifyright,fontsizeselect,|,voicelog.colors,voicelog.variables",
	theme_advanced_buttons1 : "italic,underline,|,bullist,numlist,|,justifyleft,justifycenter,justifyright,|,fontselect,fontsizeselect,forecolor,backcolor",
	theme_advanced_buttons2 : "pastetext,pasteword,|,undo,redo,|,cleanup,code,preview,fullscreen",
	theme_advanced_buttons3 : "",
	theme_advanced_buttons4 : "",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left",
	content_css : "editor_content.css",
	theme_advanced_resizing : true 

});
