/*

Copyright 2012 Daniel Gonzalez Peña


This file is part of the jARVEST Project. 

jARVEST Project is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

jARVEST Project is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser Public License for more details.

You should have received a copy of the GNU Lesser Public License
along with jARVEST Project.  If not, see <http://www.gnu.org/licenses/>.
*/
package es.uvigo.ei.sing.jarvest.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpException;


@SuppressWarnings("serial")
public class HTTPPost extends URLBasedTransformer{

	private String queryString; 
	private String URL;
	private String querySeparator="&";
	boolean postDone = false;
	
	private boolean outputHTTPOutputs = false; //if false the output is the operator input, if true the output is the server output 
	
	public String getQuerySeparator() {
		return querySeparator;
	}
	public void setQuerySeparator(String querySeparator) {
		this.querySeparator = querySeparator;
		this.setChanged();
		this.notifyObservers();
	}
	public boolean getOutputHTTPOutputs() {
		return outputHTTPOutputs;
	}
	public void setOutputHTTPOutputs(boolean outputHTTPOutputs) {
		this.outputHTTPOutputs = outputHTTPOutputs;
		this.setChanged();
		this.notifyObservers();
	}
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
		this.setChanged();
		this.notifyObservers();
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String url) {
		URL = url;
		this.setChanged();
		this.notifyObservers();
	}
	
	
	
	
	@Override
	protected void _pushString(String str){
		if (!this.outputHTTPOutputs) this.getOutputHandler().pushOutput(str);
	}
	@Override
	protected void _closeOneInput() {
			if (!this.postDone){
				doPost(this.outputHTTPOutputs);
				this.postDone = true;
			}
			if (!this.outputHTTPOutputs){
				//this.getOutputHandler().pushOutput(output);
				this.getOutputHandler().outputFinished();
			}
		//this.getOutputHandler().outputFinished();	
	}
	private void doPost(boolean pushOutput){
		InputStream stream = null;
		String toret = null;
		try {
			StringBuffer charsetb = new StringBuffer();
			stream = HTTPUtils.doPost(this.URL, this.queryString, this.querySeparator, this.getAdditionalHeaders(), charsetb);
			String charset = this.configureCharset(charsetb.toString());
			if (pushOutput){
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        	
	        	byte[] bytes = new byte[1024];
	        	int read = -1;
	        	while((read=stream.read(bytes))!=-1){
	        		bos.write(bytes, 0, read);
	        	}
	        	
	        	if (this.isBinary()){
	            	toret = new String(Base64Coder.encode(bos.toByteArray()));
	            }else{
	            	toret = new String(bos.toByteArray(), charset);
	            }
				if (stream!=null){
					this.getOutputHandler().pushOutput(toret);
					this.getOutputHandler().outputFinished();
					//super._pushString(output);
				}
			}
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (stream!=null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	@Override
	protected void _closeAllInputs(){
		if (!this.postDone){
			doPost(this.outputHTTPOutputs);
		}
		this.postDone = false;
		//this.getOutputHandler().allFinished();
		super._closeAllInputs();
		
	}
	
	public static void main(String args[]){
		HTTPPost httpPost = new HTTPPost();
		httpPost.setOutputHTTPOutputs(true);
		httpPost.setOutputHandler(new OutputHandler(){

			public void allFinished() {
				// TODO Auto-generated method stub
				
			}

			public void outputFinished() {
				// TODO Auto-generated method stub
				
			}

			public void pushOutput(String string) {
				// TODO Auto-generated method stub
				
			}
			
		});
		httpPost.setQueryString("query=<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE Query><Query  virtualSchemaName = \"default\" formatter = \"TSV\" header = \"0\" uniqueRows = \"0\" count = \"\" datasetConfigVersion = \"0.6\" ><Dataset name = \"hsapiens_gene_ensembl\" interface = \"default\" ><Filter name = \"hgnc_symbol\" value = \"BRCA2,BRCA1\"/><Attribute name = \"affy_hg_u133_plus_2\" /></Dataset></Query>");
		httpPost.setURL("http://www.ensembl.org/biomart/martservice");
		httpPost.setQuerySeparator("@@impossible");
		httpPost.pushString("hola");
/*		try {
			//System.out.println(HTTPUtils.getURLBodyAsString("https://www.ei.uvigo.es/privado/supervisor/listar_usuarios_modificar.php"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		httpPost.closeAllInputs();
		
	}

	
	@Override
	protected String[] _apply(String[] source) {
		return source;
	}

}
