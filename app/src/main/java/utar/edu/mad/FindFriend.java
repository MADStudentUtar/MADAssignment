package utar.edu.mad;

public class FindFriend {

    public String url, name, bio;

    public FindFriend(){

    }

    public FindFriend(String url, String name, String bio) {
        this.url = url;
        this.name = name;
        this.bio = bio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
