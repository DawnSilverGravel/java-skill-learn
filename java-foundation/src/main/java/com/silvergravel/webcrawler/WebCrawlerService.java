package com.silvergravel.webcrawler;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author DawnStar
 * Date: 2023/9/5
 */
public class WebCrawlerService {

    private final Logger logger = Logger.getLogger("webcrawler");

    private final static String CRAWLER_PROPERTIES_PATH = File.separator
            + "webcrawler" + File.separator + "crawler.properties";

    /**
     * 警告：对于本项目所在位置为: java-skill-learn/java-foundation
     * 所以需要在 user.dir之后加上 /java-foundation
     */
    private final String resourcePath = System.getProperty("user.dir")
            + File.separator + "java-foundation"
            + File.separator + "src"
            + File.separator + "main"
            + File.separator + "resources";

    private final String urlPrefix = "(https://w.wallhaven.cc/|https://wallhaven.cc/)";

    /**
     * <a class="preview" href="https://wallhaven.cc/w/281d5y" target="_blank"></a>
     * href中的w/281d5y使用正则 [\\w/]+
     * 由于每个链接的空格符的数量不太一致，所以使用 \\s+匹配空格符
     */
    private final String pictureInfoUrlRegex = "<a\s+class=\"preview\"\s+href=\"" +
            urlPrefix + "[\\w/]+\"\s+target=\"_blank\"\s*></a>";
    private final Pattern picturePattern = Pattern.compile(pictureInfoUrlRegex);

    /**
     * <a class="preview" href="https://wallhaven.cc/w/281d5y" target="_blank"></a>
     * 只要图片链接路径 即：https://wallhaven.cc/w/281d5y
     */
    private final String imgInfoLinkRegex = urlPrefix + "[\\w/-]+";
    private final Pattern imgInfoLinkPattern = Pattern.compile(imgInfoLinkRegex);


    /**
     * 16进制 颜色匹配正则
     */
    private final String colorRegex = "#[A-z0-9]+";
    private final Pattern colorListPattern = Pattern.compile(colorRegex);


    /**
     * <li class="color" style="background-color:#999999"><a href="https://wallhaven.cc/search?colors=999999"></a></li>
     * 只需要匹配 <li class="color" style="background-color:#999999"> 取出颜色16进制值
     */
    private final String colorInfoRegex = "<li\s+class=\"color\"\s+style=\"background-color:" + colorRegex + "\"\s*>";
    private final Pattern colorPattern = Pattern.compile(colorInfoRegex);


    private final String downloadLinkRegex = urlPrefix + "[\\w/-]+\\.(png|jpg|jpeg|gif)";
    private final Pattern downloadPattern = Pattern.compile(downloadLinkRegex);

    /**
     * <img id="wallpaper" src="https://w.wallhaven.cc/full/28/wallhaven-281d5y.png">
     * 匹配上述路径
     */
    private final String imgRegex = "<img\s+id=\"wallpaper\"\s+src=\"" + downloadLinkRegex;
    private final Pattern imgPattern = Pattern.compile(imgRegex);


    public static void main(String[] args) throws Exception {
        System.out.println(System.getProperty("user.dir"));
        String targetWebsiteUrl = "https://wallhaven.cc/search?q=anime%20girl&categories=111" +
                "&purity=100" +
                "&resolutions=1920x1080%2C2560x1440" +
                "&ratios=16x9" +
                "&sorting=favorites" +
                "&order=desc" +
                "&ai_art_filter=1" +
                "&page=";
        WebCrawlerService webCrawlerService = new WebCrawlerService();
        webCrawlerService.setup(targetWebsiteUrl, 1);
    }

    public void setup(String websiteUrl, int page) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        logger.log(Level.INFO, "分析网页：" + websiteUrl);
        // 如果不需要写入文件注释掉相关 Properties即可
        InputStream resourceAsStream = WebCrawlerService.class.getClassLoader()
                .getResourceAsStream(CRAWLER_PROPERTIES_PATH);
        Properties properties = new Properties();
        String lastSectionUrl = "lastSectionUrl";
        String lastDownLoadPage = "lastDownLoadPage";
        properties.load(resourceAsStream);
        Object o = properties.get(lastSectionUrl);
        if (o != null) {
            // 如果相同则检查页数是否比上次页数大1页
            if (String.valueOf(o).equals(websiteUrl)) {
                int lastPage = Integer.parseInt(String.valueOf(properties.get(lastDownLoadPage)));
                if (page != lastPage + 1) {
                    page = lastPage + 1;
                }
            }
        }
        // 解析列表主页，获取每个图片的信息路径
        List<String> imgLinks = parseListPageHtml(websiteUrl + page);
        // 访问图片信息页，并生成指定对象列表
        List<Picture> pictures = accessLinksAndCreatePictures(imgLinks);
        // 综合分析 color的值
        analysisColors(pictures);
        // 下载图片
        List<String> downloadLinks = pictures.stream().map(Picture::getDownloadUrl).toList();
        String path = "F:\\Picture\\Camera Roll";
        boolean finish = downloadImgBatch(downloadLinks, path);
        if (finish) {
            properties.put(lastSectionUrl, websiteUrl);
            properties.put(lastDownLoadPage, String.valueOf(page));
            storeProperties(properties);
        }
    }

    private void storeProperties(Properties properties) throws Exception {
        // 下载完成之后存入properties
        File file = new File(resourcePath + CRAWLER_PROPERTIES_PATH);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            properties.store(fileOutputStream, "修改原始resources的属性文件");
            logger.log(Level.INFO, "原始文件存储路径：" + file.getAbsolutePath());

        }
        URL resource = WebCrawlerService.class.getClassLoader().getResource(CRAWLER_PROPERTIES_PATH);
        if (resource == null) {
            logger.log(Level.WARNING, "crawler.properties 文件不存在！");
        } else {
            logger.log(Level.INFO, "文件存储路径：" + resource.getFile());
            try (FileOutputStream fileOutputStream = new FileOutputStream(resource.getFile())) {
                properties.store(fileOutputStream, "修改target文件夹中的resources的属性文件");
            }
        }
        logger.log(Level.INFO, "下载完成!");
    }

    private void analysisColors(List<Picture> pictures) {
        // 分析颜色的占比
        Map<String, Integer> colorNameCountMap = pictures.stream()
                .flatMap(picture -> picture.colors.stream())
                .collect(Collectors.groupingBy(String::toString, Collectors.summingInt(value -> 1)));
        long total = pictures.stream()
                .mapToLong(picture -> picture.colors.size()).sum();
        colorNameCountMap.forEach(
                (key, value) -> {
                    System.out.println(key + "数量占比：" + value + "/" + total);
                }
        );
    }

    private BufferedReader openReader(String sourceUrl) {
        try {
            URL url = new URL(sourceUrl);
            URLConnection urlConnection = url.openConnection();
            return new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> parseListPageHtml(String websiteUrl) {
        try (BufferedReader reader = openReader(websiteUrl)) {
            List<String> htmlContents = reader.lines().toList();
            List<String> imgLinks = new ArrayList<>();
            for (String htmlContent : htmlContents) {
                // 检索含有图片链接的标签
                Matcher matcher = picturePattern.matcher(htmlContent);
                while (matcher.find()) {
                    String group = matcher.group();
                    Matcher imgLinkMatcher = imgInfoLinkPattern.matcher(group);
                    // 检索出符合条件的 图片信息链接
                    while (imgLinkMatcher.find()) {
                        imgLinks.add(imgLinkMatcher.group());
                    }
                }
            }
            return imgLinks;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Picture> accessLinksAndCreatePictures(List<String> links) {
        List<Picture> pictures = new ArrayList<>(links.size());
        try {
            for (String link : links) {
                // 暂停 500 毫秒，减少服务器的压力，避免被禁止访问站点
                TimeUnit.MILLISECONDS.sleep(500);
                Picture picture = new Picture();
                picture.setInfoUrl(link);
                BufferedReader reader = openReader(link);
                List<String> contents = reader.lines().toList();
                for (String content : contents) {
                    // 访问图片信息，并解析获取其下载链接以及颜色属性
                    Matcher matcher = imgPattern.matcher(content);
                    if (matcher.find()) {
                        Matcher downloadMatch = downloadPattern.matcher(matcher.group());
                        if (downloadMatch.find()) {
                            String downloadUrl = downloadMatch.group();
                            picture.setDownloadUrl(downloadUrl);
                        }
                    }
                    // 解析colors
                    List<String> colors = parseColor(content);
                    picture.setColors(colors);
                }
                pictures.add(picture);
                reader.close();

            }
            return pictures;
        } catch (Exception e) {
            e.printStackTrace();
            return pictures;
        }
    }

    private List<String> parseColor(String content) {
        Matcher matcher = colorPattern.matcher(content);
        List<String> colors = new ArrayList<>();
        while (matcher.find()) {
            Matcher colorMatch = colorListPattern.matcher(matcher.group());
            while (colorMatch.find()) {
                colors.add(colorMatch.group());
            }
        }
        return colors;
    }

    private boolean downloadImgBatch(List<String> downloadUrls, String path) {
        try {
            for (int i = 0; i < downloadUrls.size(); i++) {
                String downloadUrl = downloadUrls.get(i);
                if (downloadUrl == null) {
                    continue;
                }
                String imgName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
                logger.log(Level.INFO, "本次批量下载第 " + (i + 1) + " 张图片链接： " + downloadUrl + " 执行下载！");
                URL url = new URL(downloadUrl);
                FileOutputStream fileOutputStream = new FileOutputStream(path + "/" + imgName);
                InputStream inputStream = url.openConnection().getInputStream();
                fileOutputStream.write(inputStream.readAllBytes());
                inputStream.close();
                fileOutputStream.close();
                logger.log(Level.INFO, imgName + " 下载成功！");
                TimeUnit.MILLISECONDS.sleep(100);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private static class Picture {
        /**
         * 图片主要颜色列表
         */
        private List<String> colors;
        /**
         * 图片下载路径
         */
        private String downloadUrl;
        /**
         * 图片的主页路径
         */
        private String infoUrl;

        public Picture() {
        }

        public Picture(List<String> colors, String downloadUrl, String infoUrl) {
            this.colors = colors;
            this.downloadUrl = downloadUrl;
            this.infoUrl = infoUrl;
        }

        public List<String> getColors() {
            return colors;
        }

        public void setColors(List<String> colors) {
            this.colors = colors;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getInfoUrl() {
            return infoUrl;
        }

        public void setInfoUrl(String infoUrl) {
            this.infoUrl = infoUrl;
        }
    }

}
