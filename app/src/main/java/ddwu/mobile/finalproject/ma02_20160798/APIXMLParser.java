package ddwu.mobile.finalproject.ma02_20160798;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class APIXMLParser {
    private XmlPullParser parser;
    private enum TagType {NONE, ADDR1, ADDR2, CONTENTID, CONTENTTYPEID, CONTENT, IMAGE, MAPX, MAPY, TITLE};
    private final static String ITEM_TAG = "item";
    private final static String ADDR1_TAG = "addr1";
    private final static String ADDR2_TAG = "addr2";
    private final static String CONTENTID_TAG = "contentid";
    private final static String CONTENTTYPEID_TAG = "contenttypeid";
    private final static String CONTENT_TAG = "overview";
    private final static String IMAGE_TAG = "firstimage";
    private final static String MAPX_TAG = "mapx";
    private final static String MAPY_TAG = "mapy";
    private  final static String TITLE_TAG = "title";

    public APIXMLParser(){
        try {
            parser = XmlPullParserFactory.newInstance().newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
    public Void parse(TravelDto travelDto, String xml){
        TagType tagType = TagType.NONE;
        try{
            parser.setInput(new StringReader(xml));

            for(int eventType = parser.getEventType(); eventType!=XmlPullParser.END_DOCUMENT; eventType = parser.next()){

                switch(eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if(tag.equals(CONTENT_TAG)){
                            tagType = TagType.CONTENT;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType){
                            case CONTENT:
                                travelDto.setDetailContent(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
            }
        }catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<TravelDto> parse(String xml){
        ArrayList<TravelDto> lists = new ArrayList<>();
        TravelDto list = null;
        TagType tagType = TagType.NONE;

        try{
            parser.setInput(new StringReader(xml));

            for(int eventType = parser.getEventType(); eventType!=XmlPullParser.END_DOCUMENT; eventType = parser.next()){

                switch(eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if(tag.equals(ITEM_TAG)){
                            list = new TravelDto();
                        }else if(tag.equals(ADDR1_TAG)){
                            tagType = TagType.ADDR1;
                        }else if(tag.equals(ADDR2_TAG)){
                            tagType = TagType.ADDR2;
                        }else if(tag.equals(CONTENTID_TAG)){
                            tagType = TagType.CONTENTID;
                        }else if(tag.equals(CONTENTTYPEID_TAG)){
                            tagType = TagType.CONTENTTYPEID;
                        }else if(tag.equals(IMAGE_TAG)){
                            tagType = TagType.IMAGE;
                        }else if(tag.equals(MAPX_TAG)){
                            tagType = TagType.MAPX;
                        }else if(tag.equals(MAPY_TAG)){
                            tagType = TagType.MAPY;
                        }else if(tag.equals(TITLE_TAG)){
                            tagType = TagType.TITLE;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals(ITEM_TAG)){
                            lists.add(list);
                            list = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType){
                            case ADDR1:
                                list.setAddr1(parser.getText());
                                break;
                            case ADDR2:
                                list.setAddr2(parser.getText());
                                break;
                            case CONTENTID:
                                list.setContentId(parser.getText());
                                break;
                            case CONTENTTYPEID:
                                list.setContentTypeId(parser.getText());
                                break;

                            case IMAGE:
                                list.setImageLink(parser.getText());
                                break;
                            case MAPX:
                                list.setMapX(Double.valueOf(parser.getText()));
                                break;
                            case MAPY:
                                list.setMapY(Double.valueOf(parser.getText()));
                                break;
                            case TITLE:
                                list.setTitle(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
            }

        }catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return lists;
    }
}
