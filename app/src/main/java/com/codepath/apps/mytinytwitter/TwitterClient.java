package com.codepath.apps.mytinytwitter;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "vWrme030QRsgjkYJRQNFsCQSB";
	public static final String REST_CONSUMER_SECRET = "C5Gjamds1Rl9H8Coz9HCeF3nldPzkkFeDI15lRLfXE2flbR3TH";
	public static final String REST_CALLBACK_URL = "oauth://mytinytwitter";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	// GET statuses/home_timeline
	// a collection of the most recent Tweets and retweets posted by the authenticating user and the users they follow
	public void getHomeTimeline(int count, String maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		if (count > 0)
			params.put("count", count);
		if (maxId != null && !maxId.trim().isEmpty())
			params.put("max_id", maxId);
		params.put("include_entities", "true");
		client.get(apiUrl, params, handler);
	}

	// GET account/verify_credentials
	public void getUserAccount(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl, new RequestParams(), handler);
	}

	// POST statuses/update
	public void postStatus(String status, String inReplyToStatusId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", status);
		if (inReplyToStatusId != null)
			params.put("in_reply_to_status_id", inReplyToStatusId);
		client.post(apiUrl, params, handler);
	}

	// GET statuses/mentions_timeline
	// Returns the 20 most recent mentions (tweets containing a users’s @screen_name) for the authenticating user
	public void getMentionsTimeline(int count, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		if (count > 0)
			params.put("count", count);
		params.put("include_entities", "true");
		client.get(apiUrl, params, handler);
	}

	// GET statuses/user_timeline
	// Returns a collection of the most recent Tweets posted by the user indicated by the screen_name or user_id parameters
	public void getUserTimeline(int count, String userId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		if (count > 0)
			params.put("count", count);
		if (userId != null && !userId.trim().isEmpty())
			params.put("user_id", userId);
		client.get(apiUrl, params, handler);
	}

	// GET users/show
	// Returns a variety of information about the user specified by the required user_id or screen_name parameter
	public void getUser(String userId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("user_id", userId);
		client.get(apiUrl, params, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}