package com.epam.microservices.dto;

public class SoundMetadata {

    private Long id;
    private String name;
    private String artist;
    private String album;
    private String length;

    private SoundMetadata(final SoundMetadata.Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.artist = builder.artist;
        this.album = builder.album;
        this.length = builder.length;
    }

    public static class Builder {

        private Long id;
        private String name;
        private String artist;
        private String album;
        private String length;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setArtist(String artist) {
            this.artist = artist;
            return this;
        }

        public Builder setAlbum(String album) {
            this.album = album;
            return this;
        }

        public Builder setLength(String length) {
            this.length = length;
            return this;
        }

        public SoundMetadata build() {
            return new SoundMetadata(this);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getLength() {
        return length;
    }

}
