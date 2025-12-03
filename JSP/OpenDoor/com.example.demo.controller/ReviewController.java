package com.example.demo.controller;

import com.example.demo.entity.Member;
import com.example.demo.entity.Place;
import com.example.demo.entity.Review;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PlaceRepository;
import com.example.demo.service.ReviewService; // ⭐ Service Layer Import
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable; // ⭐ PathVariable Import
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ReviewController {

    private final ReviewService reviewService; // ⭐ Service Layer 주입
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;

    // 1. 생성자 주입
    public ReviewController(ReviewService reviewService,
                            MemberRepository memberRepository,
                            PlaceRepository placeRepository) {
        this.reviewService = reviewService;
        this.memberRepository = memberRepository;
        this.placeRepository = placeRepository;
    }

    // 2. 리뷰 목록 보여주기 (메인 페이지, 필터링 기능 포함)
    @GetMapping("/review/list")
    public String list(@RequestParam(value = "filter", required = false) String filter, Model model) {
        List<Review> reviewList;
        List<Place> placeList;

        if (filter != null && !filter.isEmpty()) {
            // Service method calls (assuming findByPlace_Category is implemented in Repository/used by Service)
            reviewList = reviewService.findByPlace_Category(filter); 
            placeList = placeRepository.findByCategory(filter);
        } else {
            reviewList = reviewService.findAll(); // ⭐ Service 호출
            placeList = placeRepository.findAll();
        }
        
        model.addAttribute("reviews", reviewList);
        model.addAttribute("places", placeList); 
        model.addAttribute("currentFilter", filter);
        
        return "review_list";
    }

    // 3. 리뷰 작성 페이지 보여주기
    @GetMapping("/review/create")
    public String createForm(Model model) {
        List<Place> placeList = placeRepository.findAll();
        model.addAttribute("places", placeList);
        return "review_form";
    }

    // 4. 리뷰 저장하기 (직접 입력 기능 처리)
    @PostMapping("/review/create")
    public String create(@ModelAttribute Review review,
                         @RequestParam(value = "placeId", required = false) Long placeId,
                         @RequestParam(value = "newPlaceName", required = false) String newPlaceName,
                         @RequestParam(value = "newCategory", required = false) String newCategory,
                         HttpSession session) {

        Member writer = (Member) session.getAttribute("loginMember");
        if (writer == null) return "redirect:/member/login";

        Place place = null;

        // Case 1: '직접 입력(-1)'을 선택했을 때 (새 장소 생성)
        if (placeId != null && placeId == -1) {
            
            if (newPlaceName == null || newPlaceName.trim().isEmpty()) {
                 newPlaceName = "사용자 직접 등록 장소";
            }
            
            place = new Place();
            place.setPlaceName(newPlaceName);
            
            // 카테고리 설정: 선택한 카테고리 사용
            place.setCategory(
                (newCategory != null && !newCategory.trim().isEmpty() && !newCategory.equals("기타")) 
                ? newCategory : "기타/사용자등록"
            );
            
            place.setAddress("위치 정보 없음");
            placeRepository.save(place);
        } 
        // Case 2: 기존 장소를 선택했을 때
        else if (placeId != null) {
            place = placeRepository.findById(placeId).orElse(null);
        }

        // 리뷰 저장 진행
        if (place != null) {
            review.setMember(writer);
            review.setPlace(place);
            review.setCreateDate(LocalDateTime.now());
            reviewService.save(review);
        }

        return "redirect:/review/list";
    }

    // 5. 내가 쓴 리뷰 모아보기 (마이페이지)
    @GetMapping("/review/my")
    public String myReviewList(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        // Service 호출
        List<Review> myReviews = reviewService.findByMember(loginMember); 
        model.addAttribute("myReviews", myReviews);

        return "my_reviews";
    }

    // 6. 리뷰 삭제하기 (POST 요청 처리 - PathVariable 사용)
    @PostMapping("/review/delete/{id}") // ✅ 404 에러를 잡는 최종 매핑
    public String deleteReview(@PathVariable("id") Long id) { 
        
        // [TODO: 실제로는 삭제 전 권한 체크 로직 필요]
        reviewService.delete(id); // Service 호출
        
        return "redirect:/review/list";
    }
}
