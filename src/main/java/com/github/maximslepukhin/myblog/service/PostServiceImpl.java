package com.github.maximslepukhin.service;

import com.github.maximslepukhin.dto.PostDto;
import com.github.maximslepukhin.mapper.PostMapper;
import com.github.maximslepukhin.model.Post;
import com.github.maximslepukhin.model.PostsResult;
import com.github.maximslepukhin.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public PostDto findById(Long postId) {
        Post post = postRepository.findById(postId);
        return PostMapper.toPostDto(post);
    }

    @Override
    public PostsResult findPosts(int pageSize, int page) {
        List<Post> posts = postRepository.findPosts(pageSize, page);

        boolean hasNextPage = posts.size() > pageSize;
        if (hasNextPage) {
            posts = posts.subList(0, pageSize);
        }

        return new PostsResult(posts, hasNextPage, page, posts.size());
    }

    @Override
    public Post findPostById(Long postId) {
        return postRepository.findById(postId);
    }

    @Override
    public void saveImage(MultipartFile image, HttpServletRequest request, Long postId) {
        if (image == null || image.isEmpty()) {
            log.info("Invalid image file");
            return;
        }

        String fileName = postId.toString();
        String path = request.getServletContext().getRealPath("/") + "/images/";
        Path uploadDir = Paths.get(path);
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                log.error("Could not create directory: " + uploadDir, e);
                return;
            }
        }
        Path filePath = uploadDir.resolve(fileName);

        try {
            Files.write(filePath, image.getBytes());
        } catch (IOException e) {
            System.out.println("Could not write file: " + filePath);
        }
    }

    @Override
    public Post savePost(Post post) {
        post.setLikesCount(0);
        return postRepository.savePost(post);
    }

    @Override
    public ResponseEntity<byte[]> getImageById(Long postId, HttpServletRequest request) {

        String fileName = postId.toString();
        String path = request.getServletContext().getRealPath("/") + "images/" + fileName;

        Path imagePath = Paths.get(path);

        try {
            byte[] imageBytes = Files.readAllBytes(imagePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл изображения: " + imagePath.toString());
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public void addLike(Long postId, boolean like) {
        postRepository.addLike(postId, like);
    }

    @Override
    public void updatePost(Post post) {
        post.setLikesCount(0);
        postRepository.updatePost(post);
    }

    @Override
    public void deletePostById(Long postId, HttpServletRequest request) {
        String fileName = postId.toString();
        String path = request.getServletContext().getRealPath("/") + "/images/";
        Path imagePath = Paths.get(path).resolve(fileName);
        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            System.out.println("Could not delete file: " + imagePath);
        }
        postRepository.deletePostById(postId);
    }
}
