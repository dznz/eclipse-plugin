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
package org.gradle.eclipse.util;

import java.io.File;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Rene Groeschke
 * @FIXME refactor this class
 * */
public class GradleUtil {
	
	/**
	 * Returns the list of Strings that were delimiter separated.
	 * 
	 * @param delimString the String to be tokenized based on the delimiter
	 * @return a list of Strings
	 */
	public static String[] parseString(String delimString, String delim) {
		if (delimString == null) {
			return new String[0];
		}
		
		// Need to handle case where separator character is
		// actually part of the target name!
		StringTokenizer tokenizer = new StringTokenizer(delimString, delim);
		String[] results = new String[tokenizer.countTokens()];
		for (int i = 0; i < results.length; i++) {
			results[i] = tokenizer.nextToken();
		}
		
		return results;
	}
	
	/**
	 * Returns the workspace file associated with the given path in the
	 * local file system, or <code>null</code> if none.
	 * If the path happens to be a relative path, then the path is interpreted as
	 * relative to the specified parent file.
	 * 
	 * Attempts to handle linked files; the first found linked file with the correct
	 * path is returned.
	 *   
	 * @param path
	 * @param buildFileParent
	 * @return file or <code>null</code>
	 * @see org.eclipse.core.resources.IWorkspaceRoot#findFilesForLocation(IPath)
	 */
	public static IFile getFileForLocation(String path, File buildFileParent) {
		if (path == null) {
			return null;
		}
		IPath filePath= new Path(path);
		IFile file = null;
;		System.out.println(filePath.toFile().getAbsolutePath());
		IFile[] files= ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(filePath.toFile().toURI());
		if (files.length > 0) {
			file= files[0];
		}
		if (file == null) {
			//relative path
			try {
				//this call is ok if buildFileParent is null
				files= ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(filePath.toFile().toURI());
				if (files.length > 0) {
					file= files[0];
				} else {
					return null;
				}
			} catch (Exception be) {
				return null;
			}
		}
		
		if (file.exists()) {
			return file;
		} 
		File ioFile= file.getLocation().toFile();
		if (ioFile.exists()) {//needs to handle case insensitivity on WINOS
			files= ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(ioFile.toURI());
			if (files.length > 0) {
				return files[0];
			}			
		}
			
		return null;
	}

	public static IFile getFileForLocation(IPath filePath, File buildFileParent) {
		if (filePath == null) {
			return null;
		}
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(filePath);
		return file;
	}

}
