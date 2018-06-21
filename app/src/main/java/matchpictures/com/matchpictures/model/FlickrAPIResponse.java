package matchpictures.com.matchpictures.model;

import java.util.List;

public class FlickrAPIResponse {
    private List<PhotoItem> photo;

    public List<PhotoItem> getPhoto() {
        return photo;
    }

    public void setPhoto(List<PhotoItem> photo) {
        this.photo = photo;
    }
}
