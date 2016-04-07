/**
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

package edu.usc.cs.ir.selenium.handler;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import edu.usc.cs.ir.selenium.model.InteractiveSeleniumHandler;

/**
 * Selenium Handler for http://www.arguntrader.com/ to extract weapon related posts
 * 
 * 
 * Seed list is as below:
 * http://arguntrader.com/viewforum.php?f=77
 * http://arguntrader.com/viewforum.php?f=5
 * http://arguntrader.com/viewforum.php?f=56
 * http://arguntrader.com/viewforum.php?f=20
 * http://arguntrader.com/viewforum.php?f=21
 * http://arguntrader.com/viewforum.php?f=6
 * 
 * @author karanjeets
 *
 */
public class Arguntrader implements InteractiveSeleniumHandler {

	   
	   
       public String processDriver(final WebDriver driver) {
    	   		boolean check = false;
    	   		try {
    	   			WebElement e = driver.findElement(By.id("username"));
    	   			check = true;
    	   		}
    	   		catch(Exception e) {
    	   			System.out.println("No Login Form detected");
    	   		}
    	   
	    	   	if(check){
	        		WebElement username = driver.findElement(By.id("username"));
	        		WebElement password = driver.findElement(By.id("password"));
	        		username.sendKeys("username");
	        		password.sendKeys("password");
	        		driver.findElement(By.className("button1")).click();
	        		
	        		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
	                    public Boolean apply(WebDriver d) {
	                    	try {
	    						Thread.sleep(8000);
	    					} catch (InterruptedException e) {
	    						e.printStackTrace();
	    					}
	                        return d.getCurrentUrl().toLowerCase().contains(driver.getCurrentUrl().toLowerCase());
	                    }
	                });
	        	}
               
               //System.out.println();
               return driver.getPageSource().replaceAll("&amp;", "&");
       }

       public boolean shouldProcessURL(String URL) {
           return true;
       }
       
       public static void main(String[] args) {
               Arguntrader glocktalk = new Arguntrader();
               FirefoxProfile profile = new FirefoxProfile();
               profile.setPreference(FirefoxProfile.ALLOWED_HOSTS_PREFERENCE, "localhost");
               profile.setPreference("dom.ipc.plugins.enabled.libflashplayer.so", "false");
               profile.setPreference("permissions.default.stylesheet", 1);
               profile.setPreference("permissions.default.image", 1);
               
               FirefoxBinary binary = new FirefoxBinary();
               binary.setTimeout(TimeUnit.SECONDS.toMillis(180));
               
               WebDriver driver = new FirefoxDriver(binary, profile);
               driver.manage().timeouts().pageLoadTimeout(10000, TimeUnit.MILLISECONDS);
               
               try {
                       driver.get("http://arguntrader.com/viewforum.php?f=8");
                       System.out.println(new String(glocktalk.processDriver(driver).getBytes("UTF-8")));
               } 
               catch(Exception e) {
            	   if(e instanceof TimeoutException) {
            		   System.out.println("Timeout Exception");
            		   
            		   try {
       					System.out.println(new String(glocktalk.processDriver(driver).getBytes("UTF-8")));
       				} catch (UnsupportedEncodingException e1) {
       					// TODO Auto-generated catch block
       					e1.printStackTrace();
       				}
       				
            	   }
                   e.printStackTrace();
               }
               finally {
                       if(driver != null) {
                    	   	driver.close();
                            driver.quit();
                       }
               }
       }
       
}

