package syu.autobiography.spring.board.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import syu.autobiography.spring.dto.PostsDTO;
import syu.autobiography.spring.board.repository.BoardRepository;
import syu.autobiography.spring.board.repository.LikesRepository;
import syu.autobiography.spring.entity.Likes;
import syu.autobiography.spring.entity.Posts;
import syu.autobiography.spring.entity.Users;
import syu.autobiography.spring.login.repository.LoginRepository;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final LoginRepository loginRepository;
    private final LikesRepository likesRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository, LoginRepository loginRepository, LikesRepository likesRepository) {
        this.boardRepository = boardRepository;
        this.loginRepository = loginRepository;
        this.likesRepository = likesRepository;
    }

    public List<PostsDTO> getTopStories(int limit, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("user");
        int currentUserNo = (currentUser != null) ? currentUser.getUserNo() : -1;

        return boardRepository.findAllPublicWithUsersAndLikesOrderByLikesCountDesc(PageRequest.of(0, limit)).getContent().stream()
                .map(posts -> {
                    int userAge = calculateAge(posts.getUser().getUserBirth());
                    String truncatedText = truncateContent(posts.getFinalText());
                    int likesCount = likesRepository.countByPostsId(posts.getPostsId());
                    boolean liked = (currentUserNo != -1) && likesRepository.findByUserUserNoAndPostsPostsId(currentUserNo, posts.getPostsId()).isPresent();
                    return new PostsDTO(posts.getPostsId(), posts.getUser().getUserNo(), posts.getQuestionNumber(), posts.getDraftText(), posts.getFinalText(), truncatedText, posts.getTitle(), posts.getIsPublic(), posts.getCreatedAt(), posts.getUpdatedAt(), posts.getUser().getUserName(), userAge, likesCount, liked);
                })
                .collect(Collectors.toList());
    }

    public Page<PostsDTO> getAllDrafts(PageRequest pageRequest, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("user");
        int currentUserNo = (currentUser != null) ? currentUser.getUserNo() : -1;

        return boardRepository.findAllPublicWithUsersAndLikesOrderByLikesCountDesc(pageRequest).map(posts -> {
            int userAge = calculateAge(posts.getUser().getUserBirth());
            String truncatedText = truncateContent(posts.getFinalText());
            int likesCount = likesRepository.countByPostsId(posts.getPostsId());
            boolean liked = (currentUserNo != -1) && likesRepository.findByUserUserNoAndPostsPostsId(currentUserNo, posts.getPostsId()).isPresent();
            return new PostsDTO(posts.getPostsId(), posts.getUser().getUserNo(), posts.getQuestionNumber(), posts.getDraftText(), posts.getFinalText(), truncatedText, posts.getTitle(), posts.getIsPublic(), posts.getCreatedAt(), posts.getUpdatedAt(), posts.getUser().getUserName(), userAge, likesCount, liked);
        });
    }

    private int calculateAge(String birthDateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate birthDate = LocalDate.parse(birthDateString, formatter);
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private String truncateContent(String content) {
        if (content.length() > 500) {
            return content.substring(0, 500) + "...";
        }
        return content;
    }

    @Transactional
    public void addLike(int userNo, int postsId) {
        Users user = loginRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("유저번호가 존재하지 않습니다."));
        Posts posts = boardRepository.findById(postsId)
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않습니다."));

        Likes like = new Likes();
        like.setUser(user);
        like.setPosts(posts);
        likesRepository.save(like);
    }

    @Transactional
    public void removeLike(int userNo, int postsId) {
        Users user = loginRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("유저번호가 존재하지 않습니다."));
        Posts posts = boardRepository.findById(postsId)
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않습니다."));

        Optional<Likes> like = likesRepository.findByUserUserNoAndPostsPostsId(userNo, postsId);
        like.ifPresent(likesRepository::delete);
    }

    public int countLikes(int postsId) {
        return likesRepository.countByPostsId(postsId);
    }

    @Transactional
    public LikeResponse toggleLike(int userNo, int postsId) {
        Optional<Likes> existingLike = likesRepository.findByUserUserNoAndPostsPostsId(userNo, postsId);
        boolean isLiked;

        if (existingLike.isPresent()) {
            likesRepository.delete(existingLike.get());
            isLiked = false;
        } else {
            Users user = loginRepository.findById(userNo)
                    .orElseThrow(() -> new IllegalArgumentException("유저번호가 존재하지 않습니다."));
            Posts posts = boardRepository.findById(postsId)
                    .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않습니다."));

            Likes like = new Likes();
            like.setUser(user);
            like.setPosts(posts);
            likesRepository.save(like);
            isLiked = true;
        }

        int likeCount = likesRepository.countByPostsId(postsId);
        return new LikeResponse(isLiked, likeCount);
    }

    public static class LikeResponse {
        private boolean liked;
        private int likeCount;

        public LikeResponse(boolean liked, int likeCount) {
            this.liked = liked;
            this.likeCount = likeCount;
        }

        public boolean isLiked() {
            return liked;
        }

        public int getLikeCount() {
            return likeCount;
        }
    }
}
