package matchpictures.com.matchpictures.model;

public class PhotoItem {
    private String id;
    private String secret;
    private String server;
    private String farm;
    private String url;
    private boolean isOpen;

    public PhotoItem(
        String id, String secret, String server, String farm, String url, boolean isOpen) {
        this.id = id;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
        this.url = url;
        this.isOpen = isOpen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getFarm() {
        return farm;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
