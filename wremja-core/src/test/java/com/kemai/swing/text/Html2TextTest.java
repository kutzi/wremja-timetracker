package com.kemai.swing.text;

import org.junit.Assert;
import org.junit.Test;

public class Html2TextTest {

	
	private String html = "<html>\n" + 
			"  <head>\n" + 
			"    \n" + 
			"  </head>\n" + 
			"  <body>\n" + 
			"    <h1 align=\"start\" content=\"text/html; charset=utf-8\" style=\"font-variant: normal; padding-top: 0px; line-height: 1.7; word-spacing: 0px; margin-bottom: 15px; border-bottom-color: rgb(221, 221, 221); margin-top: 0; text-indent: 0px; white-space: normal; padding-bottom: 0px; letter-spacing: normal; padding-left: 0px; margin-right: 0px; border-bottom-style: solid; margin-left: 0px; border-bottom-width: 1px; padding-right: 0px; text-transform: none\" http-equiv=\"content-type\">\n" + 
			"      Build Pipeline Plugin\n" + 
			"    </h1>\n" + 
			"    <ul align=\"start\" class=\"task-list\" style=\"padding-top: 0px; margin-right: 0px; white-space: normal; text-indent: 0px; margin-bottom: 15px; padding-left: 30px; letter-spacing: normal; word-spacing: 0px; text-transform: none; font-variant: normal; line-height: 20.399999618530273px; padding-bottom: 0px; margin-left: 0px; padding-right: 0px; margin-top: 15px\">\n" + 
			"      <li>\n" + 
			"        <font color=\"rgb(65, 131, 196)\"><a href=\"https://wiki.jenkins-ci.org/display/JENKINS/Build+Pipeline+Plugin\">Wiki</a></font>\n" + 
			"      </li>\n" + 
			"      <li>\n" + 
			"        <a href=\"http://issues.jenkins-ci.org/secure/IssueNavigator.jspa?mode=hide&reset=true&jqlQuery=project+%3D+JENKINS+AND+status+in+%28Open%2C+%22In+Progress%22%2C+Reopened%29+AND+component+%3D+%27build-pipeline%27\"><font color=\"rgb(65, 131, 196)\">Issue \n" + 
			"        Tracking</font></a>\n" + 
			"      </li>\n" + 
			"      <li>\n" + 
			"        <a href=\"https://wiki.jenkins-ci.org/display/JENKINS/Build+Pipeline+Plugin+-+How+to+Contribute\"><font color=\"rgb(65, 131, 196)\">How \n" + 
			"        to Contribute</font></a>\n" + 
			"      </li>\n" + 
			"    </ul>\n" + 
			"    <h2 align=\"start\" style=\"font-variant: normal; padding-top: 0px; line-height: 1.7; word-spacing: 0px; margin-bottom: 15px; border-bottom-color: rgb(238, 238, 238); margin-top: 0; text-indent: 0px; white-space: normal; padding-bottom: 0px; letter-spacing: normal; padding-left: 0px; margin-right: 0px; margin-left: 0px; border-bottom-style: solid; border-bottom-width: 1px; padding-right: 0px; text-transform: none\">\n" + 
			"      <a name=\"user-content-building-the-project\" href=\"https://github.com/jenkinsci/build-pipeline-plugin#building-the-project\" class=\"anchor\" style=\"display: block; padding-left: 30px; margin-left: -30px; padding-right: 6px\"><font color=\"rgb(65, 131, 196)\">\n" + 
			"</font></a>Building the Project\n" + 
			"    </h2>\n" + 
			"    <h3 align=\"start\" style=\"padding-top: 0px; margin-right: 0px; white-space: normal; text-indent: 0px; margin-bottom: 15px; padding-left: 0px; letter-spacing: normal; word-spacing: 0px; text-transform: none; font-variant: normal; line-height: 1.7; padding-bottom: 0px; margin-left: 0px; padding-right: 0px; margin-top: 0\">\n" + 
			"      <a name=\"user-content-dependencies\" href=\"https://github.com/jenkinsci/build-pipeline-plugin#dependencies\" class=\"anchor\" style=\"display: block; padding-left: 30px; margin-left: -30px; padding-right: 6px\"><font color=\"rgb(65, 131, 196)\">\n" + 
			"</font></a>Dependencies\n" + 
			"    </h3>\n" + 
			"    <ul align=\"start\" class=\"task-list\" style=\"padding-top: 0px; margin-right: 0px; white-space: normal; text-indent: 0px; margin-bottom: 15px; padding-left: 30px; letter-spacing: normal; word-spacing: 0px; text-transform: none; font-variant: normal; line-height: 20.399999618530273px; padding-bottom: 0px; margin-left: 0px; padding-right: 0px; margin-top: 15px\">\n" + 
			"      <li>\n" + 
			"        <a href=\"https://maven.apache.org/\"><font color=\"rgb(65, 131, 196)\">Apache \n" + 
			"        Maven</font></a> 3.0.4 \n" + 
			"        or later\n" + 
			"      </li>\n" + 
			"    </ul>\n" + 
			"    <h3 align=\"start\" style=\"padding-top: 0px; margin-right: 0px; white-space: normal; text-indent: 0px; margin-bottom: 15px; padding-left: 0px; letter-spacing: normal; word-spacing: 0px; text-transform: none; font-variant: normal; line-height: 1.7; padding-bottom: 0px; margin-left: 0px; padding-right: 0px; margin-top: 0\">\n" + 
			"      <a name=\"user-content-targets\" href=\"https://github.com/jenkinsci/build-pipeline-plugin#targets\" class=\"anchor\" style=\"display: block; padding-left: 30px; margin-left: -30px; padding-right: 6px\"><font color=\"rgb(65, 131, 196)\">\n" + 
			"</font></a>Targets\n" + 
			"    </h3>\n" + 
			"    <pre align=\"start\" style=\"padding-top: 6px; margin-right: 0px; text-indent: 0px; margin-bottom: 15px; background-color: rgb(248, 248, 248); padding-left: 10px; letter-spacing: normal; word-spacing: 0px; text-transform: none; font-variant: normal; line-height: 19px; padding-bottom: 6px; margin-left: 0px; padding-right: 10px; margin-top: 15px\" lang=\"shell\"><font size=\"12px\" face=\"Consolas, Liberation Mono, Menlo, Courier, monospace\"><code style=\"background-image: null; padding-top: 0px; border-left-color: border-color; background-repeat: repeat; border-right-color: border-color; line-height: inherit; border-top-width: medium; border-right-width: medium; border-right-style: none; margin-bottom: 0px; border-top-style: none; background-attachment: scroll; border-bottom-color: border-color; margin-top: 0px; display: inline; white-space: pre; border-left-width: medium; border-left-style: none; border-top-color: border-color; margin-right: 0px; padding-left: 0px; padding-bottom: 0px; border-bottom-style: none; margin-left: 0px; background-position: null; border-bottom-width: medium; padding-right: 0px\">  $ mvn clean install</code></font></pre>\n" + 
			"    &#0;\n" + 
			"  </body>\n" + 
			"</html>\n";
	
	
	@Test
	public void shouldExtractPlainText() {
		String plainText = Html2Text.parse(html);
		
		String expected = "Build Pipeline Plugin\n"
				+ "- Wiki\n"
				+ "- Issue Tracking\n"
				+ "- How to Contribute\n"
				+ "Building the Project\n"
				+ "Dependencies\n"
				+ "- Apache Maven 3.0.4 or later\n"
				+ "Targets\n"
				+ "  $ mvn clean install ";
		
		Assert.assertEquals(expected.trim(), plainText.trim());
	}
	
}
