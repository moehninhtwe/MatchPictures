package matchpictures.com.matchpictures.model;

import java.util.List;

public class Sizes {
    private List<Size> size;

    public List<Size> getAvailableSizeList() {
        return size;
    }

    public void setAvailableSizeList(
        List<Size> size) {
        this.size = size;
    }
}
