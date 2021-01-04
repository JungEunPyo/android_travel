package ddwu.mobile.finalproject.ma02_20160798;

import java.io.Serializable;

public class TravelDto implements Serializable {
   private long _id;
   private String title;
   private String addr1;
   private String addr2;
   private String contentId;
   private String contentTypeId;
   private String detailContent;

   private String imageFileName;
   private String imageLink;
   private double mapX;
   private double mapY;
   private String memo;

    public TravelDto(){
        set_id(-1);
    }


    public TravelDto(long id, String title, String addr, String detailContent, String imageFileName, String imageLink, Double mapX, Double mapY, String memo) {
        this._id = id;
        this.title = title;
        this.addr1 = addr;
        this.detailContent = detailContent;
        this.imageFileName = imageFileName;
        this.imageLink = imageLink;
        this.mapX = mapX;
        this.mapY = mapY;
        this.memo = memo;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getAddr(){
        String s = getAddr1();
        String s1 = getAddr2();
        if(s1!=null)
            s += s1;
        return s;
    }
    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentTypeId() {
        return contentTypeId;
    }

    public void setContentTypeId(String contentTypeId) {
        this.contentTypeId = contentTypeId;
    }

    public String getDetailContent() {
        return detailContent;
    }

    public void setDetailContent(String detailContent) {
        this.detailContent = detailContent;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public double getMapX() {
        return mapX;
    }

    public void setMapX(double mapX) {
        this.mapX = mapX;
    }

    public double getMapY() {
        return mapY;
    }

    public void setMapY(double mapY) {
        this.mapY = mapY;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return  " 장소=" + title + "\n" +
                ", 주소=" + getAddr() + "\n" +
                ", detailContent=" + detailContent + "\n"  +
                ", imageLink=" + imageLink + "\n";
    }
    public String toStringFavorite(){
        String str = " 장소=" + title + "\n" +
                ", 주소=" + getAddr() + "\n" +
                ", detailContent=" + detailContent + "\n"  +
                ", imageLink=" + imageLink + "\n";
        if(getMemo()!=null && !getMemo().equals(""))
            str += ",메모: " + getMemo();
        return str;
    }
}
