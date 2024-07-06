package syu.autobiography.spring.mypage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import syu.autobiography.spring.dto.PostsDTO;
import syu.autobiography.spring.entity.Posts;
import syu.autobiography.spring.mypage.repository.MyPageRepository;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyPageService {

    private final MyPageRepository myPageRepository;

    @Autowired
    public MyPageService(MyPageRepository myPageRepository) {
        this.myPageRepository = myPageRepository;
    }

    public List<PostsDTO> getPostsByUser(int userNo) {
        return myPageRepository.findByUserUserNo(userNo).stream()
                .map(posts -> new PostsDTO(
                        posts.getPostsId(),
                        posts.getUser().getUserNo(),
                        posts.getQuestionNumber(),
                        posts.getDraftText(),
                        posts.getGptText(),
                        posts.getFinalText(),
                        posts.getTitle(),
                        posts.getIsPublic(),
                        posts.getCreatedAt(),
                        posts.getUpdatedAt(),
                        posts.getUser().getUserName(),
                        calculateAge(posts.getUser().getUserBirth()),
                        posts.getLikes().size(),
                        false
                ))
                .collect(Collectors.toList());
    }

    public List<PostsDTO> getLikedPostsByUser(int userNo) {
        return myPageRepository.findLikedPostsByUser(userNo).stream()
                .map(posts -> new PostsDTO(
                        posts.getPostsId(),
                        posts.getUser().getUserNo(),
                        posts.getQuestionNumber(),
                        posts.getDraftText(),
                        posts.getGptText(),
                        posts.getFinalText(),
                        posts.getTitle(),
                        posts.getIsPublic(),
                        posts.getCreatedAt(),
                        posts.getUpdatedAt(),
                        posts.getUser().getUserName(),
                        calculateAge(posts.getUser().getUserBirth()),
                        posts.getLikes().size(),
                        true
                ))
                .collect(Collectors.toList());
    }

    public void updatePostVisibility(int postsId, String isPublic) {
        Posts post = myPageRepository.findById(postsId)
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않습니다."));
        post.setIsPublic(isPublic);
        myPageRepository.save(post);
    }
    public void deletePost(int postId) {
        myPageRepository.deleteById(postId);
    }

    private int calculateAge(String birthDateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate birthDate = LocalDate.parse(birthDateString, formatter);
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
