package com.rklymus.ittask;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepo postRepo;

    public Post get(Integer id) {
        return getPost(id);
    }

    public Post create(PostRequest request) {
        return postRepo.save(request.toEntity());
    }

    public Post update(Integer id, PostRequest request) {
        Post post = getPost(id);
        post.updateWith(request);
        return postRepo.save(post);
    }

    public void delete(Integer id) {
        Post post = getPost(id);
        postRepo.delete(post);
    }

    private Post getPost(Integer id) {
        return postRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    }
}
