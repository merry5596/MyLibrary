package ddwu.mobile.finalproject.ma02_20180962;

import android.text.Html;
import android.text.Spanned;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class BookXmlParser {
    public enum TagType { NONE, TITLE, AUTHOR, PUBLISHER, PUBDATE, ISBN, PRICE, DESCRIPTION, IMAGE };

    final static String TAG_ITEM = "item";
    final static String TAG_TITLE = "title";
    final static String TAG_AUTHOR = "author";
    final static String TAG_PUBLISHER = "publisher";
    final static String TAG_PUBDATE = "pubdate";
    final static String TAG_ISBN = "isbn";
    final static String TAG_PRICE = "price";
    final static String TAG_DESCRIPTION = "description";
    final static String TAG_IMAGE = "image";

    public BookXmlParser() {}

    public ArrayList<BookDto> parse(String xml) {

        ArrayList<BookDto> resultList = new ArrayList();
        BookDto dto = null;

        TagType tagType = TagType.NONE;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            // parsing을 수행할 xml 설정
            parser.setInput(new StringReader(xml));

            // eventType에 맞게 필요한 작업 수행하여 파싱함
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            dto = new BookDto();
                        } else if (parser.getName().equals(TAG_TITLE)) {
                            if (dto != null) tagType = TagType.TITLE;
                        } else if (parser.getName().equals(TAG_AUTHOR)) {
                            if (dto != null) tagType = TagType.AUTHOR;
                        } else if (parser.getName().equals(TAG_PUBLISHER)) {
                            if (dto != null) tagType = TagType.PUBLISHER;
                        } else if (parser.getName().equals(TAG_PUBDATE)) {
                            if (dto != null) tagType = TagType.PUBDATE;
                        } else if (parser.getName().equals(TAG_ISBN)) {
                            if (dto != null) tagType = TagType.ISBN;
                        } else if (parser.getName().equals(TAG_PRICE)) {
                            if (dto != null) tagType = TagType.PRICE;
                        } else if (parser.getName().equals(TAG_DESCRIPTION)) {
                            if (dto != null) tagType = TagType.DESCRIPTION;
                        } else if (parser.getName().equals(TAG_IMAGE)) {
                            if (dto != null) tagType = TagType.IMAGE;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            resultList.add(dto);
                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        Spanned spanned = Html.fromHtml(parser.getText());  // html 태그 그대로 출력되는 현상 제거하기 위함
                        String text = spanned.toString();
                        switch(tagType) {
                            case TITLE:
                                dto.setTitle(text);
                                break;
                            case AUTHOR:
                                dto.setAuthor(text);
                                break;
                            case PUBLISHER:
                                dto.setPublisher(text);
                                break;
                            case PUBDATE:
                                SimpleDateFormat beforeFormat = new SimpleDateFormat("yyyymmdd");
                                SimpleDateFormat afterFormat = new SimpleDateFormat("yyyy-mm-dd");
                                Date tmp = beforeFormat.parse(text);
                                String date = afterFormat.format(tmp);
                                dto.setPubdate(date);
                                break;
                            case ISBN:
                                dto.setIsbn(text);
                                break;
                            case PRICE:
                                dto.setPrice(Integer.parseInt(text));
                                break;
                            case DESCRIPTION:
                                dto.setDescription(text);
                                break;
                            case IMAGE:
                                dto.setImageLink(text);
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
}
