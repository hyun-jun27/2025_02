package com.example.demo.controller;

import com.example.demo.entity.Member;
import com.example.demo.entity.Place;
import com.example.demo.entity.Review;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PlaceRepository;
import com.example.demo.repository.ReviewRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;

    public ReviewController(ReviewRepository reviewRepository,
                            MemberRepository memberRepository,
                            PlaceRepository placeRepository) {
        this.reviewRepository = reviewRepository;
        this.memberRepository = memberRepository;
        this.placeRepository = placeRepository;
    }

    @GetMapping("/review/list")
    public String list(@RequestParam(value = "filter", required = false) String filter, Model model) {
        List<Review> reviewList;
        List<Place> placeList;

        if (filter != null && !filter.isEmpty()) {
            reviewList = reviewRepository.findByPlace_Category(filter);
            placeList = placeRepository.findByCategory(filter);
        } else {
            reviewList = reviewRepository.findAll();
            placeList = placeRepository.findAll();
        }
        
        model.addAttribute("reviews", reviewList);
        model.addAttribute("places", placeList);
        model.addAttribute("currentFilter", filter);
        
        return "review_list";
    }

    @GetMapping("/review/create")
    public String createForm(Model model) {
        List<Place> placeList = placeRepository.findAll();
        model.addAttribute("places", placeList);
        return "review_form";
    }

    @PostMapping("/review/create")
    public String create(@ModelAttribute Review review,
                         @RequestParam(value = "placeId", required = false) Long placeId,
                         @RequestParam(value = "newPlaceName", required = false) String newPlaceName,
                         @RequestParam(value = "newCategory", required = false) String newCategory,
                         HttpSession session) {

        Member writer = (Member) session.getAttribute("loginMember");
        if (writer == null) return "redirect:/member/login";

        Place place = null;

        if (placeId != null && placeId == -1) {
            
            if (newPlaceName == null || newPlaceName.trim().isEmpty()) {
                 newPlaceName = "사용자 직접 등록 장소";
            }
            
            place = new Place();
            place.setPlaceName(newPlaceName);
            
            // 카테고리 설정: 사용자가 '식당'/'카페'를 선택하면 그것을 사용, 아니면 '기타/사용자등록'
            place.setCategory(
                (newCategory != null && !newCategory.trim().isEmpty() && !newCategory.equals("기타")) 
                ? newCategory : "기타"
            );
            
            place.setAddress("위치 정보 없음");
            placeRepository.save(place);
        } 
        else if (placeId != null) {
            place = placeRepository.findById(placeId).orElse(null);
        }

        if (place != null) {
            review.setMember(writer);
            review.setPlace(place);
            review.setCreateDate(LocalDateTime.now());
            reviewRepository.save(review);
        }

        return "redirect:/review/list";
    }

    @GetMapping("/review/my")
    public String myReviewList(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        List<Review> myReviews = reviewRepository.findByMember(loginMember);
        model.addAttribute("myReviews", myReviews);

        return "my_reviews";
    }

    @GetMapping("/review/delete")
    public String deleteReview(@RequestParam("id") Long id) {
        reviewRepository.deleteById(id);
        return "redirect:/review/my";
    }
}
