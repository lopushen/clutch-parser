package net.omisoft.clutch;

import net.omisoft.clutch.domain.Company;
import net.omisoft.clutch.domain.Reviewer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static final String CLUTCH = "https://clutch.co";

    public static void main(String[] args) {
        parseSingleCategory("https://clutch.co/agencies/financial-services");
    }

    private static void parseReviewers(String url) {
        Document doc = createPageDocument(url);
        Elements reviewers = doc.select("div.review-mobile-reviewer2-text");
        reviewers.forEach(e -> System.out.println(e.text()));
    }

    public static List<Reviewer> parseReviewersNew(String url) {
        Document doc = createPageDocument(url);
//        Elements reviewers = doc.select("div.review-mobile-reviewer2-text");
        Elements reviewers = doc.select("div[property=review]");

        return reviewers.stream().map(e -> {
            Reviewer reviewer = new Reviewer();
            reviewer.setName(e.select("div[class=field field-name-field-fdb-full-name-display field-type-text field-label-hidden]").text());
            reviewer.setTitle(e.select("div[class=field field-name-field-fdb-title field-type-text field-label-hidden]").text());
            reviewer.setVerified(e.select("div[class=field field-name-field-fdb-verified field-type-list-text field-label-hidden field-label-inline clearfix]").text());
            reviewer.setProjectSummary(e.select("div[class=field field-name-field-fdb-proj-description field-type-text-long field-label-inline clearfix]").text());
            Elements projectDetail = e.select("a[class=inner_url]");
            reviewer.setProject(projectDetail.text());
            reviewer.setUrl(CLUTCH + projectDetail.attr("href"));
            reviewer.setFeedBackSummary(e.select("div[class=field field-name-field-fdb-comments field-type-text field-label-inline clearfix]").text());
            System.out.println(reviewer);
            return reviewer;
        }).collect(Collectors.toList());
    }

    private static Document createPageDocument(String url) {
        try {
            return Jsoup
                    .connect(url)
//                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .timeout(1000000)
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void parseCatgories() {
//        BufferedWriter bufferedWriter = new BufferedWriter();
        Document doc = createPageDocument(CLUTCH);
        Elements categories = doc.select("li[id^=clutchmenu-mlid]");
        categories.forEach(e -> {
            Elements allElements = e.getAllElements();
            if (allElements.size() == 2) {
                String link = allElements.get(1).attr("href");
                System.out.println(link);
            }
        });
    }

    private static void parseSingleCategory(String categoryUrl) {
        Document doc = createPageDocument(categoryUrl);
        String pager = doc.select("li[class=pager-current]").text();
        String[] split = pager.split(" ");
        Integer currentPage = Integer.parseInt(split[0]);
        Integer totalPages = Integer.parseInt(split[2]);
        System.out.println(currentPage);
        System.out.println(totalPages);

        // let's go through pages

        List<Company> companies = IntStream.range(1, /*/*3*/totalPages + 1).boxed().map(i -> {
            Document categoryDoc = createPageDocument(categoryUrl + "?page=" + String.valueOf(i));
            Elements companyNameElements = categoryDoc.select("h3[class=company-name]");
            return companyNameElements.stream().map(e -> {
                Company company = new Company();
                company.setName(e.text());
                company.setUrl("https://clutch.co" + e.getAllElements().get(1).getAllElements().get(1).attr("href"));
                //System.out.println(title);
                return company;
            });
        }).flatMap(x -> x).collect(Collectors.toList());
        File csvOutputFile = new File("Clutch report");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println("Name,Title,Verified,Company Name,Company URL,Category URL");
            companies.forEach(c -> {
                List<Reviewer> reviewers = parseReviewersNew(c.getUrl());
                reviewers.stream().map(r -> String.format("%s,%s,%s,%s,%s,%s", r.getName(), r.getTitle(), r.getVerified(),
                        c.getName(), c.getUrl(), categoryUrl))
                        .forEach(pw::println);
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}

