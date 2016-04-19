/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.usc.cs.ir.htmlunit.handler;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.io.TemporaryFilesystem;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

public class HtmlUnitWebDriver extends HtmlUnitDriver {

  private static boolean enableJavascript;
  private static boolean enableCss;
  private static boolean enableRedirect;
  private static long javascriptTimeout;
  private static int maxRedirects;
  
  public HtmlUnitWebDriver() {
    super(BrowserVersion.INTERNET_EXPLORER_11);
    //super(enableJavascript);
  }
  
  @Override
  protected WebClient modifyWebClient(WebClient client) {
    client.getOptions().setJavaScriptEnabled(enableJavascript);
    client.getOptions().setCssEnabled(enableCss);
    client.getOptions().setRedirectEnabled(enableRedirect);
    if(enableJavascript)
      client.setJavaScriptTimeout(javascriptTimeout);
    client.getOptions().setThrowExceptionOnScriptError(true);
    if(enableRedirect)
      client.addWebWindowListener(new HtmlUnitWebWindowListener(maxRedirects));
	  return client;
  }
  
  public static WebDriver getDriverForPage(String url) {
    long pageLoadTimout = 3;
    enableJavascript = true;
    enableCss = false;
    javascriptTimeout = 13500;
    int redirects = 4;
    enableRedirect = redirects <= 0 ? false : true;
    maxRedirects = redirects;
	  
    WebDriver driver = null;
	  
    try {
      driver = new HtmlUnitWebDriver();
      driver.manage().timeouts().pageLoadTimeout(pageLoadTimout, TimeUnit.SECONDS);
      driver.get(url);
     } catch(Exception e) {
       if(e instanceof TimeoutException) {
	       System.out.println("HtmlUnit WebDriver: Timeout Exception: Capturing whatever loaded so far...");
	       return driver;
     }
     cleanUpDriver(driver);
     throw new RuntimeException(e);
    }

    return driver;
  }

  public static String getHTMLContent(WebDriver driver) {
    try {
		  
      String innerHtml = "";
      if(enableJavascript) {
	      WebElement body = driver.findElement(By.tagName("body"));
	      innerHtml = (String)((JavascriptExecutor)driver).executeScript("return arguments[0].innerHTML;", body); 
      }
      else
	      innerHtml = driver.getPageSource().replaceAll("&amp;", "&");
      return innerHtml;
    } catch(Exception e) {
	    TemporaryFilesystem.getDefaultTmpFS().deleteTemporaryFiles();
    	cleanUpDriver(driver);
    	throw new RuntimeException(e);
    } 
  }

  public static void cleanUpDriver(WebDriver driver) {
    if (driver != null) {
      try {
        driver.close();
        driver.quit();
        TemporaryFilesystem.getDefaultTmpFS().deleteTemporaryFiles();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

}
