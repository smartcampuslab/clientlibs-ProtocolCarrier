package eu.trentorise.smartcampus.protocolcarrier;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.net.http.AndroidHttpClient;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants;
import eu.trentorise.smartcampus.protocolcarrier.common.Utils;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants.Method;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants.RequestHeader;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants.ResponseHeader;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageRequest;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageResponse;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;

public class Communicator {

	public static MessageResponse call(MessageRequest msgRequest, String appToken, String authToken,
			Long responseTimeout) throws ConnectionException, ProtocolException {
		MessageResponse msgResponse = null;

		try {
			HttpClient httpClient;

			if (responseTimeout != null && responseTimeout > 0) {
				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters, responseTimeout.intValue());
				HttpConnectionParams.setSoTimeout(httpParameters, responseTimeout.intValue());
				httpClient = new DefaultHttpClient(httpParameters);
			} else {
				httpClient = new DefaultHttpClient();
			}

			HttpRequestBase request = buildRequest(msgRequest, appToken, authToken);

			HttpResponse response = httpClient.execute(request);

			int status = response.getStatusLine().getStatusCode();
			long timestamp = AndroidHttpClient.parseDate(response.getFirstHeader(ResponseHeader.DATE.toString())
					.getValue());
			msgResponse = new MessageResponse(status, timestamp);

			// content
			String result = Utils.responseContentToString(response.getEntity().getContent());
			if (result != null) {
				msgResponse.setBody(result);
			}
			// TODO: SECURITY_EXCEPTION???
		} catch (SocketTimeoutException e) {
			throw new ConnectionException(e.getMessage());
		} catch (ClientProtocolException e) {
			throw new ConnectionException(e.getMessage());
		} catch (IOException e) {
			throw new ProtocolException(e.getMessage());
		} catch (URISyntaxException e) {
			throw new ProtocolException(e.getMessage());
		}

		return msgResponse;
	}

	private static HttpRequestBase buildRequest(MessageRequest msgRequest, String appToken, String authToken)
			throws URISyntaxException {
		String[] targetHostTokens = msgRequest.getTargetHost().split(":");
		int targetHostPort = targetHostTokens.length > 1 ? Integer.parseInt(targetHostTokens[1])
				: Constants.URI_DEFAULT_PORT;

		URI uri = new URI(Constants.URI_DEFAULT_SCHEME, null, targetHostTokens[0], targetHostPort,
				msgRequest.getTargetAddress(), msgRequest.getQuery(), null);

		String uriString = uri.toString();

		HttpRequestBase request = null;
		if (msgRequest.getMethod().equals(Method.POST)) {
			request = new HttpPost(uriString);
		} else if (msgRequest.getMethod().equals(Method.PUT)) {
			request = new HttpPut(uriString);
		} else if (msgRequest.getMethod().equals(Method.DELETE)) {
			request = new HttpDelete(uriString);
		} else {
			// default: GET
			request = new HttpGet(uriString);
		}

		request.addHeader(RequestHeader.APP_TOKEN.toString(), appToken);
		request.addHeader(RequestHeader.AUTH_TOKEN.toString(), authToken);
		request.addHeader(RequestHeader.ACCEPT.toString(), msgRequest.getContentType());

		return request;
	}
}
