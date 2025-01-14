package com.mishiranu.dashchan.chan.v2nulltirech;

import android.net.Uri;
import chan.content.ChanLocator;
import java.util.List;
import java.util.regex.Pattern;

public class V2nulltirechChanLocator extends ChanLocator {
	private static final Pattern BOARD_PATH = Pattern.compile("/\\w+(?:/(?:(?:catalog|index|\\d+)\\.html)?)?");
	private static final Pattern THREAD_PATH = Pattern.compile("/\\w+/res/(\\d+)\\.html");
	private static final Pattern ATTACHMENT_PATH = Pattern.compile("/\\w+/src/\\d+\\.\\w+");

	public V2nulltirechChanLocator() {
		addChanHost("2.0-chan.ru");
		addConvertableChanHost("www.2.0-chan.ru");
		addChanHost("v2.0-chan.ru");
		setHttpsMode(HttpsMode.CONFIGURABLE);
	}

	@Override
	public boolean isBoardUri(Uri uri) {
		return isChanHostOrRelative(uri) && isPathMatches(uri, BOARD_PATH);
	}

	@Override
	public boolean isThreadUri(Uri uri) {
		return isChanHostOrRelative(uri) && isPathMatches(uri, THREAD_PATH);
	}

	@Override
	public boolean isAttachmentUri(Uri uri) {
		return isChanHostOrRelative(uri) && isPathMatches(uri, ATTACHMENT_PATH);
	}

	@Override
	public String getBoardName(Uri uri) {
		List<String> segments = uri.getPathSegments();
		return segments.isEmpty() ? null : segments.get(0);
	}

	@Override
	public String getThreadNumber(Uri uri) {
		String value = getGroupValue(uri.getPath(), THREAD_PATH, 1);
		if (value == null) {
			value = getGroupValue(uri.getPath(), ATTACHMENT_PATH, 1);
		}
		return value;
	}

	@Override
	public String getPostNumber(Uri uri) {
		return uri.getFragment();
	}

	@Override
	public Uri createBoardUri(String boardName, int pageNumber) {
		return pageNumber > 0 ? buildPath(boardName, pageNumber + ".html") : buildPath(boardName, "");
	}

	@Override
	public Uri createThreadUri(String boardName, String threadNumber) {
		return buildPath(boardName, "res", threadNumber + ".html");
	}

	@Override
	public Uri createPostUri(String boardName, String threadNumber, String postNumber) {
		return createThreadUri(boardName, threadNumber).buildUpon().fragment(postNumber).build();
	}

	public Uri buildAttachmentPath(String boardName, String fileName, String ext){
		return buildPath(boardName, "src", fileName + '.' + ext);
	}

	public Uri buildThumbnailPath(String boardName, String fileName, String ext){
		return buildPath(boardName, "thumb", fileName + "s." + ext);
	}
}
