/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.protocolcarrier;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.net.http.AndroidHttpClient;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants.Method;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants.RequestHeader;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants.ResponseHeader;
import eu.trentorise.smartcampus.protocolcarrier.common.Utils;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageRequest;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageResponse;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class Communicator {

	public static MessageResponse call(MessageRequest msgRequest,
			String appToken, String authToken, Long responseTimeout)
			throws ConnectionException, ProtocolException, SecurityException {
		MessageResponse msgResponse = null;

		try {
			HttpClient httpClient;
			
			if (responseTimeout != null && responseTimeout > 0) {
				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						responseTimeout.intValue());
				HttpConnectionParams.setSoTimeout(httpParameters,
						responseTimeout.intValue());
				httpClient = new DefaultHttpClient(httpParameters);
//				httpClient = HttpsClientBuilder.getNewHttpClient(httpParameters);
			} else {
//				httpClient = HttpsClientBuilder.getNewHttpClient(null);
				httpClient = new DefaultHttpClient();
			}

			HttpRequestBase request = buildRequest(msgRequest, appToken,
					authToken);

			HttpResponse response = httpClient.execute(request);

			int status = response.getStatusLine().getStatusCode();

			if (status == Constants.CODE_SECURITY_ERROR) {
				throw new SecurityException("Invalid token");
			}
			if (status != 200) {
				throw new ProtocolException("Internal error: " + status);
			}

			long timestamp = AndroidHttpClient.parseDate(response
					.getFirstHeader(ResponseHeader.DATE.toString()).getValue());
			msgResponse = new MessageResponse(status, timestamp);

			// file
			if (msgRequest.isRequestFile()) {
				msgResponse.setFileContent(Utils
						.responseContentToByteArray(response.getEntity()
								.getContent()));
			} else {

				// content
				String result = Utils.responseContentToString(response
						.getEntity().getContent());
				if (result != null) {
					msgResponse.setBody(result);
				}
			}
		} catch (SecurityException e) {
			throw e;
		} catch (SocketTimeoutException e) {
			throw new ConnectionException(e.getMessage());
		} catch (ClientProtocolException e) {
			throw new ProtocolException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConnectionException(e.getMessage());
		} catch (URISyntaxException e) {
			throw new ProtocolException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ProtocolException(e.getMessage());
		}

		return msgResponse;
	}

	private static HttpRequestBase buildRequest(MessageRequest msgRequest,
			String appToken, String authToken) throws URISyntaxException,
			UnsupportedEncodingException {
		String[] targetHostTokens = msgRequest.getTargetHost().split("://");
		String scheme = null;
		String host = null;
		if (targetHostTokens.length > 1) {
			scheme = targetHostTokens[0];
			host = targetHostTokens[1];
		} else {
			scheme = Constants.URI_DEFAULT_SCHEME;
			host = targetHostTokens[0];
		}
		targetHostTokens = host.split(":");

		int port = targetHostTokens.length > 1 ? Integer
				.parseInt(targetHostTokens[1]) : Constants.URI_DEFAULT_PORT.get(scheme);
		host = targetHostTokens[0];
		String path = msgRequest.getTargetAddress();
		if (!path.startsWith("/"))
			path = "/" + path;

		URI uri = new URI(scheme, null, host, port, path,
				msgRequest.getQuery(), null);

		String uriString = uri.toString();
//		String uriString = msgRequest.getTargetHost()+path;
//		if (msgRequest.getQuery() != null) {
//			uriString += "?"+msgRequest.getQuery();
//		}

		HttpRequestBase request = null;
		if (msgRequest.getMethod().equals(Method.POST)) {
			HttpPost post = new HttpPost(uriString);
			// fileContent !+ null -> post multipart to server upload
			if (msgRequest.getFileContent() != null) {
				MultipartEntity mpe = new MultipartEntity();

				// "file" is constant field for multipart post according to
				// server
				mpe.addPart("file",
						new ByteArrayBody(msgRequest.getFileContent(), ""));
				post.setEntity(mpe);
			}
			if (msgRequest.getBody() != null) {
				StringEntity se = new StringEntity(msgRequest.getBody(),
						Constants.CHARSET);
				se.setContentType(msgRequest.getContentType());
				post.setEntity(se);
			}
			request = post;
		} else if (msgRequest.getMethod().equals(Method.PUT)) {
			HttpPut put = new HttpPut(uriString);
			if (msgRequest.getBody() != null) {
				StringEntity se = new StringEntity(msgRequest.getBody(),
						Constants.CHARSET);
				se.setContentType(msgRequest.getContentType());
				put.setEntity(se);
			}
			request = put;
		} else if (msgRequest.getMethod().equals(Method.DELETE)) {
			request = new HttpDelete(uriString);
		} else {
			// default: GET
			request = new HttpGet(uriString);
		}

		request.addHeader(RequestHeader.APP_TOKEN.toString(), appToken);
		request.addHeader(RequestHeader.AUTH_TOKEN.toString(), authToken);
		request.addHeader(RequestHeader.ACCEPT.toString(),
				msgRequest.getContentType());

		return request;
	}
	
	
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
	private static void trustEveryone() {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
	    			public boolean verify(String hostname, SSLSession session) {
	    				return true;
	    			}});
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[]{new X509TrustManager(){
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {}
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {}
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}}}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(
					context.getSocketFactory());
		} catch (Exception e) { // should never happen
			e.printStackTrace();
		}
	}
}
