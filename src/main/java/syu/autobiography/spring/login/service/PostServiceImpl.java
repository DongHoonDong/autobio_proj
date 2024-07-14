package syu.autobiography.spring.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import syu.autobiography.spring.entity.Posts;
import syu.autobiography.spring.login.repository.PostRepository;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Posts> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public void deletePost(int postsId) {
        postRepository.deleteById(postsId);
    }
}

