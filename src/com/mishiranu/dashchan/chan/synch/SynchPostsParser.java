package com.mishiranu.dashchan.chan.synch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
import android.util.Pair;
import chan.util.StringUtils;

@SuppressLint("SimpleDateFormat")
public class SynchPostsParser
{
    private String mSource;

    private String mPostNumber;

//    private static final Pattern FLAG = Pattern.compile("<span class=\"flag flag-(.*?)\"");



    public SynchPostsParser(String source, String postNumber)
    {
        mSource = source;
        mPostNumber = postNumber;
    }

    public Pair<String, String> convertSinglePost() {
        final Pattern FLAG = Pattern.compile("<label for=\"delete_" + mPostNumber + "\">(.*?)<span class=\"flag flag-(.*?)\"(.*?)title=\"(.*?)\" ></span>(.*?)</label>");
        final Pattern NOT_NEEDED = Pattern.compile("<label for=\"delete_(.*?)\"");
        Matcher matcher = FLAG.matcher(mSource);
        if(matcher.find()){
            Matcher matcher1 = NOT_NEEDED.matcher(matcher.group(1));
            if(!matcher1.find()){
                final Pair<String, String> toIcon = new Pair<String, String>(matcher.group(2), StringUtils.unescapeHtml(matcher.group(4)));
                return toIcon;
            } else {
                return null;
            }
        } else{
            return null;
        }
    }
}