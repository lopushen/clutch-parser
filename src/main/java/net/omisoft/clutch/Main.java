package net.omisoft.clutch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Document doc = Jsoup
                .connect("https://clutch.co/profile/omisoft")
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                .get();
        Elements reviewers = doc.select("div.review-mobile-reviewer2-text");
        reviewers.forEach(e-> System.out.println(e.text()));
    }
}
