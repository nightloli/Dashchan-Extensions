package com.mishiranu.dashchan.chan.v2nulltirech;

import chan.content.ChanConfiguration;
import chan.util.StringUtils;
import java.util.Locale;

public class V2nulltirechChanConfiguration extends ChanConfiguration {
	public static final String CAPTCHA_TYPE_V2NULLTIRECH = "v2nulltirech";

	private static final String KEY_FILES_ENABLED = "files_enabled";
	private static final String KEY_NAMES_ENABLED = "names_enabled";
	private static final String KEY_POSTING_ENABLED = "posting_enabled";
	private static final String KEY_DELETING_ENABLED = "deleting_enabled";
	private static final String KEY_MAX_COMMENT_LENGTH = "max_comment_length";

	public V2nulltirechChanConfiguration() {
//		request(OPTION_READ_THREAD_PARTIALLY);
		request(OPTION_READ_POSTS_COUNT);
		request(OPTION_READ_SINGLE_POST);
		setDefaultName("Аноним");
		setBumpLimit(500);
		addCaptchaType(CAPTCHA_TYPE_V2NULLTIRECH);
	}

	@Override
	public Board obtainBoardConfiguration(String boardName) {
		Board board = new Board();
		board.allowCatalog = true;
		board.allowPosting = true;
		board.allowDeleting = true;
		board.allowReporting = true;
		return board;
	}

	@Override
	public Captcha obtainCustomCaptchaConfiguration(String captchaType) {
		if (CAPTCHA_TYPE_V2NULLTIRECH.equals(captchaType)) {
			Captcha captcha = new Captcha();
			captcha.title = "v2nulltirech";
			captcha.input = Captcha.Input.ALL;
			captcha.validity = Captcha.Validity.IN_BOARD_SEPARATELY;
			return captcha;
		}
		return null;
	}

	@Override
	public Posting obtainPostingConfiguration(String boardName, boolean newThread) {
		Posting posting = new Posting();
		posting.allowName = true;
		posting.allowTripcode = true;
		posting.allowEmail = true;
		posting.allowSubject = true;
		posting.optionSage = true;
		posting.attachmentSpoiler = true;
		posting.attachmentCount = 4;
		posting.attachmentMimeTypes.add("image/jpeg");
		posting.attachmentMimeTypes.add("image/gif");
		posting.attachmentMimeTypes.add("image/png");
		posting.attachmentMimeTypes.add("video/webm");
		posting.attachmentMimeTypes.add("video/mp4");
		posting.attachmentMimeTypes.add("audio/mp3");
		return posting;
	}

	@Override
	public Deleting obtainDeletingConfiguration(String boardName) {
		Deleting deleting = new Deleting();
		deleting.password = true;
		deleting.multiplePosts = true;
		deleting.optionFilesOnly = true;
		return deleting;
	}

	@Override
	public Reporting obtainReportingConfiguration(String boardName) {
		Reporting reporting = new Reporting();
		reporting.comment = true;
		reporting.multiplePosts = true;
		return reporting;
	}
}
