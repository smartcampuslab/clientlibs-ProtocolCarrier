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
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
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
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.map.ObjectMapper;

import android.net.http.AndroidHttpClient;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants.Method;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants.RequestHeader;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants.ResponseHeader;
import eu.trentorise.smartcampus.protocolcarrier.common.Utils;
import eu.trentorise.smartcampus.protocolcarrier.custom.FileRequestParam;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageRequest;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageResponse;
import eu.trentorise.smartcampus.protocolcarrier.custom.ObjectRequestParam;
import eu.trentorise.smartcampus.protocolcarrier.custom.RequestParam;
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
				httpClient = HttpsClientBuilder.getNewHttpClient(httpParameters);
			} else {
				httpClient = HttpsClientBuilder.getNewHttpClient(null);
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

	private static HttpRequestBase buildRequest(MessageRequest msgRequest, String appToken, String authToken) throws URISyntaxException,
			UnsupportedEncodingException {
		String host = msgRequest.getTargetHost();
		if (host == null) throw new URISyntaxException(host,"null URI");
		if (!host.endsWith("/")) host += '/';
		String address = msgRequest.getTargetAddress();
		if (address == null) address = ""; 
		if (address.startsWith("/")) address = address.substring(1);
		String uriString = host+address;
		if (msgRequest.getQuery() != null) uriString += "?"+msgRequest.getQuery();
		new URI(uriString);

		HttpRequestBase request = null;
		if (msgRequest.getMethod().equals(Method.POST)) {
			HttpPost post = new HttpPost(uriString);
			HttpEntity httpEntity = null;
			if (msgRequest.getRequestParams() != null) {
				// if body and requestparams are either not null there is an
				// exception
				if (msgRequest.getBody() != null && msgRequest != null) {
					throw new IllegalArgumentException(
							"body and requestParams cannot be either populated");
				}
				httpEntity = new MultipartEntity();

				for (RequestParam param : msgRequest.getRequestParams()) {
					if (param.getParamName() == null
							|| param.getParamName().trim().length() == 0) {
						throw new IllegalArgumentException(
								"paramName cannot be null or empty");
					}
					if (param instanceof FileRequestParam) {
						FileRequestParam fileparam = (FileRequestParam) param;
						((MultipartEntity) httpEntity).addPart(
								param.getParamName(),
								new ByteArrayBody(fileparam.getContent(),
										fileparam.getContentType(), fileparam
												.getFilename()));
					}
					if (param instanceof ObjectRequestParam) {
						ObjectRequestParam objectparam = (ObjectRequestParam) param;
						((MultipartEntity) httpEntity).addPart(param
								.getParamName(), new StringBody(
								convertObject(objectparam.getVars())));
					}
				}
				// mpe.addPart("file",
				// new ByteArrayBody(msgRequest.getFileContent(), ""));
				// post.setEntity(mpe);
			}
			if (msgRequest.getBody() != null) {
				httpEntity = new StringEntity(msgRequest.getBody(),
						Constants.CHARSET);
				((StringEntity) httpEntity).setContentType(msgRequest
						.getContentType());
			}
			post.setEntity(httpEntity);
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

		Map<String,String> headers = new HashMap<String, String>();
		
		// default headers
		if (appToken != null) {
			headers.put(RequestHeader.APP_TOKEN.toString(), appToken);
		}
		if (authToken != null) {
			// is here for compatibility
			headers.put(RequestHeader.AUTH_TOKEN.toString(), authToken);
			headers.put(RequestHeader.AUTHORIZATION.toString(), "Bearer " + authToken);
		}
		headers.put(RequestHeader.ACCEPT.toString(), msgRequest.getContentType());
		
		if (msgRequest.getCustomHeaders() != null) {
			headers.putAll(msgRequest.getCustomHeaders());
		}

		for (String key : headers.keySet()) {
			request.addHeader(key,headers.get(key));
		}
		
		return request;
	}

	private static String convertObject(Map<String, Object> object) {
		ObjectMapper jsonMapper = new ObjectMapper();
		try {

			String jsonRepresentation = jsonMapper.writeValueAsString(object);
			return jsonRepresentation;
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"ObjectRequestParam cannot convert in JSON");
		}
	}
}
