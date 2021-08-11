package com.mishiranu.dashchan.chan.v2nulltirech;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import chan.content.ApiException;
import chan.content.ChanLocator;
import chan.content.ChanPerformer;
import chan.content.InvalidResponseException;
import chan.content.model.Board;
import chan.content.model.BoardCategory;
import chan.content.model.Post;
import chan.content.model.Posts;
import chan.http.HttpException;
import chan.http.HttpRequest;
import chan.http.HttpResponse;
import chan.http.MultipartEntity;
import chan.http.UrlEncodedEntity;
import chan.text.JsonSerial;
import chan.text.ParseException;
import chan.util.CommonUtils;
import chan.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class V2nulltirechChanPerformer extends ChanPerformer {
	private static final String COOKIE_SESSION = "_ssid";

	private static final String[] PREFERRED_BOARDS_ORDER = {"1 круг", "2 круг", "9 круг"};
	private static Pattern removeDeleted = Pattern.compile("<details class=\"deleted\">(.*?)</details>");

	@Override
	public ReadThreadsResult onReadThreads(ReadThreadsData data) throws HttpException, InvalidResponseException {
		V2nulltirechChanLocator locator = V2nulltirechChanLocator.get(this);
		if(data.isCatalog()) {
			Uri uri = locator.buildPath(data.boardName, "catalog.json");
			HttpResponse response = new HttpRequest(uri, data).perform();
			ArrayList<Posts> threads = new ArrayList<>();
			try (InputStream input = response.open(); JsonSerial.Reader reader = JsonSerial.reader(input)) {
				reader.startArray();
				while (!reader.endStruct()) {
						threads.add(V2nulltirechModelMapper.createThread(reader, locator, data.boardName, true));
				}
			} catch (ParseException e) {
				throw new InvalidResponseException(e);
			} catch (IOException e) {
				throw response.fail(e);
			}
			return new ReadThreadsResult(threads);
		} else {
			Uri uri = locator.createBoardUri(data.boardName, data.pageNumber);
			String responseText = new HttpRequest(uri, data).perform().readString();
			responseText = responseText.replaceAll("(?s)<details class=\"deleted\">.*?</details>", "");
			try {
				return new ReadThreadsResult(new V2nulltirechPostsParser(responseText, this, data.boardName).convertThreads());
			} catch (ParseException e){
				throw new InvalidResponseException(e);
			}
		}
	}

	@SuppressWarnings("SwitchStatementWithTooFewBranches")
	@Override
	public ReadPostsResult onReadPosts(ReadPostsData data) throws HttpException, InvalidResponseException {
		V2nulltirechChanLocator locator = ChanLocator.get(this);
		/*
		String lastPostNumber = data.partialThreadLoading ? data.lastPostNumber : null;
		if (lastPostNumber != null){
			Uri uri = locator.buildQuery("expand.php", "board", data.boardName, "threadid", data.threadNumber, "after", lastPostNumber);
			String responseText = new HttpRequest(uri, data).setValidator(data.validator).perform().readString();
			ArrayList<Post> posts = null;
			if(!StringUtils.isEmpty(responseText)){
				try {
					posts = new V2nulltirechPostsParser(responseText, this, data.boardName, data.threadNumber).convertPosts();
				} catch (ParseException e){
					throw new InvalidResponseException(e);
				}
			}
			if (posts == null || posts.isEmpty()){
				uri = locator.buildQuery("read.php", "b", data.boardName, "t", "0", "p", data.threadNumber, "single", "");
				responseText = new HttpRequest(uri, data).perform().readString();
				if(!responseText.contains("<div id=\"thread")) {
					throw HttpException.createNotFoundException();
				}
			}
			return new ReadPostsResult(posts);
		} else {

		 */
			Uri uri = locator.createThreadUri(data.boardName, data.threadNumber);
			String responseText = new HttpRequest(uri, data).perform().readString();
			responseText = responseText.replaceAll("(?s)<details class=\"deleted\">.*?</details>", "");
		try {
				return new ReadPostsResult(new V2nulltirechPostsParser(responseText, this, data.boardName).convertPosts());
			} catch (ParseException e){
				throw new InvalidResponseException(e);
			}
//		}
	}

	@Override
	public ReadSinglePostResult onReadSinglePost(ReadSinglePostData data) throws HttpException, InvalidResponseException {
		V2nulltirechChanLocator locator = ChanLocator.get(this);
		Uri uri = locator.buildQuery("read.php", "b", data.boardName, "t", "0", "p", data.postNumber, "single", "");
		String responseText = new HttpRequest(uri, data).perform().readString();
		try {
			Post post = new V2nulltirechPostsParser(responseText, this, data.boardName).convertSinglePost();
			if(post == null){
				throw HttpException.createNotFoundException();
			}
			return new ReadSinglePostResult(post);
		} catch (ParseException e){
			throw new InvalidResponseException(e);
		}
	}

	@Override
	public ReadBoardsResult onReadBoards(ReadBoardsData data) throws HttpException, InvalidResponseException {
		V2nulltirechChanLocator locator = V2nulltirechChanLocator.get(this);
		Uri uri = locator.buildPath("boards10.json");
		HttpResponse response = new HttpRequest(uri, data).perform();
		LinkedHashMap<String, ArrayList<Board>> boardsMap = new LinkedHashMap<>();
		try (InputStream input = response.open();
				JsonSerial.Reader reader = JsonSerial.reader(input)) {
			reader.startArray();
			while (!reader.endStruct()) {
				String category = null;
				String order = null;
				reader.startObject();
				while (!reader.endStruct()) {
					switch (reader.nextName()){
						case "name": {
							category = reader.nextString();
							break;
						}
						case "order": {
							order = reader.nextString();
							break;
						}
						case "boards": {
							String boardName = null;
							String title = null;
							reader.startArray();
							while(!reader.endStruct()){
								reader.startObject();
								while (!reader.endStruct()){
									switch (reader.nextName()) {
										case "dir": {
											boardName = reader.nextString();
											break;
										}
										case "desc": {
											title = reader.nextString();
											break;
										}
										case "order": {
											order = reader.nextString();
											break;
										}
										default: {
											reader.skip();
											break;
										}
									}
								}
								if (!StringUtils.isEmpty(category) && !StringUtils.isEmpty(boardName) && !StringUtils.isEmpty(title)) {
									ArrayList<Board> boards = boardsMap.get(category);
									if (boards == null) {
										boards = new ArrayList<>();
										boardsMap.put(category, boards);
									}
									boards.add(new Board(boardName, title));
								}
							}
						}
					}
				}
			}
			ArrayList<BoardCategory> boardCategories = new ArrayList<>();
			for (String title : PREFERRED_BOARDS_ORDER) {
				for (HashMap.Entry<String, ArrayList<Board>> entry : boardsMap.entrySet()) {
					if (title.equals(entry.getKey())) {
						ArrayList<Board> boards = entry.getValue();
						Collections.sort(boards);
						boardCategories.add(new BoardCategory(title, boards));
						break;
					}
				}
			}
			return new ReadBoardsResult(boardCategories);
		} catch (ParseException e) {
			throw new InvalidResponseException(e);
		} catch (IOException e) {
			throw response.fail(e);
		}
	}

	@Override
	public ReadPostsCountResult onReadPostsCount(ReadPostsCountData data) throws HttpException,
			InvalidResponseException {
		V2nulltirechChanLocator locator = ChanLocator.get(this);
		Uri uri = locator.createThreadUri(data.boardName, data.threadNumber);
		String responseText = new HttpRequest(uri, data).perform().readString();
		responseText = responseText.replaceAll("(?s)<details class=\"deleted\">.*?</details>", "");
		int count = 0;
		int index = 0;
		while (index != -1) {
			count++;
			index = responseText.indexOf("<td class=\"reply\"", index + 1);
		}
		return new ReadPostsCountResult(count);
	}

	private static final ColorMatrixColorFilter CAPTCHA_FILTER = new ColorMatrixColorFilter(new float[]
			{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f});

	@Override
	public ReadCaptchaResult onReadCaptcha(ReadCaptchaData data) throws HttpException, InvalidResponseException {
		V2nulltirechChanLocator locator = V2nulltirechChanLocator.get(this);
		Uri uri = locator.buildPath("captcha.php");
		HttpResponse captchaReq = new HttpRequest(uri, data).perform();
		Bitmap image = captchaReq.readBitmap();
		String sessionId = captchaReq.getCookieValue("PHPSESSID");
		if(image != null){
			Bitmap newImage = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(newImage);
			canvas.drawColor(0xffffffff);
			Paint paint = new Paint();
			paint.setColorFilter(CAPTCHA_FILTER);
			canvas.drawBitmap(image, 0f, 0f, paint);
			image.recycle();
			CaptchaData captchaData = new CaptchaData();
			captchaData.put(CaptchaData.CHALLENGE, sessionId);
			return new ReadCaptchaResult(CaptchaState.CAPTCHA, captchaData).setImage(newImage);
		}
		throw new InvalidResponseException();
	}

	private static final HttpRequest.RedirectHandler POST_REDIRECT_HANDLER = (responseCode) ->
			responseCode.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM ? HttpRequest.RedirectHandler.Action.RETRANSMIT
					: HttpRequest.RedirectHandler.Action.CANCEL;

	private static final Pattern PATTERN_POST_ERROR = Pattern.compile("(?s)<h2.*?>(.*?)</h2>");
	private static final Pattern PATTERN_BLACK_LIST_WORD = Pattern.compile("Blacklisted link \\( (.*) \\) detected.");
	private static final Pattern PATTERN_BAN_DATA = Pattern.compile("<strong>(.*?)</strong>");

	private static final SimpleDateFormat DATE_FORMAT_BAN = new SimpleDateFormat("MMMM d, yyyy, KK:mm a", Locale.US);

	static {
		DATE_FORMAT_BAN.setTimeZone(TimeZone.getTimeZone("GMT+3"));
	}

	@Override
	public SendPostResult onSendPost(SendPostData data) throws HttpException, ApiException, InvalidResponseException {
		MultipartEntity entity = new MultipartEntity();
		entity.add("board", data.boardName);
		entity.add("replythread", data.threadNumber == null ? "0" : data.threadNumber);
		entity.add("name", data.name);
		if (data.optionSage) {
			entity.add("em", "sage");
		}
		String sessionCookie = null;
		if (data.captchaData != null) {
			entity.add("captcha", data.captchaData.get(CaptchaData.INPUT));
			sessionCookie = data.captchaData.get(CaptchaData.CHALLENGE);
		}
		entity.add("subject", data.subject);
		entity.add("message", StringUtils.emptyIfNull(data.comment));
		entity.add("postpassword", data.password);
		if (data.attachments != null) {
			for(int i = 0; i< data.attachments.length; i++){
				SendPostData.Attachment attachment = data.attachments[i];
				attachment.addToEntity(entity, "imagefile[]");
				if (attachment.optionSpoiler) {
					entity.add("spoiler-" + i, "1");
					entity.add("filename-" + i, attachment.getFileName());
				}
			}
		}
		entity.add("makepost", "1");
		entity.add("legacy-posting", "1");
		entity.add("redirecttothread", "1");
		V2nulltirechChanLocator locator = ChanLocator.get(this);
		Uri uri = locator.buildPath("board.php");

		String responseText = null;
		try {
//			HttpRequest request = new HttpRequest(uri, data).addCookie("PHPSESSID", sessionCookie).setRedirectHandler(POST_REDIRECT_HANDLER);
			HttpResponse response = new HttpRequest(uri, data).setPostMethod(entity).addCookie("PHPSESSID", sessionCookie).setRedirectHandler(POST_REDIRECT_HANDLER).perform();
			responseText = response.readString();
//			responseText = request.setPostMethod(entity).setRedirectHandler(HttpRequest.RedirectHandler.NONE).perform().readString();
			if (response.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP || responseText.contains("301")) {
				uri = response.getRedirectedUri();
				String threadNumber = locator.getThreadNumber(uri);
				return new SendPostResult(threadNumber, null);
			}
			responseText = response.readString();
			Log.d("benis", responseText);
		} finally {

		}
		Matcher matcher = PATTERN_POST_ERROR.matcher(responseText);
		if (matcher.find()) {
			String errorMessage = matcher.group(1).trim();
			int errorType = 0;
			Object extra = null;
			if (errorMessage.contains("Капча введена неверно") || errorMessage.contains("Капча протухла")) {
				errorType = ApiException.SEND_ERROR_CAPTCHA;
			} else if (errorMessage.contains("Для ответа нужна картинка, видео или сообщение")) {
				errorType = ApiException.SEND_ERROR_EMPTY_COMMENT;
			} else if (errorMessage.contains("A file is required for a new thread") ||
					errorMessage.contains("Please enter an embed ID")) {
				errorType = ApiException.SEND_ERROR_EMPTY_FILE;
			} else if (errorMessage.contains("Неверный ID треда")) {
				errorType = ApiException.SEND_ERROR_NO_THREAD;
			} else if (errorMessage.contains("Sorry, your message is too long")) {
				errorType = ApiException.SEND_ERROR_FIELD_TOO_LONG;
			} else if (errorMessage.contains("Flood Detected")) {
				errorType = ApiException.SEND_ERROR_TOO_FAST;
			} else if (errorMessage.contains("Убедитесь, что Ваш файл меньше")) {
				errorType = ApiException.SEND_ERROR_FILE_TOO_BIG;
			} else if (errorMessage.contains("Обнаружен дублирующий файл")) {
				errorType = ApiException.SEND_ERROR_FILE_EXISTS;
			} else if (errorMessage.contains("Этот тред закрыт")) {
				errorType = ApiException.SEND_ERROR_CLOSED;
			} else if (errorMessage.contains("Эта доска закрыта")) {
				errorType = ApiException.SEND_ERROR_NO_ACCESS;
			} else if (errorMessage.contains("Вы забанены")) {
				errorType = ApiException.SEND_ERROR_BANNED;
				matcher = PATTERN_BAN_DATA.matcher(responseText);
				ApiException.BanExtra banExtra = new ApiException.BanExtra();
				boolean startDateFound = false;
				while (matcher.find()) {
					String group = matcher.group(1);
					boolean parseSuccess = false;
					long date = 0;
					try {
						date = DATE_FORMAT_BAN.parse(group).getTime();
						parseSuccess = true;
					} catch (java.text.ParseException e) {
						// Ignore exception
					}
					if (parseSuccess || startDateFound) {
						if (startDateFound) {
							if (parseSuccess) {
								banExtra.setExpireDate(date);
							} else if (group.contains("не истечет")) {
								banExtra.setExpireDate(Long.MAX_VALUE);
							}
							extra = banExtra;
							break;
						} else {
							banExtra.setStartDate(date);
							startDateFound = true;
						}
					} else {
						banExtra.setMessage(group);
					}
				}
			} else if (errorMessage.contains("Blacklisted link")) {
				errorType = ApiException.SEND_ERROR_SPAM_LIST;
				matcher = PATTERN_BLACK_LIST_WORD.matcher(errorMessage);
				if (matcher.matches()) {
					extra = new ApiException.WordsExtra().addWord(matcher.group(1));
				}
			}
			if (errorType != 0) {
				throw new ApiException(errorType, extra);
			}
			CommonUtils.writeLog("Nullone send message", errorMessage);
			throw new ApiException(errorMessage);
		}
		throw new InvalidResponseException();

	}

	@Override
	public SendDeletePostsResult onSendDeletePosts(SendDeletePostsData data)
			throws HttpException, ApiException, InvalidResponseException {

		V2nulltirechChanLocator locator = ChanLocator.get(this);
		UrlEncodedEntity entity = new UrlEncodedEntity();
		entity.add("board", data.boardName);
		for (String postNumber : data.postNumbers) {
			entity.add("post[]", postNumber);
		}
		if (data.optionFilesOnly) {
			entity.add("fileonly", "on");
		}
		entity.add("postpassword", data.password);
		entity.add("deletepost", "Удалить");
		Uri uri = locator.buildPath("board.php");
		String responseText = new HttpRequest(uri, data).setPostMethod(entity).setRedirectHandler(POST_REDIRECT_HANDLER).perform().readString();
		if (responseText != null) {
			if (responseText.contains("Пост удален") || responseText.contains("Изображение успешно удалено") ||
					responseText.contains("Ваш пост не имеет изображения")) {
				// Response has message for any post
				// Ignore them, if at least 1 of them was deleted
				return null;
			} else if (responseText.contains("Неверный пароль")) {
				throw new ApiException(ApiException.DELETE_ERROR_PASSWORD);
			}
			CommonUtils.writeLog("v2nulltirech delete message", responseText);
		}
		throw new InvalidResponseException();
	}


	@Override
	public SendReportPostsResult onSendReportPosts(SendReportPostsData data) throws HttpException, ApiException,
			InvalidResponseException {
		V2nulltirechChanLocator locator = ChanLocator.get(this);
		UrlEncodedEntity entity = new UrlEncodedEntity();
		entity.add("board", data.boardName);
		for(String postNumber: data.postNumbers){
			entity.add("post[]", postNumber);
		}
		entity.add("reportreason", data.comment);
		entity.add("reportpost", "Пожаловаться");
		Uri uri = locator.buildPath("board.php");
		String responseText = new HttpRequest(uri, data).setPostMethod(entity).setRedirectHandler(POST_REDIRECT_HANDLER).perform().readString();
		if(responseText!=null){
			if(responseText.contains("Post successfully reported")){
				return null;
			}
			CommonUtils.writeLog("v2nulltirech report message", responseText);
		}
		throw new InvalidResponseException();
	}
}
