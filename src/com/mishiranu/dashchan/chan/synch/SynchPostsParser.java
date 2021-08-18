package com.mishiranu.dashchan.chan.synch;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.text.Html;
import android.util.Log;
import android.util.Pair;

import chan.content.ChanConfiguration;
import chan.content.ChanLocator;
import chan.content.model.FileAttachment;
import chan.content.model.Post;
import chan.content.model.Posts;
import chan.text.ParseException;
import chan.text.TemplateParser;
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
        Pattern FLAG = Pattern.compile("<label for=\"delete_" + mPostNumber + "\">(.*?)<span class=\"flag flag-(.*?)\"(.*?)title=\"(.*?)\" ></span>(.*?)</label>");
        Pattern NOT_NEEDED = Pattern.compile("<label for=\"delete_(.*?)\"");
        Matcher matcher = FLAG.matcher(mSource);
        if(matcher.find()){
            Matcher matcher1 = NOT_NEEDED.matcher(matcher.group(1));
            if(!matcher1.find()){
                Log.d("benis", "found" + matcher.group(2));
                Log.d("benis", matcher.group(4));
                Pair<String, String> toIcon = new Pair<String, String>(matcher.group(2), StringUtils.unescapeHtml(matcher.group(4)));
                return toIcon;
            } else {
                return null;
            }
        } else{
            return null;
        }
    }
}