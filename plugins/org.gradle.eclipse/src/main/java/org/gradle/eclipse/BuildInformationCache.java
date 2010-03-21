/**
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.eclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gradle.foundation.ProjectView;

/**
 * This class is used to cache projectview calculations of build files
 * Since files can be edited outside the ide the absolute path isn't reliable enough.
 * The absolute path as key is replaced by a more reliable md5 hash of the build file.
 * @author Rene Groeschke
 * */
/**
 * @author Rene
 *
 */
public class BuildInformationCache{

	private Map<String, List<ProjectView>> internalMd5Cache = new HashMap<String, List<ProjectView>>();
	
	/**
	 * key 		the absolute path to a buildfile
	 * value 	the cached md5 of the buildfile 
	 * */
	private Map<String, String> pathToMd5Map = new HashMap<String, String>();
	
	/**
	 * stores gradle task informations of a given file
	 * 
	 * buildFilePath the absolute path to a build file
	 * */
	public void put(String buildFilePath, List<ProjectView> projects) {
		long start = System.currentTimeMillis();
		String md5String = calculateMd5StringForFile(buildFilePath);
		if(md5String!=null){
			internalMd5Cache.put(md5String, projects);
			pathToMd5Map.put(buildFilePath, md5String);
		}
		long end = System.currentTimeMillis();
		System.out.println("build info cache put takes: " + Long.toString(end-start) + "millis");
	}


	/**
	 * checks if task informations for the given build file are already calculated 
	 * and up to date
	 * */
	public List<ProjectView> get(String buildFilePath) {
		long start = System.currentTimeMillis();

		String storedMd5 = pathToMd5Map.get(buildFilePath);
		if(storedMd5==null){
			//no tasks calculated for buildfile
			return null;
		}
		if(!storedMd5.equals(calculateMd5StringForFile(buildFilePath))){
			// calculated tasks are outdated since buildfile has changed
			//remove stored values
			pathToMd5Map.remove(buildFilePath);
			internalMd5Cache.remove(storedMd5);
			long end = System.currentTimeMillis();
			System.out.println("build info cache get with wrong md5 takes: " + Long.toString(end-start) + " millis");
			return null;
		}
		long end = System.currentTimeMillis();
		System.out.println("build info cache get with valid md5 takes: " + Long.toString(end-start) + " millis");
		return internalMd5Cache.get(storedMd5);
	}

	/**
	 * calculates a md5 hash of a file.
	 * */
	private String calculateMd5StringForFile(String buildFilePath) {
		InputStream is = null;				
		String output = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			File f = new File(buildFilePath);
			byte[] buffer = new byte[8192];
			int read = 0;
			is = new FileInputStream(f);
			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}		
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			output = bigInt.toString(16);
		}
		catch (Exception e) {
			GradlePlugin.log(e);
		}
		finally {
			try {
				if(is!=null){
					is.close();
				}
			}
			catch(IOException e) {
				throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
			}
		}		
		return output;
	}
}
