package com.mishiranu.dashchan.chan.v2nulltirech;

import chan.content.model.FileAttachment;
import chan.content.model.Post;
import chan.content.model.Posts;
import chan.text.JsonSerial;
import chan.text.ParseException;
import chan.util.StringUtils;
import java.io.IOException;
import java.util.ArrayList;

public class V2nulltirechModelMapper {
	public static class Extra {
		private int postsCount;
		private int postsWithFilesCount;
	}

	public static Post createPost(JsonSerial.Reader reader, V2nulltirechChanLocator locator, String boardName, Extra extra, Boolean fromCatalog)
			throws IOException, ParseException {
		if (fromCatalog) {
			Post post = new Post();
			String tim = null;
			String ext = null;
			int width = 0;
			int height = 0;
			reader.startObject();
			int page = -1;
			while (!reader.endStruct()) {
				switch (reader.nextName()) {
					case "id": {
						post.setPostNumber(reader.nextString());
						break;
					}
					case "subject": {
						post.setSubject(StringUtils.clearHtml(reader.nextString()).trim());
						break;
					}
					case "message": {
						post.setComment(reader.nextString());
						break;
					}
					case "timestamp": {
						post.setTimestamp(Long.valueOf(reader.nextString()) * 1000L);
						break;
					}
					case "stickied": {
						if (reader.nextString().equals("0")) {
							post.setSticky(false);
						} else {
							post.setSticky(true);
						}
						break;
					}
					case "locked": {
						if (reader.nextString().equals("0")) {
							post.setClosed(false);
						} else {
							post.setClosed(true);
						}
						break;
					}
					case "bumped": {
						reader.nextString();
						break;
					}
					case "name": {
						post.setName(reader.nextString());
						break;
					}
					case "tripcode": {
						post.setTripcode(reader.nextString());
						break;
					}
					case "posterauthority": {
						reader.nextString();
						break;
					}
					case "deleted_timestamp": {
						reader.nextString();
						break;
					}
					case "page": {
						page = reader.nextInt();
						break;
					}
					case "reply_count": {
						extra.postsCount = reader.nextInt();
						break;
					}
					case "replied": {
						reader.nextInt();
						break;
					}
					case "last_reply": {
						reader.nextString();
						break;
					}
					case "images": {
						extra.postsWithFilesCount = reader.nextInt();
						break;
					}
					case "embeds": {
						reader.startArray();
						while (!reader.endStruct()) {
							reader.startObject();
							while (!reader.endStruct()) {
								FileAttachment attachment = new FileAttachment();
								switch (reader.nextName()) {
									case "file": {
										tim = reader.nextString();
										break;
									}
									case "file_type": {
										ext = reader.nextString();
										break;
									}
									case "image_w": {
										width = Integer.parseInt(reader.nextString());
										break;
									}
									case "image_h": {
										height = Integer.parseInt(reader.nextString());
										break;
									}
									case "thumb_w": {
										reader.nextString();
										break;
									}
									case "thumb_h": {
										reader.nextString();
										break;
									}
									default: {
//									reader.skip();
										break;
									}
								}
								if (tim != null && ext != null) {
									attachment.setFileUri(locator, locator.buildAttachmentPath(boardName, tim, ext));
									attachment.setHeight(height);
									attachment.setWidth(width);
									attachment.setThumbnailUri(locator, locator.buildThumbnailPath(boardName, tim, ext));
									attachment.setOriginalName(tim);
									post.setAttachments(attachment);
								}
							}
						}
					}
					default: {
//					reader.skip();
						break;
					}
				}
			}
			return post;
		} else {
			return null;
		}
	}

	public static Posts createThread(JsonSerial.Reader reader, V2nulltirechChanLocator locator, String boardName, boolean fromCatalog) throws IOException, ParseException {
		if (fromCatalog) {
			ArrayList<Post> posts = new ArrayList<>();
			int postsCount = 0;
			int postsWithFilesCount = 0;
			Extra extra = new Extra();
			Post originalPost = createPost(reader, locator, boardName, extra, true);
			postsCount = extra.postsCount;
			postsWithFilesCount = extra.postsWithFilesCount;
			posts.add(originalPost);
			return new Posts(posts).addPostsCount(postsCount).addPostsWithFilesCount(postsWithFilesCount);
		} else {
			return null;
		}
	}
}
