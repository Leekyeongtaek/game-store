package com.mrlee.game_store.repository.query;

import com.mrlee.game_store.domain.*;
import com.mrlee.game_store.dto.request.GameDiscountSearchCondition;
import com.mrlee.game_store.dto.response.*;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mrlee.game_store.domain.QGame.game;
import static com.mrlee.game_store.domain.QGameDiscount.gameDiscount;
import static com.mrlee.game_store.domain.QGameGenre.gameGenre;
import static com.mrlee.game_store.domain.QGamePlatform.gamePlatform;
import static com.mrlee.game_store.domain.QPublisher.*;

//todo 디폴트 생성자와 리플렉션이 사용, QueryProject 애노테이션은 해당 생성자 사용
@Repository
public class GameQueryRepository {

    private final JPAQueryFactory queryFactory;

    public GameQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public PageImpl<GameListItem> findGameList(Pageable pageable) {
        List<GameListItem> gameListItems = queryFactory.select(new QGameListItem(game))
                .from(game)
                .orderBy(game.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = Optional.ofNullable(queryFactory.select(game.count())
                .from(game)
                .fetchOne()).orElse(0L);

        return new PageImpl<>(gameListItems, pageable, totalCount);
    }

    // 모든 정렬 조건을 동적으로 전달할 수 있으므로 다양한 상황에 대응 가능
    // toArray(T[] a) 메서드는 리스트의 내용을 배열로 변환할 때 사용
    // new OrderSpecifier[0]는 빈 배열을 의미
    // 이 배열의 크기는 0이지만, toArray 메서드는 이 배열을 기반으로 리스트의 크기에 맞는 새로운 배열을 생성.
    public PageImpl<GamePromotionResponse> searchPromotionGame(Pageable pageable, GameDiscountSearchCondition condition) {
        List<Game> games = queryFactory.select(game)
                .from(game)
                .join(game.gameDiscount, gameDiscount).fetchJoin()
                .where(
                        genreCondition(condition.getGenreIds()),
                        platformCondition(condition.getPlatformIds()),
                        gameTypesIn(condition.getTypes()),
                        gameDiscountPriceCondition(condition.getWebBasePrices())
                )
                .orderBy(createPromotionOrderSpecifiers(pageable).toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = Optional.ofNullable(queryFactory.select(game.id.count())
                .from(game)
                .join(game.gameDiscount, gameDiscount)
                .where(
                        genreCondition(condition.getGenreIds()),
                        platformCondition(condition.getPlatformIds()),
                        gameTypesIn(condition.getTypes()),
                        gameDiscountPriceCondition(condition.getWebBasePrices())
                ).fetchOne()).orElse(0L);

        List<GamePromotionResponse> contents = games.stream()
                .map(GamePromotionResponse::new)
                .toList();

        return new PageImpl<>(contents, pageable, totalCount);
    }

    // 제네릭(Generics)에서의 와일드카드(Wildcard) 사용, 어떤 타입이든 올 수 있다
    // 와일드 카드를 사용하지 않으면 id는 <Long>, name는 <String>, releaseDate는 <LocalDate> 타입을 미리 선언해야함.
    private List<OrderSpecifier<?>> createPromotionOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.DESC, game.id));
            return orders;
        }

        pageable.getSort().forEach(order -> {
            String property = order.getProperty();
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            switch (property) {
                case "name" -> orders.add(new OrderSpecifier<>(direction, game.name));
                case "releaseDate" -> orders.add(new OrderSpecifier<>(direction, game.releaseDate));
                case "discountPrice" -> orders.add(new OrderSpecifier<>(direction, gameDiscount.discountPrice));
                default -> throw new IllegalArgumentException("지원하지 않는 정렬 방식입니다.");
            }
        });

        return orders;
    }

    private BooleanExpression genreCondition(List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return null;
        }

        return JPAExpressions
                .selectOne()
                .from(gameGenre)
                .where(
                        gameGenre.game.id.eq(game.id),
                        gameGenre.genre.id.in(genreIds)
                ).exists();
    }

    private BooleanExpression platformCondition(List<Long> platformIds) {
        if (platformIds == null || platformIds.isEmpty()) {
            return null;
        }

        return JPAExpressions
                .selectOne()
                .from(gamePlatform)
                .where(
                        gamePlatform.game.id.eq(game.id),
                        gamePlatform.platform.id.in(platformIds)
                )
                .exists();
    }

    private BooleanExpression gameTypesIn(List<GameType> types) {
        return types.isEmpty() ? null : game.type.in(types);
    }

    private BooleanExpression gameDiscountPriceCondition(List<String> webBasePrices) {
        if (webBasePrices == null || webBasePrices.isEmpty()) {
            return null;
        }

        BooleanExpression condition = null;

        for (String webBasePrice : webBasePrices) {
            String[] split = webBasePrice.split("-");
            int minDiscountPrice = Integer.parseInt(split[0]);
            int maxDiscountPrice = Integer.parseInt(split[1]);

            BooleanExpression rangeCondition = gameDiscount.discountPrice.between(minDiscountPrice, maxDiscountPrice);

            //java.lang.NullPointerException: Cannot invoke "com.querydsl.core.types.dsl.BooleanExpression.or(com.querydsl.core.types.Predicate)" because "condition" is null
            condition = (condition == null) ? rangeCondition : condition.or(rangeCondition);
        }

        return condition;
    }

    public GameDetailsResponse getGameDetails(Long gameId) {
        Game fetchedGame = queryFactory.select(game)
                .from(game)
                .leftJoin(game.gameDiscount, gameDiscount).fetchJoin()
                .where(game.id.eq(gameId))
                .fetchOne();

        List<Game> fetchedGames = queryFactory.select(game)
                .from(game)
                .leftJoin(game.gameDiscount, gameDiscount).fetchJoin()
                .where(game.gameGroupId.eq(fetchedGame.getGameGroupId()))
                .fetch();

        Publisher fetchedPublisher = queryFactory
                .selectFrom(publisher)
                .where(publisher.id.eq(fetchedGame.getPublisherId()))
                .fetchOne();

        return new GameDetailsResponse(fetchedGame, fetchedPublisher, fetchedGames);
    }

}
