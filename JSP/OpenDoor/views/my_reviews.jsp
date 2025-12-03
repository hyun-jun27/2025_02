<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>마이페이지 - 내가 쓴 리뷰</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { font-family: 'Noto Sans KR', sans-serif; background-color: #f8f9fa; }
    </style>
</head>
<body>

    <nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-5 shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-bold" href="/review/list">♿ 모두의 장소</a>
            <div class="collapse navbar-collapse">
                <ul class="navbar-nav ms-auto align-items-center">
                    <li class="nav-item"><a class="nav-link active" href="/review/list">홈</a></li>
                    <li class="nav-item"><a class="nav-link text-warning fw-bold" href="/review/my">마이페이지</a></li>
                    <li class="nav-item">
                        <a class="nav-link btn btn-sm btn-danger text-white ms-3 px-3" href="/member/logout">로그아웃</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container">
        <h3 class="fw-bold mb-4 text-primary">👤 내가 작성한 리뷰 관리</h3>

        <div class="card shadow-sm border-0">
            <div class="card-body p-0">
                <table class="table table-hover align-middle text-center mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>장소명</th>
                            <th style="width: 40%;">내용</th>
                            <th>작성일</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="review" items="${myReviews}">
                            <tr>
                                <td class="fw-bold">${review.place.placeName}</td>
                                <td class="text-start text-truncate" style="max-width: 300px;">${review.content}</td>
                                <td class="text-muted small">${review.createDate}</td>
                                <td>
                                    <a href="/review/delete?id=${review.id}" 
                                       class="btn btn-outline-danger btn-sm"
                                       onclick="return confirm('정말 삭제하시겠습니까?');">
                                        🗑️ 삭제
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty myReviews}">
                            <tr>
                                <td colspan="4" class="py-5 text-muted">작성한 리뷰가 없습니다.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
        
        <div class="mt-3 text-end">
            <a href="/review/list" class="btn btn-secondary">메인으로 돌아가기</a>
        </div>
    </div>

</body>
</html>
