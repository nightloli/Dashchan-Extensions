package com.mishiranu.dashchan.chan.synch;

import android.util.Pair;

import chan.content.ChanConfiguration;

public class SynchChanConfiguration extends ChanConfiguration {
	public SynchChanConfiguration() {
		request(OPTION_READ_POSTS_COUNT);
		setDefaultName("Аноним");
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
	public Posting obtainPostingConfiguration(String boardName, boolean newThread) {
		Posting posting = new Posting();
		posting.allowName = !"d".equals(boardName) && !"mlp".equals(boardName);
		posting.allowEmail = true;
		posting.allowSubject = true;
		posting.optionSage = true;
		posting.allowTripcode = true;
		posting.attachmentCount = 1;
		posting.attachmentMimeTypes.add("image/*");
		posting.attachmentMimeTypes.add("video/*");
		posting.attachmentMimeTypes.add("audio/*");
		posting.attachmentMimeTypes.add("application/*");
		posting.attachmentSpoiler = true;
		posting.userIcons.add(Pair.create("green","GREEN"));
		posting.userIcons.add(Pair.create("yellow","YELLOW"));
		posting.userIcons.add(Pair.create("red","RED"));
		posting.userIcons.add(Pair.create("anon","Anonymous"));
		posting.userIcons.add(Pair.create("doge","DOGE"));
		posting.userIcons.add(Pair.create("kappa","KAPPA"));
		posting.userIcons.add(Pair.create("mlg","MLG"));
		posting.userIcons.add(Pair.create("wwe","WWE"));
		posting.userIcons.add(Pair.create("dota2","DOTA"));
		posting.userIcons.add(Pair.create("isaac","Isaac"));
		posting.userIcons.add(Pair.create("bnet","Battle.net"));
		posting.userIcons.add(Pair.create("steam","Steam"));
		posting.userIcons.add(Pair.create("lenny","( ͡° ͜ʖ ͡°)"));
		posting.userIcons.add(Pair.create("psi","Psi"));
		posting.userIcons.add(Pair.create("psialt","Psi Alt"));
		posting.userIcons.add(Pair.create("allah","Аллах Акбар"));
		posting.userIcons.add(Pair.create("bindera","Бiндєра"));
		posting.userIcons.add(Pair.create("sovok","Совок"));
		posting.userIcons.add(Pair.create("gosdep","Госдеп"));
		posting.userIcons.add(Pair.create("wehrmacht","Wehrmacht"));
		posting.userIcons.add(Pair.create("liberator","Liberator"));
		posting.userIcons.add(Pair.create("shizik","Шизик"));
		posting.userIcons.add(Pair.create("guadel","Гваделупа"));
		posting.userIcons.add(Pair.create("poni","Poni"));
		posting.userIcons.add(Pair.create("suiseiseki","Suiseiseki"));
		posting.userIcons.add(Pair.create("souseiseki","Souseiseki"));
		posting.userIcons.add(Pair.create("kamina","Kamina"));
		posting.userIcons.add(Pair.create("mushroom","Kinoko Nasu"));
		posting.userIcons.add(Pair.create("mando","Mandalorian"));
		posting.userIcons.add(Pair.create("mando2","Mandalorian Alt"));
		posting.userIcons.add(Pair.create("synchtube","Synchtube"));
		posting.userIcons.add(Pair.create("stalt","Synchtube Alt"));
		posting.userIcons.add(Pair.create("archlinux","Arch"));
		posting.userIcons.add(Pair.create("debian","Debian"));
		posting.userIcons.add(Pair.create("vk","Facebook"));
		posting.userIcons.add(Pair.create("skype","Skype"));
		posting.userIcons.add(Pair.create("konfa","КОНФОГНИЛЬ"));
		posting.userIcons.add(Pair.create("cola","Coca-Cola"));
		posting.userIcons.add(Pair.create("pepsi","Pepsi"));
		posting.userIcons.add(Pair.create("mtndew","Mtn Dew"));

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
