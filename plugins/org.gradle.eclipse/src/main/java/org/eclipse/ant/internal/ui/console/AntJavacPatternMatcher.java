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
package org.eclipse.ant.internal.ui.console;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.gradle.eclipse.GradlePlugin;

public class AntJavacPatternMatcher implements IPatternMatchListenerDelegate {
    
	protected static final Integer warningType= new Integer(IMarker.SEVERITY_WARNING);
	protected static final Integer errorType= new Integer(IMarker.SEVERITY_ERROR);
	protected static final Integer infoType= new Integer(IMarker.SEVERITY_INFO);
	
	private Map<String, IFile> fileNames2IFiles= new HashMap<String, IFile>();
	private TextConsole console;

	public void connect(TextConsole console) {
		this.console = console;
	}

	public void disconnect() {
		 console = null;
		 fileNames2IFiles.clear();
	}

	public void matchFound(PatternMatchEvent event) {
		int offset = event.getOffset();
		String matchedText= getMatchedText(event);
        if (matchedText == null) {
            return;
        }

        int linebreakIdx = matchedText.lastIndexOf('\n');
        String fullFilePath = matchedText.substring(linebreakIdx).trim();
        int fullPathIdx = matchedText.indexOf(fullFilePath);
        int numStart = offset + fullPathIdx;
//        String filePath = ;
        int linkLength = event.getLength() - fullPathIdx;
       
       	Integer type= infoType;
       	if (-1 != matchedText.indexOf("warning", linkLength)) { //$NON-NLS-1$
       		type= warningType;
       	}
    	if (-1 != matchedText.indexOf("error", linkLength)) { //$NON-NLS-1$
       		type= errorType;
       	}
        addLink(fullFilePath, 0, numStart, linkLength, type);
	}
	
	 protected void addLink(String filePath, int lineNumber, int offset, int length, Integer type) {
	        IFile file= getIFile(filePath);
	        if (file == null) {
	            return;
	        }
	        
	        /*if (fMarkerCreator != null) {
	        	if (type == null) { //match for listfiles
	        		fMarkerCreator.addFileToBeCleaned(file);
	        	} else { //match for error or warning
	        		fMarkerCreator.addMarker(file, lineNumber, offset, type);
	        	}
	        }*/
	        
	        //Use link java sources to linenumber 0 
	        FileLink link = new FileLink(file, null, -1, -1, 0);
	        try {
	            console.addHyperlink(link, offset, length);
	        } catch (BadLocationException e) {
	            GradlePlugin.log(e);
	        }
	    }
	
	
	protected String getMatchedText(PatternMatchEvent event) {
        int eventOffset= event.getOffset();
        int eventLength= event.getLength();
        IDocument document= console.getDocument();
        String matchedText= null;
        try {
            matchedText= document.get(eventOffset, eventLength);
        } catch (BadLocationException e) {
            GradlePlugin.log(e);
        }
        return matchedText;
    }
	
	protected IFile getIFile(String filePath) {
        if (filePath == null) {
            return null; 
        }
        IFile file = fileNames2IFiles.get(filePath);
        if (file == null) {
        	IPath path = new Path(filePath);
            IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(path.toFile().toURI());
            if (files.length > 0) {
                file = files[0];
                fileNames2IFiles.put(filePath, file);
            }
        }
        return file;
    }

}
