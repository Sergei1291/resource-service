package com.epam.microservices.controller;

import com.epam.microservices.dto.SoundMetadata;
import com.epam.microservices.entity.Resource;
import com.epam.microservices.exception.Mp3ValidationException;
import com.epam.microservices.exception.ResourceNotFoundException;
import com.epam.microservices.repository.ResourceRepository;
import com.google.gson.Gson;
import org.apache.pdfbox.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/resources")
public class ResourceService {

    private final String SONGS_URI = "http://localhost:8082/songs";

    private final ResourceRepository resourceRepository;
    private final HttpClient httpClient;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository, HttpClient httpClient) {
        this.resourceRepository = resourceRepository;
        this.httpClient = httpClient;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Resource addResource() throws IOException, Mp3ValidationException {
        FileInputStream mp3RecordStream = new FileInputStream("Queen - The Show Must Go On.mp3");
        final byte[] mp3Record = IOUtils.toByteArray(mp3RecordStream);

        final Resource resource = new Resource();
        resource.setResource(IOUtils.toByteArray(new ByteArrayInputStream(mp3Record)));
        final Resource savedResource = resourceRepository.save(resource);

        final SoundMetadata soundMetadata = createSoundMetadata(savedResource.getId(),
                new ByteArrayInputStream(mp3Record));

        final Gson gson = new Gson();
        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(soundMetadata)))
                .uri(URI.create(SONGS_URI))
                .build();

        final HttpResponse<String> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (final InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (response.statusCode() != 200) {
            throw new RuntimeException("sound service works incorrectly");
        }
        return resource;
    }

    private SoundMetadata createSoundMetadata(final Long resourceId, final InputStream inputStream) throws Mp3ValidationException {
        try {
            final BodyContentHandler handler = new BodyContentHandler();
            final Metadata metadata = new Metadata();
            final ParseContext parseContext = new ParseContext();
            final Parser mp3Parser = new Mp3Parser();
            mp3Parser.parse(inputStream, handler, metadata, parseContext);
            return new SoundMetadata.Builder()
                    .setId(resourceId)
                    .setAlbum(metadata.get("album"))
                    .setArtist(metadata.get("xmpDM:artist"))
                    .setName(metadata.get("title"))
                    .setLength(metadata.get("xmpDM:duration"))
                    .build();
        } catch (IOException | SAXException | TikaException e) {
            throw new Mp3ValidationException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource getResource(@PathVariable final long id) throws ResourceNotFoundException {
        return resourceRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
    public List<Long> deleteResources(@PathVariable final String ids) {
        final List<Long> idsList = Arrays.stream(ids.split(",")).map(Long::parseLong).toList();
        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(SONGS_URI + "/" + ids))
                .build();
        final HttpResponse<String> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (final InterruptedException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (response.statusCode() != 200) {
            throw new RuntimeException("sound service works incorrectly");
        }
        resourceRepository.deleteAllById(idsList);
        return idsList;
    }
}
