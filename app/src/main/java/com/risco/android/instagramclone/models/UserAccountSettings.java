package com.risco.android.instagramclone.models;

/**
 * Created by Albert Risco on 02/12/2017.
 */

public class    UserAccountSettings {
    private String description, display_name, profile_photo, username, website;
    private long followers, following, posts;

    public UserAccountSettings(String description, String display_name, String profile_photo,
                               String username, String website, long followers, long following, long posts) {
        this.description = description;
        this.display_name = display_name;
        this.profile_photo = profile_photo;
        this.username = username;
        this.website = website;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
    }
    public UserAccountSettings(){

    }

    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "description='" + description + '\'' +
                ", display_name='" + display_name + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", username='" + username + '\'' +
                ", website='" + website + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", posts=" + posts +
                '}';
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public String getUsername() {
        return username;
    }

    public String getWebsite() {
        return website;
    }

    public long getFollowers() {
        return followers;
    }

    public long getFollowing() {
        return following;
    }

    public long getPosts() {
        return posts;
    }
}
