package matchpictures.com.matchpictures.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="photo")
public class PhotoItem {
    @Element(name = "id")
    private String id;
    @Element(name = "secret")
    private String secret;
    @Element(name = "server")
    private String server;
    @Element(name = "farm")
    private String farm;

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

}
